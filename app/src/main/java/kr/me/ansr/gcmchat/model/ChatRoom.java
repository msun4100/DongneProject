package kr.me.ansr.gcmchat.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-01.
 */
public class ChatRoom implements Serializable {
    @SerializedName("chat_room_id")
    public int id;
    public String name;
    @SerializedName("last_msg")
    public String lastMessage; //클라이언트용 변수
    @SerializedName("created_at")
    public String timestamp;
    @SerializedName("unread_count")
    public int unreadCount;
    public String image;
    public int bgColor = 0; //백그라운드 칼라
    public int activeUser;  //채팅방 나가기 했을때 activeUser를 0으로 해버리면? 안뜨게
    public ArrayList<Integer> users;    //mongo
    @SerializedName("updated_at")
    public String updatedAt;    //mongo
    @SerializedName("last_join")
    public String lastJoin;     //개별 유저가 방을 나간 case처리

    public ChatRoom() {
    }

    public ChatRoom(int id, String name, String lastMessage, String timestamp, int unreadCount) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public ChatRoom(int id, String name, String lastMessage, String timestamp, int unreadCount, String image, int bgColor, int activeUser, String lastJoin) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.image = image;
        this.bgColor = bgColor;
        this.activeUser = activeUser;
        this.lastJoin = lastJoin;
    }
    public ChatRoom(int id, String name, String lastMessage, String timestamp, int unreadCount, String image, int bgColor, int activeUser, ArrayList<Integer> users) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.image = image;
        this.bgColor = bgColor;
        this.activeUser = activeUser;
        this.users = users;
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
                ", activeUser=" + activeUser +
                ", lastJoin=" + lastJoin +
                ", lastMessage='" + lastMessage + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", unreadCount=" + unreadCount +
                ", image='" + image + '\'' +
                ", bgColor=" + bgColor +
                '}';
    }
}
