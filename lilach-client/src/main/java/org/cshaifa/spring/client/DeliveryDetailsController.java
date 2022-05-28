package org.cshaifa.spring.client;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class DeliveryDetailsController {
    @FXML
    private ComboBox<String> supplyMethodSelector;

    @FXML
    private VBox deliveryDetailsVBox;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField deliveryAddressField;

    @FXML
    private TextField messageField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private DatePicker deliveryDatePicker;

    @FXML
    private ComboBox<String> deliveryTimeSelector;

    @FXML
    private Button backButton;

    @FXML
    private Button proceedButton;

    @FXML
    void initialize() {
        supplyMethodSelector.setItems(FXCollections.observableArrayList("Self Pickup - 0$", "Delivery - 30$"));
        supplyMethodSelector.getSelectionModel().selectFirst();
        deliveryDetailsVBox.setVisible(false);
    }

    @FXML
    private void selectSupplyMethod(ActionEvent event) {
        deliveryDetailsVBox.setVisible(supplyMethodSelector.getSelectionModel().getSelectedIndex() == 1);
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            App.setContent("orderSummary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void proceed(ActionEvent event) {
        // TODO: Set entered variables somewhere
        try {
            App.setContent("paymentDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
