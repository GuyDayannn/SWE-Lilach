package org.cshaifa.spring.client;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Delivery;
import org.cshaifa.spring.entities.Employee;
import org.cshaifa.spring.entities.Order;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.SubscriptionType;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.entities.requests.AddComplaintRequest;
import org.cshaifa.spring.entities.requests.CreateItemRequest;
import org.cshaifa.spring.entities.requests.CreateOrderRequest;
import org.cshaifa.spring.entities.requests.DeleteItemRequest;
import org.cshaifa.spring.entities.requests.FreezeCustomerRequest;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.GetComplaintsRequest;
import org.cshaifa.spring.entities.requests.GetOrdersRequest;
import org.cshaifa.spring.entities.requests.GetStoresRequest;
import org.cshaifa.spring.entities.requests.IsAliveRequest;
import org.cshaifa.spring.entities.requests.LoginRequest;
import org.cshaifa.spring.entities.requests.LogoutRequest;
import org.cshaifa.spring.entities.requests.RegisterRequest;
import org.cshaifa.spring.entities.requests.UpdateComplaintRequest;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.requests.UpdateOrdersRequest;
import org.cshaifa.spring.entities.responses.AddComplaintResponse;
import org.cshaifa.spring.entities.responses.CreateItemResponse;
import org.cshaifa.spring.entities.responses.CreateOrderResponse;
import org.cshaifa.spring.entities.responses.DeleteItemResponse;
import org.cshaifa.spring.entities.responses.FreezeCustomerResponse;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.GetComplaintsResponse;
import org.cshaifa.spring.entities.responses.GetOrdersResponse;
import org.cshaifa.spring.entities.responses.GetStoresResponse;
import org.cshaifa.spring.entities.responses.IsAliveResponse;
import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.entities.responses.LogoutResponse;
import org.cshaifa.spring.entities.responses.NotifyResponse;
import org.cshaifa.spring.entities.responses.RegisterResponse;
import org.cshaifa.spring.entities.responses.Response;
import org.cshaifa.spring.entities.responses.UpdateComplaintResponse;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
import org.cshaifa.spring.entities.responses.UpdateOrdersResponse;
import org.cshaifa.spring.utils.Constants;

public class ClientHandler {
    private static LilachClient client = new LilachClient("localhost", Constants.SERVER_PORT);
    public static BlockingQueue<Object> msgQueue = new LinkedBlockingDeque<>();
    public static BlockingQueue<NotifyResponse> updateQueue = new LinkedBlockingDeque<>();
    public static volatile boolean connectionClosed = false;

    private static Object waitForMsgFromServer(int requestId) throws InterruptedException {
        while (msgQueue.isEmpty()
                || (msgQueue.peek() instanceof Response && ((Response) msgQueue.peek()).getRequestId() != requestId))
            Thread.onSpinWait();

        return msgQueue.take();
    }

    public static NotifyResponse waitForUpdateFromServer() throws InterruptedException {
        if (updateQueue.isEmpty())
            return null;

        return updateQueue.take();
    }

    public static GetCatalogResponse getCatalog() throws IOException, ConnectException, InterruptedException {
        GetCatalogRequest getCatalogRequest = new GetCatalogRequest();
        client.openConnection();
        client.sendToServer(getCatalogRequest);
        return (GetCatalogResponse) waitForMsgFromServer(getCatalogRequest.getRequestId());
    }

    public static GetComplaintsResponse getComplaints() throws IOException, ConnectException, InterruptedException {
        GetComplaintsRequest getComplaintsRequest = new GetComplaintsRequest();
        client.openConnection();
        client.sendToServer(getComplaintsRequest);
        return (GetComplaintsResponse) waitForMsgFromServer(getComplaintsRequest.getRequestId());
    }

    public static GetOrdersResponse getOrders() throws IOException, ConnectException, InterruptedException {
        GetOrdersRequest getOrdersRequest = new GetOrdersRequest();
        client.openConnection();
        client.sendToServer(getOrdersRequest);
        return (GetOrdersResponse) waitForMsgFromServer(getOrdersRequest.getRequestId());
    }

    /*
     * public static CatalogItem getItem(long itemID) throws IOException {
     * client.openConnection(); client.sendToServer(new GetItemRequest(itemID));
     * CatalogItem item = (CatalogItem) waitForMsgFromServer(); return item; }
     */

    public static UpdateItemResponse updateItem(Employee employee, CatalogItem updatedItem)
            throws IOException, ConnectException, InterruptedException {
        UpdateItemRequest updateItemRequest = new UpdateItemRequest(employee, updatedItem);
        client.openConnection();
        client.sendToServer(updateItemRequest);
        return (UpdateItemResponse) waitForMsgFromServer(updateItemRequest.getRequestId());
    }

    public static UpdateComplaintResponse updateComplaint(Complaint updatedComplaint)
            throws IOException, ConnectException, InterruptedException {
        UpdateComplaintRequest updateComplaintRequest = new UpdateComplaintRequest(updatedComplaint);
        client.openConnection();
        client.sendToServer(updateComplaintRequest);
        return (UpdateComplaintResponse) waitForMsgFromServer(updateComplaintRequest.getRequestId());
    }

    public static CreateItemResponse createItem(Employee employee, String name, double price, Map<Store, Integer> quantities,
            boolean onSale, double discountPercent, String size, String itemType, String itemColor, boolean isDefault,
            byte[] image) throws IOException, InterruptedException {
        CreateItemRequest createItemRequest = new CreateItemRequest(employee, name, price, quantities, onSale, discountPercent,
                size, itemType, itemColor, isDefault, image);
        client.openConnection();
        client.sendToServer(createItemRequest);
        return (CreateItemResponse) waitForMsgFromServer(createItemRequest.getRequestId());
    }

    public static UpdateOrdersResponse updateOrders(Order order)
            throws IOException, ConnectException, InterruptedException {
        UpdateOrdersRequest updateOrdersRequest = new UpdateOrdersRequest(order);
        client.openConnection();
        client.sendToServer(updateOrdersRequest);
        return (UpdateOrdersResponse) waitForMsgFromServer(updateOrdersRequest.getRequestId());
    }

    public static LoginResponse loginUser(String username, String password) throws IOException, InterruptedException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        client.openConnection();
        client.sendToServer(loginRequest);
        return (LoginResponse) waitForMsgFromServer(loginRequest.getRequestId());

    }

    public static RegisterResponse registerCustomer(String fullName, String username, String email, String password,
            List<Store> stores, SubscriptionType subscriptionType, String creditCard, List<Complaint> complaintList)
            throws IOException, InterruptedException {
        RegisterRequest registerRequest = new RegisterRequest(fullName, username, email, password, stores,
                subscriptionType, creditCard, complaintList);
        client.openConnection();
        client.sendToServer(registerRequest);
        return (RegisterResponse) waitForMsgFromServer(registerRequest.getRequestId());
    }

    public static LogoutResponse logoutUser(User user) throws IOException, InterruptedException {
        if (user == null)
            return null;
        LogoutRequest logoutRequest = new LogoutRequest(user);
        client.openConnection();
        client.sendToServer(logoutRequest);
        return (LogoutResponse) waitForMsgFromServer(logoutRequest.getRequestId());
    }

    public static IsAliveResponse checkServerAlive() throws IOException, InterruptedException {
        IsAliveRequest isAliveRequest = new IsAliveRequest();
        client.openConnection();
        client.sendToServer(isAliveRequest);
        return (IsAliveResponse) waitForMsgFromServer(isAliveRequest.getRequestId());
    }

    public static GetStoresResponse getStores() throws IOException, InterruptedException {
        GetStoresRequest getStoresRequest = new GetStoresRequest();
        client.openConnection();
        client.sendToServer(getStoresRequest);
        return (GetStoresResponse) waitForMsgFromServer(getStoresRequest.getRequestId());
    }

    public static String getServerHostname() {
        return client.getHost();
    }

    public static int getServerPort() {
        return client.getPort();
    }

    public static void changeServerDetails(String hostname, int port) {
        client.setHost(hostname);
        client.setPort(port);
    }

    public static IsAliveResponse changeServerDetailsAndCheckAlive(String hostname, int port)
            throws IOException, InterruptedException {
        if (client.isConnected()) {
            client.closeConnection();
            while (!connectionClosed)
                Thread.onSpinWait();
            connectionClosed = false;
        }

        changeServerDetails(hostname, port);
        return checkServerAlive();
    }

    public static CreateOrderResponse createOrder(Store store, Customer customer, Map<CatalogItem, Integer> items,
            String greeting, Timestamp orderDate, Timestamp supplyDate, boolean delivery, Delivery deliveryDetails)
            throws IOException, InterruptedException {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(store, customer, items, greeting, orderDate,
                supplyDate, delivery, deliveryDetails);
        client.openConnection();
        client.sendToServer(createOrderRequest);
        return (CreateOrderResponse) waitForMsgFromServer(createOrderRequest.getRequestId());
    }

    public static AddComplaintResponse addComplaint(String complaintDescription, Customer customer, Store store)
            throws IOException, InterruptedException {
        AddComplaintRequest addComplaintRequest = new AddComplaintRequest(complaintDescription, customer, store);
        client.openConnection();
        client.sendToServer(addComplaintRequest);
        return (AddComplaintResponse) waitForMsgFromServer(addComplaintRequest.getRequestId());
    }

    public static FreezeCustomerResponse freezeCustomer(Customer customer, boolean toFreeze)
            throws IOException, InterruptedException {
        FreezeCustomerRequest freezeCustomerRequest = new FreezeCustomerRequest(customer, toFreeze);
        client.openConnection();
        client.sendToServer(freezeCustomerRequest);
        return (FreezeCustomerResponse) waitForMsgFromServer(freezeCustomerRequest.getRequestId());
    }

    public static DeleteItemResponse deleteItem(Employee employee, CatalogItem catalogItem) throws  IOException, InterruptedException{
        DeleteItemRequest deleteItemRequest = new DeleteItemRequest(employee, catalogItem);
        client.openConnection();
        client.sendToServer(deleteItemRequest);
        return (DeleteItemResponse) waitForMsgFromServer(deleteItemRequest.getRequestId());
    }
}
