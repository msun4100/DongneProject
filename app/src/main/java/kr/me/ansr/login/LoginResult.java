package kr.me.ansr.login;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-11.
 * MySql 객체 처리
 */

public class LoginResult implements Serializable{
//    MySQL user schema
    public String user_id;
    public String name;
    public String email;
    public String created_at;
    public String univId;
    public String provider;

    @Override
    public String toString() {
        return "LoginResult{" +
                "user_id=" + user_id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", created_at='" + created_at + '\'' +
                ", univId='" + univId + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
}
