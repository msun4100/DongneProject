package kr.me.ansr.gcmchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.database.DBManager;
import kr.me.ansr.gcmchat.adapter.ChatRoomThreadAdapter;
import kr.me.ansr.gcmchat.app.Config;
import kr.me.ansr.gcmchat.app.EndPoints;
import kr.me.ansr.gcmchat.gcm.NotificationUtils;
import kr.me.ansr.gcmchat.model.ChatInfo;
import kr.me.ansr.gcmchat.model.ChatRoom;
import kr.me.ansr.gcmchat.model.Message;
import kr.me.ansr.gcmchat.model.User;
import kr.me.ansr.tab.friends.model.FriendsResult;

//FragmentGcmChat 에서 여는 채팅 방

public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private String chatRoomId;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private Button btnSend;

//    =====================
    TextView toolbarTitle;
    ImageView toolbarMenu;
    FriendsResult mItem;
    ArrayList<FriendsResult> mList = new ArrayList<>(); //단톡방 리스트
    String roomName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.e__titlebar_2));
        getSupportActionBar().setElevation(0);	//6.0이상 음영효과 제거
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.b_list_titlebar));
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
//        toolbarTitle.setText("채 팅 방");    //아래에서 번들값 받고 타이틀 지정함
        toolbarMenu = (ImageView)toolbar.findViewById(R.id.toolbar_menu1);
        toolbarMenu.setImageResource(R.drawable.d_artboard_final_titlesetting);
        toolbarMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "menu click", Toast.LENGTH_SHORT).show();
            }
        });

            //==========================
//        inputMessage = (EditText) findViewById(R.id.message);
//        btnSend = (Button) findViewById(R.id.btn_send);
        inputMessage = (EditText) findViewById(R.id.edit_detail_input);
        inputMessage.setHint("메시지를 입력해주세요.");
        btnSend = (Button) findViewById(R.id.btn_detail_send);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        messageArrayList = new ArrayList<>();

        // self user id is to identify the message owner
        String selfUserId = ""+MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("chatRoomdId: ", chatRoomId);
                if(chatRoomId.equals("-1")){
//                    addChatRoomVolley();
                    addChatRoom();
                    // chat_room 등록하고 메시지하나 저장 후 센드메시지와 같은 리턴값을 받음
                } else {
//                    sendMessageVolley();
                    sendMessage(Integer.valueOf(chatRoomId), PropertyManager.getInstance().getUserId());
                }

            }
        });

        Intent intent = getIntent();
        if(intent != null){
            chatRoomId = intent.getStringExtra("chat_room_id");
            roomName = intent.getStringExtra("name");
            if(roomName != null){
                toolbarTitle.setText(roomName);
            } else { toolbarTitle.setText("채 팅 방"); }   //채팅방으로 만들어질 예외는 없다고 봄
            mItem = (FriendsResult) intent.getSerializableExtra("mItem");
            mList = (ArrayList<FriendsResult>)intent.getSerializableExtra("mList");
            if(mList != null){
                Log.e("mList:", mList.toString());
            }
        }
        if (chatRoomId == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            finishAndReturnData(false);
        }
        if(chatRoomId.equals("-1")){
            //맨처음 생성한방. == 디비에 방이 생성되어 있지는 않고
            //클라이언트 화면에만 떠 있는 상태
            Log.e("getItemCount()", ""+mAdapter.getItemCount());
            if(mAdapter.getItemCount() != 0){
                messageArrayList.clear();
                mAdapter.notifyDataSetChanged();
            }
        } else {
//            fetchChatThreadVolley();
            fetchChatThread(Integer.valueOf(chatRoomId));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("chat_room_id");

        if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }
    }

    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     * */
    private void sendMessageVolley() {
        final String message = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String endPoint = EndPoints.CHAT_ROOM_MESSAGE.replace("_ID_", chatRoomId);
        Log.e(TAG, "endpoint: " + endPoint);

        this.inputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
//                        JSONObject commentObj = obj.getJSONObject("message");
//                        message객체가 아닌 messages 어레이로 받기 위해
//                       센드 메시지에서 messages의 length는 무조건 1일 것임.
                        JSONArray commentsObj = obj.getJSONArray("messages");
                        for (int i = 0; i < commentsObj.length(); i++) {
                            JSONObject commentObj = (JSONObject) commentsObj.get(i);
                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("message");
                            String createdAt = commentObj.getString("created_at");

                            JSONObject userObj = obj.getJSONObject("user");
                            int userId = userObj.getInt("user_id");
                            String userName = userObj.getString("name");
                            User user = new User(userId, userName, null);

                            Message message = new Message();
                            message.setId(Integer.valueOf(commentId));
                            message.setMessage(commentText);
                            message.setCreatedAt(createdAt);
                            message.setUser(user);

                            messageArrayList.add(message);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            // scrolling to bottom of the recycler view
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                inputMessage.setText(message);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", ""+MyApplication.getInstance().getPrefManager().getUser().getId());
                params.put("message", message);

                Log.e(TAG, "Params: " + params.toString());

                return params;
            };
        };


        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void sendMessage(int chatRoomId, String userId){
        final String message = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        this.inputMessage.setText("");

        NetworkManager.getInstance().postDongneSendMessage(ChatRoomActivity.this, chatRoomId, userId, message, new NetworkManager.OnResultListener<ChatInfo>(){
            @Override
            public void onSuccess(okhttp3.Request request, ChatInfo result) {
                if (result.error.equals(false)) {
//                    sendMessage 후에 오는 message 객체는 무조건 length가 1일 것임
                    if(result.messages != null){
                        for(int i=0; i<result.messages.size(); i++){
                            Log.e("msg"+i+" :", result.messages.get(i).toString());
                            Message message = new Message();
                            message.setId(result.messages.get(i).getId());
                            message.setMessage(result.messages.get(i).getMessage());
                            message.setCreatedAt(result.messages.get(i).getCreatedAt());
                            int userId = Integer.parseInt(PropertyManager.getInstance().getUserId());
                            String username = PropertyManager.getInstance().getUserName();
                            String email = PropertyManager.getInstance().getEmail();
                            User user = new User(userId, username, email);
                            //send메시지의 response객체의 message 객체 안에는 user가 포함되어 있지 않음.
                            //어차피 내가 보내는 메시지이니까. 프로퍼티 매니저에 저장된 정보 기반으로 User() 객체 생성해서 저장함
//                            message.setUser(result.messages.get(i).getUser());
                            message.setUser(user);
                            messageArrayList.add(message);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 1) {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "error: true\n " + result.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(okhttp3.Request request, int code, Throwable cause) {
                Toast.makeText(getApplicationContext(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addChatRoom(){
        final String message = this.inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        this.inputMessage.setText("");
        NetworkManager.getInstance().postDongneAddChatRoom(ChatRoomActivity.this, chatRoomId, roomName, mList, message, new NetworkManager.OnResultListener<ChatInfo>(){
            @Override
            public void onSuccess(okhttp3.Request request, ChatInfo result) {
                if (result.error.equals(false)) {
                    if(result.messages != null){
                        for(int i=0; i<result.messages.size(); i++){
                            Log.e("msg"+i+" :", result.messages.get(i).toString());
                            Message message = new Message();
                            message.setId(result.messages.get(i).getId());
                            message.setMessage(result.messages.get(i).getMessage());
                            message.setCreatedAt(result.messages.get(i).getCreatedAt());
                            message.chat_room_id = result.messages.get(i).chat_room_id;
                            chatRoomId = ""+ result.messages.get(i).chat_room_id;

                            int userId = Integer.parseInt(PropertyManager.getInstance().getUserId());
                            String username = PropertyManager.getInstance().getUserName();
                            String email = PropertyManager.getInstance().getEmail();
                            User user = new User(userId, username, email);
                            //send메시지의 response객체의 message 객체 안에는 user가 포함되어 있지 않음.
                            //어차피 내가 보내는 메시지이니까. 프로퍼티 매니저에 저장된 정보 기반으로 User() 객체 생성해서 저장함
//                            message.setUser(result.messages.get(i).getUser());
                            message.setUser(user);
                            messageArrayList.add(message);
                            // insert to DB
                            ChatRoom cr = new ChatRoom();
                            cr.setId(message.chat_room_id);
                            cr.setName(mItem.username);
                            cr.setLastMessage(message.message);
                            cr.setUnreadCount(0);
                            cr.setTimestamp(message.createdAt);
                            cr.bgColor = 0; //없으면 저장 안되나?
                            cr.image ="";
                            DBManager.getInstance().insertRoom(cr);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 1) {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "error: true\n " + result.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(okhttp3.Request request, int code, Throwable cause) {
                Toast.makeText(getApplicationContext(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchChatThread(int chatRoomId){
        NetworkManager.getInstance().getDongneFetchChatThread(ChatRoomActivity.this, chatRoomId, new NetworkManager.OnResultListener<ChatInfo>(){
            @Override
            public void onSuccess(okhttp3.Request request, ChatInfo result) {
                if (result.error.equals(false)) {
                    if(result.messages != null){
                        for(int i=0; i<result.messages.size(); i++){
                            Log.e("msg"+i+" :", result.messages.get(i).toString());
                            Message message = new Message();
                            message.setId(result.messages.get(i).getId());
                            message.setMessage(result.messages.get(i).getMessage());
                            message.setCreatedAt(result.messages.get(i).getCreatedAt());
                            message.setUser(result.messages.get(i).getUser());
                            messageArrayList.add(message);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 1) {
//                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        recyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "error: true\n " + result.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(okhttp3.Request request, int code, Throwable cause) {
                Toast.makeText(getApplicationContext(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetching all the messages of a single chat room
     * Using volley
     * */
    private void fetchChatThreadVolley() {

        String endPoint = EndPoints.CHAT_THREAD.replace("_ID_", chatRoomId);
        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONArray commentsObj = obj.getJSONArray("messages");

                        for (int i = 0; i < commentsObj.length(); i++) {
                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("message");
                            String createdAt = commentObj.getString("created_at");

                            JSONObject userObj = commentObj.getJSONObject("user");
                            int userId = userObj.getInt("user_id");
                            String userName = userObj.getString("username");
                            User user = new User(userId, userName, null);

                            Message message = new Message();
                            message.setId(Integer.valueOf(commentId));
                            message.setMessage(commentText);
                            message.setCreatedAt(createdAt);
                            message.setUser(user);

                            messageArrayList.add(message);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    //==============================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            Log.e("chat_room_id: ", chatRoomId);
            if(chatRoomId.equals("-1")){
                finishAndReturnData(false);
            } else {
                finishAndReturnData(true);
            }

//            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //chatRoomId === "-1" 인경우 요청 성공하면 메시지.챗룸아이디 로
    private void addChatRoomVolley() {
        final String message = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String endPoint = EndPoints.ADD_CHAT_ROOM;
        Log.e(TAG, "endpoint: " + endPoint);

        this.inputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONArray commentsObj = obj.getJSONArray("messages");

                        for (int i = 0; i < commentsObj.length(); i++) {
                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("message");
                            String createdAt = commentObj.getString("created_at");
                            chatRoomId = commentObj.getString("chat_room_id");

                            //addChatRoom 라우터메소드 실행후에 센드메시지와 같은 플로우가 진행되니까
                            //여기 유저도 프로퍼티 매니저기반으로 해야 됨(메시지 객체 안에 유저가 없음)
                            //그래서 No Value for user Json parsing error 발생하는 거
//                            JSONObject userObj = commentObj.getJSONObject("user");
//                            String userId = userObj.getString("user_id");
//                            String userName = userObj.getString("name");
                            int userId = Integer.parseInt(PropertyManager.getInstance().getUserId());
                            String username = PropertyManager.getInstance().getUserName();
                            String email = PropertyManager.getInstance().getEmail();
                            User user = new User(userId, username, email);

                            Message message = new Message();
                            message.setId(Integer.valueOf(commentId));
                            message.setMessage(commentText);
                            message.setCreatedAt(createdAt);
                            message.setUser(user);

                            messageArrayList.add(message);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                inputMessage.setText(message);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("room_name", mItem.username);
                params.put("user_id", ""+MyApplication.getInstance().getPrefManager().getUser().getId());
                for(int i=0; i<mList.size(); i++){
                    params.put("to["+i+"]", ""+mList.get(i).userId);
                }
                params.put("message", message);
                params.put("chat_room_id", chatRoomId);

                Log.e(TAG, "Params: " + params.toString());

                return params;
            };
        };


        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
    private void finishAndReturnData(boolean result){

        Intent intent = new Intent();
        if(result){
            intent.putExtra("return", "success");
        } else {    //result == false
            intent.putExtra("return", "failure");
        }
        this.setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Log.e("chat_room_id: ", chatRoomId);
        if(chatRoomId.equals("-1")){
            finishAndReturnData(false);
        } else {
            finishAndReturnData(true);
        }
        super.onBackPressed();

    }
}
