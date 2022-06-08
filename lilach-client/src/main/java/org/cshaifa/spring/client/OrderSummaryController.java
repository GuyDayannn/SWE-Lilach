package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javafx.scene.control.TextField;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.SubscriptionType;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class OrderSummaryController {

    @FXML
    private VBox itemsVbox;

    @FXML
    private ImageView cartImage;

    @FXML
    private HBox titleHbox;

    @FXML
    private VBox summaryVBox;

    @FXML
    private Label orderTotalLabel;

    @FXML
    private Label discountLabel;

    @FXML
    private Label newTotalLabel;

    @FXML
    private HBox discountHBox;

    @FXML
    private HBox newTotalHBox;

    @FXML
    private TextField greetingTextField;

    Map<CatalogItem, Integer> shoppingCart;

    @FXML
    HBox getItemHBox(CatalogItem item) {
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
        double finalPrice = price * shoppingCart.get(item);
        Label itemPrice = new Label(String.format("%.2f", price) + "\t");
        Label itemFinalPrice = new Label(String.format("%.2f", finalPrice) + "\t");
        Label itemQuantity = new Label(Integer.toString(shoppingCart.get(item)));
        Button decAmount = new Button("-");
        decAmount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (shoppingCart.get(item) > 1) {
                    Integer quantity = shoppingCart.get(item);
                    App.getCart().put(item, --quantity);
                    itemQuantity.setText(Integer.toString(shoppingCart.get(item)));
                    double price = item.getPrice();
                    if (item.isOnSale()) {
                        price = new BigDecimal((price * 0.01 * (100 - item.getDiscount())))
                                .setScale(2, RoundingMode.HALF_UP).doubleValue();
                    }
                    itemFinalPrice.setText(String.format("%.2f", price * shoppingCart.get(item)));
                    displayTotal();
                } else if (shoppingCart.get(item) == 1) {
                    shoppingCart.remove(item);
                    itemsVbox.getChildren().clear();
                    loadItems();
                    displayTotal();
                }
            }
        });
        Button incAmount = new Button("+");
        incAmount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // need to add support for case where quantity>stock
                Integer quantity = shoppingCart.get(item);
                App.getCart().put(item, ++quantity);
                itemQuantity.setText(Integer.toString(shoppingCart.get(item)));
                double price = item.getPrice();
                if (item.isOnSale()) {
                    price = new BigDecimal((price * 0.01 * (100 - item.getDiscount())))
                            .setScale(2, RoundingMode.HALF_UP).doubleValue();
                }
                itemFinalPrice.setText(String.format("%.2f", price * shoppingCart.get(item)));
                displayTotal();
            }
        });
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                shoppingCart.remove(item);
                itemsVbox.getChildren().clear();
                loadItems();
                displayTotal();
            }
        });

        HBox.setMargin(removeButton, new Insets(0, 120, 0, 0));
        HBox.setMargin(itemName, new Insets(0, 0, 0, 0));
        HBox.setMargin(itemPrice, new Insets(0, 60, 0, 60));
        HBox.setMargin(decAmount, new Insets(0, 0, 0, 60));
        HBox.setMargin(itemQuantity, new Insets(0, 0, 0, 0));
        HBox.setMargin(incAmount, new Insets(0, 100, 0, 0));
        HBox.setMargin(itemFinalPrice, new Insets(0, 0, 0, 0));
        hBox.getChildren().addAll(removeButton, iv, itemName, itemPrice, decAmount, itemQuantity, incAmount,
                itemFinalPrice);
        hBox.setPrefSize(100, 500);
        hBox.setSpacing(5);
        hBox.getStyleClass().add("item");
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    @FXML
    void loadItems() {
        for (Map.Entry<CatalogItem, Integer> entry : shoppingCart.entrySet()) {
            CatalogItem item = entry.getKey();
            Integer integer = entry.getValue();
            HBox itemHBox = getItemHBox(item);

            itemsVbox.getChildren().add(itemHBox);
        }
    }

    @FXML
    void displayTotal() {
        if (shoppingCart != null) {
            double total = App.getCartTotal();
            orderTotalLabel.setText(new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).toString());
            Customer currentCustomer = (Customer) App.getCurrentUser();
            if (currentCustomer.getSubscriptionType() == SubscriptionType.YEARLY) {
                discountHBox.setVisible(true);
                newTotalHBox.setVisible(true);

                discountLabel.setText(new BigDecimal(total * 0.1).setScale(2,RoundingMode.HALF_DOWN).toString());
                newTotalLabel.setText(new BigDecimal(total * 0.9).setScale(2, RoundingMode.HALF_UP).toString());
            } else {
                summaryVBox.getChildren().remove(discountHBox);
                summaryVBox.getChildren().remove(newTotalHBox);
            }
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            App.setWindowTitle("Catalog");
            App.setContent("catalog");
        } catch (IOException e) {
            System.out.println("Opening Catalog failed");
        }
    }

    @FXML
    private void continueOrder(ActionEvent event) {
        try {
            App.setGreeting(greetingTextField.getText().strip());
            App.setContent("deliveryDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        Image cart = new Image(getClass().getResource("images/cart.png").toString());
        cartImage.setImage(cart);
        cartImage.setFitWidth(40);
        cartImage.setFitHeight(40);
        shoppingCart = App.getCart();

        if(App.getGreeting() != null) greetingTextField.setText(App.getGreeting());

        if (shoppingCart.size() == 0) {
            itemsVbox.getChildren().add(new Text("Shopping cart is empty"));
        } else {
            loadItems();
            displayTotal();
        }
    }

}
