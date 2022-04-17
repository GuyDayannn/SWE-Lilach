package org.cshaifa.spring.client;

import java.io.IOException;
import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;

public class ClientHandler {
    private static LilachClient client = new LilachClient("localhost", 8080);

    @SuppressWarnings("unchecked")
    public static List<CatalogItem> getCatalog() throws IOException {
        if (!client.isConnected())
            client.openConnection();

        client.sendToServer(new GetCatalogRequest());

        return (List<CatalogItem>) client.getMsgFromServer();
    }
}
