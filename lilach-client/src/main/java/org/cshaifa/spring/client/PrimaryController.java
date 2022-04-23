package org.cshaifa.spring.client;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ConnectException;

import org.cshaifa.spring.entities.responses.*;

public class PrimaryController {

    @FXML
    private Button openButton;

    @FXML
    private ImageView lilachLogo;

    @FXML
    private Pane loadingtitle;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private Text text;

    @FXML
    void open(MouseEvent event) throws InterruptedException {
        if (checkConnection()) {
            Platform.runLater(() -> {
                App.setWindowTitle("Catalog");
                try {
                    App.setContent("catalog");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    void initialize() {
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        lilachLogo.setImage((image));
        progressbar.progressProperty().bind(thread.progressProperty());
    }

    @FXML
    void startLilach(MouseEvent event) {

    }

    @FXML
    boolean checkConnection() {
        progressbar.progressProperty().unbind();
        try {
            GetCatalogResponse response = ClientHandler.getCatalog();
            if (response.isSuccessful()) {
                progressbar.progressProperty().setValue(100.00);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    final Service thread = new Service<Integer>() {
        @Override
        public Task createTask() {
            return new Task<Integer>() {
                @Override
                public Integer call() throws InterruptedException {
                    int i;
                    for (i = 0; i < 1000; i++) {
                        updateProgress(i, 1000);
                        Thread.sleep(10);
                    }
                    return i;
                }
            };
        }
    };

}
