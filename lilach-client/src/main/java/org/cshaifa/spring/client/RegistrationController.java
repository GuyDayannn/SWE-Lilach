package org.cshaifa.spring.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.responses.RegisterResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

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
    private TextField pwdConfTxtField;

    @FXML
    private Label invalid_register_text;

    @FXML
    void cancelBtnOnAction(ActionEvent event) throws IOException {
        App.setWindowTitle("Primary");
        App.setContent("primary");
    }

    @FXML
    void onRegisterButton(ActionEvent event) {
        if (!fullNameTxtField.getText().isBlank() && !usernameTxtField.getText().isBlank()
                && !emailTxtField.getText().isBlank() && !pwdTxtField.getText().isBlank()) {
            if (!pwdTxtField.getText().equals(pwdConfTxtField.getText())) {
                invalid_register_text.setText("Passwords Does not match");
                invalid_register_text.setTextFill(Color.RED);
            } else {
                validateRegister();
            }
        } else {
            loginMessageLabel.setText("Please enter every detail");
        }
    }

    public void validateRegister() {

        Task<RegisterResponse> registerTask = App.createTimedTask(() -> {
            return ClientHandler.registerCustomer(fullNameTxtField.getText().strip(),
                    usernameTxtField.getText().strip(),
                    emailTxtField.getText().strip(), pwdTxtField.getText().strip());
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        registerTask.setOnSucceeded(e -> {
            if (registerTask.getValue() == null) {
                invalid_register_text.setText("Register Failed");
                invalid_register_text.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            RegisterResponse registerResponse = registerTask.getValue();
            if (!registerResponse.isSuccessful()) {
                invalid_register_text.setText(registerResponse.getMessage());
                invalid_register_text.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            App.setCurrentUser(registerResponse.getUser());
            App.hideLoading();
            App.setWindowTitle("Catalog");
            try {
                App.setContent("catalog");
            } catch (IOException e1) {
                // shouldn't happen
                e1.printStackTrace();
                App.setWindowTitle("Login");
            }
        });

        App.showLoading(rootPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(registerTask).start();
    }
}
