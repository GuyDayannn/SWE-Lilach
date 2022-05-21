package org.cshaifa.spring.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
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
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.utils.Constants;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage appStage;

    private static CatalogItem currentItemDisplayed;
    private static CatalogItem itemByID;
    private static int DataBaseConnected = 0;
    private static Stage loadingStage;
    private static Node loadingRootNode;
    private static ScheduledFuture<Void> scheduledCancelButtonShow;
    private static ScheduledExecutorService cancelButtonExecutorService;

    private static Text currentItemPrice;
    private static Text currentItemName;

    private static User currentUser = null;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        scene = new Scene(loadFXML("primary"), 1040, 700);
        stage.setTitle("Welcome");
        stage.setScene(scene);
        appStage = stage;
        // TODO: logout without any loading for now, maybe change, maybe don't
        appStage.setOnCloseRequest(e -> logoutUser());
        appStage.show();
    }

    public static void logoutUser() {
        final User toLogout = currentUser;
        new Thread(
                createTimedTask(() -> ClientHandler.logoutUser(toLogout), Constants.REQUEST_TIMEOUT,
                        TimeUnit.SECONDS))
                .start();
        currentUser = null;
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
        Parent root = loadFXML(pageName);
        scene = new Scene(root);
        if (pageName == "catalog") {
            System.out.println("catalog");
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

    public static void updateCurrentItemDisplayed(CatalogItem updatedItem) {
        currentItemPrice.setText(Double.toString(updatedItem.getPrice()));
        currentItemName.setText(updatedItem.getName());
    }

    static CatalogItem getCurrentItemDisplayed() {
        return currentItemDisplayed;
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
}
