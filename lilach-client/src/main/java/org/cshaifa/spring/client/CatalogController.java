package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
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

import javax.xml.catalog.Catalog;


public class CatalogController {

    @FXML    private VBox rootVBox;

    @FXML    private HBox mainHBox;

    @FXML    private Text welcomeText;

    @FXML    private ToolBar toolbar;

    @FXML    private ImageView catalogTitle;

    @FXML    private VBox salesVBox;

    @FXML    private VBox catalogVBox;

    @FXML    private HBox flowerHBox;

    @FXML    private HBox flowerHBox2;

    @FXML    private HBox flowerHBox3;

    @FXML    private Button nextPageButton;

    @FXML    private Button previousPageButton;

    @FXML    private MenuBar menuBar;

    @FXML    private Menu shoppingCart;

    @FXML    private Button filterButton;

    List<CatalogItem> catalogItems = null;

    private int current_page = 1;

    private String selectedType = "all";

    private boolean filter_applied = false;

    @FXML
    void clearCatalogDisplay() {
        flowerHBox.getChildren().clear();
        flowerHBox2.getChildren().clear();
        flowerHBox3.getChildren().clear();
    }

    @FXML
    void displayFlowers(MouseEvent event) {
        selectedType = "flower";
        current_page = 1;
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayBouquets(MouseEvent event) {
        selectedType = "bouquet";
        current_page = 1;
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayPlants(MouseEvent event) {
        selectedType = "plant";
        current_page = 1;
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayOrchids(MouseEvent event) {
        selectedType = "orchid";
        current_page = 1;
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayWine(MouseEvent event) {
        selectedType = "wine";
        current_page = 1;
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displayChocolate(MouseEvent event) {
        selectedType = "chocolate";
        current_page = 1;
        clearCatalogDisplay();
        catalogDisplay();
    }

    @FXML
    void displaySets(MouseEvent event) {
        selectedType = "set";
        current_page = 1;
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
    void filter(MouseEvent event) {
        if (filter_applied) {
            filterButton.setText("Filter");
            filter_applied = false;
        }
        else {
            filterButton.setText("Remove Filter");
            filter_applied = true;
        }
    }

    @FXML
    void refreshCatalog(MouseEvent event) throws IOException {
        selectedType = "all";
        current_page = 1;
        clearCatalogDisplay();
        toolbar.getItems().clear();
        toolbar.getItems().add(welcomeText);
        toolbar.getItems().add(menuBar);
        salesVBox.getChildren().clear();
        shoppingCart.getItems().clear();
        initialize();
    }

    @FXML
    HBox getItemHBox(CatalogItem item) {
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        ImageView iv = null;

        if (item.getImage() != null) {
            try {
                iv = new ImageView(App.getImageFromByteArray(item.getImage()));
                iv.setFitWidth(75);
                iv.setFitHeight(75);
            } catch (IOException e1) {
                // TODO: maybe log the exception somewhere
                e1.printStackTrace();
            }
        }

        Text itemName = new Text(item.getName());
        double price = item.getPrice();
        if (item.isOnSale()) {
            price = new BigDecimal(price * 0.01 * (100 - item.getDiscount())).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        Text itemPrice = new Text(Double.toString(price));
        itemPrice.getStyleClass().add("price-text");
        itemPrice.setFill(Color.GREEN);
        itemPrice.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 16));
        vBox.getChildren().addAll(itemName, itemPrice);
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        Button viewButton = new Button("View Item");
        viewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                App.setCurrentItemDisplayed(item, itemPrice, itemName);
                App.popUpLaunch(viewButton, "PopUp");
            }
        });
        Button addCartButton = new Button();
        viewButton.getStyleClass().add("catalog-item-buttons");
        addCartButton.getStyleClass().add("catalog-item-buttons");
        Image cartImage = new Image(getClass().getResource("images/cart.png").toString());
        ImageView ivCart = new ImageView(cartImage);
        ivCart.setFitHeight(15);
        ivCart.setFitWidth(15);
        addCartButton.setGraphic(ivCart);
        addCartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        if (App.getCurrentUser()==null) {
            buttonBox.getChildren().add(viewButton);
        }
        else if (App.getCurrentUser() instanceof Customer) {
            buttonBox.getChildren().addAll(viewButton, addCartButton);
        }
        else {
            buttonBox.getChildren().add(viewButton);
        }

        vBox.getChildren().add(buttonBox);
        if (iv != null)
            hBox.getChildren().add(iv);
        hBox.getChildren().add(vBox);
        hBox.setPrefSize(200,100);
        hBox.setSpacing(5);
        hBox.getStyleClass().add("catalogitem");

        return hBox;
    }

    @FXML
    void salesDisplay() {
        if (!mainHBox.getChildren().contains(salesVBox)) {
            mainHBox.getChildren().add(1, salesVBox);
        }
        int total_items_on_sale = 0;
        if (catalogItems == null) {
            System.out.println("Catalog is empty :(");
        }
        else {
            for (CatalogItem item : catalogItems) {
                if (item.isOnSale()) {
                    total_items_on_sale++;
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
                    salesVBox.getChildren().add(hBox);
                }
            }
        }

        if (total_items_on_sale == 0) {
            mainHBox.getChildren().remove(salesVBox);
            catalogVBox.setFillWidth(true);
        }
        else {
            catalogVBox.setFillWidth(false);
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
            int count_displayed_items = 0;

            int total_catalog_items = catalogItems.size();

            for (CatalogItem item : catalogItems) {
                if (selectedType=="all" || item.getItemType().equals(selectedType)) {
                    HBox hBox = getItemHBox(item);

                    if (count_displayed_items < 4) {
                        flowerHBox.getChildren().add(hBox);
                    } else if (count_displayed_items < 8) {
                        flowerHBox2.getChildren().add(hBox);
                    } else if (count_displayed_items < 12) {
                        flowerHBox3.getChildren().add(hBox);
                    } else {
                        break;
                    }
                    count_displayed_items++;
                }
            }


            if (current_page == 1) {
                previousPageButton.disableProperty().setValue(true);
            }
            else {
                previousPageButton.disableProperty().setValue(false);
            }
            if (((current_page-1)*12)+count_displayed_items == total_catalog_items) {
                nextPageButton.disableProperty().setValue(true);
            }
            else {
                nextPageButton.disableProperty().setValue(false);
            }
        }

        if (!mainHBox.getChildren().contains(salesVBox)) {
            catalogVBox.setFillWidth(true);
        }
        else {
            catalogVBox.setFillWidth(false);
        }
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

        if (App.getCurrentUser()==null) {
            menuBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    App.setWindowTitle("register");
                    try {
                        App.setContent("customerRegister");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            if (true) {
                //if cart is empty
            }
            else {
                //if cart isn't empty
                MenuItem editCart = new MenuItem("Edit Cart");
                MenuItem completeOrder = new MenuItem("Finish Order");
                shoppingCart.getItems().add(editCart);
                shoppingCart.getItems().add(completeOrder);
            }
        }

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
