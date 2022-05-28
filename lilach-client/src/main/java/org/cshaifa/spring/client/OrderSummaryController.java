package org.cshaifa.spring.client;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Order;
import org.cshaifa.spring.entities.SubscriptionType;

import javax.swing.text.Position;
import javax.xml.catalog.Catalog;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class OrderSummaryController {

    @FXML
    private VBox itemsVbox;

    @FXML
    private ImageView cartImage;

    @FXML
    private HBox titleHbox;

    @FXML
    private VBox summaryVbox;

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

        Label itemName = new Label(item.getName()+"\t\t");
        double price = item.getPrice();
        if (item.isOnSale()) {
            price = new BigDecimal((price * 0.01 * (100 - item.getDiscount()))).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        double finalPrice = price * shoppingCart.get(item);
        Label itemPrice = new Label(String.format("%.2f", price)+"\t");
        Label itemFinalPrice = new Label(String.format("%.2f", finalPrice)+"\t");
        Label itemQuantity = new Label(Integer.toString(shoppingCart.get(item)));
        Button decAmount = new Button("-");
        decAmount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (shoppingCart.get(item)>1) {
                    Integer quantity = shoppingCart.get(item);
                    App.getCart().put(item, --quantity);
                    itemQuantity.setText(Integer.toString(shoppingCart.get(item)));
                    double price = item.getPrice();
                    if (item.isOnSale()) {
                        price = new BigDecimal((price * 0.01 * (100 - item.getDiscount()))).setScale(2, RoundingMode.HALF_UP)
                                .doubleValue();
                    }
                    itemFinalPrice.setText(Double.toString(price * shoppingCart.get(item)));
                    summaryVbox.getChildren().clear();
                    displayTotal();
                }
                else if(shoppingCart.get(item) == 1) {
                    shoppingCart.remove(item);
                    itemsVbox.getChildren().clear();
                    summaryVbox.getChildren().clear();
                    loadItems();
                    displayTotal();
                }
            }
        });
        Button incAmount = new Button("+");
        incAmount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //need to add support for case where quantity>stock
                Integer quantity = shoppingCart.get(item);
                App.getCart().put(item, ++quantity);
                itemQuantity.setText(Integer.toString(shoppingCart.get(item)));
                double price = item.getPrice();
                if (item.isOnSale()) {
                    price = new BigDecimal((price * 0.01 * (100 - item.getDiscount()))).setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                }
                itemFinalPrice.setText(Double.toString(price * shoppingCart.get(item)));
                summaryVbox.getChildren().clear();
                displayTotal();
            }
        });
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                shoppingCart.remove(item);
                itemsVbox.getChildren().clear();
                summaryVbox.getChildren().clear();
                loadItems();
                displayTotal();
            }
        });

        HBox.setMargin(removeButton, new Insets(0,120,0,0));
        HBox.setMargin(itemName, new Insets(0,0,0,0));
        HBox.setMargin(itemPrice, new Insets(0,60,0,60));
        HBox.setMargin(decAmount, new Insets(0,0,0,60));
        HBox.setMargin(itemQuantity, new Insets(0,0,0,0));
        HBox.setMargin(incAmount, new Insets(0,100,0,0));
        HBox.setMargin(itemFinalPrice, new Insets(0,0,0,0));
        hBox.getChildren().addAll(removeButton, iv, itemName, itemPrice, decAmount, itemQuantity, incAmount, itemFinalPrice);
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
        if (shoppingCart!=null) {
            double total = shoppingCart.entrySet().stream().mapToDouble(entry -> entry.getValue() * entry.getKey().getFinalPrice()).sum();
            summaryVbox.getChildren().add(new Text("Order Total:\t\t" + Double.toString(total)));
            Customer currentCustomer = (Customer)App.getCurrentUser();
            if (currentCustomer.getSubscriptionType() == SubscriptionType.YEARLY) {
                summaryVbox.getChildren().add(new Text("10% Discount:\t\t" + Double.toString(total*0.1)));
                summaryVbox.getChildren().add(new Text("New Total:\t\t" + Double.toString(total*0.9)));
            }
            summaryVbox.getChildren().add(new Text("\n\n"));
            HBox buttonsBox = new HBox();
            buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);
            buttonsBox.setPrefWidth(400);

            Button backButton = new Button("Go Back");
            backButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try{
                        App.setWindowTitle("Catalog");
                        App.setContent("catalog");
                    } catch(IOException e) {
                        System.out.println("Opening Catalog failed");
                    }
                }
            });

            Button clearButton = new Button("Clear Cart");
            clearButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    shoppingCart.clear();
                    itemsVbox.getChildren().clear();
                    itemsVbox.getChildren().add(new Text("Shopping cart is empty"));
                    summaryVbox.getChildren().clear();
                }
            });
            Button finishButton = new Button("Continue");
            finishButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // TODO: add tranistion to next window
                }
            });
            buttonsBox.getChildren().addAll(backButton, finishButton);
            summaryVbox.getChildren().add(buttonsBox);
        }
    }

    @FXML
    void initialize() {
        Image cart = new Image(getClass().getResource("images/cart.png").toString());
        cartImage.setImage(cart);
        cartImage.setFitWidth(40);
        cartImage.setFitHeight(40);
        shoppingCart = App.getCart();

        if (shoppingCart.size()==0) {
            itemsVbox.getChildren().add(new Text("Shopping cart is empty"));
        }
        else {
            loadItems();
            displayTotal();
        }
    }

}
