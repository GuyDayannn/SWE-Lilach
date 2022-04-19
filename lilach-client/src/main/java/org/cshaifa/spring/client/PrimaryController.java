package org.cshaifa.spring.client;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.application.Platform;

import java.io.IOException;

import javafx.scene.control.TextArea;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrimaryController {

    @FXML
    private Button exitButton;

    @FXML
    private Pane loadingtitle;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private Text text;

    @FXML
    void exit(MouseEvent event) throws InterruptedException {
        Platform.runLater(() -> {
            App.setWindowTitle("Catalog");
            try {
                App.setContent("catalog");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void initialize() {
        progressbar.setProgress(0.0);
    }

    @FXML
    void startLilach(MouseEvent event) {
        progressbar.progressProperty().bind(thread.progressProperty());
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
