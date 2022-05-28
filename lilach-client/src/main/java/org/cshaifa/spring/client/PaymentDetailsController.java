package org.cshaifa.spring.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PaymentDetailsController {

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField cvvField;

    @FXML
    private TextField expMonthField;

    @FXML
    private TextField expYearField;

    @FXML
    void initialize() {
        expMonthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^(0[1-9]|1[0-2])$") && !newValue.matches("^[0-1]$"))
                expMonthField.setText(oldValue);
        });

        expYearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^([2-5][0-9])$") && !newValue.matches("^[2-5]$"))
                expYearField.setText(oldValue);
        });
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            App.setContent("deliveryDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void proceed(ActionEvent event) {
        // TODO: save details and proceed to final order summary screen
    }
}
