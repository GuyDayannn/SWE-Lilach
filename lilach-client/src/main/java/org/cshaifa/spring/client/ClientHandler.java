package org.cshaifa.spring.client;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.SubscriptionType;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.entities.requests.CreateOrderRequest;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.GetStoresRequest;
import org.cshaifa.spring.entities.requests.IsAliveRequest;
import org.cshaifa.spring.entities.requests.LoginRequest;
import org.cshaifa.spring.entities.requests.LogoutRequest;
import org.cshaifa.spring.entities.requests.RegisterRequest;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.responses.CreateOrderResponse;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.GetStoresResponse;
import org.cshaifa.spring.entities.responses.IsAliveResponse;
import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.entities.responses.LogoutResponse;
import org.cshaifa.spring.entities.responses.RegisterResponse;
import org.cshaifa.spring.entities.responses.Response;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
import org.cshaifa.spring.utils.Constants;

public class ClientHandler {
    private static LilachClient client = new LilachClient("localhost", Constants.SERVER_PORT);
    public static volatile Object msgFromServer = null;
    public static volatile boolean connectionClosed = false;

    private static Object waitForMsgFromServer(int requestId) {
        while (msgFromServer == null
                || (msgFromServer instanceof Response && ((Response) msgFromServer).getRequestId() != requestId)) {
            Thread.onSpinWait();
        }
        Object msg = msgFromServer;
        msgFromServer = null;
        return msg;
    }

    public static GetCatalogResponse getCatalog() throws IOException, ConnectException {
        GetCatalogRequest getCatalogRequest = new GetCatalogRequest();
        client.openConnection();
        client.sendToServer(getCatalogRequest);
        return (GetCatalogResponse) waitForMsgFromServer(getCatalogRequest.getRequestId());
    }

    /*
     * public static CatalogItem getItem(long itemID) throws IOException {
     * client.openConnection(); client.sendToServer(new GetItemRequest(itemID));
     * CatalogItem item = (CatalogItem) waitForMsgFromServer(); return item; }
     */

    public static UpdateItemResponse updateItem(CatalogItem updatedItem) throws IOException, ConnectException {
        UpdateItemRequest updateItemRequest = new UpdateItemRequest(updatedItem);
        client.openConnection();
        client.sendToServer(updateItemRequest);
        return (UpdateItemResponse) waitForMsgFromServer(updateItemRequest.getRequestId());
    }

    public static LoginResponse loginUser(String username, String password) throws IOException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        client.openConnection();
        client.sendToServer(loginRequest);
        return (LoginResponse) waitForMsgFromServer(loginRequest.getRequestId());

    }

    public static RegisterResponse registerCustomer(String fullName, String username, String email, String password,
            List<Store> stores, SubscriptionType subscriptionType) throws IOException {
        RegisterRequest registerRequest = new RegisterRequest(fullName, username, email, password, stores,
                subscriptionType);
        client.openConnection();
        client.sendToServer(registerRequest);
        return (RegisterResponse) waitForMsgFromServer(registerRequest.getRequestId());
    }

    public static LogoutResponse logoutUser(User user) throws IOException {
        if (user == null)
            return null;
        LogoutRequest logoutRequest = new LogoutRequest(user);
        client.openConnection();
        client.sendToServer(logoutRequest);
        return (LogoutResponse) waitForMsgFromServer(logoutRequest.getRequestId());
    }

    public static IsAliveResponse checkServerAlive() throws IOException {
        IsAliveRequest isAliveRequest = new IsAliveRequest();
        client.openConnection();
        client.sendToServer(isAliveRequest);
        return (IsAliveResponse) waitForMsgFromServer(isAliveRequest.getRequestId());
    }

    public static GetStoresResponse getStores() throws IOException {
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

    public static IsAliveResponse changeServerDetailsAndCheckAlive(String hostname, int port) throws IOException {
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
            String greeting, Timestamp orderDate, Timestamp supplyDate, boolean delivery) throws IOException {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(store, customer, items, greeting, orderDate,
                supplyDate, delivery);
        client.openConnection();
        client.sendToServer(createOrderRequest);
        return (CreateOrderResponse) waitForMsgFromServer(createOrderRequest.getRequestId());
    }
}
