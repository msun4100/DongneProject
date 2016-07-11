package kr.me.ansr.login;


import java.io.Serializable;


public class LoginInfo implements Serializable{
    public Boolean error;
    public String message;
    public LoginResult user;

    @Override
    public String toString() {
        return "LoginInfo{" +
                "error=" + error +
                ", message='" + message + '\'' +
                ", user=" + user +
                '}';
    }
}
