package org.cshaifa.spring.server.database;

import org.cshaifa.spring.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * This class is basically just responsible for holding
 * a static session and handling db connections
 */
public class DatabaseConnector {
    private static SessionFactory sessionFactory = null;

    public static SessionFactory getSessionFactory() throws HibernateException {
        if (sessionFactory != null)
            return sessionFactory;

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(CatalogItem.class);
        configuration.addAnnotatedClass(Store.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Customer.class);
        configuration.addAnnotatedClass(Employee.class);
        configuration.addAnnotatedClass(ChainEmployee.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.addAnnotatedClass(Complaint.class);
        configuration.addAnnotatedClass(Delivery.class);
        configuration.addAnnotatedClass(StoreManager.class);
        configuration.addAnnotatedClass(CustomerServiceEmployee.class);
        configuration.addAnnotatedClass(ChainManager.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
            .applySettings(configuration.getProperties())
            .build();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }
}
