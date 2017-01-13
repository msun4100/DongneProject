package kr.me.ansr.gcmchat.app;

/**
 * Created by KMS on 2016-07-01.
 */
public class Config {

    // flag to identify whether to show single line
    // or multi line test push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;
    public static final int PUSH_TYPE_NEW_ROOM = 3;
    public static final int PUSH_TYPE_NOTIFICATION = 4;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final int DISPLAY_NUM = 15;
    public static final String SERVER_URL = "http://ec2-52-79-144-51.ap-northeast-2.compute.amazonaws.com:3000"; //
//    public static final String SERVER_URL = "http://ec2-52-78-76-64.ap-northeast-2.compute.amazonaws.com:3000";
//    public static final String SERVER_URL = "http://10.0.3.2:3000";
}
