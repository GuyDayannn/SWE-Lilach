package org.cshaifa.spring.client;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
import javafx.scene.layout.VBox;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;


public class CatalogController {

    @FXML    private ImageView catalogTitle;

    @FXML    private HBox flowerHBox;

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
        App.popUpLaunch((Button)event.getSource());
    }

    @FXML
    void initialize() {
        File fi = new File("C:\\Users\\rtole\\Desktop\\LilachLogo.png");
        Image logo = new Image(fi.toURI().toString());
        catalogTitle.setImage(logo);

        flowerHBox.getChildren().remove(flowersRight);

        try {
            GetCatalogResponse response = ClientHandler.getCatalog();
            if (response.isSuccessful()) {
                catalogItems = response.getCatalogItems();
                if (catalogItems!=null) {
                    int count_displayed_items = 0;
                    for (CatalogItem item : catalogItems) {
                        if (count_displayed_items<4) {
                            count_displayed_items++;
                        }
                        else {
                            break;
                        }
                        HBox hBox = new HBox();
                        VBox vBox = new VBox();
                        ImageView iv = new ImageView();
                        iv.setFitWidth(140);
                        iv.setFitHeight(140);

                        if (item.getImagePath() != null) {
                            iv.setImage(new Image(item.getImagePath()));
                        }
                        vBox.getChildren().add(new Text(item.getName()));
                        vBox.getChildren().add(new Text(Double.toString(item.getPrice())));
                        Button button = new Button("Click here");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("button clicked!!");
                                App.setCurrentItemDisplayed(item);
                                App.popUpLaunch(button);
                            }
                        });
                        vBox.getChildren().add(button);
                        hBox.getChildren().addAll(iv, vBox);
                        /*hBox.setOnMouseClicked(event -> {
                            System.out.println(event.getSource());
                        });*/

                        flowerHBox.getChildren().add(hBox);

                        //File file = new File("C:\\Users\\rtole\\IdeaProjects\\SWE-Lilach\\lilach-server\\src\\main\\resources\\org\\cshaifa\\spring\\server\\database\\images\\Flower" + Integer.toString(i) + ".jpg");
                        //Image image = new Image(file.toURI().toString());
                        //item1Name.setText(item.getName());
                        //item1Price.setText(Double.toString(item.getPrice()));
                        //item1Image.setImage(image);
                    }
                }
            }
            else {
                System.out.println("Loading Catalog Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        flowerHBox.getChildren().add(flowersRight);
    }


}
