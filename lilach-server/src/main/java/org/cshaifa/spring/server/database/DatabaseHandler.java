package org.cshaifa.spring.server.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.RollbackException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.utils.Constants;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * This class will handle the server requests to the db
 * like getting the catalog, updating items etc.
 */
public class DatabaseHandler {

    private static List<String> getRandomOrderedImages() {
        List<String> imagesList = new ArrayList<>();
        for (File imageFile : new File(DatabaseHandler.class.getResource("images").getPath()).listFiles()) {
            imagesList.add(imageFile.getAbsolutePath());
        }
        Collections.shuffle(imagesList);
        return imagesList;
    }

    private static void initializeDatabaseIfEmpty() throws HibernateException {
        Session session = DatabaseConnector.getSession();
        session.beginTransaction();
        List<String> imageList = getRandomOrderedImages();
        for (int i = 0; i < imageList.size(); i++) {
            session.save(new CatalogItem("Random flower " + i, imageList.get(i), 40));
        }
        try {
            session.flush();
            session.getTransaction().commit();
        } catch (IllegalStateException | RollbackException e) {
            session.getTransaction().rollback();
            throw new HibernateException(Constants.DATABASE_ERROR);
        }
    }

    public static List<CatalogItem> getCatalog() throws HibernateException {
        // We assume that we're getting the catalog when we first run our app
        Session session = DatabaseConnector.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CatalogItem> query = builder.createQuery(CatalogItem.class);
        query.from(CatalogItem.class);
        List<CatalogItem> catalogItems = session.createQuery(query).getResultList();
        if (catalogItems.isEmpty()) {
            initializeDatabaseIfEmpty();
            catalogItems = session.createQuery(query).getResultList();
        }
        return catalogItems;
    }

    public static void updateItem(CatalogItem newItem) throws HibernateException {
        Session session = DatabaseConnector.getSession();
        session.beginTransaction();
        session.update(newItem);
        try {
            session.flush();
            session.getTransaction().commit();
        } catch (IllegalStateException | RollbackException e) {
            session.getTransaction().rollback();
            throw new HibernateException(Constants.DATABASE_ERROR);
        }
    }

    public static void closeSession() {
        DatabaseConnector.closeSession();
    }
}
