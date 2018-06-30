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
            while(true){
                System.out.println("Waiting for connection...");
                socket = server.accept();
                System.out.println("Client connected...");
                new ClientHandler(this,socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                server.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            authService.stop();
        }

    }

    public synchronized boolean isNickBusy(String nick){
        for(ClientHandler client : clients ){
            if(client.getName().equals(nick)){
                return true;
            }
        }
        return  false;
    }

    public synchronized void broadCastMsg(String msg){
        for(ClientHandler client : clients){
            client.sendMsg(msg);
        }
    }

    public synchronized void singleCastMsg(ClientHandler from, String nick, String msg){
        for(ClientHandler client : clients){
            if(client.getName().equals(nick)){
                client.sendMsg(msg);
                from.sendMsg("To user "+nick+": "+msg);
                break;
            }
        }
    }

    public synchronized void unsubscribe(ClientHandler client){
        clients.remove(client);
    }

    public synchronized void subscribe(ClientHandler client){
        clients.add(client);
    }

}

