package org.cshaifa.spring.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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


    //FXML objects
    @FXML    private VBox rootVBox;
    @FXML    private HBox mainHBox;
    @FXML    private Text welcomeText;
    @FXML    private ToolBar toolbar;
    @FXML    private ImageView catalogTitle;
    @FXML    private VBox salesVBox;
    @FXML    private VBox catalogVBox;
    @FXML    private MenuBar menuBar;
    @FXML    private Menu shoppingCart;
    @FXML    private ComboBox<String> selectedTypeComboBox;
    @FXML    private ComboBox<String> selectedColorComboBox;
    @FXML    private ComboBox<String> selectedSizeComboBox;
    @FXML    private Slider loPrice;
    @FXML    private Slider hiPrice;

    //Variables
    List<CatalogItem> catalogItems = null;
    private boolean filter_applied = false;

    @FXML
    void displayType(MouseEvent event) {
        Text source = (Text)event.getSource();
        filter_applied = true;
        selectedTypeComboBox.valueProperty().setValue(source.getId());
        catalogVBox.getChildren().clear();
        catalogDisplay();
    }

    @FXML
    void filter() {
        filter_applied = true;
        catalogVBox.getChildren().clear();
        catalogDisplay();
    }

    @FXML
    void clearFilter(MouseEvent event) {
        filter_applied = false;
        selectedTypeComboBox.valueProperty().setValue(null);
        selectedColorComboBox.valueProperty().setValue(null);
        selectedSizeComboBox.valueProperty().setValue(null);
        loPrice.valueProperty().setValue(0);
        hiPrice.valueProperty().setValue(500);
        catalogVBox.getChildren().clear();
        catalogDisplay();
    }

    @FXML
    void refreshCatalog(MouseEvent event) throws IOException {
        filter_applied = false;
        selectedTypeComboBox.valueProperty().setValue(null);
        selectedColorComboBox.valueProperty().setValue(null);
        selectedSizeComboBox.valueProperty().setValue(null);
        loPrice.valueProperty().setValue(0);
        hiPrice.valueProperty().setValue(500);
        catalogVBox.getChildren().clear();
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
        Text itemPrice = new Text(String.format("%.2f",price));
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
    boolean isInFilter(CatalogItem item) {
        String filterType = null;
        if (selectedTypeComboBox.valueProperty().getValue()!=null) {
            filterType = selectedTypeComboBox.valueProperty().getValue().toString().toLowerCase();
        }
        String filterColor = null;
        if (selectedColorComboBox.valueProperty().getValue()!=null) {
            filterColor = selectedColorComboBox.valueProperty().getValue().toString().toLowerCase();
        }
        String filterSize = null;
        if (selectedSizeComboBox.valueProperty().getValue()!=null) {
            filterSize = selectedSizeComboBox.valueProperty().getValue().toString().toLowerCase();
        }
        double lo = loPrice.getValue();
        double hi = hiPrice.getValue();

        if (!item.getItemType().equals(filterType) && filterType!=null) {
            return false;
        }
        if (!item.getItemColor().equals(filterColor) && filterColor!=null) {
            return false;
        }
        if (!item.getSize().equals(filterSize) && filterSize!=null) {
            return false;
        }
        if ( !(item.getPrice()>=lo && item.getPrice()<=hi) ) {
            return false;
        }
        return true;
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
                    VBox vBox = new VBox();
                    vBox.setAlignment(Pos.CENTER);
                    ImageView iv = null;

                    if (item.getImage() != null) {
                        try {
                            iv = new ImageView(App.getImageFromByteArray(item.getImage()));
                            iv.setFitWidth(120);
                            iv.setFitHeight(120);
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
                    Text newItemPrice = new Text(String.format("%.2f",(newPrice)));
                    newItemPrice.setFill(Color.RED);
                    newItemPrice.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    textBox.getChildren().addAll(itemPrice, newItemPrice);
                    textBox.setAlignment(Pos.CENTER);
                    Button button = new Button("View Item");
                    button.getStyleClass().add("sale-button");
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            App.setCurrentItemDisplayed(item, itemPrice, itemName);
                            App.popUpLaunch(button, "PopUp");
                        }
                    });

                    vBox.getChildren().addAll(itemName, iv, textBox, button);
                    vBox.setSpacing(5);
                    vBox.getStyleClass().add("saleitem");
                    salesVBox.getChildren().add(vBox);
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
            catalogVBox.getChildren().add(new Text("Catalog is empty"));
            System.out.println("Catalog is empty :(");
        }
        else {
            int count_displayed_items = 0;

            HBox newHBox = new HBox();
            newHBox.setAlignment(Pos.CENTER_LEFT);
            newHBox.setMinWidth(840);
            newHBox.setPrefHeight(126);
            newHBox.setPrefWidth(1040);
            newHBox.setSpacing(5);
            HBox currentRow = newHBox;
            catalogVBox.getChildren().add(currentRow);

            for (CatalogItem item : catalogItems) {
                if (!filter_applied || isInFilter(item)) {
                    HBox hBox = getItemHBox(item);

                    if (currentRow.getChildren().size() <4) {
                        currentRow.getChildren().add(hBox);
                        count_displayed_items++;
                    }
                    else if (currentRow.getChildren().size() == 4) {
                        //Create new row
                        newHBox = new HBox();
                        newHBox.setAlignment(Pos.CENTER_LEFT);
                        newHBox.setMinWidth(840);
                        newHBox.setPrefHeight(126);
                        newHBox.setPrefWidth(1040);
                        newHBox.setSpacing(5);
                        currentRow = newHBox;

                        //Add it to catalogVBox
                        catalogVBox.getChildren().add(currentRow);

                        //Add new item hBox
                        currentRow.getChildren().add(hBox);
                        count_displayed_items++;
                    }
                }
            }

            if (!mainHBox.getChildren().contains(salesVBox)) {
                catalogVBox.setFillWidth(true);
            } else {
                catalogVBox.setFillWidth(false);
            }
        }
    }

    @FXML
    void initialize() throws IOException {
        //Load Lilach Logo
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        catalogTitle.setImage((image));

        //Load Toolbar
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

        //Init filter values
        ObservableList<String> typeOptions = FXCollections.observableArrayList("Flower", "Bouquet", "Plant", "Orchid", "Wine", "Chocolate", "Set");
        selectedTypeComboBox.setItems(typeOptions);
        selectedTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                filter();
            }
        });

        ObservableList<String> colorOptions = FXCollections.observableArrayList("Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Pink", "White", "Black");
        selectedColorComboBox.setItems(colorOptions);
        selectedColorComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                filter();
            }
        });
        ObservableList<String> sizeOptions = FXCollections.observableArrayList("Small", "Medium", "Large");
        selectedSizeComboBox.setItems(sizeOptions);
        selectedSizeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                filter();
            }
        });
        loPrice.minProperty().set(50);
        loPrice.maxProperty().set(500);
        loPrice.setMajorTickUnit(150);
        loPrice.setSnapToTicks(true);
        loPrice.setShowTickMarks(true);
        loPrice.setShowTickLabels(true);
        loPrice.setValue(100);
        loPrice.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (! isChanging) {}
        });

        loPrice.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (! hiPrice.isValueChanging()) {
                filter();
            }
        });
        hiPrice.minProperty().set(50);
        hiPrice.maxProperty().set(500);
        hiPrice.setMajorTickUnit(150);
        hiPrice.setSnapToTicks(true);
        hiPrice.setShowTickMarks(true);
        hiPrice.setShowTickLabels(true);
        hiPrice.setValue(500);
        hiPrice.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (! isChanging) {}
        });

        hiPrice.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (! hiPrice.isValueChanging()) {
                filter();
            }
        });

    }


}
