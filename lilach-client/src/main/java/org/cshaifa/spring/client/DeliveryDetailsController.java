package org.cshaifa.spring.client;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.responses.GetStoresResponse;
import org.cshaifa.spring.utils.Constants;

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

        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 3);
        int startDate = calendar.get(Calendar.DATE);
        List<String> times = new ArrayList<>();
        while (calendar.get(Calendar.DATE) == startDate) {
            times.add(df.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }

        deliveryTimeSelector.setItems(FXCollections.observableArrayList(times));
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

    @FXML
    private void selectStore(ActionEvent event) {

    }

    @FXML
    private void changeImmediate(ActionEvent event) {
        deliveryTimeSelector.setDisable(immediateCheckbox.isSelected());
        if(immediateCheckbox.isSelected()) {
            deliveryTimeSelector.setValue(deliveryTimeSelector.getItems().get(0));
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
            String chosenTime = immediateCheckbox.isSelected() ? deliveryTimeSelector.getItems().get(0) : deliveryTimeSelector.getValue();
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
