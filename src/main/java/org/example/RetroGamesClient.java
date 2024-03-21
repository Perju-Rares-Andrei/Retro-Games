package org.example;

import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RetroGamesClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private static RetroGamesClient instance;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;


    private RetroGamesClient() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RetroGamesClient getInstance() {
        if (instance == null) {
            instance = new RetroGamesClient();
        }
        return instance;
    }

    public void sendMessageToServer(String message) {
        writer.println(message);
    }

    public void closeConnection() {
        try {
            writer.close();
            reader.close();
            socket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void messageReceivedFromServer(String message) {
        System.out.println("Received from server: " + message);
        setMessageReceived(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RetroGamesMenu retroGamesMenu = new RetroGamesMenu();
            retroGamesMenu.setVisible(true);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> RetroGamesClient.getInstance().closeConnection()));
        });

        Thread clientThread = new Thread(() -> {
            RetroGamesClient client = RetroGamesClient.getInstance();
            BufferedReader reader = client.getReader();

            String response;
            try {
                while ((response = reader.readLine()) != null) {
                    client.messageReceivedFromServer(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
    }

    private BufferedReader getReader() {
        return reader;
    }

    private String lastReceivedMessage;

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    private void setMessageReceived(String message) {
        lastReceivedMessage = message;
    }



}
