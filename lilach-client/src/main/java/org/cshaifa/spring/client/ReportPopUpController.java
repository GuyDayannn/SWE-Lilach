package org.cshaifa.spring.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.w3c.dom.events.Event;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReportPopUpController {
    @FXML
    Button closePopupBtn;
    @FXML
    ImageView reportImage;

    @FXML
    void initialize()  {
        try{
            String path = App.getCurrentReportDisplayed().getReportPath();
            Image image = new Image(new FileInputStream(path));
            //Setting the image view
            ImageView imageView = new ImageView(image);
            reportImage.setImage(image);
        } catch(IOException fileNotFoundException){
            fileNotFoundException.printStackTrace();
        }
        closePopupBtn.setOnAction(event -> {
            closePopupBtn.getScene().getWindow().hide();
        });
    }

}
