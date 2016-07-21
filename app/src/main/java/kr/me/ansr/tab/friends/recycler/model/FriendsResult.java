package kr.me.ansr.tab.friends.recycler.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-21.
 */
public class FriendsResult implements Serializable{
    int userId;
    String email;
    String pushId;
    String username;
    String pic;
    String provider;
    boolean isFriend;
    String temp;
    Location location;
    ArrayList<String> sns;
    ArrayList<String> desc;
    Job job;
    ArrayList<Univ> univ;
}
