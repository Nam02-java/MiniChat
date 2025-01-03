package com.example.Telegram.controller.client;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class IOServerHandle {

    private final String SERVER_HISTORY_LOAD_URL = "http://localhost:8080/messages/history";

    public void initialize(Socket socket, String userName) throws IOException {

        BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream outputToServer = new DataOutputStream(socket.getOutputStream());
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));


        new Thread(() -> {
            String messageFromServer;
            try {
                while ((messageFromServer = inputFromServer.readLine()) != null) {
                    out.println(messageFromServer);
                }
            } catch (IOException e) {
                System.err.println("Connection to server lost.");
            }
        }).start();

        String messageToServer;
        int currentPage = 0;
        while ((messageToServer = userInput.readLine()) != null) {
            if ("1".equals(messageToServer)) {
                // Request history data from this client
                List<String> history = loadChatHistory(currentPage);
                if (history.isEmpty()) {
                    System.out.println("No more history available.");
                } else {
                    for (String message : history) {
                        System.out.println("History message: " + message);
                    }
                    currentPage++; // Increase the current page
                }
            } else {
                outputToServer.writeBytes(userName + " : " + messageToServer + "\n");
            }
        }
    }

    private List<String> loadChatHistory(int currentPage) {
        String jsonResponse = "";
        List<String> historyMessages ;
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            // Create HttpRequest to call api
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_HISTORY_LOAD_URL + "?currentPage=" + currentPage + "&pageSize=5"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            // Create HttpResponse to get data from API
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            /**
             * 200 OK is true !
             */
            if (response.statusCode() == 200) {
                // Get data from JSON
                jsonResponse = response.body();
            } else {
                System.err.println("Error: HTTP response code " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
        }

        // Parse the JSON to get a list of messages
        historyMessages = parseHistoryMessages(jsonResponse);
        return historyMessages;
    }



    private static List<String> parseHistoryMessages(String jsonResponse) {
        /**
         * Actual returned json sample from database
         * {"sender":{"id":1,"username":"Linh02"},"recipient":{"id":2,"username":"Nam02"},"id":44,"sentAt":"2024-12-30T16:08:51","messageContent":"demo"}
         */
        List<String> messages = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray content = json.getJSONArray("content");

            for (int i = 0; i < content.length(); i++) {
                JSONObject message = content.getJSONObject(i);
                String messageContent = message.getString("messageContent");
                String senderUsername = message.getJSONObject("sender").getString("username");

                String combinedMessage = senderUsername + ": " + messageContent;
                messages.add(combinedMessage);
            }
        } catch (org.json.JSONException e) {
            System.out.println("Error parsing JSON response: " + e.getMessage());
        }
        return messages;
    }
}



//    private List<String> loadChatHistory(int currentPage) {
//        List<String> historyMessages = new ArrayList<>();
//        try {
//            // Create URL
//            URL url = new URL(SERVER_URL + "?currentPage=" + currentPage + "&pageSize=5");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("Accept", "application/json");
//
//
//            // Read data from server
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = in.readLine()) != null) {
//                response.append(line);
//            }
//            in.close();
//
//            // Parse the JSON to get a list of messages
//            historyMessages = parseHistoryMessages(response.toString());
//        } catch (IOException e) {
//            System.out.println("Error loading chat history: " + e.getMessage());
//        }
//        return historyMessages;
//    }
