package org.cshaifa.spring.server.database;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.ChainEmployee;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.SubscriptionType;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.utils.Constants;
import org.cshaifa.spring.utils.ImageUtils;
import org.cshaifa.spring.utils.SecureUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * This class will handle the server requests to the db
 * like getting the catalog, updating items etc.
 */
public class DatabaseHandler {

    private static final int PASSWORD_SALT_SIZE = 24;
    private static final int PASSWORD_KEY_LENGTH = 512;
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int PBKDF2_ITERATIONS = 10000;

    private static String generateHexSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[PASSWORD_SALT_SIZE];
        random.nextBytes(salt);
        return SecureUtils.encodeHexString(salt);
    }

    private static String getHashedPassword(String rawPassword, String saltHexString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = SecureUtils.decodeHexString(saltHexString);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, PBKDF2_ITERATIONS, PASSWORD_KEY_LENGTH);
        return SecureUtils.encodeHexString(secretKeyFactory.generateSecret(spec).getEncoded());
    }

    public static User loginUser(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null)
            return null;

        Session session = DatabaseConnector.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        try {
            query.select(root).where(builder.and(builder.equal(root.get("username"), username),
                    builder.equal(root.get("password"), getHashedPassword(password, user.getPasswordSalt()))));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Shouldn't happen, only if we mistyped something in the algorithm name, etc.
            e.printStackTrace();
            return null;
        }

        return session.createQuery(query).uniqueResult();
    }

    public static User getUserByUsername(String username) {
        Session session = DatabaseConnector.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(builder.equal(root.get("username"), username));

        return session.createQuery(query).uniqueResult();
    }

    public static void updateLoginStatus(User user, boolean status) throws HibernateException {
        Session session = DatabaseConnector.getSession();
        if (user.isLoggedIn() == !status) {
            if (status)
                user.login();
            else
                user.logout();
            session.beginTransaction();
            session.merge(user);
            tryFlushSession(session);
        }

    }

    public static User getUserByEmail(String email) {
        Session session = DatabaseConnector.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(builder.equal(root.get("email"), email));

        return session.createQuery(query).uniqueResult();
    }

    public static String registerCustomer(String fullName, String email, String username, String rawPassword,
            List<Store> stores, SubscriptionType subscriptionType)
            throws Exception {
        if (getUserByEmail(email) != null) {
            return Constants.EMAIL_EXISTS;
        }

        if (getUserByUsername(username) != null) {
            return Constants.USERNAME_EXISTS;
        }

        Session session = DatabaseConnector.getSession();
        session.beginTransaction();

        try {
            String hexSalt = generateHexSalt();
            Customer customer = new Customer(fullName, username, email, getHashedPassword(rawPassword, hexSalt),
                    hexSalt, stores,
                    false, subscriptionType);
            session.save(customer);
            for (Store store : stores) {
                store.addCustomer(customer);
                session.merge(store);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new HibernateException(Constants.FAIL_MSG);
        }

        tryFlushSession(session);

        return Constants.SUCCESS_MSG;
    }

    public static String registerChainEmployee(String fullName, String email, String username, String rawPassword)
            throws HibernateException {

        Session session = DatabaseConnector.getSession();
        session.beginTransaction();

        try {
            String hexSalt = generateHexSalt();
            session.save(
                    new ChainEmployee(fullName, username, email, getHashedPassword(rawPassword, hexSalt), hexSalt));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Shouldn't happen, only if we mistyped something in the algorithm name, etc.
            e.printStackTrace();
            throw new HibernateException(Constants.FAIL_MSG);
        }

        tryFlushSession(session);

        return Constants.SUCCESS_MSG;
    }


    private static List<List<Path>> getRandomOrderedImages() {
        List<List<Path>> imagesLists = new ArrayList<>();
        imagesLists.add(ImageUtils.getAllImagesFromFolder("images/flowers", DatabaseHandler.class));
        imagesLists.add(ImageUtils.getAllImagesFromFolder("images/bouquets", DatabaseHandler.class));
        imagesLists.add(ImageUtils.getAllImagesFromFolder("images/plants", DatabaseHandler.class));
        imagesLists.add(ImageUtils.getAllImagesFromFolder("images/orchids", DatabaseHandler.class));
        imagesLists.add(ImageUtils.getAllImagesFromFolder("images/wine", DatabaseHandler.class));
        imagesLists.add(ImageUtils.getAllImagesFromFolder("images/chocolate", DatabaseHandler.class));
        imagesLists.add(ImageUtils.getAllImagesFromFolder("images/sets", DatabaseHandler.class));
        //Collections.shuffle(imagesList);
        return imagesLists;
    }

    public static void initializeDatabaseIfEmpty() throws Exception {
        // Assume that we initialize only if the catalog is empty
        if (!getAllEntities(CatalogItem.class).isEmpty())
            return;

        Session session = DatabaseConnector.getSession();
        session.beginTransaction();
        List<List<Path>> imageLists = getRandomOrderedImages();
        Random random = new Random();
        List<CatalogItem> randomItems = new ArrayList<>();
        String[] sizes = {"large", "medium", "small"};
        String[] colors = {"red", "orange", "pink"};
        String[] itemTypes = {"flower", "bouquet", "plant", "orchid", "wine", "chocolate", "set"};
        int typeInd = 0;
        for (List<Path> imageList : imageLists) {
            if (imageList!=null) {
                for (Path imagePath : imageList) {
                    int randomInt = random.nextInt(0,2);
                    double randomPrice = random.nextInt(50, 500) + 0.99;
                    int randomQuantity = random.nextInt(500);
                    randomItems.add(new CatalogItem(
                            "Random Item",
                            imagePath.toUri().toString(),
                            randomPrice, randomQuantity, false, 0.0,
                            sizes[randomInt], itemTypes[typeInd], colors[randomInt]));
                }
            }
            typeInd++;

        }

        //On Sale Items
        randomItems.remove(0);
        randomItems.add(0,new CatalogItem("Sale flower", imageLists.get(0).get(0).toUri().toString(), 249.99, 10, true, 50.0, "large", "flower", "white"));
        randomItems.remove(1);
        randomItems.add(1,new CatalogItem("Sale flower", imageLists.get(0).get(1).toUri().toString(), 149.99, 10, true, 50.0, "medium", "flower", "yellow"));

        for (CatalogItem item : randomItems) {
            session.save(item);
        }

        Store store = new Store("Example Store", "Example Address", new ArrayList<CatalogItem>(randomItems.subList(0, 5)));
        session.save(store);
        tryFlushSession(session);

        List<Store> stores = new ArrayList<>();
        stores.add(store);
        for (int i = 0; i < 20; i++) {
            String email = "example" + i + "@mail.com";
            registerCustomer("Customer " + i, email, "cust" + i, "pass" + i, stores, SubscriptionType.STORE);
        }

        registerChainEmployee("Employee", "Employee", "Employee123", "Employee123");

    }

    private static <T> List<T> getAllEntities(Class<T> c) {
        Session session = DatabaseConnector.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(c);
        query.from(c);
        return session.createQuery(query).getResultList();
    }

    public static List<Store> getStores() {
        return getAllEntities(Store.class);
    }

    public static List<CatalogItem> getCatalog() {
        List<CatalogItem> catalogItems = getAllEntities(CatalogItem.class);

        for (CatalogItem catalogItem : catalogItems) {
            try {
                catalogItem.setImage(
                        ImageUtils.getByteArrayFromURI(new URI(catalogItem.getImagePath()), DatabaseHandler.class));
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
            // TODO: report somewhere
            e.printStackTrace();
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
