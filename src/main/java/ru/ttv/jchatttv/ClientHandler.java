package ru.ttv.jchatttv;

import ru.ttv.jchatttv.entity.ChatServer;

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
    private static final String START_SYMBOL = "/";
    private static final String END_STRING = "/end";
    private static final String AUTH_STRING = "/auth";
    private static final String AUTH_OK_STRING = "/authok ";
    private static final String SINGLE_CAST_STRING = "/w";

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
                    while(true){//authorization loop
                        String str = inputStream.readUTF();
                        System.out.println(str);
                        if( str.startsWith(AUTH_STRING)){
                            String[] parts = str.split("\\s");
                            String nick = chatServer.getAuthService().getNickByLoginPass(parts[1],parts[2]);
                            if(nick != null){
                                if(!chatServer.isNickBusy(nick)){
                                    sendMsg(AUTH_OK_STRING+nick);
                                    name = nick;
                                    chatServer.broadcastMsg(name + " connected to chat");
                                    chatServer.subscribe(this);
                                    break;
                                }else{
                                    sendMsg("Account is busy");
                                }
                            }else {
                                sendMsg("Incorrect login or password...");
                            }
                        }
                    }
                    while (true) {//message recieving loop
                        String str = inputStream.readUTF();
                        //System.out.println(" от "+name+": "+str);
                        if (str.startsWith(START_SYMBOL)) {
                            if (END_STRING.equals(str)) {
                                break;
                            }
                            if (str.startsWith(SINGLE_CAST_STRING)) {
                                String[] parts = str.split("\\s", 3);
                                if (parts.length == 3) {
                                    chatServer.singleCastMsg(this,parts[1], parts[2]);
                                }
                            }
                        } else {
                            chatServer.broadcastMsg(name + ": " + str);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    chatServer.unsubscribe(this);
                    chatServer.broadcastMsg(name + " left this chat");
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
