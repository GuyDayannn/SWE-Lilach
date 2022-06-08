package org.cshaifa.spring.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class OrderAcceptedController {

    @FXML
    private ImageView heartImage;

    @FXML
    void initialize() {
        Image pay = new Image(getClass().getResource("images/heart.png").toString());
        heartImage.setImage(pay);
        heartImage.setFitWidth(100);
        heartImage.setFitHeight(100);
    }

    @FXML
    private void proceed(ActionEvent event) {
        try {
            App.setContent("catalog");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
