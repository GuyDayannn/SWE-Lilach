package org.cshaifa.spring.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class PrimaryController {

    @FXML
    private Button openButton;

    @FXML
    private Button customerLoginButton;

    @FXML
    private Button employeeLoginButton;

    @FXML
    private ImageView lilachLogo;

    @FXML
    private Pane loadingtitle;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Text text;

    @FXML
    void open(ActionEvent event) throws InterruptedException, IOException {
        App.setWindowTitle("Catalog");
        App.setContent("catalog");
    }

    @FXML
    void openCustomerLogin(ActionEvent event) throws InterruptedException, IOException {
        App.setWindowTitle("Customer Login");
        App.setContent("customerLogin");
    }

    @FXML
    void openEmployeeLogin(ActionEvent event) throws InterruptedException, IOException {
        App.setWindowTitle("Employee Login");
        App.setContent("employeeLogin");
    }

    @FXML
    void openRegister(ActionEvent event) throws InterruptedException, IOException {
        App.setWindowTitle("Register");
        App.setContent("customerRegister");
    }


    @FXML
    void initialize() {
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        lilachLogo.setImage((image));
    }

}
