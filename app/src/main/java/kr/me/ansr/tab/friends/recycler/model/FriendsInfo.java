package kr.me.ansr.tab.friends.recycler.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-21.
 */
public class FriendsInfo {
    public Boolean error;
    public String message;
    public ArrayList<FriendsResult> result;
    public int total;
}
