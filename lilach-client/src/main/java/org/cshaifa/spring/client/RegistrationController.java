package org.cshaifa.spring.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.entities.responses.RegisterResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

//TODO: 1. create login btn from matin screen
//2. connect to db
//3. validatelogin



public class RegistrationController {
    @FXML
    private Button cancelBtn;
    @FXML
    private Button registerBtn;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField fullNameTxtField;

    @FXML
    private TextField usernameTxtField;

    @FXML
    private TextField emailTxtField;

    @FXML
    private TextField pwdTxtField;

    @FXML
    void cancelBtnOnAction(ActionEvent event) throws IOException {
        App.setWindowTitle("Catalog");
        App.setContent("catalog");
    }

    @FXML
    void onRegisterButton(ActionEvent event) {
        if (!fullNameTxtField.getText().isBlank() && !usernameTxtField.getText().isBlank() && !emailTxtField.getText().isBlank() && !pwdTxtField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter every detail");
        }
    }

    public void validateLogin() {

        Task<RegisterResponse> registerTask = App.createTimedTask(() -> {
            return ClientHandler.registerCustomer(fullNameTxtField.getText().strip(), usernameTxtField.getText().strip(),
                    emailTxtField.getText().strip(), pwdTxtField.getText().strip());
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        registerTask.setOnSucceeded(e -> {
            if (registerTask.getValue() == null) {
                System.err.println("Register Failed");
                App.hideLoading();
                return;
            }

            RegisterResponse registerResponse = registerTask.getValue();
            if (!registerResponse.isSuccessful()) {
                System.err.println("Register Failed");
                App.hideLoading();
                return;
            }

            System.out.println("Register Success!");
            App.hideLoading();
        });

        App.showLoading(rootPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(registerTask).start();
    }
}
