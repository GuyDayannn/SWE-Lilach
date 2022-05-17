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

import org.cshaifa.spring.entities.*;
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

    public static String registerCustomer(String fullName, String email, String username, String rawPassword)
            throws HibernateException {
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
            session.save(
                    new Customer(fullName, username, email, getHashedPassword(rawPassword, hexSalt), hexSalt, false));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Shouldn't happen, only if we mistyped something in the algorithm name, etc.
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

    private static List<Path> getRandomOrderedImages() {
        List<Path> imagesList = ImageUtils.getAllImagesFromFolder("images", DatabaseHandler.class);

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
            randomItems.add(new CatalogItem("Random flower " + i, imageList.get(i).toUri().toString(),
                    new BigDecimal(randomPrice).setScale(2, RoundingMode.HALF_UP).doubleValue(), randomQuantity, false,
                    0.0));
        }

        for (CatalogItem item : randomItems) {
            session.save(item);
        }

        Store store = new Store("Example Store", "Example Address", randomItems.subList(0, 5), null);
        session.save(store);
        tryFlushSession(session);

        for (int i = 0; i < 20; i++) {
            registerCustomer("Customer " + i, "example" + i + "@mail.com", "cust" + i, "pass" + i);
        }

        registerChainEmployee("Employee","Employee","Employee123","Employee123");

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
