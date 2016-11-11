package kr.me.ansr.tab.friends.model;

import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-21.
 */
public class FriendsInfo {
    public static final int FRIEND_DISPLAY_NUM = 6;
    public static final int FRIENDS_RC_NUM = 223;
    public static final String FRIENDS_DETAIL_USER_ID = "detailFriendsId";
//    public static final String FRIENDS_DETAIL_OBJECT = "detailObject";
    public static final String FRIENDS_DETAIL_MODIFIED_ITEM = "detailModifiedItem";
    public static final String FRIENDS_DETAIL_MODIFIED_POSITION = "detailModifiedPosition";

    public static final String STATUS_SEND = "0";
    public static final String STATUS_RECEIVE = "00";
    public static final String STATUS_ACCEPT = "1";
    public static final String STATUS_DECLINE = "2";
    public static final String STATUS_BLOCK = "3";


    public Boolean error;
    public String message;
    public ArrayList<FriendsResult> result;
    public int total;
    public int sameCnt; //FriendListActivity에서 사용
    public FriendsResult user;
}
