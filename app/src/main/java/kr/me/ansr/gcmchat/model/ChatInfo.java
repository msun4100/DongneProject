package kr.me.ansr.gcmchat.model;

import java.util.ArrayList;

import kr.me.ansr.gcmchat.model.Message;
import kr.me.ansr.gcmchat.model.User;

/**
 * Created by KMS on 2016-09-13.
 * used at NetworkManager callback func
 */
public class ChatInfo {
    public static final int CHAT_RC_NUM = 200;
    public static final int CHAT_RC_NUM_PLUS = 201;
    public static final int CHAT_RC_NUM_PLUS_NEXT = 202;
    public Boolean error;
    public String message;
    public ArrayList<Message> messages;
    public User user;
    public ArrayList<ChatRoom> chat_rooms;

    public ChatInfo(){}
    public ChatInfo(Boolean error, String message, ArrayList<Message> messages, User user) {
        this.error = error;
        this.message = message;
        this.messages = messages;
        this.user = user;
    }
}
