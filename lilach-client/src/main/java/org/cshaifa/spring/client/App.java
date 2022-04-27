package org.cshaifa.spring.client;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage appStage;

    private static CatalogItem currentItemDisplayed;
    private static int DataBaseConnected = 0;
    private static Stage loadingStage;
    private static Node loadingRootNode;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        scene = new Scene(loadFXML("primary"), 1040, 700);
        stage.setTitle("Welcome");
        stage.setScene(scene);
        appStage = stage;
        appStage.show();
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
        appStage.setScene(scene);
        appStage.show();
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
        }
    }

    public static void showLoading(Node rootNode, long cancelButtonDelay, TimeUnit unit) {
        loadingRootNode = rootNode;
        loadingRootNode.setEffect(new GaussianBlur());

        ProgressIndicator indicator = new ProgressIndicator();
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
        loadingStage.setX(appStage.getX() + (appStage.getWidth() - loadingStage.getWidth()) / 2);
        loadingStage.setY(appStage.getY() + (appStage.getHeight() - loadingStage.getHeight()) / 2);
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            cancelButton.setVisible(true);
        }, cancelButtonDelay, unit);

    }

    static void popUpLaunch(Button caller, String FXMLname) {
        Stage popUpStage = new Stage();
        Parent root;

        try {
            root = loadFXML(FXMLname);
            popUpStage.setScene(new Scene(root));
            popUpStage.initModality(Modality.APPLICATION_MODAL);    // popup
            popUpStage.initOwner(caller.getScene().getWindow());
            popUpStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setCurrentItemDisplayed(CatalogItem item) {
        currentItemDisplayed = item;
    }

    static CatalogItem getCurrentItemDisplayed() {
        return currentItemDisplayed;
    }

}
