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
import org.cshaifa.spring.utils.ImageUtils;


import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmployeeProfileController {
    @FXML
    private Button editEmployeeBtn;
    @FXML
    private TextField customerAccountText;
    @FXML
    private TextField employeeStatusText;
    @FXML
    private ComboBox<String> customerComboBox;
    @FXML
    private ComboBox<String> customerStatusComboBox;
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
    private TitledPane viewTwoReportsPane;
    @FXML
    private ComboBox<String> storeComboBox;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button viewReportButton;
    @FXML
    private ComboBox<String> employeesTypeComboBox;
    @FXML
    private ComboBox<String> selectEmployeeComboBox;
    @FXML
    private ComboBox<String> selectStoreComboBox;
    @FXML
    private ComboBox<String> employeeStatusComboBox;

    // Variables
    private List<Complaint> complaintList;
    private List<Store> storesList;
    private List<User> userList;
    private List<Customer> customerList = new ArrayList<>();

    private List<ChainEmployee> chainEmployeeList= new ArrayList<>();
    private List<CustomerServiceEmployee> customerServiceList= new ArrayList<>();
    private List<StoreManager> storeManagersList= new ArrayList<>();
    private List<Employee> employeeList= new ArrayList<>();
    private ChainManager chainManager;

    private LocalDate reportStartDate;
    private LocalDate reportEndDate;
    private ReportType reportType;
    private Store reportStore;
    private Report report;

    private Customer selectedCustomer = new Customer();
    //private Employee selectedEmployee = null;



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
    void openComplaint(ActionEvent event) {
        //on view complaint click
        //first clearning screens
        complaintdescription.clear();
        complaintStatus.clear();
        compensationamount.clear();
        complaintresponse.clear();

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
            if(store.getName().equals(storeName)){
                reportStore = store;
                break;
            }
        }
    }

    @FXML
    void selectReportType(ActionEvent event) {
        String report = reportTypeComboBox.getValue();
        if(report.equals("Orders"))
            reportType = ReportType.ORDERS;
        else if (report.equals("Revenue"))
            reportType = ReportType.REVENUE;
        else if(report.equals("Complaints"))
            reportType = ReportType.COMPLAINTS;
    }

    @FXML
    void setStartDate(ActionEvent event) {
        reportStartDate = startDatePicker.getValue();
    }

    @FXML
    void setEndDate(ActionEvent event) {
        reportEndDate = endDatePicker.getValue();
    }

    @FXML
    void generateReport(ActionEvent event) {
        report = new Report(reportType, reportStore, reportStartDate, reportEndDate);
        report.generateHistogram();
        viewReportButton.setDisable(false);

    }

    @FXML
    void viewReport(ActionEvent event) {
        String path = report.getReportPath();
        App.setCurrentReportDisplayed(report);
        App.popUpLaunch(viewReportButton, "ReportPopUp");
    }

    void initComplaints(){
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
                for (int i = 0; i < complaintList.size()-1; i++) {
                    Long id = (complaintList.get(i).getId()); //id 1 shows complaint 2
                    if(id!=0.0) {
                        complaintListID.add(id);
                    } //TODO: fix index id jumps in 1
                }
            }

            data.addAll(complaintListID); //adding to dropdown combo
            complaintComboBox.setItems(data);
            //new Thread(getComplaintsTask).start();

        });

        getComplaintsTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getComplaintsTask.getException().printStackTrace();
        });

        try {
//            Thread t1 = new Thread(getComplaintsTask);
//            t1.start();
//            t1.join();
            Thread t2 = new Thread(getComplaintsTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

        }
    }


    void initStores(){
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
                //System.out.println(store.getName());
                storeComboBox.getItems().add(store.getName());
            }

            //new Thread(getStoresTask).start();
        });

        getStoresTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getStoresTask.getException().printStackTrace();
        });
        try {
//            Thread t1 = new Thread(getComplaintsTask);
//            t1.start();
//            t1.join();
            Thread t2 = new Thread(getStoresTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

        }
    }

    void initEditEmployees(){
        employeesTypeComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> {
            System.out.println(t);
            System.out.println(t1);
            if (t1!=null && t1.equals("Chain Employee")) {
                System.out.println("hadpasa");
                selectEmployeeComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().clear();
                employeeStatusComboBox.getItems().clear();
                ObservableList<String> employeeNames = FXCollections.observableArrayList();
                if (chainEmployeeList.size() >= 1) {
                    for (Employee employee : chainEmployeeList) {
                        employeeNames.add(employee.getFullName());
                    }
                }
                selectEmployeeComboBox.getItems().addAll(employeeNames);
                employeeStatusComboBox.setDisable(false);
                ObservableList<String> employeeTypes = FXCollections.observableArrayList();
                employeeTypes.add("Customer Service");
                employeeTypes.add("Store Manager");
                employeeTypes.add("Chain Manager");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }

            if (t1!=null && t1.equals("Customer Service")) {
                selectEmployeeComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().clear();
                employeeStatusComboBox.getItems().clear();
                ObservableList<String> employeeNames = FXCollections.observableArrayList();
                if (customerServiceList.size() >= 1) {
                    for (Employee employee : customerServiceList) {
                        employeeNames.add(employee.getFullName());
                    }
                }
                selectEmployeeComboBox.getItems().addAll(employeeNames);
                employeeStatusComboBox.setDisable(false);
                ObservableList<String> employeeTypes = FXCollections.observableArrayList();
                employeeTypes.add("Chain Employee");
                employeeTypes.add("Store Manager");
                employeeTypes.add("Chain Manager");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }

            if (t1!=null && t1.equals("Store Manager")) {
                selectEmployeeComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().clear();
                employeeStatusComboBox.getItems().clear();
                ObservableList<String> employeeNames = FXCollections.observableArrayList();
                if (storeManagersList.size() >= 1) {
                    for (Employee employee : storeManagersList) {
                        employeeNames.add(employee.getFullName());
                    }
                }
                employeeStatusComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().addAll(employeeNames);
                ObservableList<String> employeeTypes = FXCollections.observableArrayList();
                employeeTypes.add("Chain Employee");
                employeeTypes.add("Customer Service");
                employeeTypes.add("Chain Manager");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }

            if (t1!=null && t1.equals("Chain Manager")) {
                selectEmployeeComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().clear();
                employeeStatusComboBox.getItems().clear();
                ObservableList<String> employeeNames = FXCollections.observableArrayList();
                if (storeManagersList.size() >= 1) {
                    // TODO: Add chain manager class
                    for (Employee employee : storeManagersList) {
                        employeeNames.add(employee.getFullName());
                    }
                }
                selectEmployeeComboBox.getItems().addAll(employeeNames);
                employeeStatusComboBox.setDisable(false);
                ObservableList<String> employeeTypes = FXCollections.observableArrayList();
                employeeTypes.add("Store Manager");
                employeeTypes.add("Customer Service");
                employeeTypes.add("Chain Manager");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }
        });

        employeeStatusComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> {
            System.out.println(t);
            System.out.println(t1);
            if (t1!=null && t1.equals("Store Manager")) {
                System.out.println("chosen store manager");
                selectStoreComboBox.setDisable(false);

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
                        //System.out.println(store.getName());
                        selectStoreComboBox.getItems().add(store.getName());
                    }
                });

                getStoresTask.setOnFailed(e -> {
                    // TODO: maybe log somewhere else...
                    getStoresTask.getException().printStackTrace();
                });
                new Thread(getStoresTask).start();
            }
        });
    }

    void initUsers(){
        Task<GetUsersResponse> getUsersTask = App.createTimedTask(() -> {
            return ClientHandler.getUsers();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getUsersTask.setOnSucceeded(e -> {
            if (getUsersTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting users failed");
                return;
            }
            GetUsersResponse response = getUsersTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting users failed");
                return;
            }
            userList = response.getUsersList();
            CustomerServiceEmployee customerServiceEmployee = new CustomerServiceEmployee();
            for(User user: userList){
                if(user.getClass().isAssignableFrom(ChainEmployee.class) ){
                    chainEmployeeList.add((ChainEmployee) user);
                    employeeList.add((Employee) user);
                    System.out.println("added chain employee");
                }
                else if(user.getClass().isAssignableFrom(StoreManager.class)){
                    storeManagersList.add((StoreManager) user);
                    employeeList.add((Employee) user);
                    System.out.println("added store manager");
                }
                else if(user.getClass().isAssignableFrom(CustomerServiceEmployee.class)){
                    customerServiceList.add((CustomerServiceEmployee) user);
                    employeeList.add((Employee) user);
                    System.out.println("added customer service employee");

                }
                else if(user.getClass().isAssignableFrom(Customer.class)){
                    customerList.add((Customer) user);
                    System.out.println("added customer");
                }
                else if(user.getClass().isAssignableFrom(ChainManager.class)){
                    chainManager = (ChainManager) user;
                    System.out.println("added chain manager");
                }
                else{
                    System.out.println("Couldn't classify user");
                }
            }

            for (Customer customer : customerList) {
                //System.out.println(customer.getUsername());
                customerComboBox.getItems().add(customer.getUsername());
            }
        });

        getUsersTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getUsersTask.getException().printStackTrace();
        });
        try {
            Thread t2 = new Thread(getUsersTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

        }
    }

    @FXML
    public void initialize() {
        if (App.getCurrentUser() != null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        } else {
            welcomeText.setText("Welcome, unknown employee");
        }
        //TODO: edit to show only to authorized users
        if (App.getCurrentUser() != null) {
            /*
            if(App.getCurrentUser().getClass() == ChainEmployee.class){
                paneStoreReport.setVisible(false);
                paneChainReport.setVisible(false);
                handleUsersPane.setVisible(false);
                viewTwoReportsPane.setVisible(false);
            }*/
            if (App.getCurrentUser().getClass() == StoreManager.class) {
                paneChainReport.setVisible(false);
                handleUsersPane.setVisible(false);
                viewTwoReportsPane.setVisible(false);
            }else if (App.getCurrentUser().getClass() == CustomerServiceEmployee.class) {
                paneStoreReport.setVisible(false);
                paneChainReport.setVisible(false);
                handleUsersPane.setVisible(false);
                viewTwoReportsPane.setVisible(false);
            } else if (App.getCurrentUser().getClass() == ChainManager.class) {
            paneStoreReport.setVisible(false);
            paneChainReport.setVisible(false);

        }
        else if (App.getCurrentUser().getClass() == SystemAdmin.class) {
                //else it's system admin and he can see all options
            }
        }
        //calling db server tasks to get data
        initComplaints();
        initStores();
        initUsers();
        initEditEmployees();

    }
    public void viewAllComplaints(ActionEvent event) {

    }

    //methods for handle users below
    @FXML
    public void selectCustomer(ActionEvent event) {
        customerAccountText.clear();
        String custUsername = customerComboBox.getSelectionModel().getSelectedItem();
        //Customer selectedCustomer = new Customer();
        for(Customer customer: customerList){
            if(customer.getUsername().equals(custUsername)){
                selectedCustomer = customer;
                break;
            }
        }
        boolean isFrozen = selectedCustomer.isFrozen();
        if(isFrozen)
            customerAccountText.setText("Frozen");
        else
            customerAccountText.setText("Active");
    }

    public void createTaskCustUpdate(boolean toFreeze){
        Task<FreezeCustomerResponse> editCustomerTask = App.createTimedTask(() -> {
            return ClientHandler.freezeCustomer(selectedCustomer, toFreeze);
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        editCustomerTask.setOnSucceeded(e -> {
            if (editCustomerTask.getValue() == null) {
                System.err.println("Updating customer failed");
                return;
            }
            FreezeCustomerResponse response = editCustomerTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                System.err.println("Updating customer failed");
                return;
            }
        });

        editCustomerTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            editCustomerTask.getException().printStackTrace();
        });
        new Thread(editCustomerTask).start();

//        try {
//            Thread t1 = new Thread(editCustomerTask);
//            t1.start();
//            t1.join();
//            //customerComboBox.valueProperty().setValue(null);
////            customerList.clear();
////            employeeList.clear();
////            customerServiceList.clear();
////            storeManagersList.clear();
////            chainEmployeeList.clear();
////
//            //initUsers();
//        } catch (InterruptedException interruptedException) {
//            interruptedException.printStackTrace();
//
//        }
    }


    public void createTaskEmployeeUpdate(ChainEmployee employee, Store store, String newType, String currType){
        Task<EditEmployeeResponse> editEmployeeTask = App.createTimedTask(() -> {
            return ClientHandler.editEmployee(employee, store, newType, currType);
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        editEmployeeTask.setOnSucceeded(e -> {
            if (editEmployeeTask.getValue() == null) {
                System.err.println("Updating employee failed");
                return;
            }
            EditEmployeeResponse response = editEmployeeTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                System.err.println("Updating employee  failed");
                return;
            }
        });

        editEmployeeTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            editEmployeeTask.getException().printStackTrace();
        });

        new Thread(editEmployeeTask).start();

    }



    public void editCustomerStatus(ActionEvent event) {
        String selectedStatus = customerStatusComboBox.getValue();
        boolean createTask = false;
        if(selectedStatus.equals("Active") && selectedCustomer.isFrozen()){
            //selectedCustomer.freeze();
            createTaskCustUpdate(false);
        }
        if(selectedStatus.equals("Frozen") && !selectedCustomer.isFrozen()){
            //selectedCustomer.unfreeze();
            createTaskCustUpdate(true);
        }



    }

    public void selectAccountStatus(ActionEvent event) {
    }

    public void selectEmployee(ActionEvent event) {
//        employeeStatusText.clear();
//        String employeeUsername = employeeComboBox.getSelectionModel().toString();
//        for(Employee employee: employeeList){
//            if(employee.getUsername().equals(employeeUsername)){
//                selectedEmployee = employee;
//                break;
//            }
//        }
//

    }

    public void editEmployeeStatus(ActionEvent event) {

    }


    public void editEmployee(ActionEvent event) {
        String selectedStatus = employeeStatusComboBox.getValue();
        String employeeName = selectEmployeeComboBox.getValue();

        if(employeesTypeComboBox.equals("Store Manager")){
            StoreManager selectedManager = null;
            for(StoreManager manager: storeManagersList) {
                if (manager.getUsername().equals(employeeName)) {
                    selectedManager = manager;
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedManager, selectedManager.getStore(),
                    selectedStatus, "Store Manager");
        }
        else if(employeesTypeComboBox.equals("Chain Employee")){
            ChainEmployee selectedEmployee = null;
            for(ChainEmployee employee: chainEmployeeList) {
                if (employee.getUsername().equals(employeeName)) {
                    selectedEmployee = employee;
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedEmployee, selectedEmployee.getStore(),
                    selectedStatus, "Chain Employee");
        }
        else if(employeesTypeComboBox.equals("Customer Service")){
            CustomerServiceEmployee selectedEmployee = null;
            for(CustomerServiceEmployee employee: customerServiceList) {
                if (employee.getUsername().equals(employeeName)) {
                    selectedEmployee = employee;
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedEmployee, null,
                    selectedStatus, "Customer Service");
        }

        else{//we're editing chain maneger
            createTaskEmployeeUpdate(chainManager, null,
                    selectedStatus, "Chain Manager");
        }
    }


    public void selectEmployeeType(ActionEvent event) {
    }

    public void editManagerStore(ActionEvent event) {
    }
}