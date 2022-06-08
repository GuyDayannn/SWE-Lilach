package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.SubscriptionType;
import org.cshaifa.spring.entities.responses.NotifyDeleteResponse;
import org.cshaifa.spring.entities.responses.NotifyResponse;
import org.cshaifa.spring.entities.responses.NotifyUpdateResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ShoppingCartController {

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

        Text itemName = new Text(item.getName() + "\t\t");
        double price = item.getPrice();
        if (item.isOnSale()) {
            price = new BigDecimal(price * 0.01 * (100 - item.getDiscount())).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        Text itemPrice = new Text(String.format("%.2f", price) + "\t");
        Text itemQuantity = new Text(Integer.toString(shoppingCart.get(item)));
        Button decAmount = new Button("-");
        decAmount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (shoppingCart.get(item) > 1) {
                    Integer quantity = shoppingCart.get(item);
                    App.getCart().put(item, --quantity);
                    itemQuantity.setText(Integer.toString(shoppingCart.get(item)));
                    summaryVbox.getChildren().clear();
                    displayTotal();
                } else if (shoppingCart.get(item) == 1) {
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
                // need to add support for case where quantity>stock
                Integer quantity = shoppingCart.get(item);
                App.getCart().put(item, ++quantity);
                itemQuantity.setText(Integer.toString(shoppingCart.get(item)));
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
        hBox.getChildren().addAll(removeButton, iv, itemName, itemPrice, decAmount, itemQuantity, incAmount);
        hBox.setPrefSize(200, 50);
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
            double total = shoppingCart.entrySet().stream()
                    .mapToDouble(entry -> entry.getValue() * entry.getKey().getFinalPrice()).sum();
            summaryVbox.getChildren().add(
                    new Text("Order Total:\t\t" + new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).toString()));
            Customer currentCustomer = (Customer) App.getCurrentUser();
            if (currentCustomer.getSubscriptionType() == SubscriptionType.YEARLY) {
                summaryVbox.getChildren().add(new Text("10% Discount:\t\t"
                        + new BigDecimal(total * 0.1).setScale(2, RoundingMode.HALF_UP).toString()));
                summaryVbox.getChildren().add(new Text(
                        "New Total:\t\t" + (new BigDecimal(total * 0.9).setScale(2, RoundingMode.HALF_UP).toString())));
            }
            summaryVbox.getChildren().add(new Text("\n\n"));
            HBox buttonsBox = new HBox();
            buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);
            buttonsBox.setPrefWidth(400);
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
            Button finishButton = new Button("Complete Order");
            finishButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        finishButton.getScene().getWindow().hide();
                        App.setWindowTitle("Order Summary");
                        App.setContent("orderSummary");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Opening Order Summary Failed");
                    }
                }
            });
            buttonsBox.getChildren().addAll(clearButton, finishButton);
            summaryVbox.getChildren().add(buttonsBox);
        }
    }

    @FXML
    void initialize() {
        App.resetUpdateScheduler();

        Image cart = new Image(getClass().getResource("images/cart.png").toString());
        cartImage.setImage(cart);
        cartImage.setFitWidth(40);
        cartImage.setFitHeight(40);
        shoppingCart = App.getCart();

        if (shoppingCart.size() == 0) {
            itemsVbox.getChildren().add(new Text("Shopping cart is empty"));
        } else {
            loadItems();
            displayTotal();
        }

        App.scheduler.scheduleAtFixedRate(() -> {
            try {
                final NotifyResponse notifyResponse = ClientHandler.waitForUpdateFromServer();
                if (notifyResponse == null)
                    return;

                boolean inCart = false;

                if (notifyResponse instanceof NotifyUpdateResponse) {
                    NotifyUpdateResponse notifyUpdateResponse = (NotifyUpdateResponse) notifyResponse;
                    CatalogItem oldItem = shoppingCart.keySet().stream().filter(
                            catalogItem -> catalogItem.getId() == notifyUpdateResponse.getToUpdate().getId())
                            .findFirst().orElse(null);
                    if (oldItem != null) {
                        inCart = true;
                        shoppingCart.put(notifyUpdateResponse.getToUpdate(), shoppingCart.get(oldItem));
                        shoppingCart.remove(oldItem);
                    }
                } else if (notifyResponse instanceof NotifyDeleteResponse) {
                    NotifyDeleteResponse notifyDeleteResponse = (NotifyDeleteResponse) notifyResponse;
                    CatalogItem toDelete = shoppingCart.keySet().stream().filter(
                            catalogItem -> catalogItem.getId() == notifyDeleteResponse.getToDelete().getId())
                            .findFirst().orElse(null);

                    if (toDelete != null) {
                        inCart = true;
                        shoppingCart.remove(toDelete);
                    }
                }

                if (inCart) {
                    Platform.runLater(() -> {
                        itemsVbox.getChildren().clear();
                        summaryVbox.getChildren().clear();
                        loadItems();
                        displayTotal();
                    });
                }

            } catch (Exception error) {
                error.printStackTrace();
                return;
            }
        }, 0, Constants.UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

}
