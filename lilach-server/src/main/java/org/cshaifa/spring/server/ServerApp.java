package org.cshaifa.spring.server;

import java.io.IOException;

import org.cshaifa.spring.utils.Constants;

public class ServerApp {
    public static void main(String[] args) throws IOException {
        LilachServer server = new LilachServer(Constants.SERVER_PORT);
        server.listen();
    }
}
