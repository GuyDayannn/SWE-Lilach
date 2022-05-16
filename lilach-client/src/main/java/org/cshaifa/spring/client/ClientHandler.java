package org.cshaifa.spring.client;

import java.io.IOException;
import java.net.ConnectException;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.entities.requests.*;
import org.cshaifa.spring.entities.responses.*;
import org.cshaifa.spring.utils.Constants;

public class ClientHandler {
    private static LilachClient client = new LilachClient("localhost", Constants.SERVER_PORT);
    public static volatile Object msgFromServer = null;

    private static Object waitForMsgFromServer(int requestId) {
        while (msgFromServer == null || (msgFromServer instanceof Response && ((Response) msgFromServer).getRequestId() != requestId)) {
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
    public static CatalogItem getItem(long itemID) throws IOException {
        client.openConnection();
        client.sendToServer(new GetItemRequest(itemID));
        CatalogItem item = (CatalogItem) waitForMsgFromServer();
        return item;
    }
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

    public static RegisterResponse registerCustomer(String fullName, String username, String email, String password) throws IOException {
        RegisterRequest registerRequest = new RegisterRequest(fullName, username, email, password);
        client.openConnection();
        client.sendToServer(registerRequest);
        return (RegisterResponse) waitForMsgFromServer(registerRequest.getRequestId());
    }

    public static LogoutResponse logoutUser(User user) throws IOException{
        if (user == null)
            return null;
        LogoutRequest logoutRequest  = new LogoutRequest(user);
        client.openConnection();
        client.sendToServer(logoutRequest);
        return (LogoutResponse) waitForMsgFromServer(logoutRequest.getRequestId());
    }
}
