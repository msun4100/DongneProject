package kr.me.ansr.gcmchat.app;

/**
 * Created by KMS on 2016-07-01.
 */
public class EndPoints {

//    public static final String BASE_URL = "http://172.20.10.2/gcm_chat/v1";
//    public static final String BASE_URL = "http://10.0.3.2:81/gcm_chat/v1";
public static final String BASE_URL = "http://10.0.3.2:3000";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
    public static final String ADD_CHAT_ROOM = BASE_URL + "/test/addChatRoom";
}
