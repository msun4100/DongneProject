package kr.me.ansr.login;


import java.io.Serializable;

/**
 * Created by KMS on 2016-07-11.
 */
public class LoginInfo implements Serializable{
    public Boolean error;
    public String message;
    public LoginResult result;

    @Override
    public String toString() {
        return "LoginInfo{" +
                "error=" + error +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
