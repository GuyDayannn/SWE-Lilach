package org.cshaifa.spring.server;

import java.io.IOException;

import org.cshaifa.spring.server.database.DatabaseHandler;
import org.cshaifa.spring.utils.Constants;

public class ServerApp {
    private static final LilachServer server = new LilachServer(Constants.SERVER_PORT);

    public static void main(String[] args) throws IOException {
        server.listen();
        DatabaseHandler.initializeDatabaseIfEmpty();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
