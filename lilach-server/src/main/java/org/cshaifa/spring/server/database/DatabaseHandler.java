package org.cshaifa.spring.server.database;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.utils.Constants;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * This class will handle the server requests to the db
 * like getting the catalog, updating items etc.
 */
public class DatabaseHandler {

    private static byte[] getByteArrayFromURI(URI imageUri) throws IOException {
        BufferedImage image;

        if (imageUri.getScheme().equals("jar"))
            image = ImageIO.read(DatabaseHandler.class.getResource(imageUri.toString().split("!")[1]));
        else
            image = ImageIO.read(new File(imageUri));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        return outputStream.toByteArray();
    }

    private static List<Path> getAllImagesFromFolder(String path) {
        List<Path> imagesList = new ArrayList<>();
        FileSystem fileSystem = null;

        try {
            Path imagesPath;
            URI imagesUri = DatabaseHandler.class.getResource(path).toURI();

            if (imagesUri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(imagesUri, Collections.emptyMap(), null);
                imagesPath = fileSystem.getPath(imagesUri.toString().split("!")[1]);
            } else {
                imagesPath = Paths.get(imagesUri);
            }

            for (Iterator<Path> it = Files.walk(imagesPath, 1).iterator(); it.hasNext();) {
                Path folderPath = it.next().toAbsolutePath();
                if (!Files.isDirectory(folderPath))
                    imagesList.add(folderPath.toAbsolutePath());
            }

            if (fileSystem != null)
                fileSystem.close();
        } catch (Exception e) {
            // TODO: maybe report this somewhere
            e.printStackTrace();
        }

        return imagesList;
    }

    private static List<Path> getRandomOrderedImages() {
        List<Path> imagesList = getAllImagesFromFolder("images");

        Collections.shuffle(imagesList);
        return imagesList;
    }

    private static void initializeDatabaseIfEmpty() throws HibernateException {
        Session session = DatabaseConnector.getSession();
        session.beginTransaction();
        List<Path> imageList = getRandomOrderedImages();
        Random random = new Random();
        List<CatalogItem> randomItems = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            double randomPrice = 200 * random.nextDouble();
            int randomQuantity = random.nextInt(500);
            randomItems.add(new CatalogItem("Random flower " + i, imageList.get(i).toUri().toString(), new BigDecimal(randomPrice).setScale(2, RoundingMode.HALF_UP).doubleValue(), randomQuantity));
        }

        for (CatalogItem item : randomItems) {
            session.save(item);
        }

        Store store = new Store("Example Store", "Example Address", randomItems.subList(0, 5));
        session.save(store);

        for (int i = 0; i < 20; i++) {
            session.save(new Customer("Customer " + i, "cust" + i, "example@mail.com", "pass", List.of(store)));
        }

        tryFlushSession(session);
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

        for (CatalogItem catalogItem : catalogItems) {
            try {
                catalogItem.setImage(getByteArrayFromURI(new URI(catalogItem.getImagePath())));
            } catch (Exception e) {
                // TODO: maybe log the exception somewhere
                e.printStackTrace();
            }
        }
        return catalogItems;
    }

    public static void updateItem(CatalogItem newItem) throws HibernateException {
        Session session = DatabaseConnector.getSession();
        session.beginTransaction();
        session.merge(newItem);
        tryFlushSession(session);
    }

    public static void tryFlushSession(Session session) throws HibernateException {
        try {
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(Constants.DATABASE_ERROR);
        }
    }

    public static void closeSession() {
        DatabaseConnector.closeSession();
    }

    public static void openSession() throws HibernateException {
        DatabaseConnector.getSession();
    }
}
