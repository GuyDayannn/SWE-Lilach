package org.cshaifa.spring.client;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.cshaifa.spring.entities.*;
import org.cshaifa.spring.entities.responses.*;
import org.cshaifa.spring.utils.Constants;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmployeeProfileController {

    //FXML objects
    @FXML
    private Button editCatalogButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button catalogButton;
    @FXML
    private TableView<CatalogItem> catalogTable;
    @FXML
    private Button closeComplaintButton;
    @FXML
    private TextField compensationamount;
    @FXML
    private ComboBox<Long> complaintComboBox;
    @FXML
    private TextField complaintStatus;
    @FXML
    private TextArea complaintdescription;
    @FXML
    private TextField complaintresponse;
    @FXML
    private TextField discountAmount;
    @FXML
    private ComboBox<Text> itemComboBox;
    @FXML
    private CheckBox selectAllCheckbox;
    @FXML
    private TableColumn<CatalogItem, Double> discountColumn;
    @FXML
    private TableColumn<CatalogItem, Long> idColumn;
    @FXML
    private TableColumn<CatalogItem, String> itemNameColumn;
    @FXML
    private TableColumn<CatalogItem, Double> itemPriceColumn;
    @FXML
    private TableColumn<CatalogItem, Boolean> onsaleColumn;
    @FXML
    private TableColumn<CatalogItem, Boolean> selectColumn;
    @FXML
    private Button updateSalesButton;
    @FXML
    private Button viewComplaint;
    @FXML
    private Text welcomeText;
    @FXML
    private Label updated_complaint_text;
    @FXML
    private TitledPane paneStoreReport;
    @FXML
    private TitledPane paneChainReport;
    @FXML
    private TitledPane handleUsersPane;
    @FXML
    private ComboBox<String> storeComboBox;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    // Variables
    private List<Complaint> complaintList;
    private List<Store> storesList;

    private LocalDate reportStartDate;
    private LocalDate reportEndDate;
    private ReportType reportType;
    private Store reportStore;



    @FXML
    void cancelUpdate(ActionEvent event) {
        System.out.println("You clicked Cancel");
        discountAmount.setText("");
        //cancelButton.getScene().getWindow().s
        //App.setContent("employee profile");
    }

    @FXML
    void showCatalogIds(ActionEvent event) {
        long selectedId = Long.parseLong(itemComboBox.getValue().toString());

    }

    @FXML
    void openComplaint(ActionEvent event) { //on view complaint click
        long complaintID  = complaintComboBox.getValue(); //getting selected complaint ID

        Complaint complaint = complaintList.get((int) complaintID);

        complaintdescription.setText(complaint.getComplaintDescription());
        String complaintStatusStr = "";
        if (complaint.getIsComplaintOpen()) {
            complaintStatusStr = "Open";
        } else {
            complaintStatusStr = "Closed";
        }

        complaintStatus.setText(complaintStatusStr);
        compensationamount.setText((Double.toString(complaint.getCompensationAmount())));
        complaintresponse.setText((complaint.getComplaintResponse()));
    }

    @FXML
    void closeComplaint(ActionEvent event) { //TODO: fix it closes the correct complaint
        long complaintID  = complaintComboBox.getValue(); //getting selected complaint ID
        Complaint updatedComplaint = complaintList.get((int) complaintID);
        updatedComplaint.setComplaintResponse(complaintresponse.getText());
        updatedComplaint.setComplaintOpen(false);
        double compensationNum  = Double.parseDouble(compensationamount.getText());
        updatedComplaint.setCompensationAmount(compensationNum);

        complaintComboBox.valueProperty().setValue(null);
        complaintresponse.setText("");
        compensationamount.setText("");
        complaintStatus.setText("");
        complaintdescription.setText("");

        Task<UpdateComplaintResponse> updateComplaintTask = App.createTimedTask(() -> {
            return ClientHandler.updateComplaint(updatedComplaint);
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        updateComplaintTask.setOnSucceeded(e -> {
            UpdateComplaintResponse response = updateComplaintTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                System.err.println("Updating Complaint failed");
                updated_complaint_text.setText("Failed to close complaint");
                updated_complaint_text.setTextFill(Color.RED);
                return;
            }
            updated_complaint_text.setText("You have successfully closed the complaint");
            updated_complaint_text.setTextFill(Color.GREEN);
        });

        updateComplaintTask.setOnFailed(e -> {
            // TODO: maybe properly log it somewhere
            updateComplaintTask.getException().printStackTrace();
            updated_complaint_text.setText("Failed to close complaint");
            updated_complaint_text.setTextFill(Color.RED);
        });
        new Thread(updateComplaintTask).start();
    }

    @FXML
    void exitProfile(ActionEvent event) throws IOException {
        App.logoutUser();
        App.setWindowTitle("primary");
        App.setContent("primary");
    }

    @FXML
    void goCatalog(ActionEvent event) throws IOException {
        App.setWindowTitle("Edit Catalog");
        App.setContent("catalog");
    }

    @FXML
    void selectStore(ActionEvent event) {
        String storeName = storeComboBox.getValue();
        for(Store store: storesList){
            if(store.getName()==storeName){
                reportStore = store;
                break;
            }
        }
    }

    @FXML
    void selectReportType(ActionEvent event) {
        String report = reportTypeComboBox.getValue();
        if(report == "Orders")
            reportType = ReportType.ORDERS;
        else if (report == "Revenue")
            reportType = ReportType.REVENUE;
        else if(report == "Complaints")
            reportType = ReportType.COMPLAINTS;

    }

    @FXML
    void setStartDate(ActionEvent event) {
        reportStartDate = startDatePicker.getValue();
//        Date startDate = (Date) event.getSource();
//        reportStartDate = LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
    }

    @FXML
    void setEndDate(ActionEvent event) {
//        Date endDate =(Date) event.getSource();
//        reportEndDate = LocalDate.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        reportEndDate = endDatePicker.getValue();
    }

    @FXML
    void generateReport(ActionEvent event) {
        Report report = new Report(reportType, reportStore, reportStartDate, reportEndDate);
        report.generateHistogram();

    }

    @FXML
    void viewReport(ActionEvent event) {
        //TODO: open popup and open image report
    }

    @FXML
    public void initialize() {
        if (App.getCurrentUser() != null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        } else {
            welcomeText.setText("Welcome, unknown employee");
        }
        //TODO: get all users request
        //TODO: edit so hide edit catalog for customer service employee
        ChainEmployee chainEmployee = new ChainEmployee();
        StoreManager storeManager = new StoreManager();
        SystemAdmin systemAdmin = new SystemAdmin();
        if (App.getCurrentUser() != null) {
            /*
            if(App.getCurrentUser().getClass() == chainEmployee.getClass()){
                paneStoreReport.setVisible(false);
                paneChainReport.setVisible(false);
                handleUsersPane.setVisible(false);
            }*/
            if (App.getCurrentUser().getClass() == storeManager.getClass()) {
                paneChainReport.setVisible(false);
                handleUsersPane.setVisible(false);
            } else if (App.getCurrentUser().getClass() == systemAdmin.getClass()) {
                //else it's system admin and he can see all options
            }
        }

        //initialize complaints below:
        Task<GetComplaintsResponse> getComplaintsTask = App.createTimedTask(() -> {
            return ClientHandler.getComplaints();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getComplaintsTask.setOnSucceeded(e -> {
            if (getComplaintsTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting complaints failed");
                return;
            }
            GetComplaintsResponse response = getComplaintsTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting complaints failed");
                return;
            }
            complaintList = response.getComplaintList();

            List<Long> complaintListID = new ArrayList<Long>();
            ObservableList<Long> data = FXCollections.observableArrayList();
            //showing customer only his complaints
            if (complaintList.size() >= 1) {
                for (int i = 0; i < complaintList.size() - 1; i++) {
                    Long id = (complaintList.get(i).getId());
                    complaintListID.add(id);
                }
            }

            data.addAll(complaintListID); //adding to dropdown combo
            complaintComboBox.setItems(data);

        });

        getComplaintsTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getComplaintsTask.getException().printStackTrace();
        });

        // initialize stores below
        Task<GetStoresResponse> getStoresTask = App.createTimedTask(() -> {
            return ClientHandler.getStores();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getStoresTask.setOnSucceeded(e -> {
            if (getStoresTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting stores failed");
                return;
            }
            GetStoresResponse response = getStoresTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting stores failed");
                return;
            }
            storesList = response.getStores();

            for (Store store : storesList) {
                System.out.println(store.getName());
                storeComboBox.getItems().add(store.getName());
            }
        });

        getStoresTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getStoresTask.getException().printStackTrace();
        });
        try {
            Thread t1 = new Thread(getComplaintsTask);
            t1.start();
            t1.join();
            Thread t2 = new Thread(getStoresTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

        }
    }
    public void viewAllComplaints(ActionEvent event) {

    }
}