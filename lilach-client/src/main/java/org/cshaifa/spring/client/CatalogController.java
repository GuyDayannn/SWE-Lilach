package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.*;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class CatalogController {

    @FXML    private VBox rootVBox;

    @FXML    private Text welcomeText;

    @FXML    private ToolBar toolbar;

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

    @FXML    private MenuBar menuBar;

    @FXML    private Menu shoppingCart;

    @FXML    private MenuItem item1;

    @FXML private ImageView item1image;

    List<CatalogItem> catalogItems = null;

    private int count_displayed_items = 0;

    private int total_catalog_items = 0;

    private int current_page = 1;

    private String selectedType = "all";

    @FXML
    void clearCatalogDisplay() {
        count_displayed_items = 0;
        flowerHBox.getChildren().clear();
        flowerHBox2.getChildren().clear();
        flowerHBox3.getChildren().clear();
        flowerHBox4.getChildren().clear();
    }

    @FXML
    void displayFlowers(MouseEvent event) {
        selectedType = "flower";
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayBouquets(MouseEvent event) {
        selectedType = "bouquet";
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayPlants(MouseEvent event) {
        selectedType = "plant";
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayOrchids(MouseEvent event) {
        selectedType = "orchid";
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayWine(MouseEvent event) {
        selectedType = "wine";
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayChocolate(MouseEvent event) {
        selectedType = "chocolate";
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void nextPage(MouseEvent event) {
        clearCatalogDisplay();
        current_page++;
        catalogDisplay();
    }

    @FXML
    void previousPage(MouseEvent event) {
        clearCatalogDisplay();
        current_page--;
        catalogDisplay();
    }

    @FXML
    void refreshCatalog(MouseEvent event) throws IOException {
        current_page = 1;
        clearCatalogDisplay();
        toolbar.getItems().clear();
        toolbar.getItems().add(welcomeText);
        toolbar.getItems().add(menuBar);
        salesHBox.getChildren().clear();
        shoppingCart.getItems().clear();
        initialize();
    }

    @FXML
    void salesDisplay() {
        if (catalogItems == null) {
            System.out.println("Catalog is empty :(");
        }
        else {
            for (CatalogItem item : catalogItems) {
                if (item.isOnSale()) {
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
                    Text itemPrice = new Text(Double.toString(item.getPrice()));

                    VBox textBox = new VBox();
                    itemPrice.strikethroughProperty().setValue(true);

                    double newPrice = new BigDecimal(item.getPrice() * 0.01 * (100 - item.getDiscount())).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    Text newItemPrice = new Text(Double.toString(newPrice));
                    newItemPrice.setFill(Color.RED);
                    newItemPrice.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    textBox.getChildren().addAll(itemPrice, newItemPrice);
                    vBox.getChildren().addAll(itemName, textBox);

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
                    hBox.getStyleClass().add("saleitem");
                    salesHBox.getChildren().add(hBox);
                }
            }
        }
    }

    @FXML
    void catalogDisplay() {
        if (catalogItems == null) {
            System.out.println("Catalog is empty :(");
            previousPageButton.disableProperty().setValue(true);
            nextPageButton.disableProperty().setValue(true);
        }
        else {
            List<CatalogItem> allTypeItems = new ArrayList<>();
            if (selectedType == "all") {
                allTypeItems = catalogItems;
            }
            else {
                for (CatalogItem item : catalogItems) {
                    if (item.getItemType().equals(selectedType)) {
                        allTypeItems.add(item);
                    }
                }
            }

            int total_catalog_type_items = allTypeItems.size();

            for (CatalogItem item : allTypeItems) {
                int itemIndex = allTypeItems.indexOf(item);
                if (itemIndex >= ((current_page-1)*20) && itemIndex < (current_page*20)) {
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
                    Text itemPrice = new Text(Double.toString(item.getPrice()));
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

                    count_displayed_items++;
                }
            }


            if (current_page == 1) {
                previousPageButton.disableProperty().setValue(true);
            }
            if (((current_page-1)*20)+count_displayed_items == total_catalog_type_items) {
                nextPageButton.disableProperty().setValue(true);
            }
        }

        count_displayed_items = 0;
    }

    @FXML
    void initialize() throws IOException {
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        catalogTitle.setImage((image));
        toolbar.getItems().remove(menuBar);
        Button NewOrderButton = new Button("New Order");
        NewOrderButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });
        Button signInButton = new Button("Sign In");
        signInButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                App.setWindowTitle("login");
                try {
                    App.setContent("customerLogin");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button registerButton = new Button("Register");
        registerButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                App.setWindowTitle("register");
                try {
                    App.setContent("customerRegister");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    refreshCatalog(mouseEvent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button contactButton = new Button("Contact");
        contactButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });
        Button viewProfileButton = new Button("View Profile");
        viewProfileButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                App.setWindowTitle("Customer Profile");
                try {
                    App.setContent("customerProfile");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        if (App.getCurrentUser()==null) {
            NewOrderButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    App.setWindowTitle("login");
                    try {
                        App.setContent("customerLogin");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            welcomeText.setText("");
            toolbar.getItems().add(NewOrderButton);
            toolbar.getItems().add(signInButton);
            toolbar.getItems().add(registerButton);
            toolbar.getItems().add(refreshButton);
            toolbar.getItems().add(contactButton);
        }
        else {
            welcomeText.setText("Welcome, "+App.getCurrentUser().getFullName());
            toolbar.getItems().add(NewOrderButton);
            toolbar.getItems().add(viewProfileButton);
            toolbar.getItems().add(refreshButton);
            toolbar.getItems().add(contactButton);
        }
        toolbar.getItems().add(menuBar);
        Image cartImage = new Image(getClass().getResource("images/cart.png").toString());
        ImageView ivCart = new ImageView(cartImage);
        ivCart.setFitHeight(20);
        ivCart.setFitWidth(20);
        shoppingCart.setGraphic(ivCart);

        //MenuItem editCart = new MenuItem("Edit Cart");
        //MenuItem completeOrder = new MenuItem("Finish Order");
        //shoppingCart.getItems().add(editCart);
        //shoppingCart.getItems().add(completeOrder);

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

            catalogItems = response.getCatalogItems();

            if (catalogItems!=null) {
                total_catalog_items = catalogItems.size();
            }

            salesDisplay();

            catalogDisplay();

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
