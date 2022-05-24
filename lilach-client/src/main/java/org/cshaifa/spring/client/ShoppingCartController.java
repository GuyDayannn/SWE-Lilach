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

import javax.xml.catalog.Catalog;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

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
    HBox getItemHBox(CatalogItem item, Integer integer) {
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
        hBox.getChildren().addAll(iv, itemName, itemPrice, new Text(Integer.toString(integer)));
        hBox.setPrefSize(200, 100);
        hBox.setSpacing(5);
        return hBox;
    }

    @FXML
    void loadItems() {
        for (Map.Entry<CatalogItem, Integer> entry : shoppingCart.entrySet()) {
            CatalogItem item = entry.getKey();
            Integer integer = entry.getValue();
            HBox itemHBox = getItemHBox(item, integer);

            itemsVbox.getChildren().add(itemHBox);
        }
    }

    @FXML
    void displayTotal() {
        double total = shoppingCart.entrySet().stream().mapToDouble(entry -> entry.getValue() * entry.getKey().getFinalPrice()).sum();
        summaryVbox.getChildren().add(new Text("Order Total:\t" + Double.toString(total)));
    }

    @FXML
    void initialize() {
        Image cart = new Image(getClass().getResource("images/cart.png").toString());
        cartImage.setImage(cart);
        cartImage.setFitWidth(40);
        cartImage.setFitHeight(40);

        shoppingCart = App.getCart();

        if (shoppingCart==null) {
            System.out.println("No current order");
        }
        else {
            System.out.println("Order exists");
            loadItems();
            displayTotal();
        }
    }

}
