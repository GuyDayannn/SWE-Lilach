package org.cshaifa.spring.client;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.responses.CreateItemResponse;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.utils.Constants;
import org.cshaifa.spring.utils.ImageUtils;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class CatalogController {

    // FXML objects
    @FXML
    private VBox rootVBox;
    @FXML
    private HBox mainHBox;
    @FXML
    private Text welcomeText;
    @FXML
    private ToolBar toolbar;
    @FXML
    private ImageView catalogTitle;
    @FXML
    private VBox salesVBox;
    @FXML
    private VBox catalogVBox;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu shoppingCart;
    @FXML
    private ComboBox<String> selectedTypeComboBox;
    @FXML
    private ComboBox<String> selectedColorComboBox;
    @FXML
    private ComboBox<String> selectedSizeComboBox;
    @FXML
    private Slider loPrice;
    @FXML
    private Slider hiPrice;
    @FXML
    private TilePane tilePane;

    // Variables
    List<CatalogItem> catalogItems = null;
    private boolean filter_applied = false;
    private ObservableList<HBox> itemCells = null;

    @FXML
    void displayType(MouseEvent event) {
        Text source = (Text) event.getSource();
        filter_applied = true;
        selectedTypeComboBox.valueProperty().setValue(source.getId());
        refreshList();
    }

    void refreshList() {
        tilePane.getChildren().setAll(itemCells.filtered(s -> !filter_applied || isInFilter(catalogItems.get(itemCells.indexOf(s)))));
    }

    void listDisplay() {
        itemCells = FXCollections.observableArrayList(catalogItems.stream().map(item -> getItemHBox(item)).toList());
        refreshList();
    }

    @FXML
    void filter() {
        filter_applied = true;
        refreshList();
    }

    void clearFilters() {
        filter_applied = false;
        selectedTypeComboBox.valueProperty().setValue(null);
        selectedSizeComboBox.valueProperty().setValue(null);
        selectedColorComboBox.valueProperty().setValue(null);
        selectedTypeComboBox.getStyleClass().remove("combo-selected");
        selectedColorComboBox.getStyleClass().remove("combo-selected");
        selectedSizeComboBox.getStyleClass().remove("combo-selected");
        loPrice.valueProperty().setValue(0);
        hiPrice.valueProperty().setValue(500);
    }

    @FXML
    void clearFilter(ActionEvent event) {
        clearFilters();
        refreshList();
    }

    @FXML
    void refreshCatalog(MouseEvent event) throws IOException {
        clearFilters();
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
            price = new BigDecimal(price * 0.01 * (100 - item.getDiscount())).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        Text itemPrice = new Text(String.format("%.2f", price));
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
        if (App.getCurrentUser() == null) {
            buttonBox.getChildren().add(viewButton);
        } else if (App.getCurrentUser() instanceof Customer) {
            buttonBox.getChildren().addAll(viewButton, addCartButton);
        } else {
            buttonBox.getChildren().add(viewButton);
        }

        vBox.getChildren().add(buttonBox);
        if (iv != null)
            hBox.getChildren().add(iv);
        hBox.getChildren().add(vBox);
        hBox.setPrefSize(200, 100);
        hBox.setSpacing(5);
        hBox.getStyleClass().add("catalogitem");

        return hBox;
    }

    @FXML
    boolean isInFilter(CatalogItem item) {
        String filterType = null;
        if (selectedTypeComboBox.valueProperty().getValue() != null) {
            filterType = selectedTypeComboBox.valueProperty().getValue().toString().toLowerCase();
        }
        String filterColor = null;
        if (selectedColorComboBox.valueProperty().getValue() != null) {
            filterColor = selectedColorComboBox.valueProperty().getValue().toString().toLowerCase();
        }
        String filterSize = null;
        if (selectedSizeComboBox.valueProperty().getValue() != null) {
            filterSize = selectedSizeComboBox.valueProperty().getValue().toString().toLowerCase();
        }
        double lo = loPrice.getValue();
        double hi = hiPrice.getValue();

        if (!item.getItemType().equals(filterType) && filterType != null) {
            return false;
        }
        if (!item.getItemColor().equals(filterColor) && filterColor != null) {
            return false;
        }
        if (!item.getSize().equals(filterSize) && filterSize != null) {
            return false;
        }
        if (!(item.getPrice() >= lo && item.getPrice() <= hi)) {
            return false;
        }
        return true;
    }

    @FXML
    void onAddItem(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        ExtensionFilter filter = new ExtensionFilter("JPG files (*.jpg)", "*.jpeg", "*.jpg", "*.JPG");
        chooser.getExtensionFilters().add(filter);
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile == null)
            return;

        Task<CreateItemResponse> createItemTask = App.createTimedTask(
                () -> ClientHandler.createItem("Example", 400, false, 0, "large", "flower", "white",
                        ImageUtils.getByteArrayFromURI(selectedFile.toURI())),
                Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        createItemTask.setOnSucceeded(e -> {
            if (createItemTask.getValue() == null || !createItemTask.getValue().isSuccessful()) {
                App.hideLoading();
                return;
            }

            CreateItemResponse response = createItemTask.getValue();
            catalogItems.add(response.getItem());

            itemCells.add(getItemHBox(response.getItem()));
            refreshList();
            App.hideLoading();
        });

        createItemTask.setOnFailed(e -> {
            createItemTask.getException().printStackTrace();
            App.hideLoading();
        });

        App.showLoading(rootVBox, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(createItemTask).start();

    }

    @FXML
    void salesDisplay() {
        if (!mainHBox.getChildren().contains(salesVBox)) {
            mainHBox.getChildren().add(1, salesVBox);
        }
        int total_items_on_sale = 0;
        if (catalogItems == null) {
            System.out.println("Catalog is empty :(");
        } else {
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

                    double newPrice = new BigDecimal(item.getPrice() * 0.01 * (100 - item.getDiscount()))
                            .setScale(2, RoundingMode.HALF_UP).doubleValue();
                    Text newItemPrice = new Text(String.format("%.2f", (newPrice)));
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
        } else {
            catalogVBox.setFillWidth(false);
        }
    }

    @FXML
    void initialize() throws IOException {
        // Load Lilach Logo
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        catalogTitle.setImage((image));

        // Load Toolbar
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
                if (App.getCurrentUser().getClass().toString().equals("Customer")) {
                    App.setWindowTitle("Customer Profile");
                    try {
                        App.setContent("customerProfile");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    App.setWindowTitle("Employee Profile");
                    try {
                        App.setContent("employeeProfile");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (App.getCurrentUser() == null) {
            NewOrderButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
        } else {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
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

        if (App.getCurrentUser() == null) {
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
        } else {
            if (true) {
                // if cart is empty
            } else {
                // if cart isn't empty
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

            // catalogDisplay();
            listDisplay();

            App.hideLoading();
        });

        getCatalogTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getCatalogTask.getException().printStackTrace();
            App.hideLoading();
        });

        App.showLoading(rootVBox, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(getCatalogTask).start();

        // Init filter values
        ObservableList<String> typeOptions = FXCollections.observableArrayList("Flower", "Bouquet", "Plant", "Orchid",
                "Wine", "Chocolate", "Set");
        selectedTypeComboBox.setItems(typeOptions);
        selectedTypeComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> filter());
        selectedTypeComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (empty || item == null) {
                    setText("Type");
                    selectedTypeComboBox.getStyleClass().remove("combo-selected");
                } else {
                    setText(item);
                    selectedTypeComboBox.getStyleClass().add("combo-selected");
                }
            }
        });

        ObservableList<String> colorOptions = FXCollections.observableArrayList("Red", "Orange", "Yellow", "Green",
                "Blue", "Purple", "Pink", "White", "Black");
        selectedColorComboBox.setItems(colorOptions);
        selectedColorComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> filter());
        selectedColorComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (empty || item == null) {
                    setText("Type");
                    selectedColorComboBox.getStyleClass().remove("combo-selected");
                } else {
                    setText(item);
                    selectedColorComboBox.getStyleClass().add("combo-selected");
                }
            }
        });

        ObservableList<String> sizeOptions = FXCollections.observableArrayList("Small", "Medium", "Large");
        selectedSizeComboBox.setItems(sizeOptions);
        selectedSizeComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> {
            if (t1 != null) {
                filter();
            }
        });
        selectedSizeComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (empty || item == null) {
                    setText("Type");
                    selectedSizeComboBox.getStyleClass().remove("combo-selected");
                } else {
                    selectedSizeComboBox.getStyleClass().add("combo-selected");
                    setText(item);
                }
            }
        });

        loPrice.minProperty().set(50);
        loPrice.maxProperty().set(500);
        loPrice.setMajorTickUnit(150);
        loPrice.setSnapToTicks(true);
        loPrice.setShowTickMarks(true);
        loPrice.setShowTickLabels(true);
        loPrice.setValue(100);

        loPrice.valueProperty().addListener((obs, oldValue, newValue) -> {
            filter();
        });
        hiPrice.minProperty().set(50);
        hiPrice.maxProperty().set(500);
        hiPrice.setMajorTickUnit(150);
        hiPrice.setSnapToTicks(true);
        hiPrice.setShowTickMarks(true);
        hiPrice.setShowTickLabels(true);
        hiPrice.setValue(500);

        hiPrice.valueProperty().addListener((obs, oldValue, newValue) -> {
            filter();
        });

    }

}
