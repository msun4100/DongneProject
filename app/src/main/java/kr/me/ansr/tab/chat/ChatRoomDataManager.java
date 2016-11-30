package kr.me.ansr.tab.chat;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.gcmchat.model.ChatRoom;

/**
 * Created by KMS on 2016-11-21.
 */
public class ChatRoomDataManager {
    private static ChatRoomDataManager instance;

    public static ChatRoomDataManager getInstance() {
        if(instance == null) {
            instance = new ChatRoomDataManager();
        }
        return instance;
    }
    public List<ChatRoom> items = new ArrayList<ChatRoom>();
    public int start = 0;
    public String reqDate = null;
    public int lastVisibleItemPosition = 0;
    public int totalCount = 0;
    public int mSearchOption = 0;

    private ChatRoomDataManager() { }

    public List<ChatRoom> getList(){
        return items;
    }

    public void clearAll(){
        this.items.clear();
//        for(int i=0; i<items.size(); i++){
//            items.get(i).children.clear();
//        }
    }
    public void addAll(List<ChatRoom> items) {
        this.items.addAll(items);
    }
    public void add(ChatRoom item) {
        this.items.add(item);
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
