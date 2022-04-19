package org.cshaifa.spring.server.database;

import java.util.List;

import javax.persistence.RollbackException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.utils.Constants;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * This class will handle the server requests to the db
 * like getting the catalog, updating items etc.
 */
public class DatabaseHandler {

    public List<CatalogItem> getCatalog() throws HibernateException {
        Session session = DatabaseConnector.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CatalogItem> query = builder.createQuery(CatalogItem.class);
        query.from(CatalogItem.class);
        return session.createQuery(query).getResultList();
    }

    public void updateItem(CatalogItem newItem) throws HibernateException {
        Session session = DatabaseConnector.getSession();
        session.beginTransaction();
        session.update(newItem);
        try {
            session.getTransaction().commit();
        } catch (IllegalStateException | RollbackException e) {
            throw new HibernateException(Constants.DATABASE_ERROR);
        }
    }

    public void closeSession() {
        DatabaseConnector.closeSession();
    }
}
