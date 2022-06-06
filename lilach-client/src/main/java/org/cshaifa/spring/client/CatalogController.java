package org.cshaifa.spring.client;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Employee;
import org.cshaifa.spring.entities.responses.CreateItemResponse;
import org.cshaifa.spring.entities.responses.DeleteItemResponse;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.NotifyResponse;
import org.cshaifa.spring.entities.responses.NotifyUpdateResponse;
import org.cshaifa.spring.utils.Constants;
import org.cshaifa.spring.utils.ImageUtils;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private Pane spacer;
    @FXML
    private ImageView catalogTitle;
    @FXML
    private VBox salesVBox;
    @FXML
    private VBox catalogVBox;
    @FXML
    private Button shoppingCart;
    @FXML
    private Button addItemButton;
    @FXML
    private Button signInButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button contactButton;
    @FXML
    private Button viewProfileButton;
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
    @FXML
    private Label updateNotification;

    private final String PRODUCT_UPDATED_NOTIFICATION = "Product Updated";
    private final String PRODUCT_DELETED_NOTIFICATION = "Product Deleted";

    // Variables
    List<CatalogItem> catalogItems = null;
    private boolean filter_applied = false;
    private ObservableList<HBox> itemCells = null;

    @FXML
    void displayType(MouseEvent event) {
        Text source = (Text) event.getSource();
        filter_applied = true;
        selectedTypeComboBox.valueProperty().setValue(source.getId());
        selectedSizeComboBox.valueProperty().setValue(null);
        selectedColorComboBox.valueProperty().setValue(null);
        refreshList();
    }

    void refreshList() {
        tilePane.getChildren()
                .setAll(itemCells.filtered(s -> !filter_applied || isInFilter(catalogItems.get(itemCells.indexOf(s)))));
    }

    void listDisplay() {
        // itemCells = FXCollections.observableArrayList(catalogItems.stream().map(item -> getItemHBox(item)).toList());
        itemCells = FXCollections.observableArrayList();
        for (int i = 0; i < catalogItems.size(); i++) {
            itemCells.add(getItemHBox(catalogItems.get(i)));
        }
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
        loPrice.valueProperty().setValue(0);
        hiPrice.valueProperty().setValue(500);
    }

    @FXML
    void clearFilter(ActionEvent event) {
        clearFilters();
        refreshList();
    }

    @FXML
    private void refreshCatalog(ActionEvent event) {
        clearFilters();
        salesVBox.getChildren().clear();
        initialize();
    }

    @FXML
    private void contact(ActionEvent event) {
        // TODO: do something here?
    }

    @FXML
    private void viewProfile(ActionEvent event) {
        if (App.getCurrentUser() instanceof Customer) {

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
        Button viewButton = new Button("View");
        viewButton.getStyleClass().add("catalog-item-buttons");
        viewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                App.setCurrentItemDisplayed(item, itemPrice, itemName);
                App.popUpLaunch(viewButton, "PopUp");
            }
        });
        Button addCartButton = new Button();
        addCartButton.getStyleClass().add("catalog-item-buttons");
        Image cartImage = new Image(getClass().getResource("images/cart.png").toString());
        ImageView ivCart = new ImageView(cartImage);
        ivCart.setFitHeight(15);
        ivCart.setFitWidth(15);
        addCartButton.setGraphic(ivCart);
        addCartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (App.getCart().containsKey(item)) {
                    Integer quantity = App.getCart().get(item);
                    App.getCart().put(item, ++quantity);
                } else {
                    App.getCart().put(item, 1);
                }
            }
        });
        Button removeItemButton = new Button();
        removeItemButton.getStyleClass().add("catalog-item-buttons");
        Image removeImage = new Image(getClass().getResource("images/remove.png").toString());
        ImageView ivRemove = new ImageView(removeImage);
        ivRemove.setFitHeight(15);
        ivRemove.setFitWidth(15);
        removeItemButton.setGraphic(ivRemove);
        removeItemButton.setOnAction(event -> {
            Task<DeleteItemResponse> deleteItemTask = App.createTimedTask(() -> {
                return ClientHandler.deleteItem((Employee) App.getCurrentUser(), item);
            }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

            deleteItemTask.setOnSucceeded(e -> {
                DeleteItemResponse response = deleteItemTask.getValue();
                if (!response.isSuccessful()) {
                    // TODO: maybe log the specific exception somewhere
                    App.hideLoading();
                    System.err.println("Deleting item failed");
                    return;
                }

                // TODO: update list - delete item
                int itemIndex = catalogItems.indexOf(item);
                if (itemIndex != -1) {
                    itemCells.remove(itemIndex);
                    catalogItems.remove(item);
                    refreshList();
                }
                App.hideLoading();
            });

            deleteItemTask.setOnFailed(e -> {
                App.hideLoading();
                // TODO: maybe properly log it somewhere
                deleteItemTask.getException().printStackTrace();
            });

            Stage rootStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            App.showLoading(rootVBox, rootStage, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
            new Thread(deleteItemTask).start();

        });
        if (App.getCurrentUser() == null) {
            buttonBox.getChildren().add(viewButton);
        } else if (App.getCurrentUser() instanceof Customer) {
            buttonBox.getChildren().addAll(viewButton, addCartButton);
        } else {
            buttonBox.getChildren().addAll(viewButton, removeItemButton);
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
    private void signIn(ActionEvent event) {
        App.setWindowTitle("login");
        try {
            App.setContent("customerLogin");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void register(ActionEvent event) {
        App.setWindowTitle("register");
        try {
            App.setContent("customerRegister");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refresh(ActionEvent event) {

    }

    @FXML
    void addItem(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        ExtensionFilter filter = new ExtensionFilter("JPG files (*.jpg)", "*.jpeg", "*.jpg", "*.JPG");
        chooser.getExtensionFilters().add(filter);
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile == null)
            return;

        Task<CreateItemResponse> createItemTask = App.createTimedTask(
                () -> ClientHandler.createItem("Example", 400, new HashMap<>(), false, 0, "large", "flower", "white",
                        true, ImageUtils.getByteArrayFromURI(selectedFile.toURI())),
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
    void openCart(ActionEvent event) {
        App.popUpLaunch(shoppingCart, "shoppingCart");
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

                    HBox buttonBox = new HBox();
                    buttonBox.setAlignment(Pos.CENTER);
                    Button viewButton = new Button("View Item");
                    viewButton.getStyleClass().add("sale-button");
                    viewButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            App.setCurrentItemDisplayed(item, itemPrice, itemName);
                            App.popUpLaunch(viewButton, "PopUp");
                        }
                    });

                    Button addCartButton = new Button();
                    addCartButton.getStyleClass().add("sale-button");
                    Image cartImage = new Image(getClass().getResource("images/cart.png").toString());
                    ImageView ivCart = new ImageView(cartImage);
                    ivCart.setFitHeight(15);
                    ivCart.setFitWidth(15);
                    addCartButton.setGraphic(ivCart);
                    addCartButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (App.getCart().containsKey(item)) {
                                Integer quantity = App.getCart().get(item);
                                App.getCart().put(item, ++quantity);
                            } else {
                                App.getCart().put(item, 1);
                            }
                        }
                    });
                    if (App.getCurrentUser() == null) {
                        buttonBox.getChildren().add(viewButton);
                    } else if (App.getCurrentUser() instanceof Customer) {
                        buttonBox.getChildren().addAll(viewButton, addCartButton);
                    } else {
                        buttonBox.getChildren().add(viewButton);
                    }

                    vBox.getChildren().addAll(itemName, iv, textBox, buttonBox);
                    vBox.setSpacing(5);
                    vBox.getStyleClass().add("saleitem");
                    salesVBox.getChildren().add(vBox);
                }
            }
        }

        if (total_items_on_sale == 0) {
            mainHBox.getChildren().remove(salesVBox);
            catalogVBox.setFillWidth(true);
            tilePane.setPrefWidth(1040);
        } else {
            catalogVBox.setFillWidth(false);
            tilePane.setPrefWidth(840);
        }
    }

    @FXML
    void initialize() {
        if (App.getCurrentUser() == null) {
            welcomeText.setText("");
            toolbar.getItems().remove(viewProfileButton);
            toolbar.getItems().remove(shoppingCart);
            toolbar.getItems().remove(addItemButton);
        } else if (App.getCurrentUser() instanceof Customer) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
            toolbar.getItems().remove(signInButton);
            toolbar.getItems().remove(registerButton);
            toolbar.getItems().remove(addItemButton);
        } else {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
            toolbar.getItems().remove(shoppingCart);
            toolbar.getItems().remove(signInButton);
            toolbar.getItems().remove(registerButton);
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

            App.scheduler.scheduleAtFixedRate(() -> {
                try {
                    NotifyResponse notifyResponse = ClientHandler.waitForUpdateFromServer();
                    if (notifyResponse == null)
                        return;

                    boolean sameUser = App.getCurrentUser() instanceof Employee
                            && notifyResponse.getSendingEmployee().getId() == App.getCurrentUser().getId();

                    if (!sameUser) {
                        if (notifyResponse instanceof NotifyUpdateResponse) {
                            NotifyUpdateResponse notifyUpdateResponse = (NotifyUpdateResponse) notifyResponse;

                            for (int i = 0; i < catalogItems.size(); i++) {
                                if (catalogItems.get(i).getId() == notifyUpdateResponse.getToUpdate().getId()) {
                                    System.out.println("Found id at index " + i + "!");
                                }
                            }
                            int itemIndex = catalogItems.indexOf(catalogItems.stream().filter(
                                    catalogItem -> catalogItem.getId() == notifyUpdateResponse.getToUpdate().getId())
                                    .findFirst().get());
                            System.out.println("updating " + catalogItems.get(itemIndex).getName());
                            catalogItems.set(itemIndex, notifyUpdateResponse.getToUpdate());
                            System.out.println("Size before: " + itemCells.size());
                            itemCells.set(itemIndex, getItemHBox(catalogItems.get(itemIndex)));
                            System.out.println("Size after: " + itemCells.size());
                        }
                    }

                    Platform.runLater(() -> {
                        if (!sameUser)
                            refreshList();
                        updateNotification.setText(PRODUCT_UPDATED_NOTIFICATION);
                        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1),
                                updateNotification);
                        updateNotification.setVisible(true);
                        fadeTransition.setFromValue(0.0);
                        fadeTransition.setToValue(1.0);
                        fadeTransition.setCycleCount(3);
                        fadeTransition.setOnFinished(event -> updateNotification.setVisible(false));
                        fadeTransition.play();
                    });

                } catch (Exception error) {
                    error.printStackTrace();
                    return;
                }
            }, 0, Constants.UPDATE_INTERVAL, TimeUnit.SECONDS);

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
        selectedTypeComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> {
            filter();

            if (t1 == "Chocolate" || t1 == "Set") {
                selectedColorComboBox.setDisable(true);
            } else {
                selectedColorComboBox.setDisable(false);
            }
        });
        selectedTypeComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (empty || item == null) {
                    setText("Type");
                } else {
                    setText(item);
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
                } else {
                    setText(item);
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
                } else {
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
