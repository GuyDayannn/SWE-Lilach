package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;

public class ItemPopUpController {
    @FXML AnchorPane popupPane;
    @FXML Text  popUpMessageText;
    @FXML Text  popupPriceText;
    @FXML Text  popupPriceText1;
    @FXML Text popupNewPriceText;
    @FXML HBox buttonsHbox;
    @FXML Button    btnPopUpUpdate;
    @FXML Button    btnPopUpCancel;
    @FXML ImageView itemImage;

    public void initialize() {
        CatalogItem currentItemDisplayed = App.getCurrentItemDisplayed();
        popUpMessageText.setText(currentItemDisplayed.getName());
        popupPriceText.setText(Double.toString(currentItemDisplayed.getPrice()));

        //Change this to access permissions later
        if (App.getCurrentUser()==null || App.getCurrentUser() instanceof Customer) {
            buttonsHbox.getChildren().remove(btnPopUpUpdate);
        }

        if (App.getCurrentItemDisplayed().isOnSale()) {
            popupPriceText.setVisible(false);
            popupPriceText1.setText(Double.toString(currentItemDisplayed.getPrice())+" ");
            popupPriceText1.setStrikethrough(true);
            popupPriceText1.setVisible(true);
            double newPrice = new BigDecimal(currentItemDisplayed.getPrice()*0.01*(100-currentItemDisplayed.getDiscount())).setScale(2, RoundingMode.HALF_UP).doubleValue();
            popupNewPriceText.setText(" "+Double.toString(newPrice));
            popupNewPriceText.setFill(Color.RED);
            popupNewPriceText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            popupNewPriceText.setVisible(true);
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
