package org.cshaifa.spring.client;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
    private Label notificationLabel;

    @FXML
    void initialize() {
        supplyMethodSelector.setItems(FXCollections.observableArrayList("Self Pickup - 0$", "Delivery - 30$"));
        supplyMethodSelector.getSelectionModel().selectFirst();
        deliveryDetailsVBox.setVisible(false);
        //function printing all hours in day intervals of 30 minutes from 'current'
        //TODO: need to start from 3 hours from now and put it in list
        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        System.out.println(df.format(current.getTime()));
        current.add(Calendar.HOUR_OF_DAY, 3);
        Calendar cal = Calendar.getInstance();
        System.out.println("BARRIER!!!");
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        int startDate = cal.get(Calendar.DATE);
        while (cal.get(Calendar.DATE) == startDate) {
            if(cal.after(current) || cal.equals(current))
                System.out.println(df.format(cal.getTime()));
            cal.add(Calendar.MINUTE, 30);
        }
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
        if(supplyMethodSelector.getSelectionModel().getSelectedIndex() == 1) {
            if(firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || deliveryAddressField.getText().isEmpty()
            || phoneNumberField.getText().isEmpty() || phoneNumberField.getText().isEmpty() || deliveryAddressField.getText().isEmpty()) {
                System.out.println("Do not leave any field empty");
                notificationLabel.setText("Do not leave any field except message empty");
                notificationLabel.setTextFill(Color.RED);
                return;
            }
            notificationLabel.setText("");
            App.setRecipientFirstName(firstNameField.getText().strip());
            App.setRecipientLastName(lastNameField.getText().strip());
            App.setRecipientAddress(deliveryAddressField.getText().strip());
            App.setMessage(messageField.getText().strip());
            App.setCustomerPhoneNumber(phoneNumberField.getText().strip());
            App.setSupplyDate(Timestamp.valueOf(deliveryDatePicker.getValue().atTime(15, 30)));
            /*
            System.out.println(App.getRecipientFirstName() + " " + App.getRecipientLastName());
            System.out.println(App.getRecipientAddress());
            System.out.println(App.getMessage());
            System.out.println(App.getCustomerPhoneNumber());
            System.out.println(App.getSupplyDate());
             */
        } else {
            App.setRecipientFirstName(null);
            App.setRecipientLastName(null);
            App.setRecipientAddress(null);
            App.setMessage(null);
            App.setCustomerPhoneNumber(null);
            App.setSupplyDate(null);
        }

        try {
            App.setContent("paymentDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
