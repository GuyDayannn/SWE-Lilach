package org.cshaifa.spring.client;
import org.cshaifa.spring.entities.*;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

import org.cshaifa.spring.entities.CatalogItem;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage appStage;

    private static CatalogItem currentItemDisplayed;
    private static int DataBaseConnected = 0;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        scene = new Scene(loadFXML("primary"), 1280, 720);
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
        Parent root= loadFXML(pageName);
        scene = new Scene(root);
        appStage.setScene(scene);
        appStage.show();
    }

    static void popUpLaunch(Button caller){
        Stage popUpStage = new Stage();
        Parent root;

        try {
            root = loadFXML("PopUp");
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
