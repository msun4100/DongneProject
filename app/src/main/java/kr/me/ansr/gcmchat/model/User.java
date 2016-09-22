package kr.me.ansr.gcmchat.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-01.
 */
public class User implements Serializable {
    @SerializedName("user_id")
    int id;
    @SerializedName("username")
    String name;
    String email;

    public User() {
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
