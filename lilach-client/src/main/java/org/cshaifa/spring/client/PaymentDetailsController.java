package org.cshaifa.spring.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
}
