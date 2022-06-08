package org.cshaifa.spring.client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class ReportPopUpController {
    @FXML
    Button closePopupBtn;
    @FXML
    ImageView reportImage;

    @FXML
    void initialize()  {
        try{
            //Setting the image view
            reportImage.setImage(App.getImageFromByteArray(App.getCurrentReportDisplayed().getReportImage()));
        } catch(IOException fileNotFoundException){
            fileNotFoundException.printStackTrace();
        }
        closePopupBtn.setOnAction(event -> {
            closePopupBtn.getScene().getWindow().hide();
        });
    }

}
