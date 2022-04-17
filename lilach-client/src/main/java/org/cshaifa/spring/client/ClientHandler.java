package org.cshaifa.spring.client;

import java.io.IOException;
import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;

public class ClientHandler {
    private static LilachClient client = new LilachClient("localhost", 8080);
    public static volatile Object msgFromServer = null;

    private static Object waitForMsgFromServer() {
        while (msgFromServer == null) {} // TODO: limit waiting
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
}
