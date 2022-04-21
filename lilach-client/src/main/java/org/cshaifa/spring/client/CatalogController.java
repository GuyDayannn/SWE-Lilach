package org.cshaifa.spring.client;

import org.cshaifa.spring.entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

import javafx.scene.layout.HBox;


public class CatalogController {

    @FXML    private Button NewOrderButton;

    @FXML    private HBox catalogItem;

    @FXML    private Button flowersLeft;

    @FXML    private Button flowersRight;

    @FXML    private ImageView itemImage;

    @FXML    private Text itemName;

    @FXML    private Text itemPrice;

    @FXML    private Button showItemButton;

    private List<CatalogItem> catalogItems;

    @FXML
    void showItem(MouseEvent event) { //popup item in catalog
        Button button = (Button)  event.getSource();
        int id = (int) Long.parseLong(button.getId());
        CatalogItem item = catalogItems.get(id);
        App.setCurrentItemDisplayed(item);
        App.popUpLaunch(showItemButton);
    }

    @FXML
    void initialize(List<CatalogItem> catalogItems_list) {
        try {
            catalogItems = ClientHandler.getCatalog();
            int i =0;
            for(i=0; i< catalogItems.size(); i++){
                File file = new File("E:\\myFolder\\uni\\7th semestru\\program engineering\\flower.webp");
                Image image = new Image(file.toURI().toString());
                itemImage.setImage(image);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
