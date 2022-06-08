package org.cshaifa.spring.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.IOException;

public class TwoReportsPopUpController {
    @FXML
    Button closePopupBtn;
    @FXML
    ImageView reportImage1;
    @FXML
    ImageView reportImage2;
    @FXML
    Text reportText1;
    @FXML
    Text reportText2;

    @FXML
    void initialize()  {
        try{
            //Setting the image view
            reportImage1.setImage(App.getImageFromByteArray(App.getCurrentReportDisplayed().getReportImage()));
            reportImage2.setImage(App.getImageFromByteArray(App.getCurrentReport1Displayed().getReportImage()));
        } catch(IOException fileNotFoundException){
            fileNotFoundException.printStackTrace();
        }
        closePopupBtn.setOnAction(event -> {
            closePopupBtn.getScene().getWindow().hide();
        });
    }

}
