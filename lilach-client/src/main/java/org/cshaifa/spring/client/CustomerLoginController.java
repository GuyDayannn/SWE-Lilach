package org.cshaifa.spring.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class CustomerLoginController {
    @FXML
    private ImageView lilachLogo;

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField usernameTxtField;

    @FXML
    private TextField pwdTxtField;

    @FXML
    private Label invalid_login_text;

    @FXML
    void cancelBtnOnAction(ActionEvent event) throws IOException {
        App.setWindowTitle("Primary");
        App.setContent("primary");
    }

    @FXML
    void onLoginButton(ActionEvent event) {
        if (!usernameTxtField.getText().isBlank() && !pwdTxtField.getText().isBlank()) {
            validateLogin();
        } else {
            invalid_login_text.setText("Please enter username and password");
            invalid_login_text.setVisible(true);
        }
    }

    public void validateLogin() {

        Task<LoginResponse> loginTask = App.createTimedTask(() -> {
            return ClientHandler.loginUser(usernameTxtField.getText().strip(), pwdTxtField.getText().strip());
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        loginTask.setOnSucceeded(e -> {
            if (loginTask.getValue() == null) {
                invalid_login_text.setText("Login Failed");
                invalid_login_text.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            LoginResponse loginResponse = loginTask.getValue();
            if (!loginResponse.isSuccessful()) {
                invalid_login_text.setText(loginResponse.getMessage());
                invalid_login_text.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            App.setCurrentUser(loginResponse.getUser());
            App.hideLoading();
            try {
                App.setWindowTitle("Customer Profile");
                App.setContent("customerProfile");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        App.showLoading(rootPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(loginTask).start();
    }

    @FXML
    void initialize() {
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        lilachLogo.setImage(image);
        lilachLogo.setFitHeight(50);
        invalid_login_text.setText("");
    }
}
