package kr.me.ansr.tab.friends.model;

import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-21.
 */
public class FriendsInfo {
    public static final int FRIEND_DISPLAY_NUM = 5;
    public static final int FRIENDS_RC_NUM = 222;
    public static final String FRIENDS_DETAIL_USER_ID = "detailFriendsId";
//    public static final String FRIENDS_DETAIL_OBJECT = "detailObject";
    public static final String FRIENDS_DETAIL_MODIFIED_ITEM = "detailModifiedItem";
    public static final String FRIENDS_DETAIL_MODIFIED_POSITION = "detailModifiedPosition";


    public Boolean error;
    public String message;
    public ArrayList<FriendsResult> result;
    public int total;
    public FriendsResult user;
}
