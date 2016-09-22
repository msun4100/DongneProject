package kr.me.ansr.tab.friends.recycler;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-07-22.
 */
public class FriendsDataManager {
    private static FriendsDataManager instance;

    public static FriendsDataManager getInstance() {
        if(instance == null) {
            instance = new FriendsDataManager();
        }
        return instance;
    }
    public List<FriendsResult> items = new ArrayList<FriendsResult>();
    private FriendsDataManager() { }

    public List<FriendsResult> getList(){
        return items;
    }

    public void clearFriends(){
        this.items.clear();
    }
    public void addAllFriends(List<FriendsResult> items) {
        this.items.addAll(items);
    }
//    public void put(String groupName, FriendsResult child) {
//        GroupItem group = null;
//        for (GroupItem g : items) {
//            if (g.groupName.equals(groupName)) {
//                group = g;
//                break;
//            }
//        }
//        if (group == null) {
//            group = new GroupItem();
//            group.groupName = groupName;
//            items.add(group);
//        }
//        if(child != null){
//            group.children.add(child);
//        }
//    }

//    public void clearAllFriends() {
////        items.clear();
//        if(items.size() < 2){
//            return;
//        }
//        items.get(SectionAdapter.GROUP_FRIENDS).children.clear();
//    }

}
