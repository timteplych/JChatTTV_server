package ru.ttv.jchatttv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private ChatServer chatServer;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(ChatServer chatServer, Socket socket){
        try {
            this.chatServer = chatServer;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(()->{
                try{
                    while(true){//цикл автоизации
                        String str = inputStream.readUTF();
                        if(str.startsWith("/auth")){
                            String[] parts = str.split("\\s");
                            String nick = chatServer.getAuthService().getNickByLoginPass(parts[1],parts[2]);
                            if(nick != null){
                                if(!chatServer.isNickBusy(nick)){
                                    sendMsg("/authok "+nick);
                                    name = nick;
                                    chatServer.broadCastMsg(name + " зашел в чат");
                                    chatServer.subscribe(this);
                                    break;
                                }else{
                                    sendMsg("Account is used already");
                                }
                            }else {
                                sendMsg("Incorrect login or password...");
                            }
                        }
                    }
                    while(true){//Цикл получения сообщений
                        String str = inputStream.readUTF();
                        System.out.println(" от "+name+": "+str);
                        if(str.equals("/end")){
                            break;
                        }
                        chatServer.broadCastMsg(name+": "+str);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }finally{
                    chatServer.unsubscribe(this);
                    chatServer.broadCastMsg(name + " вышел из чата");
                    try {
                        socket.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch(IOException e){
            throw new RuntimeException("Problem while creating client handler...");
        }
    }

    public void sendMsg(String msg){
        try {
            outputStream.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
