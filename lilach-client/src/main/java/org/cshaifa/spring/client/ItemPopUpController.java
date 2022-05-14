package org.cshaifa.spring.client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ItemPopUpController {
    @FXML Text  popUpMessageText;
    @FXML Text  popupPriceText;
    @FXML Button    btnPopUpUpdate;
    @FXML Button    btnPopUpCancel;
    @FXML ImageView itemImage;

    public void initialize() {
        popUpMessageText.setText(App.getCurrentItemDisplayed().getName());
        popupPriceText.setText(Double.toString(App.getCurrentItemDisplayed().getPrice()));
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
            System.out.println("You clicked Cancel");
            btnPopUpCancel.getScene().getWindow().hide();
        });
    }

}
