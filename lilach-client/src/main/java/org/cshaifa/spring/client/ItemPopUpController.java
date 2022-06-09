package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.CustomerServiceEmployee;
import org.cshaifa.spring.entities.SystemAdmin;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class ItemPopUpController {
    @FXML AnchorPane popupPane;
    @FXML Text  popUpMessageText;
    @FXML Text  popupPriceText;
    @FXML Text popupNewPriceText;
    @FXML HBox buttonsHbox;
    @FXML HBox priceBox;
    @FXML HBox itemDetailsBox;
    @FXML Button    btnPopUpUpdate;
    @FXML Button    btnPopUpCancel;
    @FXML ImageView itemImage;
    @FXML
    Label notificationLabel;

    public void initialize() {
        CatalogItem currentItemDisplayed = App.getCurrentItemDisplayed();
        popUpMessageText.setText(currentItemDisplayed.getName());
        popupPriceText.setText(Double.toString(currentItemDisplayed.getPrice()));
        Text itemDetails = new Text("Type: " + currentItemDisplayed.getItemType() + " | Color: " + currentItemDisplayed.getItemColor() + " | Size: " + currentItemDisplayed.getSize());
        itemDetailsBox.getChildren().add(itemDetails);

        //Change this to access permissions later
        if (App.getCurrentUser()==null || Customer.class == App.getCurrentUser().getClass() || CustomerServiceEmployee.class == App.getCurrentUser().getClass() || App.getCurrentUser().getClass() == SystemAdmin.class) {
            buttonsHbox.getChildren().remove(btnPopUpUpdate);
        }

        if (App.getCurrentUser()!=null && Customer.class == App.getCurrentUser().getClass()) {
            Button addCartButton = new Button();
            addCartButton.getStyleClass().add("catalog-item-buttons");
            Image cartImage = new Image(getClass().getResource("images/cart.png").toString());
            ImageView ivCart = new ImageView(cartImage);
            ivCart.setFitHeight(25);
            ivCart.setFitWidth(25);
            addCartButton.setGraphic(ivCart);
            addCartButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    notificationLabel.setText("Added to cart!");
                    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1),
                            notificationLabel);
                    notificationLabel.setVisible(true);
                    fadeTransition.setFromValue(0.0);
                    fadeTransition.setToValue(1.0);
                    fadeTransition.setOnFinished(e -> notificationLabel.setVisible(false));
                    fadeTransition.play();
                    if (App.getCart().containsKey(currentItemDisplayed)) {
                        Integer quantity = App.getCart().get(currentItemDisplayed);
                        App.getCart().put(currentItemDisplayed, ++quantity);
                    } else {
                        App.getCart().put(currentItemDisplayed, 1);
                    }
                }
            });
            buttonsHbox.getChildren().add(0,addCartButton);
        }

        if (App.getCurrentItemDisplayed().isOnSale()) {
            popupPriceText.setText(Double.toString(currentItemDisplayed.getPrice())+" ");
            popupPriceText.setStrikethrough(true);
            popupPriceText.setVisible(true);
            double newPrice = new BigDecimal(currentItemDisplayed.getPrice()*0.01*(100-currentItemDisplayed.getDiscount())).setScale(2, RoundingMode.HALF_UP).doubleValue();
            popupNewPriceText.setText(" "+Double.toString(newPrice));
            popupNewPriceText.setFill(Color.RED);
            popupNewPriceText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            popupNewPriceText.setVisible(true);
        } else {
            priceBox.getChildren().remove(popupNewPriceText);
            popupPriceText.setTextAlignment(TextAlignment.CENTER);
        }
        try {
            itemImage.setImage(App.getImageFromByteArray(App.getCurrentItemDisplayed().getImage()));
        } catch (IOException e) {
            // TODO: log this somewhere
            e.printStackTrace();
        }

        btnPopUpUpdate.setOnAction(event -> {
            App.popUpLaunch(btnPopUpUpdate, "UpdatePopUp");
            btnPopUpUpdate.getScene().getWindow().hide();
        });

        btnPopUpCancel.setOnAction(event -> {
            btnPopUpCancel.getScene().getWindow().hide();
        });
    }

}
