package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Delivery;
import org.cshaifa.spring.entities.responses.CreateOrderResponse;
import org.cshaifa.spring.entities.responses.NotifyDeleteResponse;
import org.cshaifa.spring.entities.responses.NotifyResponse;
import org.cshaifa.spring.entities.responses.NotifyUpdateResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OrderConfirmationController {

    @FXML
    private Label addressLabel;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label cardNumberLabel;

    @FXML
    private Label deliveryTimeLabel;

    @FXML
    private Label expDateLabel;

    @FXML
    private Label firstNameDeliveryLabel;

    @FXML
    private Label firstNamePaymentLabel;

    @FXML
    private VBox itemsVbox;

    @FXML
    private Label lastNameDeliveryLabel;

    @FXML
    private Label lastNamePaymentLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private Label storeNameLabel;

    @FXML
    private Label storeAddressLabel;

    @FXML
    private VBox summaryVbox;

    @FXML
    private AnchorPane deliveryAnchorPane;

    @FXML
    private AnchorPane pickupAnchorPane;

    @FXML
    private Label totalPrice;

    private HBox getItemHBox(CatalogItem item, int quantity) {
        HBox hBox = new HBox();
        ImageView iv = null;

        if (item.getImage() != null) {
            try {
                iv = new ImageView(App.getImageFromByteArray(item.getImage()));
                iv.setFitWidth(50);
                iv.setFitHeight(50);
            } catch (IOException e1) {
                // TODO: maybe log the exception somewhere
                e1.printStackTrace();
            }
        }

        Label itemName = new Label(item.getName() + "\t\t");
        double price = item.getPrice();
        if (item.isOnSale()) {
            price = new BigDecimal((price * 0.01 * (100 - item.getDiscount()))).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        double finalPrice = price * quantity;
        Label itemPrice = new Label(String.format("%.2f", price) + "\t");
        Label itemFinalPrice = new Label(String.format("%.2f", finalPrice) + "\t");
        Label itemQuantity = new Label(Integer.toString(quantity));
        HBox.setMargin(itemName, new Insets(0, 0, 0, 0));
        HBox.setMargin(itemPrice, new Insets(0, 60, 0, 60));
        HBox.setMargin(itemQuantity, new Insets(0, 0, 0, 0));
        HBox.setMargin(itemFinalPrice, new Insets(0, 0, 0, 0));
        hBox.getChildren().addAll(iv, itemName, itemPrice, itemQuantity, itemFinalPrice);
        hBox.setSpacing(5);
        hBox.getStyleClass().add("item");
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private void showCartDetails() {
        App.getCart().forEach((item, quantity) -> itemsVbox.getChildren().add(getItemHBox(item, quantity)));
        totalPrice.setText(
                new BigDecimal(App.getCartTotal() + (App.isOrderDelivery() ? Constants.DELIVERY_PRICE : 0))
                        .setScale(2, RoundingMode.HALF_UP).toString());
    }

    @FXML
    void initialize() {
        showCartDetails();
        addressLabel.setText(App.getRecipientAddress());
        cardNumberLabel
                .setText(App.getCardNumber().substring(App.getCardNumber().length() - 4, App.getCardNumber().length()));
        expDateLabel.setText(App.getCardExpDate());
        String[] splitName = App.getCurrentUser().getFullName().split(" ", 2);
        firstNamePaymentLabel.setText(splitName[0]);
        if (splitName.length > 1) {
            lastNamePaymentLabel.setVisible(true);
            lastNamePaymentLabel.setText(splitName[1]);
        } else
            lastNamePaymentLabel.setVisible(false);

        if (!App.isOrderDelivery()) {
            storeNameLabel.setText(App.getPickupStore().getName());
            storeAddressLabel.setText(App.getPickupStore().getAddress());
            return;
        } else {
            pickupAnchorPane.setVisible(false);
            deliveryAnchorPane.setVisible(true);
        }

        firstNameDeliveryLabel.setText(App.getRecipientFirstName());
        lastNameDeliveryLabel.setText(App.getRecipientLastName());
        messageLabel.setText(App.getMessage());
        phoneNumberLabel.setText(App.getCustomerPhoneNumber());
        deliveryTimeLabel.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(App.getSupplyDate()));

        App.scheduler.scheduleAtFixedRate(() -> {
            try {
                final NotifyResponse notifyResponse = ClientHandler.waitForUpdateFromServer();
                if (notifyResponse == null)
                    return;

                if (App.updateCart(notifyResponse)) {
                    Platform.runLater(() -> {
                        itemsVbox.getChildren().clear();
                        showCartDetails();
                    });
                }

            } catch (Exception error) {
                error.printStackTrace();
                return;
            }
        }, 0, Constants.UPDATE_INTERVAL, TimeUnit.SECONDS);

    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            App.setContent("paymentDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearCart() {
        App.getCart().clear();
        App.setRecipientFirstName(null);
        App.setRecipientLastName(null);
        App.setRecipientAddress(null);
        App.setMessage(null);
        App.setCustomerPhoneNumber(null);
        App.setSupplyDate(null);
        App.setPickupStore(null);
        App.setEnteredSupplyDetails(false);

        App.setCardNumber(null);
        App.setCardExpDate(null);
        App.setCardCvv(null);
        App.setGreeting(null);
        App.setCardNumber(null);
    }

    @FXML
    private void placeOrder(ActionEvent event) {
        // TODO: add store
        String greeting;
        if (App.getGreeting() != null)
            greeting = App.getGreeting();
        else
            greeting = "Mazal Tov";

        Task<CreateOrderResponse> createOrderTask = App.createTimedTask(() -> ClientHandler.createOrder(
                App.isOrderDelivery() ? null : App.getPickupStore(), (Customer) App.getCurrentUser(), App.getCardNumber(), App.getCart(),
                greeting, new Timestamp(Calendar.getInstance().getTime().getTime()),
                App.isOrderDelivery() ? App.getSupplyDate() : null, App.isOrderDelivery(),
                App.isOrderDelivery()
                        ? new Delivery(App.getRecipientFirstName() + " " + App.getRecipientLastName(),
                                App.getCustomerPhoneNumber(), App.getRecipientAddress(), App.getMessage(),
                                App.isImmediate(), false)
                        : null),
                Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        createOrderTask.setOnSucceeded(e -> {
            if (createOrderTask.getValue() == null) {
                System.out.println("Order failed");
                // TODO: flash msg
                App.hideLoading();
                return;
            }

            if (!createOrderTask.getValue().isSuccessful()) {
                System.out.println(createOrderTask.getMessage());
                App.hideLoading();
                // TODO: flash msg
                return;
            }

            App.setCurrentUser(createOrderTask.getValue().getOrder().getCustomer());
            clearCart();

            App.hideLoading();
            try {
                App.setContent("orderAccepted");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // TODO: maybe add order success screen
        });

        createOrderTask.setOnFailed(e -> {
            createOrderTask.getException().printStackTrace();
            App.hideLoading();
            // TODO: flash msg
        });

        App.showLoading(anchorPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(createOrderTask).start();
    }
}
