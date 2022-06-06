package org.cshaifa.spring.client;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Store;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
    private CheckBox immediateCheckbox;

    @FXML
    private ComboBox<String> storeSelector;

    @FXML
    private Label notificationLabel;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView deliveryImage;

    private List<Store> stores = null;

    private void setImmediateCheckbox(boolean immediate) {
        immediateCheckbox.setSelected(immediate);
        deliveryTimeSelector.setDisable(immediate);
        if (immediate) {
            deliveryDatePicker.setValue(LocalDate.now());
            initializeTimes();
            deliveryTimeSelector.setValue(deliveryTimeSelector.getItems().get(0));
            deliveryDatePicker.setDisable(true);
            deliveryTimeSelector.setDisable(true);
        } else {
            deliveryDatePicker.setDisable(false);
            deliveryTimeSelector.setDisable(false);
        }
    }

    private void recoverSavedData() {
        if (!App.isEnteredSupplyDetails())
            return;

        if (App.isOrderDelivery()) {
            supplyMethodSelector.getSelectionModel().selectLast();
            deliveryDetailsVBox.setVisible(true);
            storeSelector.setVisible(false);
        } else {
            supplyMethodSelector.getSelectionModel().selectFirst();
            deliveryDetailsVBox.setVisible(false);
            storeSelector.setVisible(true);
            return;
        }

        firstNameField.setText(App.getRecipientFirstName());
        lastNameField.setText(App.getRecipientLastName());
        deliveryAddressField.setText(App.getRecipientAddress());
        messageField.setText(App.getMessage());
        phoneNumberField.setText(App.getCustomerPhoneNumber());
        deliveryDatePicker.setValue(App.getSupplyDate().toLocalDateTime().toLocalDate());
        deliveryTimeSelector.setValue(new SimpleDateFormat("HH:mm").format(App.getSupplyDate()));
        deliveryTimeSelector.setDisable(false);
        setImmediateCheckbox(App.isImmediate());
    }

    @FXML
    void initialize() {
        Image cart = new Image(getClass().getResource("images/delivery.png").toString());
        deliveryImage.setImage(cart);
        deliveryImage.setFitWidth(40);
        deliveryImage.setFitHeight(40);
        supplyMethodSelector.setItems(FXCollections.observableArrayList("Self Pickup - 0$", "Delivery - 30$"));
        supplyMethodSelector.getSelectionModel().selectFirst();
        deliveryDetailsVBox.setVisible(false);
        // function printing all hours in day intervals of 30 minutes from 'current'
        // TODO: need to start from 3 hours from now and put it in list

        stores = ((Customer) App.getCurrentUser()).getStores();
        storeSelector.getItems().addAll(stores.stream().map(Store::getName).toList());

        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,10}"))
                phoneNumberField.setText(oldValue);
        });

        recoverSavedData();
    }

    @FXML
    private void selectSupplyMethod(ActionEvent event) {
        deliveryDetailsVBox.setVisible(supplyMethodSelector.getSelectionModel().getSelectedIndex() == 1);
        storeSelector.setVisible(supplyMethodSelector.getSelectionModel().getSelectedIndex() == 0);
    }

    private void initializeTimes() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        List<String> times = new ArrayList<>();
        Calendar current = Calendar.getInstance();

        LocalDate pickedDate = deliveryDatePicker.getValue();

        boolean sameDate = pickedDate.getDayOfYear() == current.get(Calendar.DAY_OF_YEAR)
                && pickedDate.getYear() == current.get(Calendar.YEAR);

        if (sameDate)
            current.add(Calendar.HOUR_OF_DAY, 3);

        Calendar cal = Calendar.getInstance();
        cal.set(pickedDate.getYear(), pickedDate.getMonthValue() - 1, pickedDate.getDayOfMonth());

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        if (sameDate && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 20) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            pickedDate = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId()).toLocalDate();
            deliveryDatePicker.setValue(pickedDate);
        }

        int startDate = cal.get(Calendar.DATE);

        while (cal.get(Calendar.DATE) == startDate) {
            if (cal.after(current) || cal.equals(current) || !sameDate) {
                times.add(df.format(cal.getTime()));
            }
            cal.add(Calendar.MINUTE, 30);
        }

        deliveryTimeSelector.setItems(FXCollections.observableArrayList(times));
        deliveryTimeSelector.getSelectionModel().selectFirst();
    }

    @FXML
    private void selectDate(ActionEvent event) {
        deliveryTimeSelector.setDisable(false);
        initializeTimes();
    }

    @FXML
    private void selectStore(ActionEvent event) {

    }

    @FXML
    private void changeImmediate(ActionEvent event) {
        deliveryTimeSelector.setDisable(immediateCheckbox.isSelected());
        if (immediateCheckbox.isSelected()) {
            deliveryDatePicker.setValue(LocalDate.now());
            initializeTimes();
            deliveryTimeSelector.setValue(deliveryTimeSelector.getItems().get(0));
            deliveryDatePicker.setDisable(true);
            deliveryTimeSelector.setDisable(true);
        } else {
            deliveryDatePicker.setDisable(false);
            deliveryTimeSelector.setDisable(false);
        }
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
        boolean delivery = supplyMethodSelector.getSelectionModel().getSelectedIndex() == 1;
        App.setOrderDelivery(delivery);
        if (delivery) {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()
                    || deliveryAddressField.getText().isEmpty() || !phoneNumberField.getText().matches("\\d{9,10}")
                    || phoneNumberField.getText().isEmpty() || deliveryAddressField.getText().isEmpty()
                    || deliveryDatePicker.getValue() == null) {
                System.out.println("Not all fields are valid");
                notificationLabel.setText("Not all fields are valid");
                notificationLabel.setTextFill(Color.RED);
                return;
            }
            notificationLabel.setText("");
            App.setRecipientFirstName(firstNameField.getText().strip());
            App.setRecipientLastName(lastNameField.getText().strip());
            App.setRecipientAddress(deliveryAddressField.getText().strip());
            App.setMessage(messageField.getText().strip());
            App.setCustomerPhoneNumber(phoneNumberField.getText().strip());
            // TODO: add times of delivery
            String chosenTime = immediateCheckbox.isSelected() ? deliveryTimeSelector.getItems().get(0)
                    : deliveryTimeSelector.getValue();
            App.setImmediate(immediateCheckbox.isSelected());
            App.setSupplyDate(Timestamp.valueOf(deliveryDatePicker.getValue()
                    .atTime(LocalTime.parse(chosenTime, DateTimeFormatter.ofPattern("HH:mm")))));
        } else {
            App.setPickupStore(stores.get(storeSelector.getSelectionModel().getSelectedIndex()));
        }

        App.setEnteredSupplyDetails(true);

        try {
            App.setContent("paymentDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
