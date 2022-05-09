package org.cshaifa.spring.client;

import java.io.IOException;
import java.net.ConnectException;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.LoginRequest;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.entities.responses.Response;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
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
}
