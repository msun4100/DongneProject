package kr.me.ansr.login;

import java.io.Serializable;

public class LoginResult implements Serializable{
//	public int lastLogin;
    public String user_id;
    public String name;
    public String email;
    public String created_at;

    @Override
    public String toString() {
        return "LoginResult{" +
                "user_id=" + user_id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
