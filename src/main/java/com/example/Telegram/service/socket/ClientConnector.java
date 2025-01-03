package com.example.Telegram.service.socket;

import java.io.IOException;
import java.net.Socket;

public class ClientConnector  {
    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 8081;

    public Socket connectServer() throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        return socket;
    }
}
