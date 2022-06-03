package org.cshaifa.spring.server.database;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

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
 * This class will handle the server requests to the db like getting the
 * catalog, updating items etc.
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

        Session session = DatabaseConnector.getSessionFactory().openSession();
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

        User userFound = session.createQuery(query).uniqueResult();
        session.close();
        return userFound;
    }

    public static User getUserByUsername(String username) {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(builder.equal(root.get("username"), username));

        User user = session.createQuery(query).uniqueResult();
        session.close();
        return user;
    }

    public static void updateLoginStatus(User user, boolean status) throws HibernateException {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        if (user.isLoggedIn() == !status) {
            if (status)
                user.login();
            else
                user.logout();
            session.beginTransaction();
            updateDB(session, user);
            tryFlushSession(session);
        } else
            session.close();

    }

    public static User getUserByEmail(String email) {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(builder.equal(root.get("email"), email));

        User user = session.createQuery(query).uniqueResult();
        session.close();
        return user;
    }

    public static String registerCustomer(String fullName, String email, String username, String rawPassword,
            List<Store> stores, SubscriptionType subscriptionType, List<Complaint> complaintList) throws Exception {
        if (getUserByEmail(email) != null) {
            return Constants.EMAIL_EXISTS;
        }

        if (getUserByUsername(username) != null) {
            return Constants.USERNAME_EXISTS;
        }

        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();

        try {
            String hexSalt = generateHexSalt();
            Customer customer = new Customer(fullName, username, email, getHashedPassword(rawPassword, hexSalt),
                    hexSalt, stores, false, subscriptionType, complaintList);
            session.save(customer);
            for (Store store : stores) {
                store.addCustomer(customer);
                updateDB(session, store);
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.close();
            throw new HibernateException(Constants.FAIL_MSG);
        }

        tryFlushSession(session);

        return Constants.SUCCESS_MSG;
    }

    public static ChainEmployee registerChainEmployee(String fullName, String email, String username, String rawPassword)
            throws HibernateException {

        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        ChainEmployee chainEmployee= null;

        try {
            String hexSalt = generateHexSalt();
            chainEmployee = new ChainEmployee(fullName, username, email, getHashedPassword(rawPassword, hexSalt), hexSalt);
            session.save(chainEmployee);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Shouldn't happen, only if we mistyped something in the algorithm name, etc.
            e.printStackTrace();
            session.close();
            throw new HibernateException(Constants.FAIL_MSG);
        }

        tryFlushSession(session);

        return chainEmployee;
    }

    public static StoreManager registerStoreManager(String fullName, String email, String username, String rawPassword)
            throws HibernateException {

        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        StoreManager storeManager= null;
        try {
            String hexSalt = generateHexSalt();
            storeManager = new StoreManager(fullName, username, email, getHashedPassword(rawPassword, hexSalt), hexSalt);
            session.save(storeManager); }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Shouldn't happen, only if we mistyped something in the algorithm name, etc.
            e.printStackTrace();
            session.close();
            throw new HibernateException(Constants.FAIL_MSG);
        }

        tryFlushSession(session);

        return storeManager;
    }

    public static String freezeCustomer(Customer customer, boolean toFreeze) {
        if (customer.isFrozen() && toFreeze)
            return "Account Already Frozen";
        else if (!customer.isFrozen() && !toFreeze)
            return "An Already Unfrozen";

        if (toFreeze)
            customer.freeze();
        else
            customer.unfreeze();
        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        updateDB(session, customer);
        tryFlushSession(session);

        if (toFreeze)
            return "Account has been frozen";
        else
            return "Account has be unfrozen";
    }

    public static Store getWarehouseStore() {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Store> query = builder.createQuery(Store.class);
        Root<Store> root = query.from(Store.class);

        query.select(root).where(builder.equal(root.get("name"), Constants.WAREHOUSE_NAME));

        Store store = session.createQuery(query).uniqueResult();
        session.close();
        return store;
    }

    public static Order createOrder(Store store, Customer customer, Map<CatalogItem, Integer> items, String greeting,
            Timestamp orderDate, Timestamp supplyDate, boolean delivery, Delivery deliveryDetails)
            throws HibernateException {
        if (!delivery) {
            // Check stock
            //supplyDate = orderDate;
            if (!items.entrySet().stream().allMatch(entry -> {
                return entry.getKey().getStock().containsKey(store)
                        && entry.getValue() <= entry.getKey().getStock().get(store);
            })) {
                return null;
            }
        }

        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();

        if (deliveryDetails != null)
            session.save(deliveryDetails);

        Order order = new Order(items, store, customer, greeting, orderDate, supplyDate, delivery, deliveryDetails);
        session.save(order);

        store.addOrder(order);
        updateDB(session, store);

        customer.addOrder(order);
        updateDB(session, customer);

        if (!delivery) {
            // Update stock
            items.forEach((item, amount) -> {
                item.reduceQuantity(store, amount);
                updateDB(session, item);
            });
        }

        tryFlushSession(session);

        return order;
    }

    public static Complaint addComplaint(String complaintDescription, Customer customer, Store store)
            throws HibernateException {

        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();

        Timestamp nowTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        Complaint complaint = new Complaint(complaintDescription, "", 0.0, true, customer, store, nowTimestamp);
        session.save(complaint);

        customer.addComplaint(complaint);
        updateDB(session, customer);
        store.addComplaint(complaint);
        updateDB(session, store);

        tryFlushSession(session);

        return complaint;
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
        // Collections.shuffle(imagesLists);
        return imagesLists;
    }

    public static void saveStores(List<Store> stores) {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        for (Store store : stores) {
            session.save(store);
        }
        tryFlushSession(session);
    }


    public static void saveItems(List<CatalogItem> items) {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        for (CatalogItem item : items) {
            session.save(item);
        }
        tryFlushSession(session);
    }

    public static List<Store> initStores(List<StoreManager> managers, List<ChainEmployee> employees) {
        List<Store> stores = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<ChainEmployee> storeEmployees = new ArrayList<>();;
            storeEmployees.add(employees.get(2*i));
            storeEmployees.add(employees.get((2*i)+1));
            stores.add(new Store("Store" + i, "Address" + i, managers.get(i), storeEmployees));
        }
        stores.add(new Store(Constants.WAREHOUSE_NAME, "Everywhere", managers.get(10), new ArrayList<>()));
        return stores;
    }

    public static List<CatalogItem> initItems(List<Store> stores) {
        List<List<Path>> imageLists = getRandomOrderedImages();
        Random random = new Random();
        List<CatalogItem> randomItems = new ArrayList<>();
        String[] sizes = { "large", "medium", "small" };
        String[] colors = { "red", "orange", "pink" };
        String[] itemTypes = { "flower", "bouquet", "plant", "orchid", "wine", "chocolate", "set" };
        int typeInd = 0;
        for (List<Path> imageList : imageLists) {
            if (imageList != null) {
                for (Path imagePath : imageList) {
                    int randomInt = random.nextInt(0, 2);
                    double randomPrice = random.nextInt(50, 500) + 0.99;
                    Map<Store, Integer> stock = stores.stream()
                            .collect(Collectors.toMap(Function.identity(), __ -> random.nextInt(5000, 10000)));
                    randomItems.add(new CatalogItem("Random Item", imagePath.toUri().toString(), randomPrice, stock,
                            false, 0.0, sizes[randomInt], itemTypes[typeInd], colors[randomInt], true));
                }
            }
            typeInd++;
        }

        // On Sale Items
        // randomItems.remove(0);
        // randomItems.add(0, new CatalogItem("Sale flower",
        // imageLists.get(0).get(0).toUri().toString(), 249.99,
        // new HashMap<>(Map.of(store, 10)), true, 50.0, "large", "flower", "white",
        // true));
        // randomItems.remove(1);
        // randomItems.add(1, new CatalogItem("Sale flower",
        // imageLists.get(0).get(1).toUri().toString(), 149.99,
        // new HashMap<>(Map.of(store, 10)), true, 50.0, "medium", "flower", "yellow",
        // true));

        return randomItems;
    }

    public static void createOrders(List<Store> stores, List<CatalogItem> items) {
        Timestamp nowTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        // Calendar cal = Calendar.getInstance();
        // cal.add(Calendar.DAY_OF_MONTH, 3);

        Random random = new Random();


        // createOrder(stores.get(random.nextInt(stores.size())),
        // (Customer)getUserByUsername("cust"+random.nextInt(1,15)),
        // items.subList(0, random.nextInt(1,
        // items.size())).stream().collect(Collectors.toMap(Function.identity(), item ->
        // random.nextInt(1,4))),
        // "Mazal Tov", nowTimestamp, new Timestamp(cal.getTime().getTime()), true,
        // new Delivery("Guy Dayan", "0509889939","Address Street 1", "Hello There",
        // false));

        for (int i = 0; i < 100; i++) {
            int item_index = random.nextInt(items.size());
            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, i);
            cal2.add(Calendar.DAY_OF_MONTH, i + 3);
            Timestamp orderTime = new Timestamp(cal.getTime().getTime());
            Timestamp deliveryTime = new Timestamp(cal2.getTime().getTime());
            createOrder(stores.get(random.nextInt(11)), (Customer) getUserByUsername("cust" + random.nextInt(1, 15)),
                    items.subList(0, item_index).stream()
                            .collect(Collectors.toMap(Function.identity(), item -> random.nextInt(1, 4))),
                    "Mazal Tov", orderTime, deliveryTime, true,
                    new Delivery("Guy Dayan", "0509889939", "Address Street 1", "Hello There", false));
        }
    }

    public static void createCustomers(List<Store> stores) throws Exception {
        List<Complaint> complaintList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String email = "customer" + i + "@gmail.com";
            registerCustomer("Customer " + i, email, "cust" + i, "pass" + i, stores, SubscriptionType.STORE,
                    complaintList);
        }

    }

    public static List<ChainEmployee> createEmployees() throws Exception{
        List<ChainEmployee> chainEmployees = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            chainEmployees.add(registerChainEmployee("Employee" + i, "Employee" + i + "@lilach.co.il", "Employee" + i, "Employee" + i));
        }
        return chainEmployees;

    }

    public static List<StoreManager> createStoreManagers() throws Exception {
        List<StoreManager> storeManagers = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            storeManagers.add(registerStoreManager("Manager" + i, "Manager" + i + "@lilach.co.il", "Manager" + i, "Manager" + i));
        }
        return storeManagers;
    }

    public static void initializeDatabaseIfEmpty() throws Exception {
        // Assume that we initialize only if the catalog is empty
        if (!getAllEntities(CatalogItem.class).isEmpty())
            return;

        List<StoreManager> managers = createStoreManagers();
        List<ChainEmployee> employees = createEmployees();

        List<Store> stores = initStores(managers, employees);
        saveStores(stores);
        List<Store> pickupStores = stores.stream().filter((store) -> !store.getName().equals(Constants.WAREHOUSE_NAME))
                .toList();
        List<CatalogItem> items = initItems(pickupStores);
        saveItems(items);

        createCustomers(pickupStores);
        createOrders(stores, items);
        System.out.println("Finished initializing");
    }

    private static <T> List<T> getAllEntities(Class<T> c) {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(c);
        query.from(c);
        List<T> data = session.createQuery(query).getResultList();
        session.close();
        return data;
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

    public static CatalogItem createItem(String name, double price, Map<Store, Integer> quantities, boolean onSale, double discountPercent, String size,
            String itemType, String itemColor, boolean isDefault, byte[] image) throws HibernateException {
        CatalogItem catalogItem = new CatalogItem(name, "", price, quantities, onSale, discountPercent, size, itemType,
                itemColor, isDefault);
        catalogItem.setImage(image);
        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(catalogItem);

        Path imagesPath = Paths.get(System.getProperty("user.home")).resolve("server-images");
        if (!Files.exists(imagesPath))
            try {
                Files.createDirectory(imagesPath);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                session.delete(catalogItem);
                tryFlushSession(session);
                return null;
            }

        try {
            catalogItem.setImagePath(
                    ImageUtils.saveImage(image, Paths.get(System.getProperty("user.home")).resolve("server-images"),
                            catalogItem.getId() + ".jpg").toUri().toString());
        } catch (IOException e) {
            // Couldn't create image
            e.printStackTrace();
            session.delete(catalogItem);
            tryFlushSession(session);
            return null;
        }

        tryFlushSession(session);

        return catalogItem;
    }

    public static List<Complaint> getComplaints() {
        List<Complaint> complaintList = getAllEntities(Complaint.class);
        return complaintList;
    }

    public static List<Order> getOrders() {
        List<Order> orderList = getAllEntities(Order.class);
        return orderList;
    }

    public static List<User> getUsers() {
        List<User> userList = getAllEntities(User.class);
        return userList;
    }

    public static void updateItem(CatalogItem newItem) throws HibernateException {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        updateDB(session, newItem);
        tryFlushSession(session);
    }

    public static void updateComplaint(Complaint newComplaint) throws HibernateException {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        updateDB(session, newComplaint);
        tryFlushSession(session);
    }

    public static void updateOrders(Order order) {
        Session session = DatabaseConnector.getSessionFactory().openSession();
        session.beginTransaction();
        Customer customer = order.getCustomer();
        customer.removeOrder(order);
        order.deleteCustomer();
        Store store = order.getStore();
        store.removeOrder(order);

        try {
            session.update(customer);
            session.update(order);
            session.update(store);
            session.delete(order);
        } catch (Exception e) {
            e.printStackTrace();
            session.merge(customer);
            session.merge(order);
            session.merge(store);
        }
        // updateDB(session, order);
        tryFlushSession(session);
    }

    private static <T> void updateDB(Session session, T toUpdate) {
        try {
            session.update(toUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            session.merge(toUpdate);
        }
    }

    public static void tryFlushSession(Session session) throws HibernateException {
        try {
            session.flush();
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            // TODO: report somewhere
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            throw new HibernateException(Constants.DATABASE_ERROR);
        }
    }

}
