package org.cshaifa.spring.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ShoppingCartController {

    @FXML
    private VBox itemsVbox;

    @FXML
    private ImageView cartImage;

    @FXML
    private HBox titleHbox;

    @FXML
    private VBox summaryVbox;

    Order currentOrder;

    @FXML
    HBox getItemHBox(CatalogItem item) {
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        ImageView iv = null;

        if (item.getImage() != null) {
            try {
                iv = new ImageView(App.getImageFromByteArray(item.getImage()));
                iv.setFitWidth(75);
                iv.setFitHeight(75);
            } catch (IOException e1) {
                // TODO: maybe log the exception somewhere
                e1.printStackTrace();
            }
        }

        Text itemName = new Text(item.getName());
        double price = item.getPrice();
        if (item.isOnSale()) {
            price = new BigDecimal(price * 0.01 * (100 - item.getDiscount())).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        Text itemPrice = new Text(String.format("%.2f", price));
        vBox.getChildren().addAll(itemName, itemPrice);

        if (iv != null)
            hBox.getChildren().add(iv);
        hBox.getChildren().add(vBox);
        hBox.setPrefSize(200, 100);
        hBox.setSpacing(5);
        return hBox;
    }

    @FXML
    void loadItems() {

    }

    @FXML
    void displayTotal() {
        double total = currentOrder.getTotal();
        summaryVbox.getChildren().add(new Text("Order Total:\t" + Double.toString(total)));
    }

    @FXML
    void initialize() {
        Image cart = new Image(getClass().getResource("images/cart.png").toString());
        cartImage.setImage(cart);
        cartImage.setFitWidth(40);
        cartImage.setFitHeight(40);

        currentOrder = App.getCurrentOrder();

        if (currentOrder==null) {
            System.out.println("No current order");
        }
        else {
            loadItems();
            displayTotal();
        }
    }

}
