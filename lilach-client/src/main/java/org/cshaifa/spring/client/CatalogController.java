package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    @FXML    private Text onSaleText;

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
        shoppingCart.getItems().clear();
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

            int count_displayed_items = 0;
            for (CatalogItem item : catalogItems) {
                HBox hBox = new HBox();
                VBox vBox = new VBox();
                ImageView iv = null;
                //ImageView iv2 = null;

                if (item.getImage() != null) {
                    try {
                        iv = new ImageView(App.getImageFromByteArray(item.getImage()));
                        //iv2 = new ImageView(App.getImageFromByteArray(item.getImage()));
                        iv.setFitWidth(60);
                        iv.setFitHeight(60);
                        //iv2.setFitWidth(20);
                        //iv2.setFitHeight(20);
                    } catch (IOException e1) {
                        // TODO: maybe log the exception somewhere
                        e1.printStackTrace();
                    }
                }

                /*
                if (catalogItems.indexOf(item)%5==0) {
                    MenuItem menuItem = new MenuItem();
                    menuItem.setGraphic(iv2);
                    menuItem.setText(item.getName());
                    menuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            // add event
                        }
                    });
                    shoppingCart.getItems().add(menuItem);
                }
                */

                Text itemName = new Text(item.getName());
                Text itemPrice = new Text(Double.toString(item.getPrice()));
                if(item.isOnSale()) {
                    VBox textBox = new VBox();
                    itemPrice.strikethroughProperty().setValue(true);

                    double newPrice = new BigDecimal(item.getPrice()*0.01*(100-item.getDiscount())).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    Text newItemPrice = new Text(Double.toString(newPrice));
                    newItemPrice.setFill(Color.RED);
                    newItemPrice.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    textBox.getChildren().addAll(itemPrice, newItemPrice);
                    vBox.getChildren().addAll(itemName, textBox);
                }
                else {
                    vBox.getChildren().addAll(itemName, itemPrice);
                }
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
                if(item.isOnSale()) {
                    hBox.getStyleClass().add("saleitem");
                    salesHBox.getChildren().add(hBox);
                }
                else {
                    hBox.getStyleClass().add("catalogitem");

                    if (count_displayed_items<5) {
                        flowerHBox.getChildren().add(hBox);
                    }
                    else if (count_displayed_items < 10) {
                        flowerHBox2.getChildren().add(hBox);
                    }
                    else if ( count_displayed_items < 15) {
                        flowerHBox3.getChildren().add(hBox);
                    }
                    else if (count_displayed_items < 20) {
                        flowerHBox4.getChildren().add(hBox);
                    }
                    else {
                        break;
                    }
                }

                count_displayed_items++;
            }

            if (current_page == 1) {
                previousPageButton.disableProperty().setValue(true);
            }
            if (count_displayed_items >= total_catalog_items) {
                nextPageButton.disableProperty().setValue(true);
            }

            MenuItem editCart = new MenuItem("Edit Cart");
            MenuItem completeOrder = new MenuItem("Finish Order");
            shoppingCart.getItems().add(editCart);
            shoppingCart.getItems().add(completeOrder);

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
