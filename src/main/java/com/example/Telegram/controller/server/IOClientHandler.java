package com.example.Telegram.controller.server;

import com.example.Telegram.model.data.Message;
import com.example.Telegram.model.data.User;
import com.example.Telegram.model.repository.MessageRepository;
import com.example.Telegram.model.repository.UserRepository;
import com.example.Telegram.service.socket.ClientManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class IOClientHandler implements Runnable {
    private final ClientManager clientManager;
    private final Socket currentSocket;
    private final String currentUniqueKeyName;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private int currentPage = 0;

    public IOClientHandler(ClientManager clientManager, Socket socket, String currentUniqueKeyName, UserRepository userRepository, MessageRepository messageRepository) {
        this.clientManager = clientManager;
        this.currentSocket = socket;
        this.currentUniqueKeyName = currentUniqueKeyName;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }


    @Override
    public void run() {
        try {
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
            String messageFromClient;

            while ((messageFromClient = inputFromClient.readLine()) != null) {
                System.out.println("Received: " + messageFromClient);

                // Save the message in the database before broadcast to other clients
                User sender = userRepository.findByUsername(clientManager.getUsernameBySocket(currentSocket));
                User recipient = userRepository.findByUsername(clientManager.getAnySocketOfUser(currentUniqueKeyName));
                String[] parts = messageFromClient.split(" : ", 2);
                String messageContent = parts[1];
                if (recipient != null) {
                    Message message = new Message();
                    message.setSender(sender);
                    message.setRecipient(recipient);
                    message.setMessageContent(messageContent);
                    messageRepository.save(message);
                }

                // Broadcast to other clients
                Message lastMessage = messageRepository.findTop1ByOrderByIdDesc();
                Long messageId = lastMessage.getId();
                for (Socket targetSocket : clientManager.getAllSockets()) {
                    if (targetSocket != currentSocket) {
                        DataOutputStream outputToClient = new DataOutputStream(targetSocket.getOutputStream());
                        outputToClient.writeBytes(messageFromClient + " - " + "Message ID: " + messageId + "\n");
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Client disconnected: " + currentSocket.getInetAddress());
        } finally {
            synchronized (clientManager.getClientSocketsMap()) {
                clientManager.removeClient(currentUniqueKeyName);
            }
            try {
                currentSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
