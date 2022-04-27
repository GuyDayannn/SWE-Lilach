package org.cshaifa.spring.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class PrimaryController {

    @FXML
    private Button openButton;

    @FXML
    private ImageView lilachLogo;

    @FXML
    private Pane loadingtitle;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Text text;

    @FXML
    void open(ActionEvent event) throws InterruptedException, IOException {
        App.setWindowTitle("Catalog");
        App.setContent("catalog");
    }


    @FXML
    void initialize() {
        Image image = new Image(getClass().getResource("images/LiLachLogo.png").toString());
        lilachLogo.setImage((image));
    }

    @FXML
    void startLilach(MouseEvent event) {

    }

}
