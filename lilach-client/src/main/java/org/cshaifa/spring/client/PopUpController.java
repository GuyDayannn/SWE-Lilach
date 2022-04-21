package org.cshaifa.spring.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.File;

public class PopUpController {
    @FXML Text  popUpMessageText;
    @FXML Button    btnPopUpOK;
    @FXML Button    btnPopUpCancel;
    @FXML ImageView itemImage;

    public void initialize() {
        popUpMessageText.setText(App.getCurrentItemDisplayed().getName());
        Image im= new Image(App.getCurrentItemDisplayed().getImagePath());
        itemImage.setImage(im);

        btnPopUpOK.setOnAction(event -> {
            System.out.println("You clicked OK...");
            btnPopUpCancel.getScene().getWindow().hide();
        });
        btnPopUpCancel.setOnAction(event -> {
            System.out.println("You clicked Cancel");
            btnPopUpCancel.getScene().getWindow().hide();
        });
    }

}