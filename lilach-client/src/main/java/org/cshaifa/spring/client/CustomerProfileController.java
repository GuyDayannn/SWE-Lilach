package org.cshaifa.spring.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class CustomerProfileController {

    @FXML
    private Button closeComplaintButton;

    @FXML
    private TextField compensationAmount;

    @FXML
    private TextArea complaintDescription;

    @FXML
    private ListView<?> complaintList;

    @FXML
    private TextField complaintOrderID;

    @FXML
    private TextField complaintResponse;

    @FXML
    private TextField complaintStatus;

    @FXML
    private Button newComplaintButton;

    @FXML
    private ListView<?> orderList;

    @FXML
    private Button signOutButton;

    @FXML
    private Button viewCatalogButton;

    @FXML
    private Text welcomeText;

    @FXML
    void closeComplaint(ActionEvent event) {

    }

    @FXML
    void logOut(ActionEvent event) throws IOException {
        App.setCurrentUser(null);
        App.setWindowTitle("primary");
        App.setContent("primary");
    }

    @FXML
    void openCatalog(ActionEvent event) throws IOException {
        App.setWindowTitle("catalog");
        App.setContent("catalog");
    }

    @FXML
    void sendComplaint(ActionEvent event) {

    }

    @FXML
    public void initialize() {
        if (App.getCurrentUser()!=null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        }
        else {
            welcomeText.setText("Welcome, unknown customer");
        }
    }

}
