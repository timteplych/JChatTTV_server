package ru.ttv.jchatttv;

import java.util.ArrayList;

public class BaseAuthService implements AuthService {
    private ArrayList<User> users;

    public BaseAuthService(){
        users = new ArrayList<User>();
        users.add(new User("login1","pass1",  "nick1"));
        users.add(new User("login2","pass2",  "nick2"));
        users.add(new User("login3","pass3",  "nick3"));
    }

    public void start() {

    }

    public String getNickByLoginPass(String login, String pass) {
        for (User user: users) {
            if(user.getLogin().equals(login) && user.getPass().equals(pass)){
                return user.getNick();
            }
        }
        return null;
    }

    public void stop() {

    }
}
