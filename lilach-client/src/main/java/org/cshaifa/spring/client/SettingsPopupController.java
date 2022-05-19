package org.cshaifa.spring.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.responses.IsAliveResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Window;

public class SettingsPopupController {

    @FXML
    private TextField hostnameInput;

    @FXML
    private TextField portInput;

    @FXML
    private Label errorText;

    @FXML
    private AnchorPane popupPane;

    @FXML
    void initialize() {
        hostnameInput.setText(ClientHandler.getServerHostname());
        portInput.setText(Integer.toString(ClientHandler.getServerPort()));

        portInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*?")) {
                    portInput.setText(oldValue);
                }
            }
        });

    }

    /**
     * This method can be used to check if the server is alive,
     * and if it isn't, it reverts to the default settings
     */
    @SuppressWarnings("unused")
    private void changeServer(String hostname, int port, Window window, boolean revert) {
        Task<IsAliveResponse> changeServerTask = App
                .createTimedTask(
                        () -> ClientHandler.changeServerDetailsAndCheckAlive(hostname,
                                port),
                        Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);
        changeServerTask.setOnSucceeded(e -> {
            App.hideLoading();

            if (!revert) {
                window.hide();
                return;
            } else {
                errorText.setText("Couldn't connect to server");
                errorText.setTextFill(Color.RED);
            }
        });

        changeServerTask.setOnFailed(e -> {
            changeServerTask.getException().printStackTrace();
            if (revert) {
                errorText.setText("Couldn't connect to server");
                errorText.setTextFill(Color.RED);
                App.hideLoading();
            } else {
                changeServer("localhost", Constants.SERVER_PORT, window, true);
            }
        });

        if (!revert)
            App.showLoading(popupPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);

        new Thread(changeServerTask).start();
    }

    @FXML
    void onSaveSettingsClick(ActionEvent event) {
        if (!hostnameInput.getText().isBlank() && !portInput.getText().isBlank()) {
            try {
                int portNum = Integer.parseInt(portInput.getText());
                if (portNum < 1 || portNum > 65535)
                    throw new NumberFormatException();

                InetAddress.getByName(hostnameInput.getText());
                ClientHandler.changeServerDetails(hostnameInput.getText(), portNum);
                ((Button)event.getSource()).getScene().getWindow().hide();
            } catch (NumberFormatException e) {
                errorText.setText("Invalid port number.");
                errorText.setTextFill(Color.RED);
            } catch (UnknownHostException e) {
                errorText.setText("Invalid hostname.");
                errorText.setTextFill(Color.RED);
            }
        }
    }
}
