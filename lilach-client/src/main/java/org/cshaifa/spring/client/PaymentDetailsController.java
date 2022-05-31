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
            if (!newValue.isEmpty() && !newValue.matches("0[1-9]|1[0-2]") && !newValue.matches("[0-1]"))
                expMonthField.setText(oldValue);
        });

        expYearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("[2-5][0-9]") && !newValue.matches("[2-5]"))
                expYearField.setText(oldValue);
        });

        cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                cardNumberField.setText(oldValue);
        });

        cvvField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,3}"))
                cvvField.setText(oldValue);
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
        if (!cardNumberField.getText().matches("\\d{4,}") || !expYearField.getText().matches("[2-5][0-9]")
                || !expMonthField.getText().matches("0[1-9]|1[0-2]") || !cvvField.getText().matches("\\d{3}")) {
            System.out.println("Not all fields are valid");
            // TODO: flash error msg
            return;
        }

        App.setCardNumber(cardNumberField.getText());
        App.setCardExpDate(expMonthField.getText() + "/" + expYearField.getText());
        App.setCardCvv(cvvField.getText());

        try {
            App.setContent("orderConfirmation");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
