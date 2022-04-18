package org.cshaifa.spring.client;

import java.io.IOException;
import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.GetItemRequest;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;

public class ClientHandler {
    private static LilachClient client = new LilachClient("localhost", 8080);
    public static volatile Object msgFromServer = null;

    private static Object waitForMsgFromServer() {
        while (msgFromServer == null) {
            Thread.onSpinWait();
        } // TODO: limit waiting
        Object msg = msgFromServer;
        msgFromServer = null;
        return msg;
    }

    @SuppressWarnings("unchecked")
    public static List<CatalogItem> getCatalog() throws IOException {
        client.openConnection();
        client.sendToServer(new GetCatalogRequest());
        List<CatalogItem> list = (List<CatalogItem>) waitForMsgFromServer();
        return list;
    }

    public static CatalogItem getItem(long itemID) throws IOException {
        client.openConnection();
        client.sendToServer(new GetItemRequest(itemID));
        CatalogItem item = (CatalogItem) waitForMsgFromServer();
        return item;
    }

    public static Object updateItem(long itemID, CatalogItem updatedItem) throws IOException{
        client.openConnection();
        client.sendToServer(new UpdateItemRequest(itemID, updatedItem));
        // TODO: need to figure out what to be returned (notification of succession or failure)
        Object msg = waitForMsgFromServer();
        return msg;
    }
}
