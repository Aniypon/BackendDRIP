package org.example.storage;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.example.storage.mongo.Author;
import org.example.storage.mongo.AuthorRepository;
import org.example.storage.postgres.User;
import org.example.storage.postgres.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;

/**
 * Демонстрация всех типов хранилищ: PostgreSQL (JPA), MongoDB, Redis, ClickHouse, S3 и Hazelcast.
 */
@Component
public class StorageRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StorageRunner.class);

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate clickHouseJdbcTemplate;
    private final S3Client s3Client;
    private final HazelcastInstance hazelcastInstance;

    public StorageRunner(UserRepository userRepository,
                         AuthorRepository authorRepository,
                         StringRedisTemplate redisTemplate,
                         @Qualifier("clickHouseJdbcTemplate") JdbcTemplate clickHouseJdbcTemplate,
                         S3Client s3Client,
                         HazelcastInstance hazelcastInstance) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.redisTemplate = redisTemplate;
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.s3Client = s3Client;
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void run(String... args) {
        postgres();
        mongo();
        redis();
        clickHouse();
        s3();
        hazelcast();
        log.info("All storage types verified successfully");
    }

    private void postgres() {
        userRepository.deleteAll();
        userRepository.save(new User("Ivan"));
        userRepository.save(new User("Olga"));
        log.info("[PostgreSQL/JPA] users count = {}", userRepository.count());
    }

    private void mongo() {
        authorRepository.deleteAll();
        authorRepository.save(new Author("Tolkien"));
        log.info("[MongoDB] authors count = {}", authorRepository.count());
    }

    private void redis() {
        redisTemplate.opsForValue().set("greeting", "hello from redis");
        log.info("[Redis] greeting = {}", redisTemplate.opsForValue().get("greeting"));
    }

    private void clickHouse() {
        clickHouseJdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS events (id UInt64, name String) ENGINE = MergeTree ORDER BY id");
        clickHouseJdbcTemplate.execute("TRUNCATE TABLE events");
        clickHouseJdbcTemplate.update("INSERT INTO events (id, name) VALUES (1, 'login')");
        clickHouseJdbcTemplate.update("INSERT INTO events (id, name) VALUES (2, 'logout')");
        Long count = clickHouseJdbcTemplate.queryForObject("SELECT count() FROM events", Long.class);
        log.info("[ClickHouse] events count = {}", count);
    }

    private void s3() {
        String bucket = "demo";
        String key = "hello.txt";
        if (s3Client.listBuckets().buckets().stream().noneMatch(b -> b.name().equals(bucket))) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(key).build(),
                RequestBody.fromString("hello from s3", StandardCharsets.UTF_8));
        ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(
                GetObjectRequest.builder().bucket(bucket).key(key).build());
        log.info("[S3/MinIO] object {}/{} = {}", bucket, key, object.asUtf8String());
    }

    private void hazelcast() {
        IMap<String, String> map = hazelcastInstance.getMap("demo");
        map.put("key", "hello from hazelcast");
        log.info("[Hazelcast] map.get(key) = {}", map.get("key"));
    }
}
