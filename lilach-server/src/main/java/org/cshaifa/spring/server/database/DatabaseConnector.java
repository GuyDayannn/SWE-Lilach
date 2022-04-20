package org.cshaifa.spring.server.database;

import org.cshaifa.spring.entities.CatalogItem;
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
    private static Session session = null;

    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(CatalogItem.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
            .applySettings(configuration.getProperties())
            .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static void closeSession() {
        if (session != null)
            session.close();
    }

    public static Session getSession() throws HibernateException {
        if (session != null && session.isOpen())
            return session;

        session = getSessionFactory().openSession();
        return session;
    }
}
