package org.cshaifa.spring.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.image.Image;

import java.io.File;

public class CatalogController {

    @FXML
    private Pane catalogItem;

    @FXML
    private ImageView itemImage;

    @FXML
    private Text itemName;

    @FXML
    private Text itemPrice;

    @FXML
    private Button showItemButton;

    @FXML
    void showItem(MouseEvent event) {
        CatalogItem item = new CatalogItem(itemName, itemPrice, itemImage);
        App.setCurrentItemDisplayed(item);
        App.popUpLaunch(showItemButton);
    }

    @FXML
    void initialize() {
        try {
            File file = new File("C:\\Users\\rtole\\Desktop\\flower.jpg");
            Image image = new Image(file.toURI().toString());
            itemImage.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
