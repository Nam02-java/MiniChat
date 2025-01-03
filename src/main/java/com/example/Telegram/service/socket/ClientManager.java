package com.example.Telegram.service.socket;


import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {

    private final Map<String, Socket> clientSocketsMap = new ConcurrentHashMap<>();

    public void addClient(String uniqueKey, Socket socket) {
        clientSocketsMap.put(uniqueKey, socket);
    }

    public void removeClient(String uniqueKey) {
        clientSocketsMap.remove(uniqueKey);
    }

    public Map<String, Socket> getClientSocketsMap() {
        return Collections.unmodifiableMap(clientSocketsMap);
    }

    public Collection<Socket> getAllSockets() {
        return clientSocketsMap.values();
    }

    public String getUsernameBySocket(Socket socket) {
        for (Map.Entry<String, Socket> entry : clientSocketsMap.entrySet()) {
            if (entry.getValue().equals(socket)) {
                // split userName from uniquekey
                return entry.getKey().split("_")[0];
            }
        }
        return null;
    }

    public String getAnySocketOfUser(String senderUsername) {
        senderUsername = senderUsername.split("_")[0];
        for (Map.Entry<String, Socket> entry : clientSocketsMap.entrySet()) {
            String username = entry.getKey().split("_")[0];
            if (!username.equals(senderUsername)) {
                // returns the remaining users
                return username;
            }
        }
        return null;
    }
}
