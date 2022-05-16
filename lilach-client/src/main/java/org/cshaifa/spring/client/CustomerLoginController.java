package org.cshaifa.spring.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

//TODO: 1. create login btn from main screen
//2. connect to db
//3. validatelogin



public class CustomerLoginController {
    @FXML
    private Button cancelBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label loginMessageLabel;

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
            loginMessageLabel.setText("Please enter username and password");
            loginMessageLabel.setVisible(true);
        }
    }

    public void validateLogin() {

        Task<LoginResponse> loginTask = App.createTimedTask(() -> {
            return ClientHandler.loginUser(usernameTxtField.getText().strip(), pwdTxtField.getText().strip());
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        loginTask.setOnSucceeded(e -> {
            if (loginTask.getValue() == null) {
                System.err.println("Login Failed");
                invalid_login_text.setText("Login Failed");
                invalid_login_text.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            LoginResponse loginResponse = loginTask.getValue();
            if (!loginResponse.isSuccessful()) {
                System.err.println("Login Failed " + loginResponse.getMessage());
                invalid_login_text.setText(loginResponse.getMessage());
                invalid_login_text.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            System.out.println("Login Success!");
            invalid_login_text.setText(Constants.LOGIN_SUCCESS);
            invalid_login_text.setTextFill(Color.GREEN);
            App.hideLoading();
            App.setCurrentUser(loginResponse.getUser());
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

    void initialize() {
        loginMessageLabel.setText("");
    }
}
