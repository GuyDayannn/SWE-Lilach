package org.cshaifa.spring.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

//TODO: 1. create login btn from matin screen
//2. connect to db
//3. validatelogin



public class CustomerLoginController {
    @FXML
    private Button cancelBtn;
    @FXML
    private Button loginBtn;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField usernameTxtField;

    @FXML
    private TextField pwdTxtField;

    public void initialize() {

        cancelBtn.setOnAction(event -> {
            //Stage stage  =(Stage) cancelBtn.getScene().getWindow();
            //stage.close();
            cancelBtn.getScene().getWindow().hide();
        });

        loginBtn.setOnAction(event -> {
            validateLogin();
            if (usernameTxtField.getText().isBlank() == false && pwdTxtField.getText().isBlank() == false) {

            } else {
                loginMessageLabel.setText("Please enter username and password");
            }
        });


    }

    public void validateLogin() {

        try {

        } catch (Exception e) {
            e.getCause();
            e.printStackTrace();
        }
    }
}