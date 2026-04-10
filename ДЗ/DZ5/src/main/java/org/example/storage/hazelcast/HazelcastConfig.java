package org.example.storage.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("dz5-cluster");
        // Embedded single-node grid: external network discovery is disabled.
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        return Hazelcast.newHazelcastInstance(config);
    }
}
