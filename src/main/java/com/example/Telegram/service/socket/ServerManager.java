package com.example.Telegram.service.socket;

import com.example.Telegram.controller.server.IOClientHandler;
import com.example.Telegram.model.repository.MessageRepository;
import com.example.Telegram.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServerManager {
    private final int PORT = 8081;

    /**
     * Using a thread pool to optimize resources and improve performance, instead of creating a new thread for each client connection
     * This approach allows for better scalability and resource management when handling multiple client connections.
     */
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    /**
     * Single shared clientManager instance
     */
    private final ClientManager clientManager = new ClientManager();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    public void startServer() throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);

        while (true) {

            Socket clientSocket = serverSocket.accept();

            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // save uniqueKey and socket to cletnSocketMap to avoid duplicate usernames
            String uniqueKey = input.readLine();
            clientManager.addClient(uniqueKey, clientSocket);

            System.out.println("New client connected : " + uniqueKey + " - " + clientSocket.getInetAddress());

            threadPool.submit(new IOClientHandler(clientManager, clientSocket, uniqueKey, userRepository, messageRepository));
        }
    }
}
