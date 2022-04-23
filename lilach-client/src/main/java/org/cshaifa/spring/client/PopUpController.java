package org.cshaifa.spring.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PopUpController {
    @FXML Text  popUpMessageText;
    @FXML Button    btnPopUpUpdate;
    @FXML Button    btnPopUpCancel;
    @FXML ImageView itemImage;

    public void initialize() {
        popUpMessageText.setText(App.getCurrentItemDisplayed().getName());
        Image image = new Image(App.getCurrentItemDisplayed().getImagePath());
        itemImage.setImage(image);

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