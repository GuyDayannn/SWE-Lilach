package org.cshaifa.spring.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @FXML    private HBox flowerHBox;

    @FXML    private HBox flowerHBox2;

    @FXML    private HBox flowerHBox3;

    @FXML    private HBox flowerHBox4;

    public static List<CatalogItem> catalogItems;

    @FXML    private Button nextPageButton;

    @FXML    private Button previousPageButton;

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
            if (getCatalogTask.getValue() == null) return;
            GetCatalogResponse response = getCatalogTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                System.err.println("Getting catalog failed");
                return;
            }
            catalogItems = response.getCatalogItems();

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
                        App.popUpLaunch(button, "PopUp");
                    }
                });
                vBox.getChildren().add(button);
                hBox.getChildren().addAll(iv, vBox);
                hBox.setPrefSize(200,100);
                hBox.setSpacing(5);
                hBox.setStyle("-fx-padding: 5;" + "-fx-border-style: solid inside;"
                              + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                              + "-fx-border-radius: 5;" + "-fx-border-color: green;");

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

            App.hideLoading();
        });

        getCatalogTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getCatalogTask.getException().printStackTrace();
            App.hideLoading();
        });

        App.showLoading(rootVBox, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(getCatalogTask).start();


    }


}
