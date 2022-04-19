package org.cshaifa.spring.server;

import org.cshaifa.spring.entities.requests.Request;
import org.cshaifa.spring.server.ocsf.AbstractServer;
import org.cshaifa.spring.server.ocsf.ConnectionToClient;

public class LilachServer extends AbstractServer {

    public LilachServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        // TODO: Add message handler here, unwrap the request sent by the client
        if (msg instanceof Request) {

        } else {
            // TODO: Return an error message to the client
        }
    }

}
