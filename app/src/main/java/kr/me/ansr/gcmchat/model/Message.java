package kr.me.ansr.gcmchat.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-01.
 */
public class Message implements Serializable {
    @SerializedName("message_id")
    public int id;
    public String message;
    @SerializedName("created_at")
    public String createdAt;
    public int chat_room_id; //Config.PUSH_TYPE_NEW_ROOM notification
    public User user;
    public String image;
    public int bgColor = 0; //백그라운드 칼라

    public Message() {
    }

    public Message(int id, String message, String createdAt, User user) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", user=" + user +
                ", chat_room_id=" + chat_room_id +
                '}';
    }
}
