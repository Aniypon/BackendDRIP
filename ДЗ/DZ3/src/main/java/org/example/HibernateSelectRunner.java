package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public final class HibernateSelectRunner {

    private HibernateSelectRunner() {
    }

    public static void run() {
        try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            List<User> users = session.createQuery("from User order by id", User.class).getResultList();
            System.out.println("Hibernate SELECT result:");
            for (User user : users) {
                System.out.printf("id=%d, name=%s, email=%s%n", user.getId(), user.getName(), user.getEmail());
            }
        }
    }
}
