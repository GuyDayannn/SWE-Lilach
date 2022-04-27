package org.cshaifa.spring.client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.GetItemRequest;
import org.cshaifa.spring.entities.requests.Request;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.Response;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;

public class ClientHandler {
    private static LilachClient client = new LilachClient("localhost", 8095);
    public static volatile Object msgFromServer = null;

    private static Object waitForMsgFromServer() {
        while (msgFromServer == null) {
            Thread.onSpinWait();
        } // TODO: limit waiting
        Object msg = msgFromServer;
        msgFromServer = null;
        return msg;
    }

    public static GetCatalogResponse getCatalog() throws IOException, ConnectException {
        client.openConnection();
        client.sendToServer(new GetCatalogRequest());
        return (GetCatalogResponse) waitForMsgFromServer();
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
        client.openConnection();
        client.sendToServer(new UpdateItemRequest(updatedItem));
        return (UpdateItemResponse) waitForMsgFromServer();
    }
}
