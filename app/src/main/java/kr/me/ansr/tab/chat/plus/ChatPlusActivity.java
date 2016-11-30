package kr.me.ansr.tab.chat.plus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.database.DBManager;
import kr.me.ansr.gcmchat.activity.ChatRoomActivity;
import kr.me.ansr.gcmchat.model.ChatInfo;
import kr.me.ansr.gcmchat.model.ChatRoom;
import kr.me.ansr.gcmchat.model.Message;
import kr.me.ansr.tab.chat.ChatRoomInfo;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.FriendsDataManager;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import okhttp3.Request;

public class ChatPlusActivity extends AppCompatActivity {

    TextView toolbarTitle;
    ImageView toolbarMenu;
    ImageView searchIcon;
    CustomEditText searchInput;
    //============
    RecyclerView recyclerView;
    ChatPlusAdapter mAdapter;

    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());
    public int mSearchOption = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_plus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.c_artboard_final_title_cancel);
        toolbar.setBackgroundResource(R.drawable.d_artboard_final_list_title2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("");
        toolbarMenu = (ImageView)toolbar.findViewById(R.id.toolbar_menu1);
        toolbarMenu.setImageResource(R.drawable.d_title_confirm_selector);
        toolbarMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItems = mAdapter.getCheckedItemPositions();
                ArrayList<FriendsResult> list = new ArrayList<>();

                Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory(getClass().getSimpleName()).setAction("Press Button").setLabel("Add Chat Room").build());
                switch (mAdapter.getMode()){
                    case ChatPlusAdapter.MODE_MULTIPLE:
                        String roomName = PropertyManager.getInstance().getUserName() + ",";
                        for(int i=0; i<mAdapter.getItemCount(); i++){
                            if(checkedItems.get(i)){
                                list.add(mAdapter.getItem(i));
                                roomName += mAdapter.getItem(i).getUsername()+ ",";
                            }
                        }
                        roomName = roomName.substring(0, roomName.length() - 1 ); //맨 마지막 ','제거
                        if(list != null){
                            Toast.makeText(ChatPlusActivity.this, "checkedItems:"+mAdapter.getCheckedItemPositions(), Toast.LENGTH_SHORT).show();
                            Log.e("checkedItems", list.toString());
                            searchRoom(list, roomName);
//                            int num = DBManager.getInstance().searchRoomName(roomName);
//                            Intent intent = new Intent(ChatPlusActivity.this, ChatRoomActivity.class);
//                            intent.putExtra("chat_room_id", ""+num);
//                            intent.putExtra("name", roomName);
////                            intent.putExtra("mItem", list.get(0));
//                            intent.putExtra("mList", list);
//                            startActivityForResult(intent, ChatInfo.CHAT_RC_NUM_PLUS_NEXT);
//                            finish();
                        }
                        break;
                    case ChatPlusAdapter.MODE_SINGLE:
                        int checkedPos = mAdapter.getCheckItemPosition();
                        FriendsResult item = mAdapter.getItem(checkedPos);
                        if(item != null){
                            Toast.makeText(ChatPlusActivity.this, "checkedItem:"+mAdapter.getItem(mAdapter.getCheckItemPosition()), Toast.LENGTH_SHORT).show();
                            Log.e("checkedItem", item.toString());
                            list.clear();
                            list.add(item);
//                            int num = DBManager.getInstance().searchRoomName(item.username);
//                            Intent intent = new Intent(ChatPlusActivity.this, ChatRoomActivity.class);
//                            intent.putExtra("chat_room_id", "-1");
                            searchRoom(list, item.username + ","+PropertyManager.getInstance().getUserName());
//                            intent.putExtra("chat_room_id", num);
//                            intent.putExtra("name", item.username + ","+PropertyManager.getInstance().getUserName());
////                            intent.putExtra("mItem", item);
//                            intent.putExtra("mList", list);
//                            startActivityForResult(intent, ChatInfo.CHAT_RC_NUM_PLUS_NEXT);
                        }
                        break;
                    default:
                        break;
                }

            }
        });
        //        search views
        searchInput = (CustomEditText)findViewById(R.id.custom_editText1);
        searchInput.setHint(getResources().getString(R.string.chat_search_hint_msg));
        searchIcon = (ImageView)findViewById(R.id.image_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String input = searchInput.getText().toString();
                if(!input.equals("") && input != null){
                    Toast.makeText(ChatPlusActivity.this, "searchInput:"+input, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //end of default init
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        if(!dialog.isShowing()){
//                            refreshLayout.setRefreshing(false);
//                        }

//                        일단 그냥 1초 있다가 사라지게, 현재 구현한 것은 getMoreItem() 같은 것 없이 전체리스트 불러오도록 처리
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });   //this로 하려면 implements 하고 오버라이드 코드 작성하면 됨.
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    getMoreItem();
                }

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mAdapter.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (totalItemCount > 0 && lastVisibleItemPosition != RecyclerView.NO_POSITION && (totalItemCount - 1 <= lastVisibleItemPosition)) {
                    isLast = true;
                } else {
                    isLast = false;
                }
            }
        });
        mAdapter = new ChatPlusAdapter();
        mAdapter.setMode(ChatPlusAdapter.MODE_MULTIPLE);
//        mAdapter.setMode(ChatPlusAdapter.MODE_SINGLE);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Log.e("ChatPlus->", "onItemClickListener");
//                FriendsResult data = mAdapter.getItem(position);
//                Log.e("ChatPlus->data", data.toString());
//                Intent intent = new Intent(ChatPlusActivity.this, FriendsDetailActivity.class);
//                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
//                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
//                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, position);
//                startActivity(intent);
            }
        });
        mAdapter.setOnAdapterItemClickListener(new ChatPlusAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(ChatPlusAdapter adapter, View view, FriendsResult item, int type) {
                switch (type) {
                    case 100:
                        Toast.makeText(ChatPlusActivity.this, "nameView click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(ChatPlusActivity.this, "imageView click\nmItem.temp:"+ item.temp +"\n"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 300:
                        Toast.makeText(ChatPlusActivity.this, "statusView click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
//                    case 400:
//                        Toast.makeText(ChatPlusActivity.this, "checkView click"+ item.toString(), Toast.LENGTH_SHORT).show();
//                        break;
                    default:
                        Toast.makeText(ChatPlusActivity.this, "default click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(ChatPlusActivity.this);
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(ChatPlusActivity.this));

//        start = 0;
//        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        initData();

        Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());

    }

    ProgressDialog dialog = null;
    boolean isMoreData = false;
    private static final int DISPLAY_NUM = 4;
    private int start=0;
    private String reqDate = null;

    private void searchRoom(final ArrayList<FriendsResult> mList, final String roomName){
        final ArrayList<String> list = new ArrayList<>();
        for(FriendsResult fr : mList){
            list.add(""+fr.getUserId());
        }
        list.add(""+PropertyManager.getInstance().getUserId());

        Log.d("TAG", "searchRoom: "+ list.toString());


        NetworkManager.getInstance().postDongneCheckChatRooms(ChatPlusActivity.this, list,
                new NetworkManager.OnResultListener<ChatRoomInfo>() {
                    @Override
                    public void onSuccess(Request request, ChatRoomInfo result) {
                        if(result.error.equals(false)){
                            Intent intent = new Intent(ChatPlusActivity.this, ChatRoomActivity.class);
                            if(result.chat_rooms.size() == 1 ){
                                ChatRoom cr = result.chat_rooms.get(0);
                                intent.putExtra("chat_room_id", ""+cr.getId());
                                intent.putExtra("name", roomName);
                                intent.putExtra("mList", mList);
                                startActivityForResult(intent, ChatInfo.CHAT_RC_NUM_PLUS_NEXT);
                            } else {
//                                -1로 넘어감
                                intent.putExtra("chat_room_id", "-1");
                                intent.putExtra("name", roomName);
                                intent.putExtra("mList", mList);
                                startActivityForResult(intent, ChatInfo.CHAT_RC_NUM_PLUS_NEXT);
                            }
                        } else {
                            Toast.makeText(ChatPlusActivity.this, ""+result.message, Toast.LENGTH_SHORT).show();
                        }
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                        finish();
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                        finish();
                    }
                });
        dialog = new ProgressDialog(ChatPlusActivity.this);
        dialog.setTitle("Loading....");
        dialog.show();
    }


    private void initData(){
        NetworkManager.getInstance().getDongneFriendsStatus(ChatPlusActivity.this, "" + 1, //status
                new NetworkManager.OnResultListener<FriendsInfo>() {
                    @Override
                    public void onSuccess(Request request, FriendsInfo result) {
                        if(result.error.equals(false)){
                            mAdapter.clear();
                            mAdapter.addAllFriends(result.result);
                        } else {
                            Toast.makeText(ChatPlusActivity.this, ""+result.message, Toast.LENGTH_SHORT).show();
                        }
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(ChatPlusActivity.this);
        dialog.setTitle("Loading....");
        dialog.show();
    }
    private void forcedFinish(){
        finish();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ChatPlusActivity:", "onActivityResult: "+ requestCode);
        switch (requestCode) {
            case ChatInfo.CHAT_RC_NUM_PLUS_NEXT:
                //그냥 리스트 클릭해서 들어가는 경우
                if (resultCode == RESULT_OK) {
                    Log.e("PLUS_NEXT", "RESULT_OK");
                    Bundle extraBundle = data.getExtras();
                    String returnString = extraBundle.getString("return");
                    Message lastMsg = (Message)extraBundle.getSerializable("lastMsg");
                    Intent intent = new Intent();
                    intent.putExtra("return", returnString);
                    intent.putExtra("lastMsg", lastMsg);
                    this.setResult(RESULT_OK, intent);
                    finish();
                }
                if(requestCode == RESULT_CANCELED){
                    Log.e("PLUS_NEXT", "RESULT_CANCELED");
                    Bundle extraBundle = data.getExtras();
                    String returnString = extraBundle.getString("return");
                    Intent intent = new Intent();
                    intent.putExtra("return", returnString);
                    this.setResult(RESULT_CANCELED, intent);
                    finish();
                }
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

}
