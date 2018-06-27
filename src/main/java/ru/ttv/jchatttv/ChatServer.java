package ru.ttv.jchatttv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {
    private ServerSocket server;
    private Vector<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService(){
        return authService;
    }

    private final int PORT = 8189;

    public ChatServer(){
        try {
            server = new ServerSocket(PORT);
            Socket socket = null;
            authService = new BaseAuthService();
            authService.start();
            clients = new Vector<>();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }
}

