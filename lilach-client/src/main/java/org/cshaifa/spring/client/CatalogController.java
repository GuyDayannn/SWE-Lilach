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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.List;
import javafx.application.Platform;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cshaifa.spring.entities.requests.*;
import org.cshaifa.spring.entities.responses.*;

import javax.imageio.ImageIO;


public class CatalogController {

    @FXML    private Button NewOrderButton;

    @FXML    private HBox bottomBar;

    @FXML    private ImageView catalogTitle;

    @FXML    private HBox flowerHBox;

    @FXML    private HBox flowerHBox2;

    @FXML    private HBox flowerHBox3;

    @FXML    private HBox flowerHBox4;

    @FXML    private Button flowersLeft;

    @FXML    private Button flowersRight;

    private List<CatalogItem> catalogItems;


    @FXML    private Button nextPageButton;

    @FXML    private Button previousPageButton;

    @FXML
    void nextPage(MouseEvent event) {

    }

    @FXML
    void previousPage(MouseEvent event) {

    }

    @FXML
    void showItem(MouseEvent event) { //popup item in catalog
        App.popUpLaunch((Button)event.getSource());
    }

    @FXML
    void initialize() throws IOException {
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        catalogTitle.setImage((image));

        // remove left/right buttons for now
        flowerHBox.getChildren().remove(flowersLeft);
        flowerHBox.getChildren().remove(flowersRight);

        try {
            GetCatalogResponse response = ClientHandler.getCatalog();
            if (response.isSuccessful()) {
                catalogItems = response.getCatalogItems();
                if (catalogItems!=null) {
                    int count_displayed_items = 0;
                    for (CatalogItem item : catalogItems) {
                        HBox hBox = new HBox();
                        VBox vBox = new VBox();
                        ImageView iv = new ImageView();
                        iv.setFitWidth(60);
                        iv.setFitHeight(60);

                        if (item.getImagePath() != null) {
                            iv.setImage(new Image(item.getImagePath()));
                        }
                        vBox.getChildren().add(new Text(item.getName()));
                        vBox.getChildren().add(new Text(Double.toString(item.getPrice())));
                        Button button = new Button("View Item");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                App.setCurrentItemDisplayed(item);
                                App.popUpLaunch(button);
                            }
                        });
                        Button refresh_button = new Button("\uD83D\uDDD8");
                        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override public void handle(MouseEvent event) {
                                // to add
                            }
                        });
                        HBox buttonBox = new HBox();
                        buttonBox.getChildren().addAll(button, refresh_button);
                        vBox.getChildren().addAll(buttonBox);
                        hBox.getChildren().addAll(iv, vBox);
                        hBox.setPrefSize(200,100);
                        hBox.setSpacing(5);
                        hBox.setStyle("-fx-padding: 5;" + "-fx-border-style: solid inside;"
                                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                                + "-fx-border-radius: 5;" + "-fx-border-color: green;");

                        /*hBox.setOnMouseClicked(event -> {
                            System.out.println(event.getSource());
                        });*/

                        if (count_displayed_items<5) {
                            flowerHBox.getChildren().add(hBox);
                        }
                        else if (count_displayed_items >= 5 && count_displayed_items < 10) {
                            flowerHBox2.getChildren().add(hBox);
                        }
                        else if (count_displayed_items >= 10 && count_displayed_items < 15) {
                            flowerHBox3.getChildren().add(hBox);
                        }
                        else if (count_displayed_items >= 15 && count_displayed_items < 20) {
                            flowerHBox4.getChildren().add(hBox);
                        }
                        else if (count_displayed_items >= 20) {
                            break;
                        }
                        count_displayed_items++;
                    }
                }
            }
            else {
                System.out.println("Loading Catalog Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //flowerHBox.getChildren().add(flowersRight);
    }


}
