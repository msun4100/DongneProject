package kr.me.ansr.gcmchat.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-01.
 */
public class ChatRoom implements Serializable {
    @SerializedName("chat_room_id")
    public int id;
    public String name;
    public String lastMessage; //클라이언트용 변수
    @SerializedName("created_at")
    public String timestamp;
    public int unreadCount;
    public String image;
    public int bgColor = 0; //백그라운드 칼라
    public ChatRoom() {
    }

    public ChatRoom(int id, String name, String lastMessage, String timestamp, int unreadCount) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", unreadCount=" + unreadCount +
                ", image='" + image + '\'' +
                ", bgColor=" + bgColor +
                '}';
    }
}
