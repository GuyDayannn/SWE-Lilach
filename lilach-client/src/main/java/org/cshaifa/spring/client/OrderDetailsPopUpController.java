package org.cshaifa.spring.client;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Order;
import org.cshaifa.spring.entities.responses.GetOrdersResponse;
import org.cshaifa.spring.utils.Constants;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OrderDetailsPopUpController {

    @FXML
    private Label addressLabel;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label orderDateLabel;
    @FXML
    private Label deliveryTimeLabel;
    @FXML
    private VBox deliveryVbox;
    @FXML
    private Label supplyDateLabel;
    @FXML
    private Label firstNameDeliveryLabel;
    @FXML
    private Label firstNamePaymentLabel;
    @FXML
    private VBox itemsVbox;
    @FXML
    private VBox paneVBox;
    @FXML
    private VBox orderDetailsVbox;
    @FXML
    private Label lastNameDeliveryLabel;
    @FXML
    private Label lastNamePaymentLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private VBox selfpickupVbox;
    @FXML
    private Label storeAddressLabel;
    @FXML
    private Label storeNameLabel;
    @FXML
    private HBox titleHbox;
    @FXML
    private Button goBackButton;

    private Order selectedOrder;

    @FXML
    HBox getItemHBox(CatalogItem item, int quantity) {
        HBox hBox = new HBox();

        Label itemName = new Label(item.getName() + "\t");
        double price = item.getPrice();
        if (item.isOnSale()) {
            price = new BigDecimal((price * 0.01 * (100 - item.getDiscount()))).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        double finalPrice = price * quantity;
        Label itemPrice = new Label(String.format("%.2f", price) + "\t");
        Label itemFinalPrice = new Label(String.format("%.2f", finalPrice) + "\t");
        Label itemQuantity = new Label(Integer.toString(quantity) + "\t");
        HBox.setMargin(itemName, new Insets(0, 0, 0, 0));
        HBox.setMargin(itemPrice, new Insets(0, 60, 0, 60));
        HBox.setMargin(itemQuantity, new Insets(0, 0, 0, 0));
        HBox.setMargin(itemFinalPrice, new Insets(0, 0, 0, 0));

        hBox.getChildren().addAll(itemName, itemPrice, itemQuantity, itemFinalPrice);
        hBox.setSpacing(5);
        hBox.getStyleClass().add("item");
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    @FXML
    void displayOrderDetails() {
        firstNamePaymentLabel.setText(selectedOrder.getCustomer().getFullName());
        orderDateLabel.setText(selectedOrder.getOrderDate().toString());
        supplyDateLabel.setText(selectedOrder.getSupplyDate().toString());
        if (App.getSelectedOrder().isDelivery()) {
            paneVBox.getChildren().remove(selfpickupVbox);
            firstNameDeliveryLabel.setText(selectedOrder.getDeliveryDetails().getRecipientName());
            lastNameDeliveryLabel.setText(selectedOrder.getDeliveryDetails().getRecipientName());
            phoneNumberLabel.setText(selectedOrder.getDeliveryDetails().getPhoneNumber());
            addressLabel.setText(selectedOrder.getDeliveryDetails().getAddress());
            messageLabel.setText(selectedOrder.getDeliveryDetails().getMessage());
        }
        else {
            paneVBox.getChildren().removeAll(deliveryVbox);
            storeAddressLabel.setText(selectedOrder.getStore().getAddress());
            storeNameLabel.setText(selectedOrder.getStore().getName());
        }
    }

    @FXML
    void initialize() {
        selectedOrder = App.getSelectedOrder();
        for (Map.Entry<CatalogItem, Integer> entry : selectedOrder.getItems().entrySet()) {
            CatalogItem item = entry.getKey();
            Integer integer = entry.getValue();
            HBox itemHBox = getItemHBox(item, integer);

            itemsVbox.getChildren().add(itemHBox);
        }

        displayOrderDetails();

        goBackButton.setOnAction(event -> {
            goBackButton.getScene().getWindow().hide();
        });
    }

}
