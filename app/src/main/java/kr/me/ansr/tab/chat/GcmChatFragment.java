package kr.me.ansr.tab.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.playlog.internal.LogEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.account.ConfirmDialogFragment;
import kr.me.ansr.common.account.LogoutDialogFragment;
import kr.me.ansr.database.DBManager;
import kr.me.ansr.gcmchat.activity.ChatRoomActivity;
import kr.me.ansr.gcmchat.activity.LoginActivity;
import kr.me.ansr.gcmchat.adapter.ChatRoomsAdapter;
import kr.me.ansr.gcmchat.app.Config;
import kr.me.ansr.gcmchat.app.EndPoints;
import kr.me.ansr.gcmchat.gcm.GcmIntentService;
import kr.me.ansr.gcmchat.gcm.NotificationUtils;
import kr.me.ansr.gcmchat.helper.SimpleDividerItemDecoration;
import kr.me.ansr.gcmchat.model.ChatInfo;
import kr.me.ansr.gcmchat.model.ChatRoom;
import kr.me.ansr.gcmchat.model.Message;
import kr.me.ansr.gcmchat.model.User;
import kr.me.ansr.tab.chat.plus.ChatPlusActivity;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.FriendsDataManager;
import kr.me.ansr.tab.friends.tabtwo.FriendsTwoFragment;

/**
 * Created by KMS on 2016-07-06.
 */
public class GcmChatFragment extends PagerFragment {

//    private String TAG = "GcmChatFragment>>";
    private String TAG = GcmChatFragment.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    AppCompatActivity activity;
    TextView emptyMsg;
    ImageView searchIcon;
    CustomEditText searchInput;

    public static int lastRoomSize = 0;
    public static final int DIALOG_RC_ROOM_DELETE = 203;
//    public static int unreadCount = 0;
    InputMethodManager imm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gcmchat, container, false);
        setHasOptionsMenu(true);
        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
//            launchLoginActivity();
            Toast.makeText(getActivity(), "getUser() == null ", Toast.LENGTH_LONG).show();
        }
        activity = (AppCompatActivity) getActivity();
        emptyMsg = (TextView)view.findViewById(R.id.text_empty_msg);
        emptyMsg.setText(getResources().getString(R.string.empty_chat_msg));
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        /**
         * Broadcast receiver calls in two scenarios
         * 1. gcm registration is completed
         * 2. when new push notification is received
         * */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    subscribeToGlobalTopic();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                    Log.e(TAG, "GCM registration id is sent to our server");

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };

        chatRoomArrayList = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(getActivity(), chatRoomArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getActivity(), recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                ChatRoom chatRoom = chatRoomArrayList.get(position);
//                removeUnreadCount(""+chatRoom.getId());
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("chat_room_id", ""+chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                startActivityForResult(intent, ChatInfo.CHAT_RC_NUM);
                Log.e(TAG, ""+chatRoom.getId());
            }

            @Override
            public void onLongClick(View view, int position) {
                ChatRoom chatRoom = chatRoomArrayList.get(position);
                LogoutDialogFragment mDialogFragment = LogoutDialogFragment.newInstance();  //자주 안쓰니까 newInstance 안함.
                Bundle b = new Bundle();
                b.putString("tag", LogoutDialogFragment.TAG_ROOM_DELETE);
                b.putString("title","해당 채팅방을 나가시겠습니까?.");
                b.putString("body",  "모든 채팅 내용이 삭제 됩니다.");
                b.putInt("chatRoomId", chatRoom.id);
                mDialogFragment.setArguments(b);
                mDialogFragment.setTargetFragment(GcmChatFragment.this, DIALOG_RC_ROOM_DELETE);
                mDialogFragment.show(getActivity().getSupportFragmentManager(), "logoutDialog");
            }
        }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy){
                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0) {
//                    // Scroll Down
//                    if (fab.isShown()) {
//                        fab.hide();
//                    }
//                } else if (dy < 0) {
//                    // Scroll Up
//                    if (!fab.isShown()) {
//                        fab.show();
//                    }
//                }
            }
        });
        /**
         * Always check for google play services availability before
         * proceeding further with GCM
         * */
        if (checkPlayServices()) {
            registerGCM();
            fetchChatRooms();
//            fetchChatRoomsVolley();
        }


        //        search views
        searchInput = (CustomEditText)view.findViewById(R.id.custom_editText1);
        searchInput.setHint(getResources().getString(R.string.chat_search_hint_msg));
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
//                    String input = searchInput.getText().toString();
//                    if(!input.equals("") && input != null){
//                        searchProcess(input);
//                    } else {
//                        Toast.makeText(getActivity(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    }
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        searchInput.addTextChangedListener(new MyTextWatcher(searchInput));
        searchIcon = (ImageView)view.findViewById(R.id.image_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                String input = searchInput.getText().toString();
//                if(!input.equals("") && input != null){
//                    if (isSearching == false) {
//                        searchProcess(input);
//                    } else if(isSearching == true){
//                        refreshList();
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
//                }
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                searchIcon.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
                searchInput.setText("");
                mAdapter.filter("", (ArrayList<ChatRoom>) ChatRoomDataManager.getInstance().getList()); //empty text --> restore list
            }
        });

        //	on 1118
        Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName("TAB2_"+getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());

        return view;
    }   //end of onCreateView

    public boolean isSearching = false;
    private void searchProcess(String input){
        ChatRoomDataManager.getInstance().clearAll();
        for(ChatRoom cr : chatRoomArrayList){
            if (cr.name.contains(input)){
                ChatRoomDataManager.getInstance().add(cr);
            }
        }

        if(ChatRoomDataManager.getInstance().getList().size() > 0){
            isSearching = true;
            searchIcon.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
            chatRoomArrayList.clear();
//            mAdapter.notifyDataSetChanged();
            chatRoomArrayList.addAll(ChatRoomDataManager.getInstance().getList());
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), "'"+input+"' 채팅방을 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
        }
    }
    private void showLayout(){
        if (mAdapter.getItemCount() > 0){
            emptyMsg.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyMsg.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


//    private  void isRoomExists(String chatRoomId, Message message){
//        if(!DBManager.getInstance().isRoomExists(Integer.parseInt(chatRoomId))){
//            //존재하지 않으면 생성
//            ChatRoom cr = new ChatRoom(Integer.parseInt(chatRoomId),
//                    message.user.name,/*네임*/
//                    message.message, /* 라스트메시지*/ message.createdAt, /* 타임스탬프*/ 1, /* 언리드카운트,*/ "", /*image url*/ 0   /* bgColor*/, Integer.parseInt(PropertyManager.getInstance().getUserId()));
//            DBManager.getInstance().insertRoom(cr);
//            refreshList();
//        }
//    }
    /**
     * Handles new push notification
     */

    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);
        isFirst = intent.getBooleanExtra("isFirst", false);
        if(isFirst){
            num++;
        }
        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == Config.PUSH_TYPE_CHATROOM) {
            Message message = (Message) intent.getSerializableExtra("message");
            String chatRoomId = intent.getStringExtra("chat_room_id");
            Log.e("PUSH_TYPE_CHATROOM", ""+Config.PUSH_TYPE_CHATROOM);
            Log.e("message", message.toString());
            Log.e("chatRoomId", chatRoomId);

            if (message != null && chatRoomId != null) {
//                updateRow(chatRoomId, message);
//                MainActivity.setChatCount(num); //0보다 크면 N 뜨니까
                refreshList();
                Log.d(TAG, "handlePushNotification: "+num);
            }
        } else if (type == Config.PUSH_TYPE_USER) {
            // push belongs to user alone
            // just showing the message in a toast
            Message message = (Message) intent.getSerializableExtra("message");
            Toast.makeText(getActivity(), "PUSH_TYPE_USER: " + message.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "handlePushNotification: "+"PUSH_TYPE_USER " + message.getMessage()  );
        } else if (type == Config.PUSH_TYPE_NEW_ROOM) {
            // push from addChatRoom url
            // just invoke refreshList() and updateRow()
            Message message = (Message) intent.getSerializableExtra("message");
            String chatRoomId = intent.getStringExtra("chat_room_id");
            Log.e("PUSH_TYPE_NEW_ROOM", ""+Config.PUSH_TYPE_NEW_ROOM);
            Log.e("message", message.toString());
            Log.e("chatRoomId", chatRoomId);

            if (message != null && chatRoomId != null) {
//                updateRow(chatRoomId, message);
//                MainActivity.setChatCount(num);
                refreshList();
                Log.d(TAG, "handlePushNotification: "+num);
            }
        } else if (type == Config.PUSH_TYPE_NOTIFICATION){
            Message message = (Message) intent.getSerializableExtra("message");
            Toast.makeText(getActivity(), "PUSH_TYPE_NOTIFICATION" + message.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "handlePushNotification: "+"PUSH_TYPE_NOTIFICATION " + message.getMessage()  );
        }
    }

    /**
     * Updates the chat list unread count and the last message
     */
    //채팅방 들어갔다 나올 때
    private void updateRowLastMsg(String chatRoomId, Message message) {
        for (ChatRoom cr : chatRoomArrayList) {
            String crGetId = ""+cr.getId();
            if (crGetId.equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message.getMessage());
                cr.setUnreadCount(0);
                cr.setTimestamp(message.getCreatedAt());
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    //push message 받으면
    private void updateRow(String chatRoomId, Message message) {
        for (ChatRoom cr : chatRoomArrayList) {
            String crGetId = ""+cr.getId();
            if (crGetId.equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message.getMessage());
                cr.setUnreadCount(cr.getUnreadCount() + 1);
                cr.setTimestamp(message.getCreatedAt());
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                Log.d(TAG, "updateRow: "+ num);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    //채팅방 들어갈때 unReadCount 초기화
    private void removeUnreadCount(String chatRoomId) {
        for (ChatRoom cr : chatRoomArrayList) {
            String crGetId = ""+cr.getId();
            if (crGetId.equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage("");  //last message 초기화든 값 안 넣으면 레이아웃이 겹쳐짐
                cr.setUnreadCount(0);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);

                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * fetching the chat rooms by making http call
     */
    int num = 0;
    boolean isFirst = true;
    private void fetchChatRooms(){
//        if(FriendsTwoFragment.mAdapter != null && FriendsTwoFragment.mAdapter.getItemCount() > 0){
//            Log.d(TAG, "fetchChatRooms: " + FriendsTwoFragment.mAdapter.getItemCount());
//        }
        NetworkManager.getInstance().postDongneChatRooms(getActivity(), PropertyManager.getInstance().getUserId(), new NetworkManager.OnResultListener<ChatRoomInfo>() {
            @Override
            public void onSuccess(okhttp3.Request request, ChatRoomInfo result) {
                if (result.error.equals(false)) {
                    List<ChatRoom> list = DBManager.getInstance().searchAllRoom();  //'N' 표시 전 비교 위해
                    String lastJoin = "";
                    if(result.chat_rooms != null){
                        ArrayList<ChatRoom> chatRoomsArray = result.chat_rooms;
                        for(int i=0; i<chatRoomsArray.size(); i++){
                            ChatRoom cr = new ChatRoom();
                            cr.setId(chatRoomsArray.get(i).id);
                            cr.setName(getSortedRoomName(chatRoomsArray.get(i).name));
                            cr.setLastMessage(chatRoomsArray.get(i).lastMessage);  //modification required
                            cr.setTimestamp(chatRoomsArray.get(i).timestamp);   //Adapter 의 함수 자체에서 seoul시간으로 변환됨
                            cr.activeUser = Integer.parseInt(PropertyManager.getInstance().getUserId());
                            cr.users = chatRoomsArray.get(i).users; //mongodb field

                            for(ChatRoom dbRoom : list){
                                if(chatRoomsArray.get(i).id == dbRoom.id){
                                    //compareTo : a > b :긍정값,  a < b 부정값 a==b :0
//                                    cr.activeUser = dbRoom.activeUser;
                                    lastJoin = dbRoom.lastJoin; //방탈했다가 다시 받는 경우 고려
                                    if(chatRoomsArray.get(i).timestamp.compareTo(dbRoom.timestamp) > 0 ){
                                        //라스트메시지의 타임스탬프가 큰경우 == unread_message가 있다.
                                        cr.setUnreadCount(1); // N 띄움
//                                        num++;
//                                        Log.e(TAG, "onSuccess: num++ "+num );
                                    } else {
                                        cr.setUnreadCount(0); // N 없앰
                                    }
                                    break;
                                }
                            }
                            //채팅방 나가면 단말기 디비에 lastJoin을 ""로(채팅방이 액티브상태면 라스트조인이 ""이 아님. )
                            //방탈했다가 다시들어온 시간보다 메시지 시간이 크면
                            if( !lastJoin.equals("")){
                                chatRoomArrayList.add(cr);
                            }
                        }
//                        lastRoomSize = chatRoomsArray.size();
//                        mAdapter.setOriginItems(chatRoomArrayList); //using at filter list
                        ChatRoomDataManager.getInstance().clearAll();
                        ChatRoomDataManager.getInstance().addAll(chatRoomArrayList);
                        Log.e(TAG, "onSuccess: num "+num );
                        Log.e(TAG, "onSuccess: isFirst "+isFirst );
                        if(num > 0 && isFirst) {	//isFirst는 isVisibleToUser에서 false로 변경 됨
                            MainActivity.setChatCount(num);
                        } else {
                            MainActivity.setChatCount(0);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "error: true\n " + result.message, Toast.LENGTH_SHORT).show();
                }
                mAdapter.notifyDataSetChanged();
                showLayout();
                // subscribing to all chat room topics
//                subscribeToAllTopics();
                if(result.chat_rooms != null && result.chat_rooms.size() > 0){
                    if(lastRoomSize != result.chat_rooms.size()){   //매번 sub 하는 걸 막음 기존에 받은 사이즈와 서버에서 받은 룸사이즈가 다른 경우만 subscribe All.
                        lastRoomSize = result.chat_rooms.size();
                        subscribeToAllTopics(result.chat_rooms);
                    }
                }

            }

            @Override
            public void onFailure(okhttp3.Request request, int code, Throwable cause) {
                if(chatRoomArrayList != null){
                    chatRoomArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                }
                Toast.makeText(getActivity(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchChatRoomsVolley() {
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.CHAT_ROOMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");
//                        subscribeToAllTopics(chatRoomsArray);
                        for (int i = 0; i < chatRoomsArray.length(); i++) {
                            JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                            ChatRoom cr = new ChatRoom();
                            cr.setId(chatRoomsObj.getInt("chat_room_id"));
                            cr.setName(chatRoomsObj.getString("name"));
                            cr.setLastMessage("last msg..");
                            Random r = new Random();
                            cr.setUnreadCount(r.nextInt(10)+1);
//                            cr.setLastMessage("");
//                            cr.setUnreadCount(0);
                            cr.setTimestamp(chatRoomsObj.getString("created_at"));
                            chatRoomArrayList.add(cr);
                        }
                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(getActivity(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                mAdapter.notifyDataSetChanged();

                // subscribing to all chat room topics
//                subscribeToAllTopics();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getActivity(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    // subscribing to global topic
    private void subscribeToGlobalTopic() {
        Intent intent = new Intent(getActivity(), GcmIntentService.class);
        intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
        intent.putExtra(GcmIntentService.TOPIC, Config.TOPIC_GLOBAL);
        getActivity().startService(intent);
    }

    // Subscribing to all chat room topics
    // each topic name starts with `topic_` followed by the ID of the chat room
    // Ex: topic_1, topic_2
//    private void subscribeToAllTopics() {
//        for (ChatRoom cr : chatRoomArrayList) {
//            Intent intent = new Intent(getActivity(), GcmIntentService.class);
//            intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
//            intent.putExtra(GcmIntentService.TOPIC, "topic_" + cr.getId());
//            getActivity().startService(intent);
//        }
//    }

    private void subscribeToAllTopics(List<ChatRoom> list) {
        for (ChatRoom cr : list) {
            Intent intent = new Intent(getActivity(), GcmIntentService.class);
            intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
            intent.putExtra(GcmIntentService.TOPIC, "topic_" + cr.getId());
            getActivity().startService(intent);
        }
    }


    private void launchLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(getActivity(), GcmIntentService.class);
        intent.putExtra("key", "register");
        getActivity().startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getActivity(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
//                finish();
            }
            return false;
        }
        return true;
    }





//===========================================================
    @Override
    public void onPageCurrent() {
        super.onPageCurrent();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // ...
//            if (!fab.isShown()) {
//                fab.show();
//            }
            fab.setVisibility(View.GONE);

            if( activity != null ){
                activity.getSupportActionBar().setTitle("");
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.d_list_title1));
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.d_title_edit_selector);
                ((MainActivity)getActivity()).getToolbarTitle().setText("");
            }
            PropertyManager.getInstance().setIsTab2Visible("visible");
            num = 0;
            isFirst = false;
            refreshList();  // == initData 서버 요청까지하고 subscribe까지
            if(withIntent){
//                Log.d(TAG, "setUserVisibleHint: "+ getActivity().getIntent().toString());
//                Bundle extraBundle = ((MainActivity)getActivity()).getIntent().getExtras();
//                FriendsResult item = (FriendsResult) extraBundle.getSerializable("mItem");
//                String c_id = extraBundle.getString("chat_room_id");
//                String roomname = extraBundle.getString("name");
//                int rc_num = extraBundle.getInt("rc_num", -1);
                ArrayList<FriendsResult> itemList = new ArrayList<>();
                itemList.add(item);
                Intent i = new Intent(getActivity(), ChatRoomActivity.class);
                i.putExtra("chat_room_id", ""+c_id);
                i.putExtra("name", roomName);
                i.putExtra("mList", itemList);
                Log.e(TAG, "setUserVisibleHint: "+c_id);
                Log.e(TAG, "setUserVisibleHint: "+roomName);
                Log.e(TAG, "setUserVisibleHint: "+itemList.toString());
                startActivityForResult(i, rc_num);
                withIntent = false;

            }
            withIntent = false;
        } else {
            PropertyManager.getInstance().setIsTab2Visible("inVisible");
        }
        Log.d(TAG, "setUserVisibleHint: istab2" + PropertyManager.getInstance().getIsTab2Visible());
    }


    MenuItem menuNext;
    ImageView imageNext;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_f_board, menu);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        menuNext = menu.findItem(R.id.menu_board_write);
        imageNext = new ImageView(getActivity());
        imageNext.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageNext.setPadding(16, 0, 16, 0);
        imageNext.setImageResource(R.drawable.d_title_plus_selector);
        imageNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                DBManager.getInstance().deleteRoom(21);   //delete
////                ArrayList<ChatRoom> list = (ArrayList<ChatRoom>)DBManager.getInstance().searchRoom("new"); //keyword search
//                ArrayList<ChatRoom> list = (ArrayList<ChatRoom>)DBManager.getInstance().searchAllRoom();    //show all room threads
//                if(list != null && list.size() > 0){
//                    for(ChatRoom cr : list){
//                        Log.e("chat list", cr.toString());
//                    }
//                }
//                Toast.makeText(getActivity(), ""+ FriendsDataManager.getInstance().getList().size(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ChatPlusActivity.class);
                intent.putExtra("key", "passed key");
                startActivityForResult(intent, ChatInfo.CHAT_RC_NUM_PLUS); //tabHost가 있는 BoardFragment에서 리절트를 받음

            }
        });
        menuNext.setActionView(imageNext);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Toast.makeText(getActivity(), "홈클릭", Toast.LENGTH_SHORT).show();
            ArrayList<ChatRoom> list = (ArrayList<ChatRoom>)DBManager.getInstance().searchAllRoom();
            Log.e(TAG, "onOptionsItemSelected: ChatRoom Size"+ list.size() );
            for(ChatRoom cr : list){
                Log.e(TAG, "onOptionsItemSelected: "+cr.id+" "+DBManager.getInstance().searchAllMsg(cr.id).toString() );
            }
            refreshList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("GcmChat", "onActivityResult: "+ requestCode);
        switch (requestCode) {
            case ChatInfo.CHAT_RC_NUM:
                //그냥 리스트 클릭해서 들어가는 경우
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle extraBundle = data.getExtras();
                    String returnString = extraBundle.getString("return");
                    Message lastMsg = (Message)extraBundle.getSerializable("lastMsg");
                    if(lastMsg != null && returnString.equals("success")){
                        Log.e("afterChatRoomActivity", "success");
                        Toast.makeText(getActivity(), "return key== "+requestCode+"/"+returnString, Toast.LENGTH_LONG).show();
                        Log.e("after lastMsg ", lastMsg.toString());
                        ChatRoom cr = null;
                        if(DBManager.getInstance().isRoomExists(lastMsg.chat_room_id) == true){
                            cr = DBManager.getInstance().searchRoom(lastMsg.chat_room_id).get(0);
                            Log.e("after cr", cr.toString());
                        }
                        Log.e("after cr lst", DBManager.getInstance().searchRoom(lastMsg.chat_room_id).toString());
                        if(cr != null && cr.id == lastMsg.chat_room_id){
                            cr.unreadCount = 0;
                            cr.setTimestamp(lastMsg.getCreatedAt());    //updates room's last joined timeStamp
                            DBManager.getInstance().updateRoom(cr);
                        }
                    } else {
                        Log.e("afterChatRoomActivity", "failure");
                        Toast.makeText(getActivity(), "return key== "+requestCode+"/"+returnString, Toast.LENGTH_SHORT).show();
                    }
                    num = 0;
                    isFirst = false;
//                    refreshList();
                    if(data.getAction() == null){
                        //기존대로 리프레시 하는 케이스
                        refreshList();
                    } else if (data.getAction() != null && data.getAction().equals(ChatRoomActivity.ACTION_GET_ROOM)){
                        //Action값이 있는 경우  === ChatRoom -> FriendsDetail -> 1:1대화버튼클릭 한 경우
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<FriendsResult> itemList = new ArrayList<>();
                                itemList.add(item);
                                Intent i = new Intent(getActivity(), ChatRoomActivity.class);
                                i.putExtra("chat_room_id", ""+c_id);
                                i.putExtra("name", roomName);
                                i.putExtra("mList", itemList);
                                Log.e(TAG, "setUserVisibleHint: "+c_id);
                                Log.e(TAG, "setUserVisibleHint: "+roomName);
                                Log.e(TAG, "setUserVisibleHint: "+itemList.toString());
                                startActivityForResult(i, rc_num);
                                withIntent = false;
                            }
                        }, 100);
                    }
                }
                break;
            case ChatInfo.CHAT_RC_NUM_PLUS:
                //플러스 아이콘 클릭해서 들어가는 경우 == 201
                Log.e("resultCode", ""+resultCode);
                if (resultCode == getActivity().RESULT_CANCELED) {
                    if(data != null){
//                        data는 null이라 아마 코드실행 안됨
                        Bundle extraBundle = data.getExtras();
                        String returnString = extraBundle.getString("return");
                        Log.e("returnString", returnString);
                    }
                } else if(resultCode == getActivity().RESULT_OK){
                    Bundle extraBundle = data.getExtras();
                    String returnString = extraBundle.getString("return");
                    Message lastMsg = (Message)extraBundle.getSerializable("lastMsg");
                    if(lastMsg != null && returnString.equals("success")){
                        Log.e("afterChatRoomActivity", "success");
                        Toast.makeText(getActivity(), "return key== "+requestCode+"/"+returnString, Toast.LENGTH_LONG).show();
                        Log.e("after lastMsg ", lastMsg.toString());
                        ChatRoom cr = null;
                        if(DBManager.getInstance().isRoomExists(lastMsg.chat_room_id) == true){
                            cr = DBManager.getInstance().searchRoom(lastMsg.chat_room_id).get(0);
                            Log.e("after cr1", cr.toString());
                        }
                        Log.e("after cr lst", DBManager.getInstance().searchRoom(lastMsg.chat_room_id).toString());
                        if(cr != null && cr.id == lastMsg.chat_room_id){
                            cr.unreadCount = 0;
                            cr.setTimestamp(lastMsg.getCreatedAt());    //updates room's last joined timeStamp
                            DBManager.getInstance().updateRoom(cr);
                        }
                    } else {
                        Log.e("afterChatRoomActivity", "failure");
                        Toast.makeText(getActivity(), "return key== "+requestCode+"/"+returnString, Toast.LENGTH_SHORT).show();
                    }
                    num = 0;
                    isFirst = false;
                    refreshList();
                }
                break;
            case DIALOG_RC_ROOM_DELETE:
                if (resultCode == getActivity().RESULT_OK) {
                    Log.e("DIALOG_RC_ROOM_DELETE","aaaaaaaa");
                    Bundle extraBundle = data.getExtras();
                    int chatRoomId = extraBundle.getInt("chatRoomId", -1);
                    if(chatRoomId != -1){
//                        List<Message> list = DBManager.getInstance().searchAllMsg(chatRoomId);
//                        Log.e(TAG, "onActivityResult: msgs1"+ list.toString() );
                        if(DBManager.getInstance().deleteRoomMsg(chatRoomId) >= 0){
                            if(DBManager.getInstance().isRoomExists(chatRoomId)){
//                                지우는게 아니라 activeUser만 0으로 고쳐줌 x 유니크 제약조건 위배 --> 라스트조인 널로
                                ChatRoom cr = DBManager.getInstance().searchRoom(chatRoomId).get(0);
                                cr.lastJoin = "";
                                if(DBManager.getInstance().updateRoom(cr) >= 0){
                                    Log.e(TAG, "onActivityResult: chatRoom update" + cr.toString() );
                                    num = 0;
                                    isFirst = false;
                                    refreshList();
                                }
//                                if(DBManager.getInstance().deleteRoom(chatRoomId) >= 0){
//                                    Log.e(TAG, "onActivityResult: chatRoom " + chatRoomId + " removed" );
//                                    refreshList();
//                                }
                            }
                        }
                    }
                } else if(resultCode == getActivity().RESULT_CANCELED){
                    //.. cancel process
                    Log.e("dialog cancel", "RESULT_CANCELED");
                }
                break;
        }

    }


    Handler mHandler = new Handler(Looper.getMainLooper());
    private void refreshList(){
        Log.e(TAG, "refreshList: searchAllRoom "+ DBManager.getInstance().searchAllRoom().toString() );
        isSearching = false;
        searchInput.setText("");
        searchIcon.setImageResource(R.drawable.b_list_search_icon_selector);
        ChatRoomDataManager.getInstance().clearAll();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getActivity(), "refresh method", Toast.LENGTH_SHORT).show();
                chatRoomArrayList.clear();
                mAdapter.notifyDataSetChanged();
                fetchChatRooms();
//                fetchChatRoomsVolley();
            }
        }, 100);
    }

    static class NameAscCompare implements Comparator<String> {
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }
    }

    public static String getSortedRoomName(String old){
        String []arr = old.split(",");
        ArrayList<String> names = new ArrayList<String>();
        for (String s : arr){
            if(s.equals(PropertyManager.getInstance().getUserName()))
                continue;
            names.add(s);
        }
        Collections.sort(names, new NameAscCompare());
        String roomname = "";
        for (String s : names){
            roomname += s + ", ";
        }
        return roomname.substring(0, roomname.length()-2);
    }

    //newIntent values
    public static boolean withIntent = false;
    public static FriendsResult item = new FriendsResult();
    public static int c_id;
    public static int rc_num;
    public static String roomName;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.custom_editText1:
                    String str = charSequence.toString();
                    mAdapter.filter(str, (ArrayList<ChatRoom>) ChatRoomDataManager.getInstance().getList());
                    if (str.length() == 0){
                        searchIcon.setImageResource(R.drawable.b_list_search_icon_selector);
                    } else if(str.length() > 0){
                        searchIcon.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
                    }
                    break;
            }
        }
        public void afterTextChanged(Editable editable) {}
    }

} //end of class
