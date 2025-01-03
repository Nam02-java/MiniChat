package com.example.Telegram;

import com.example.Telegram.controller.client.IOServerHandle;
import com.example.Telegram.service.socket.ClientConnector;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

public class StartClientDevice {

    /**
     * allow multiple instance with Run/Debug Configurations in IntelliJ IDEA
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Random random = new Random();
        String[] names = {"Nam02", "Linh02"};
        String userName = names[random.nextInt(names.length)];
        String uniqueKeyName = userName + "_" + UUID.randomUUID();

        // Set up client connection to server
        ClientConnector clientConnector = new ClientConnector();
        Socket socket = clientConnector.connectServer();

        // Send uniqueKeyName to server
        DataOutputStream outputToServer = new DataOutputStream(socket.getOutputStream());
        outputToServer.writeBytes(uniqueKeyName + "\n");
        System.out.println(userName + " - " + uniqueKeyName);

        // Set up IO for client
        IOServerHandle ioServerHandle = new IOServerHandle();
        ioServerHandle.initialize(socket, userName);


    }
}
