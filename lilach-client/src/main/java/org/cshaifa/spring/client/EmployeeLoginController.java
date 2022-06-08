package org.cshaifa.spring.client;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.cshaifa.spring.entities.ChainEmployee;
import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.utils.Constants;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

//TODO: 1. create login btn from main screen
//2. connect to db
//3. validatelogin


public class EmployeeLoginController {
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
            if (App.getCurrentUser().getClass() == ChainEmployee.class) {
                try {
                    App.setWindowTitle("Catalog");
                    App.setContent("catalog");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            else {
                try {
                    App.setWindowTitle("Employee Profile");
                    App.setContent("employeeProfile");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
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
