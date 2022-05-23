package org.cshaifa.spring.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.SubscriptionType;
import org.cshaifa.spring.entities.responses.GetStoresResponse;
import org.cshaifa.spring.entities.responses.RegisterResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class RegistrationController {
    @FXML
    private Button cancelBtn;
    @FXML
    private Button registerBtn;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField fullNameTxtField;

    @FXML
    private TextField usernameTxtField;

    @FXML
    private TextField emailTxtField;

    @FXML
    private TextField pwdTxtField;

    @FXML
    private TextField pwdConfTxtField;

    @FXML
    private Label invalid_register_text;

    @FXML
    private ComboBox<String> subscriptionSelector;

    @FXML
    private ComboBox<String> storeSelector;

    private List<Store> stores = null;

    @FXML
    void initialize() {
        subscriptionSelector.getItems().addAll("Store Subscription", "Chain Subscription", "Yearly Subscription");

        Task<GetStoresResponse> getStoresTask = App.createTimedTask(ClientHandler::getStores, Constants.REQUEST_TIMEOUT,
                TimeUnit.SECONDS);
        getStoresTask.setOnSucceeded(e -> {
            if (getStoresTask.getValue() == null || !getStoresTask.getValue().isSuccessful()) {
                // TODO: notify on failure
                App.hideLoading();
                return;
            }

            stores = getStoresTask.getValue().getStores();
            storeSelector.getItems().addAll(stores.stream().map(Store::getName).toList());
            App.hideLoading();
        });

        getStoresTask.setOnFailed(e -> {
            // TODO: notify on failure
            getStoresTask.getException().printStackTrace();
            App.hideLoading();
        });

        App.showLoading(rootPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(getStoresTask).start();
    }

    @FXML
    void onSubscriptionSelect(ActionEvent event) {
        int subscriptionIndex = subscriptionSelector.getSelectionModel().getSelectedIndex();
        if (subscriptionIndex == SubscriptionType.STORE.ordinal()) {
            storeSelector.setDisable(false);
        } else {
            storeSelector.setDisable(true);
        }
    }

    @FXML
    void cancelBtnOnAction(ActionEvent event) throws IOException {
        App.setWindowTitle("Primary");
        App.setContent("primary");
    }

    @FXML
    void onRegisterButton(ActionEvent event) {
        if (!fullNameTxtField.getText().isBlank() && !usernameTxtField.getText().isBlank()
                && !emailTxtField.getText().isBlank() && !pwdTxtField.getText().isBlank()) {
            if (!pwdTxtField.getText().equals(pwdConfTxtField.getText())) {
                invalid_register_text.setText("Passwords Does not match");
                invalid_register_text.setTextFill(Color.RED);
            } else {
                validateRegister();
            }
        } else {
            loginMessageLabel.setText("Please enter every detail");
        }
    }

    public void validateRegister() {

        Task<RegisterResponse> registerTask = App.createTimedTask(() -> {
            List<Store> registredStores = new ArrayList<>();
            List<Complaint> complaintList = new ArrayList<>();
            int subscriptionIndex = subscriptionSelector.getSelectionModel().getSelectedIndex();

            if (subscriptionIndex == SubscriptionType.STORE.ordinal())
                registredStores.add(stores.get(storeSelector.getSelectionModel().getSelectedIndex()));
            else
                registredStores = stores;
            return ClientHandler.registerCustomer(fullNameTxtField.getText().strip(),
                    usernameTxtField.getText().strip(),
                    emailTxtField.getText().strip(), pwdTxtField.getText().strip(),
                    registredStores, SubscriptionType.values()[subscriptionIndex], complaintList);
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        registerTask.setOnSucceeded(e -> {
            if (registerTask.getValue() == null) {
                invalid_register_text.setText("Register Failed");
                invalid_register_text.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            RegisterResponse registerResponse = registerTask.getValue();
            if (!registerResponse.isSuccessful()) {
                invalid_register_text.setText(registerResponse.getMessage());
                invalid_register_text.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            App.setCurrentUser(registerResponse.getUser());
            App.hideLoading();
            App.setWindowTitle("Catalog");
            try {
                App.setContent("catalog");
            } catch (IOException e1) {
                // shouldn't happen
                e1.printStackTrace();
                App.setWindowTitle("Login");
            }
        });

        registerTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            registerTask.getException().printStackTrace();
            App.hideLoading();
        });

        App.showLoading(rootPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(registerTask).start();
    }
}
