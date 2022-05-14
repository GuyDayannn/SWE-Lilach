package org.cshaifa.spring.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class CatalogController {

    @FXML    private Button NewOrderButton;

    @FXML    private Button refreshButton;

    @FXML    private HBox bottomBar;

    @FXML    private ImageView catalogTitle;

    @FXML    private HBox salesHBox;

    @FXML    private HBox flowerHBox;

    @FXML    private HBox flowerHBox2;

    @FXML    private HBox flowerHBox3;

    @FXML    private HBox flowerHBox4;

    @FXML    private Button nextPageButton;

    @FXML    private Button previousPageButton;

    @FXML    private Menu shoppingCart;

    @FXML    private MenuItem item1;

    @FXML private ImageView item1image;

    private int count_displayed_items = 0;
    private int count_displayed_sales_items = 0;

    private int total_catalog_items = 0;

    private int current_page = 1;

    @FXML
    private VBox rootVBox;

    @FXML
    void nextPage(MouseEvent event) {

    }

    @FXML
    void previousPage(MouseEvent event) {

    }

    @FXML
    void refreshCatalog(MouseEvent event) throws IOException {
        flowerHBox.getChildren().clear();
        flowerHBox2.getChildren().clear();
        flowerHBox3.getChildren().clear();
        flowerHBox4.getChildren().clear();
        salesHBox.getChildren().clear();
        initialize();
    }


    @FXML
    void initialize() throws IOException {
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        catalogTitle.setImage((image));
        Task<GetCatalogResponse> getCatalogTask = App.createTimedTask(() -> {
            return ClientHandler.getCatalog();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getCatalogTask.setOnSucceeded(e -> {
            if (getCatalogTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting catalog failed");
                return;
            }
            GetCatalogResponse response = getCatalogTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting catalog failed");
                return;
            }
            List<CatalogItem> catalogItems = response.getCatalogItems();

            Image imagexample = new Image(catalogItems.get(0).getImagePath());
            item1image.setImage(imagexample);
            int count_displayed_items = 0;
            for (CatalogItem item : catalogItems) {
                HBox hBox = new HBox();
                VBox vBox = new VBox();
                ImageView iv = null;

                if (item.getImage() != null) {
                    try {
                        iv = new ImageView(App.getImageFromByteArray(item.getImage()));
                        iv.setFitWidth(60);
                        iv.setFitHeight(60);
                    } catch (IOException e1) {
                        // TODO: maybe log the exception somewhere
                        e1.printStackTrace();
                    }
                }
                Text itemName = new Text(item.getName());
                Text itemPrice;
                if(item.getIsOnSale()==true){
                    itemPrice = new Text(Double.toString(item.getPrice()*(1-item.getDiscount())));
                }
                else{
                    itemPrice = new Text(Double.toString(item.getPrice()));
                }
                vBox.getChildren().addAll(itemName, itemPrice);
                Button button = new Button("View Item");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        App.setCurrentItemDisplayed(item, itemPrice, itemName);
                        App.popUpLaunch(button, "PopUp");
                    }
                });
                vBox.getChildren().add(button);
                if (iv != null)
                    hBox.getChildren().add(iv);
                hBox.getChildren().add(vBox);
                hBox.setPrefSize(200,100);
                hBox.setSpacing(5);
                hBox.setStyle("-fx-padding: 5;" + "-fx-border-style: solid inside;"
                              + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                              + "-fx-border-radius: 5;" + "-fx-border-color: green;");
                if(item.getIsOnSale()==true){
                    salesHBox.getChildren().add(hBox);

                }
                else{
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
//                else if (count_displayed_items < 25) { //adding sales
//                        salesHBox.getChildren().add(hBox);
//                        salesHBox.setStyle("-fx-border-color: red;");
                    else{// if (count_displayed_items >= 25) {
                        break;
                    }

                }
                               count_displayed_items++;
            }

            App.hideLoading();
        });

        getCatalogTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getCatalogTask.getException().printStackTrace();
            App.hideLoading();
        });

        App.showLoading(rootVBox, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(getCatalogTask).start();

    }


}
