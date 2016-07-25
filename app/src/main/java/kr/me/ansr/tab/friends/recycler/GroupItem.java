package kr.me.ansr.tab.friends.recycler;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-07-20.
 */
public class GroupItem {
    public String groupName;
//    List<ChildItem> children = new ArrayList<ChildItem>();
    public List<FriendsResult> children = new ArrayList<FriendsResult>();
}
