package kr.me.ansr.tab.friends.tabtwo;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.GroupItem;

/**
 * Created by KMS on 2016-07-22.
 * 검색시 백업용
 */
public class FriendsTwoDataManager {
    private static FriendsTwoDataManager instance;

    public static FriendsTwoDataManager getInstance() {
        if(instance == null) {
            instance = new FriendsTwoDataManager();
        }
        return instance;
    }
    public List<GroupItem> items = new ArrayList<GroupItem>();
    public int start = 0;
    public String reqDate = null;
    public int lastVisibleItemPosition = 0;
    public int totalCount = 0;
    public int mSearchOption = 0;

    private FriendsTwoDataManager() { }

    public List<GroupItem> getList(){
        return items;
    }

    public void clearAll(){
        this.items.clear();
//        for(int i=0; i<items.size(); i++){
//            items.get(i).children.clear();
//        }
    }
    public void addAll(List<GroupItem> items) {
        this.items.addAll(items);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public int getLastVisibleItemPosition() {
        return lastVisibleItemPosition;
    }

    public void setLastVisibleItemPosition(int lastVisibleItemPosition) {
        this.lastVisibleItemPosition = lastVisibleItemPosition;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getmSearchOption() {
        return mSearchOption;
    }

    public void setmSearchOption(int mSearchOption) {
        this.mSearchOption = mSearchOption;
    }
}
