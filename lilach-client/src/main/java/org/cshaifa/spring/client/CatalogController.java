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
import javafx.application.Platform;

import javafx.scene.layout.HBox;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;


public class CatalogController {

    @FXML    private ImageView catalogTitle;

    @FXML    private HBox catalogItem1;

    @FXML    private HBox catalogItem2;

    @FXML    private HBox catalogItem3;

    @FXML    private HBox catalogItem4;

    @FXML    private HBox catalogItem5;

    @FXML    private Button flowersLeft;

    @FXML    private Button flowersRight;

    @FXML    private ImageView item1Image;

    @FXML    private Text item1Name;

    @FXML    private Text item1Price;

    @FXML    private Button showItem1Button;

    private List<CatalogItem> catalogItems;

    @FXML
    void showItem(MouseEvent event) { //popup item in catalog
        //Button button = (Button)event.getSource();
        //int id = (int) Long.parseLong(button.getId());
        //CatalogItem item = catalogItems.get(id);
        //App.setCurrentItemDisplayed(item);
        App.popUpLaunch(showItem1Button);
    }

    @FXML
    void initialize() {
        File fi = new File("C:\\Users\\rtole\\Desktop\\LilachLogo.png");
        Image logo = new Image(fi.toURI().toString());
        catalogItem1.setId("1");
        catalogTitle.setImage(logo);
        try {
            GetCatalogResponse response = ClientHandler.getCatalog();
            if (response.isSuccessful()) {
                catalogItems = response.getCatalogItems();
                if (catalogItems!=null) {
                    for (CatalogItem item : catalogItems) {
                        //File file = new File("C:\\Users\\rtole\\Desktop\\flower.jpg");
                        //Image image = new Image(file.toURI().toString());
                        //itemName.setText(item.getName());
                        //itemPrice.setText(Double.toString(item.getPrice()));
                        //itemImage.setImage(image);
                    }
                }
            }
            else {
                System.out.println("Loading Catalog Failed");
                File file = new File("C:\\Users\\rtole\\Desktop\\flower.jpg");
                Image image = new Image(file.toURI().toString());
                item1Name.setText("trying");
                item1Price.setText("123");
                item1Image.setImage(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
