package org.cshaifa.spring.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.cshaifa.spring.entities.CatalogItem;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.Report;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.utils.Constants;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage appStage;

    private static CatalogItem currentItemDisplayed;
    private static CatalogItem createdItem = null;
    private static CatalogItem itemByID;
    private static Stage loadingStage;
    private static Node loadingRootNode;
    private static ScheduledFuture<Void> scheduledCancelButtonShow;
    private static ScheduledExecutorService cancelButtonExecutorService;
    public static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static Text currentItemPrice;
    private static Text currentItemName;

    private static User currentUser = null;

    private static Map<CatalogItem, Integer> shoppingCart = new HashMap<>();

    private static double totalOrderPrice = -1;

    private static String greeting = null;

    private static String recipientFirstName = null;

    private static String recipientLastName = null;

    private static String recipientAddress = null;

    private static String message = null;

    private static String customerPhoneNumber = null;

    private static Timestamp supplyDate = null;

    private static boolean immediate = false;

    private static Store pickupStore = null;

    private static boolean orderDelivery;

    private static String cardNumber;

    private static String cardCvv;

    private static String cardExpDate;

    private static boolean enteredSupplyDetails = false;

    private static Report report;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        scene = new Scene(loadFXML("primary"), 800, 600);
        stage.setTitle("Welcome");
        stage.setScene(scene);
        appStage = stage;
        // TODO: logout without any loading for now, maybe change, maybe don't
        appStage.setOnCloseRequest(e -> {
            logoutUser();
            scheduler.shutdown();
        });
        appStage.show();
    }

    public static void logoutUser() {
        final User toLogout = currentUser;
        new Thread(
                createTimedTask(() -> ClientHandler.logoutUser(toLogout), Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS))
                        .start();
        currentUser = null;
        shoppingCart.clear();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    static void setWindowTitle(String title) {
        appStage.setTitle(title);
    }

    static void setContent(String pageName) throws IOException {
        scheduler.shutdown();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        Parent root = loadFXML(pageName);
        scene = new Scene(root);
        if (pageName == "catalog") {
            URL styleSheet = App.class.getResource("stylesheets/" + pageName + ".css");
            if (styleSheet != null) {
                scene.getStylesheets().add(styleSheet.toExternalForm());
            }
        }
        appStage.setScene(scene);
        appStage.show();
    }

    public static WritableImage getImageFromByteArray(byte[] image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
        WritableImage wr = null;
        wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                pw.setArgb(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return wr;
    }

    public static <T> Task<T> createTimedTask(Callable<T> callable, long timeout, TimeUnit unit) {
        return new Task<T>() {
            @Override
            public T call() throws Exception {

                CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return callable.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                return future.get(timeout, unit);
            }
        };
    }

    public static void hideLoading() {
        if (loadingStage != null && loadingRootNode != null) {
            loadingRootNode.setEffect(null);
            loadingStage.hide();
            loadingStage = null;
            scheduledCancelButtonShow.cancel(true);
            cancelButtonExecutorService.shutdown();
        }
    }

    public static void showLoading(Node rootNode, Stage rootStage, long cancelButtonDelay, TimeUnit unit) {
        loadingRootNode = rootNode;
        loadingRootNode.setEffect(new GaussianBlur());

        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setStyle("-fx-progress-color: green");
        Button cancelButton = new Button("Cancel");
        cancelButton.setVisible(false);
        VBox vBox = new VBox(indicator, cancelButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: transparent");

        loadingStage = new Stage();
        loadingStage.initStyle(StageStyle.TRANSPARENT);
        loadingStage.initOwner(appStage);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setScene(new Scene(vBox, Color.TRANSPARENT));
        cancelButton.setOnAction(e -> {
            hideLoading();
        });

        loadingStage.show();
        if (rootStage == null)
            rootStage = appStage;
        loadingStage.setX(rootStage.getX() + (rootStage.getWidth() - loadingStage.getWidth()) / 2);
        loadingStage.setY(rootStage.getY() + (rootStage.getHeight() - loadingStage.getHeight()) / 2);
        cancelButtonExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledCancelButtonShow = cancelButtonExecutorService.schedule(() -> {
            cancelButton.setVisible(true);
            return null;
        }, cancelButtonDelay, unit);
    }

    static void popUpLaunch(Button caller, String FXMLname) {
        Stage popUpStage = new Stage();
        Parent root;

        try {
            root = loadFXML(FXMLname);
            popUpStage.setScene(new Scene(root));
            popUpStage.initModality(Modality.APPLICATION_MODAL); // popup
            if (caller != null) {
                popUpStage.initOwner(caller.getScene().getWindow());
            }
            popUpStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setCurrentItemDisplayed(CatalogItem item, Text itemPrice, Text itemName) {
        currentItemDisplayed = item;
        currentItemPrice = itemPrice;
        currentItemName = itemName;
    }

    static void setCurrentReportDisplayed(Report report1) {
        report = report1;
    }

    public static void updateCurrentItemDisplayed(CatalogItem updatedItem) {
        if (updatedItem.getDiscount() != 0) {
            double price = updatedItem.getPrice() * (1 - updatedItem.getDiscount() / 100);
            currentItemPrice.setText(String.format("%.2f", price));
        } else {
            currentItemPrice.setText(Double.toString(updatedItem.getPrice()));
        }

        currentItemName.setText(updatedItem.getName());
    }

    static CatalogItem getCurrentItemDisplayed() {
        return currentItemDisplayed;
    }

    static Report getCurrentReportDisplayed() {
        return report;
    }

    static CatalogItem getItemByID(long itemID) {
        return itemByID;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static Map<CatalogItem, Integer> getCart() {
        return shoppingCart;
    }

    public static void addToCart(CatalogItem item, int quantity) {
        shoppingCart.put(item, quantity);
    }

    public static String getRecipientFirstName() {
        return recipientFirstName;
    }

    public static void setRecipientFirstName(String firstName) {
        App.recipientFirstName = firstName;
    }

    public static String getRecipientLastName() {
        return recipientLastName;
    }

    public static void setRecipientLastName(String lastName) {
        App.recipientLastName = lastName;
    }

    public static String getRecipientAddress() {
        return recipientAddress;
    }

    public static void setRecipientAddress(String address) {
        App.recipientAddress = address;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        App.message = message;
    }

    public static String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public static void setCustomerPhoneNumber(String phoneNumber) {
        App.customerPhoneNumber = phoneNumber;
    }

    public static Timestamp getSupplyDate() {
        return supplyDate;
    }

    public static void setSupplyDate(Timestamp supplyDate) {
        App.supplyDate = supplyDate;
    }

    public static boolean isOrderDelivery() {
        return orderDelivery;
    }

    public static void setOrderDelivery(boolean orderDelivery) {
        App.orderDelivery = orderDelivery;
    }

    public static String getCardNumber() {
        return cardNumber;
    }

    public static void setCardNumber(String cardNumber) {
        App.cardNumber = cardNumber;
    }

    public static String getCardCvv() {
        return cardCvv;
    }

    public static void setCardCvv(String cardCvv) {
        App.cardCvv = cardCvv;
    }

    public static String getCardExpDate() {
        return cardExpDate;
    }

    public static void setCardExpDate(String cardExpDate) {
        App.cardExpDate = cardExpDate;
    }

    public static boolean isEnteredSupplyDetails() {
        return enteredSupplyDetails;
    }

    public static void setEnteredSupplyDetails(boolean enteredSupplyDetails) {
        App.enteredSupplyDetails = enteredSupplyDetails;
    }

    public static Store getPickupStore() {
        return pickupStore;
    }

    public static void setPickupStore(Store pickupStore) {
        App.pickupStore = pickupStore;
    }

    public static boolean isImmediate() { return immediate; }

    public static void setImmediate(boolean immediate) { App.immediate = immediate; }
    public static CatalogItem getCreatedItem() {
        return createdItem;
    }

    public static void setCreatedItem(CatalogItem createdItem) {
        App.createdItem = createdItem;
    }

    public static String getGreeting() { return greeting; }

    public static void setGreeting(String greeting) { App.greeting = greeting; }

    public static double getTotalOrderPrice() { return totalOrderPrice; }

    public static void setTotalOrderPrice(double totalOrderPrice) { App.totalOrderPrice = totalOrderPrice; }
}
