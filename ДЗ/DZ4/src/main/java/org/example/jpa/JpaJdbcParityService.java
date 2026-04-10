package org.example.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class JpaJdbcParityService {

    @PersistenceContext
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager txManager;

    public JpaJdbcParityService(JdbcTemplate jdbcTemplate, PlatformTransactionManager txManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.txManager = txManager;
    }

    public void runAllExamples() {
        List<User> users = List.of(
                new User("John Doe 1", "Test"),
                new User("Jane Doe 2", "Test"),
                new User("Jane Doe 3", "Test")
        );

        clearUsers();
        insertUser(users.get(0));
        insertMultipleUsers(users);
        printUsers("after insertUser + insertMultipleUsers");

        clearUsers();
        insertWithTransaction(users);
        printUsers("after insertWithTransaction");

        clearUsers();
        insertWithTransactionRollback(users);
        printUsers("after insertWithTransactionRollback");

        clearUsers();
        testReadNotCommitted(users);

        clearUsers();
        notDirtyRead();

        clearUsers();
        dirtyRead();

        clearUsers();
        repeatableRead();

        clearUsers();
        notRepeatableRead();

        clearUsers();
        phantomRead();

        clearUsers();
        notPhantomRead();

        anomalyExample();
        notAnomalyExample();

        clearUsers();
        propagationExample(TxMode.REQUIRED);

        clearUsers();
        propagationExample(TxMode.SUPPORTS);

        clearUsers();
        propagationExample(TxMode.REQUIRES_NEW);

        printUsers("final state");
    }

    public void clearUsers() {
        jdbcTemplate.execute("DELETE FROM users");
    }

    public void printUsers(String label) {
        System.out.println("--- " + label + " ---");
        usersOrdered().forEach(System.out::println);
        System.out.println("count=" + countUsers());
    }

    public int countUsers() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        return count == null ? 0 : count;
    }

    public String concatUsersName() {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(string_agg(name, ', ' ORDER BY id), '') FROM users",
                String.class
        );
    }

    public void insertUser(User user) {
        inTx(TransactionDefinition.ISOLATION_DEFAULT, TxMode.REQUIRED, false, () -> entityManager.persist(user));
    }

    public void insertMultipleUsers(List<User> users) {
        inTx(TransactionDefinition.ISOLATION_DEFAULT, TxMode.REQUIRED, false, () -> {
            for (User user : users) {
                entityManager.persist(new User(user.getName(), user.getAbout()));
            }
        });
    }

    public void insertWithTransaction(List<User> users) {
        inTx(TransactionDefinition.ISOLATION_DEFAULT, TxMode.REQUIRED, false, () -> {
            for (User user : users) {
                entityManager.persist(new User(user.getName(), user.getAbout()));
                System.out.println("inserted inside tx -> " + user.getName());
            }
        });
    }

    public void insertWithTransactionRollback(List<User> users) {
        inTx(TransactionDefinition.ISOLATION_DEFAULT, TxMode.REQUIRED, true, () -> {
            for (User user : users) {
                entityManager.persist(new User(user.getName(), user.getAbout()));
                System.out.println("inserted then rollback -> " + user.getName());
            }
        });
    }

    public void testReadNotCommitted(List<User> users) {
        System.out.println("--- testReadNotCommitted (PostgreSQL behaves as READ COMMITTED) ---");
        Thread t1 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_READ_UNCOMMITTED, TxMode.REQUIRED, false, () -> {
            for (User user : users) {
                entityManager.persist(new User(user.getName(), user.getAbout()));
                sleep(300);
            }
        }));
        Thread t2 = new Thread(() -> {
            while (t1.isAlive()) {
                System.out.println("concurrent count=" + countUsers());
                sleep(120);
            }
        });
        t1.start();
        t2.start();
        joinAll(t1, t2);
        printUsers("after testReadNotCommitted");
    }

    public void notDirtyRead() {
        insertUser(new User("Luci", "Test"));
        Thread t1 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, TxMode.REQUIRED, true, () -> {
            jdbcTemplate.update("UPDATE users SET about = about || ' Updated' WHERE name='Luci'");
            sleep(500);
        }));
        Thread t2 = new Thread(() -> {
            sleep(200);
            printUsers("notDirtyRead observed");
        });
        t1.start();
        t2.start();
        joinAll(t1, t2);
    }

    public void dirtyRead() {
        insertUser(new User("Luci", "Test"));
        Thread t1 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_READ_UNCOMMITTED, TxMode.REQUIRED, true, () -> {
            jdbcTemplate.update("UPDATE users SET about = about || ' Updated' WHERE name='Luci'");
            sleep(500);
        }));
        Thread t2 = new Thread(() -> {
            sleep(200);
            printUsers("dirtyRead observed");
        });
        t1.start();
        t2.start();
        joinAll(t1, t2);
        System.out.println("PostgreSQL не допускает грязное чтение даже при READ_UNCOMMITTED");
    }

    public void repeatableRead() {
        insertUser(new User("Luci", "Test"));
        Thread t1 = new Thread(() -> {
            sleep(150);
            inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, TxMode.REQUIRED, false,
                    () -> jdbcTemplate.update("UPDATE users SET about = about || ' V2' WHERE name='Luci'"));
        });
        Thread t2 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, TxMode.REQUIRED, false, () -> {
            printUsers("repeatableRead first read");
            sleep(500);
            printUsers("repeatableRead second read");
        }));
        t1.start();
        t2.start();
        joinAll(t1, t2);
    }

    public void notRepeatableRead() {
        insertUser(new User("Luci", "Test"));
        Thread t1 = new Thread(() -> {
            sleep(150);
            inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, TxMode.REQUIRED, false,
                    () -> jdbcTemplate.update("UPDATE users SET about = about || ' V2' WHERE name='Luci'"));
        });
        Thread t2 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_REPEATABLE_READ, TxMode.REQUIRED, false, () -> {
            printUsers("notRepeatableRead first read");
            sleep(500);
            printUsers("notRepeatableRead second read");
        }));
        t1.start();
        t2.start();
        joinAll(t1, t2);
    }

    public void phantomRead() {
        insertUser(new User("Luci", "Test"));
        Thread t1 = new Thread(() -> {
            sleep(150);
            inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, TxMode.REQUIRED, false,
                    () -> entityManager.persist(new User("Mark", "Test")));
        });
        Thread t2 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, TxMode.REQUIRED, false, () -> {
            System.out.println("phantomRead first aggregate=" + concatUsersName());
            sleep(500);
            System.out.println("phantomRead second aggregate=" + concatUsersName());
        }));
        t1.start();
        t2.start();
        joinAll(t1, t2);
    }

    public void notPhantomRead() {
        insertUser(new User("Luci", "Test"));
        Thread t1 = new Thread(() -> {
            sleep(150);
            inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, TxMode.REQUIRED, false,
                    () -> entityManager.persist(new User("Mark", "Test")));
        });
        Thread t2 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_REPEATABLE_READ, TxMode.REQUIRED, false, () -> {
            System.out.println("notPhantomRead first aggregate=" + concatUsersName());
            sleep(500);
            System.out.println("notPhantomRead second aggregate=" + concatUsersName());
        }));
        t1.start();
        t2.start();
        joinAll(t1, t2);
    }

    public void anomalyExample() {
        prepareCalculator();
        System.out.println("--- anomalyExample (REPEATABLE_READ) ---");
        Thread t1 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_REPEATABLE_READ, TxMode.REQUIRED, false, () -> {
            jdbcTemplate.queryForObject("SELECT SUM(value) FROM calculator WHERE class = 1", Integer.class);
            sleep(300);
            jdbcTemplate.update("INSERT INTO calculator (class, value) VALUES (2, 30)");
        }));
        Thread t2 = new Thread(() -> inTx(TransactionDefinition.ISOLATION_REPEATABLE_READ, TxMode.REQUIRED, false, () -> {
            jdbcTemplate.queryForObject("SELECT SUM(value) FROM calculator WHERE class = 2", Integer.class);
            sleep(300);
            jdbcTemplate.update("INSERT INTO calculator (class, value) VALUES (1, 300)");
        }));
        t1.start();
        t2.start();
        joinAll(t1, t2);
        printCalculator();
        jdbcTemplate.execute("DROP TABLE calculator");
    }

    public void notAnomalyExample() {
        prepareCalculator();
        System.out.println("--- notAnomalyExample (SERIALIZABLE) ---");
        Thread t1 = new Thread(() -> {
            try {
                inTx(TransactionDefinition.ISOLATION_SERIALIZABLE, TxMode.REQUIRED, false, () -> {
                    jdbcTemplate.queryForObject("SELECT SUM(value) FROM calculator WHERE class = 1", Integer.class);
                    sleep(300);
                    jdbcTemplate.update("INSERT INTO calculator (class, value) VALUES (2, 30)");
                });
            } catch (RuntimeException e) {
                System.out.println("serializable conflict (thread1): " + e.getClass().getSimpleName());
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                inTx(TransactionDefinition.ISOLATION_SERIALIZABLE, TxMode.REQUIRED, false, () -> {
                    jdbcTemplate.queryForObject("SELECT SUM(value) FROM calculator WHERE class = 2", Integer.class);
                    sleep(300);
                    jdbcTemplate.update("INSERT INTO calculator (class, value) VALUES (1, 300)");
                });
            } catch (RuntimeException e) {
                System.out.println("serializable conflict (thread2): " + e.getClass().getSimpleName());
            }
        });
        t1.start();
        t2.start();
        joinAll(t1, t2);
        printCalculator();
        jdbcTemplate.execute("DROP TABLE calculator");
    }

    public void propagationExample(TxMode mode) {
        System.out.println("--- propagation " + mode + " ---");
        Thread t = new Thread(() -> {
            if (mode == TxMode.SUPPORTS) {
                // SUPPORTS does not start a transaction by itself.
                jdbcTemplate.update("INSERT INTO users (name, about) VALUES (?, ?)", "Luci", "Test");
                sleep(200);
                jdbcTemplate.update("INSERT INTO users (name, about) VALUES (?, ?)", "Mark", "Test");
                sleep(200);
                return;
            }

            inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, mode, false, () -> {
                entityManager.persist(new User("Luci", "Test"));
                sleep(200);
                inTx(TransactionDefinition.ISOLATION_READ_COMMITTED, mode, false,
                        () -> entityManager.persist(new User("Mark", "Test")));
                sleep(200);
            });
        });
        Thread observer = new Thread(() -> {
            while (t.isAlive()) {
                System.out.println("observer count=" + countUsers());
                sleep(80);
            }
        });
        t.start();
        observer.start();
        joinAll(t, observer);
        printUsers("after propagation " + mode);
    }

    private void inTx(int isolationLevel, TxMode mode, boolean rollback, Runnable action) {
        TransactionTemplate template = new TransactionTemplate(txManager);
        template.setIsolationLevel(isolationLevel);
        template.setPropagationBehavior(toPropagation(mode));
        template.execute(status -> {
            try {
                action.run();
                if (rollback) {
                    status.setRollbackOnly();
                }
            } catch (RuntimeException e) {
                status.setRollbackOnly();
                throw e;
            }
            return null;
        });
    }

    private int toPropagation(TxMode mode) {
        return switch (mode) {
            case REQUIRED -> TransactionDefinition.PROPAGATION_REQUIRED;
            case SUPPORTS -> TransactionDefinition.PROPAGATION_SUPPORTS;
            case REQUIRES_NEW -> TransactionDefinition.PROPAGATION_REQUIRES_NEW;
        };
    }

    private List<User> usersOrdered() {
        return entityManager.createQuery("from User u order by u.id", User.class).getResultList();
    }

    private void prepareCalculator() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS calculator");
        jdbcTemplate.execute("CREATE TABLE calculator (class INT, value INT)");
        jdbcTemplate.update("INSERT INTO calculator (class, value) VALUES (1, 10)");
        jdbcTemplate.update("INSERT INTO calculator (class, value) VALUES (1, 20)");
        jdbcTemplate.update("INSERT INTO calculator (class, value) VALUES (2, 100)");
        jdbcTemplate.update("INSERT INTO calculator (class, value) VALUES (2, 200)");
    }

    private void printCalculator() {
        List<String> rows = new ArrayList<>();
        jdbcTemplate.query("SELECT class, value FROM calculator ORDER BY class, value",
                (rs, rowNum) -> rows.add(rs.getInt("class") + " " + rs.getInt("value")));
        rows.forEach(System.out::println);
    }

    private void joinAll(Thread... threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
