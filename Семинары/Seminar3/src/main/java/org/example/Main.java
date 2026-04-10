package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        entityManagerLinks();
    }

    public static void sessionFactory() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = new User("Иван");
        session.persist(user);

        session.getTransaction().commit();

        List<User> users = session.createQuery("FROM User", User.class).getResultList();
        for (User u : users) {
            System.out.println("User ID: " + u.getId() + ", Name: " + u.getName());
        }

        session.close();
        sessionFactory.close();
    }

    public static void entityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManager entityManager = emf.createEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        User user = new User();
        user.setName("Петр Петров");
        entityManager.persist(user);
        transaction.commit();


        List<User> users = entityManager.createQuery("FROM User", User.class).getResultList();
        for (User it : users) {
            System.out.println("ID: " + it.getId() + ", Name: " + it.getName());
        }

        entityManager.close();
        emf.close();
    }

    public static void entityManager_merge() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManager entityManager = emf.createEntityManager();

        entityManager.getTransaction().begin();
        User user = new User();
        user.setName("Петр Петров");
        user.setAbout("Описание");
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        user = entityManager.find(User.class, user.getId());
        System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", About: " + user.getAbout());

        entityManager.getTransaction().begin();
        user.setName("Петр Степанович");
        user.setAbout("Описание обновлено");
        entityManager.merge(user);
        entityManager.getTransaction().commit();
        entityManager.detach(user);
        user = entityManager.find(User.class, user.getId());
        System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", About: " + user.getAbout());

        entityManager.close();
        emf.close();
    }

    public static void entityManager_entity_status() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManager entityManager = emf.createEntityManager();

        entityManager.getTransaction().begin();
        User user = new User();
        user.setName("Петр Петров");
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        user = entityManager.find(User.class, user.getId());
        System.out.println("ID: " + user.getId() + ", Name: " + user.getName());

        entityManager.getTransaction().begin();
        user.setName("Петр Ильич");
        entityManager.getTransaction().commit();
        user = entityManager.find(User.class, user.getId());
        System.out.println("ID: " + user.getId() + ", Name: " + user.getName());

        entityManager.getTransaction().begin();
        entityManager.detach(user);
        user.setName("Петр Степанович");
        entityManager.getTransaction().commit();
        user = entityManager.find(User.class, user.getId());
        System.out.println("ID: " + user.getId() + ", Name: " + user.getName());

        entityManager.getTransaction().begin();
        entityManager.remove(user);
        entityManager.getTransaction().commit();

        user = entityManager.find(User.class, user.getId());
        System.out.println("Is null: " + (user == null));

        entityManager.close();
        emf.close();
    }

    public static void getAllUsersJPQL() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManager entityManager = emf.createEntityManager();

        String jpql = "FROM User u";
        List<User> users = entityManager.createQuery(jpql, User.class).getResultList();

        for (User u : users) {
            System.out.println("ID: " + u.getId() + ", Name: " + u.getName() + ", About: " + u.getAbout());
        }

        entityManager.close();
    }

    public static void findUserByNativeQuery() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");

        EntityManager entityManager = emf.createEntityManager();

        String sql = "SELECT * FROM users WHERE name = ?";
        List<User> users = entityManager.createNativeQuery(sql, User.class)
            .setParameter(1, "Петр Петров")
            .getResultList();

        if (!users.isEmpty()) {
            User user = users.get(0);
            System.out.println("Найден (Native): " + user.getName() + ", About: " + user.getAbout());
        } else {
            System.out.println("Пользователь с именем Иван Петрович не найден.");
        }

        entityManager.close();
    }

    public static void findUserByCriteria() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");

        EntityManager entityManager = emf.createEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(cb.equal(root.get("name"), "Петр Петров"));

        List<User> users = entityManager.createQuery(query).getResultList();

        for (User u : users) {
            System.out.println("Criteria — ID: " + u.getId() + ", Name: " + u.getName() + ", About: " + u.getAbout());
        }

        entityManager.close();
    }

    public static void updateUserName() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");

        EntityManager entityManager = emf.createEntityManager();

        entityManager.getTransaction().begin();

        String jpql = "UPDATE User u SET u.name = :newName WHERE u.id = :id";
        int updated = entityManager.createQuery(jpql)
            .setParameter("newName", "Иван Серый")
            .setParameter("id", 1L)
            .executeUpdate();

        entityManager.getTransaction().commit();

        System.out.println("Обновлено записей: " + updated);

        entityManager.close();
    }

    public static void entityManagerLinks() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManager em = emf.createEntityManager();

        // Создание данных
        em.getTransaction().begin();

        Category electronics = new Category("Электроника");
        Category books = new Category("Книги");

        Product phone = new Product("Смартфон", new BigDecimal("30000"));
        Product novel = new Product("Роман", new BigDecimal("500"));

        phone.setCategory(electronics);
        novel.setCategory(books);

        Customer customer = new Customer("Алексей");

        Order order1 = new Order(LocalDate.now());
        order1.addProduct(phone);
        order1.addProduct(novel);

        customer.addOrder(order1);

        em.persist(electronics);
        em.persist(books);
        em.persist(phone);
        em.persist(novel);
        em.persist(customer);

        em.getTransaction().commit();
        em.close();

        // Чтение данных
        em = emf.createEntityManager();
        List<Customer> customers = em.createQuery("SELECT c FROM Customer c JOIN FETCH c.orders", Customer.class)
            .getResultList();

        for (Customer c : customers) {
            System.out.println("Покупатель: " + c.getName());
            for (Order o : c.getOrders()) {
                System.out.println("  Заказ от: " + o.getOrderDate());
                for (Product p : o.getProducts()) {
                    System.out.println("    Товар: " + p.getName());
                }
            }
        }

        em.close();
        emf.close();
    }
}