/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.me.ansr.gcmchat.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.database.DBManager;
import kr.me.ansr.database.Push;
import kr.me.ansr.gcmchat.activity.ChatRoomActivity;
import kr.me.ansr.gcmchat.app.Config;
import kr.me.ansr.gcmchat.model.ChatRoom;
import kr.me.ansr.gcmchat.model.Message;
import kr.me.ansr.gcmchat.model.User;
import kr.me.ansr.login.SplashActivity;
import kr.me.ansr.tab.chat.GcmChatFragment;


public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    String tab = null;

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String title = bundle.getString("title");
        Boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
        String flag = bundle.getString("flag");
        String data = bundle.getString("data");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "title: " + title);
        Log.d(TAG, "isBackground: " + isBackground);
        Log.d(TAG, "flag: " + flag);
        Log.d(TAG, "data: " + data);

        if (flag == null) {
            Log.e(TAG, "flag is null");
            return;
        }

        if(MyApplication.getInstance().getPrefManager().getUser() == null){
            // user is not logged in, skipping push notification
            Log.e(TAG, "user is not logged in, skipping push notification");
            return;
        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        switch (Integer.parseInt(flag)) {
            case Config.PUSH_TYPE_CHATROOM:
                // push notification belongs to a chat room
                tab = "tab2";
                processChatRoomPush(title, isBackground, data);
                break;
            case Config.PUSH_TYPE_USER:
                // push notification is specific to user
                processUserMessage(title, isBackground, data);
                break;
            case Config.PUSH_TYPE_NEW_ROOM:
                // push notification belongs to a new chat room
                tab = "tab2";
                processChatRoomPushAndRefresh(title, isBackground, data);
                break;
            case Config.PUSH_TYPE_NOTIFICATION:
                // 알림설정이 off만 아니면
                // case 4: 좋아요, 친구 요청 등은 무조건 노티 트레이에 뜨도록함.
                tab = "tab4";
                processNotification(title, isBackground, data);
                break;
        }
    }

    /**
     * Processing chat room push message
     * this message will be broadcasts to all the activities registered
     * */
    private void processChatRoomPush(String title, boolean isBackground, String data) {
        if (!isBackground) {
            try {
                JSONObject datObj = new JSONObject(data);
                String chatRoomId = datObj.getString("chat_room_id");
                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getInt("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));
                message.chat_room_id = Integer.parseInt(chatRoomId);    //added on 1026
                JSONObject uObj = datObj.getJSONObject("user");

                User user = new User();
                user.setId(uObj.getInt("user_id"));
                user.setEmail(uObj.getString("email"));
                user.setName(uObj.getString("name"));
                message.setUser(user);

                if(DBManager.getInstance().isRoomExists(Integer.parseInt(chatRoomId)) == false){
                    ChatRoom cr = new ChatRoom(Integer.parseInt(chatRoomId), message.user.name,/*네임*/ message.message, /* 라스트메시지*/ message.createdAt, /* 타임스탬프*/ 0, /* 언리드카운트,*/ "", /*image url*/ 0/* bgColor*/, Integer.parseInt(PropertyManager.getInstance().getUserId()), message.getCreatedAt()/*lastJoin*/);
                    long num = DBManager.getInstance().insertRoom(cr);
                    Log.e(TAG, "processChatRoomPush: 1 insertRoom "+num);
                } else {
//                    있는데 lastJoin == "" 인 케이스
                    ChatRoom cr = DBManager.getInstance().searchRoom(Integer.parseInt(chatRoomId)).get(0);
                    if(cr.lastJoin.equals("") || cr.lastJoin == null){
                        cr.activeUser = Integer.parseInt(PropertyManager.getInstance().getUserId() );
                        cr.lastJoin = message.getCreatedAt();
                        cr.timestamp = message.getCreatedAt();
                        int num = DBManager.getInstance().updateRoom(cr);
                        if(num > 0){
                            Log.e(TAG, "processChatRoomPush: 2 updateRoom "+num);
                        }
                        Intent intent = new Intent(MyApplication.getContext(), GcmIntentService.class);
                        intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
                        intent.putExtra(GcmIntentService.TOPIC, "topic_" + cr.id);
                        MyApplication.getContext().startService(intent);
                    }
                }

                // skip the message if the message belongs to same user as
                // the user would be having the same message when he was sending
                // but it might differs in your scenario
                if (uObj.getString("user_id").equals(PropertyManager.getInstance().getUserId())) {
                    Log.e(TAG, "Skipping the push message as it belongs to same user");
                    return;
                }

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    //백그라운드가 아니면 켜져 있는 거고
                    //소리만 나고 메시지를 뿌리는 코드
                    if( PropertyManager.getInstance().getIsTab2Visible() == "visible"){
                        // app is in foreground, broadcast the push message
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("type", Config.PUSH_TYPE_CHATROOM);
                        pushNotification.putExtra("isFirst", true); //1115
                        pushNotification.putExtra("message", message);
                        pushNotification.putExtra("chat_room_id", chatRoomId);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                        // play notification sound  //ChatRoomActivity로 코드 이동 1104
//                        NotificationUtils notificationUtils = new NotificationUtils();
//                        notificationUtils.playNotificationSound();
                    } else {
                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        resultIntent.putExtra("chat_room_id", chatRoomId);
                        resultIntent.putExtra("name", ""+chatRoomId);   //1103
                        MainActivity.setChatCount(1);   //다른탭에 있을 경우 'N' 띄움
                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                    }
                } else {
                    // app is in background. show the message in notification try
                    //백그라운드일 경우 그냥 탭이동까지만
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("chat_room_id", chatRoomId);
                    resultIntent.putExtra("name", ""+chatRoomId);
                    showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                }

            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    private void processChatRoomPushAndRefresh(String title, boolean isBackground, String data) {
        if (!isBackground) {
            try {
                JSONObject datObj = new JSONObject(data);
                String chatRoomId = datObj.getString("chat_room_id");
//                String roomname = datObj.getString("room_name");
                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getInt("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));
                message.chat_room_id = Integer.parseInt(chatRoomId);    //added on 1026
                JSONObject uObj = datObj.getJSONObject("user");

                //원래 userId같으면 리턴하는 코드를 여기에 두는게 맞지만 디비저장하려면 유저정보가 필요하므로 아래로 내림
                User user = new User();
                user.setId(uObj.getInt("user_id"));
                user.setEmail(uObj.getString("email"));
                user.setName(uObj.getString("name"));
                message.setUser(user);


                if(DBManager.getInstance().isRoomExists(Integer.parseInt(chatRoomId)) == false){
                    ChatRoom cr = new ChatRoom(Integer.parseInt(chatRoomId), message.user.name,/*네임*/ message.message, /* 라스트메시지*/ message.createdAt, /* 타임스탬프*/ 0, /* 언리드카운트,*/ "", /*image url*/ 0/* bgColor*/, Integer.parseInt(PropertyManager.getInstance().getUserId()), message.getCreatedAt()/*lastJoin*/);
                    long num = DBManager.getInstance().insertRoom(cr);
                    Log.e(TAG, "processChatRoomPushAndRefresh: 1 insertRoom "+num);
                } else {
//                    있는데 lastJoin == "" 인 케이스
                    ChatRoom cr = DBManager.getInstance().searchRoom(Integer.parseInt(chatRoomId)).get(0);
                    if(cr.lastJoin.equals("") || cr.lastJoin == null){
                        cr.activeUser = Integer.parseInt(PropertyManager.getInstance().getUserId() );
                        cr.lastJoin = message.getCreatedAt();
                        cr.timestamp = message.getCreatedAt();
                        int num = DBManager.getInstance().updateRoom(cr);
                        if(num > 0){
                            Log.e(TAG, "processChatRoomPushAndRefresh: 2 updateRoom "+num);
                        }
                        Intent intent = new Intent(MyApplication.getContext(), GcmIntentService.class);
                        intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
                        intent.putExtra(GcmIntentService.TOPIC, "topic_" + cr.id);
                        MyApplication.getContext().startService(intent);
                    }
                }
                // skip the message if the message belongs to same user as
                // the user would be having the same message when he was sending
                // but it might differs in your scenario
                if (uObj.getString("user_id").equals(PropertyManager.getInstance().getUserId())) {
                    Log.e(TAG, "Skipping the push message as it belongs to same user");
//                    푸쉬 건너 뛰고 ChatRoomActivity의 sendMessage에서 응답받으면 디비에 메시지 저장.
                    return;
                }

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    //백그라운드가 아니면 켜져 있는 거고
                    //소리만 나고 메시지를 뿌리는 코드
                    if( PropertyManager.getInstance().getIsTab2Visible() == "visible"){
                        // app is in foreground, broadcast the push message
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("type", Config.PUSH_TYPE_NEW_ROOM);
                        pushNotification.putExtra("isFirst", true); //1115
                        pushNotification.putExtra("message", message);
                        pushNotification.putExtra("chat_room_id", chatRoomId);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils();
                        notificationUtils.playNotificationSound();
                    } else {
                        //아래 3줄 기존 코드
//                        addRoomAndMessage(chatRoomId, message);
                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        resultIntent.putExtra("chat_room_id", chatRoomId);
                        resultIntent.putExtra("name", ""+chatRoomId);
                        MainActivity.setChatCount(1);   //다른탭에 있을 경우 'N' 띄움
                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                    }

                } else {
                    // app is in background. show the message in notification try
//                    addRoomAndMessage(chatRoomId, message);
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("chat_room_id", chatRoomId);
                    resultIntent.putExtra("name", ""+chatRoomId);
                    showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                }

            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    /**
     * Processing user specific push message
     * It will be displayed with / without image in push notification tray
     * */
    private void processUserMessage(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject datObj = new JSONObject(data);
                String chatRoomId = datObj.getString("chat_room_id");
                String imageUrl = datObj.getString("image");

                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getInt("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));
                if(chatRoomId != null) message.chat_room_id = Integer.parseInt(chatRoomId);    //added on 1026
                JSONObject uObj = datObj.getJSONObject("user");
                User user = new User();
                user.setId(uObj.getInt("user_id"));
                user.setEmail(uObj.getString("email"));
                user.setName(uObj.getString("name"));
                message.setUser(user);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", Config.PUSH_TYPE_USER);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {
                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    // check for push notification image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                    } else {
                        // push notification contains image
                        // show it with the image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent, imageUrl);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    /**
     * Processing push messages for likes or replies
     * It will be displayed with / without image in push notification tray
     * */
    private void processNotification(String title, boolean isBackground, String data) {
        if (!isBackground) {
            try {
                JSONObject datObj = new JSONObject(data);

                String imageUrl = datObj.getString("image");

                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getInt("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));
                message.chat_room_id = mObj.getInt("chat_room_id");

                JSONObject uObj = datObj.getJSONObject("user");
                User user = new User();
                user.setId(uObj.getInt("user_id"));
                user.setEmail(uObj.getString("email"));
                user.setName(uObj.getString("name"));
                message.setUser(user);
                //db에 인서트
                Push p = new Push(
                        imageUrl,
                        message.chat_room_id,
                        message.getId(),
                        message.getMessage(),
                        message.getCreatedAt(),
                        user.getId(),
                        user.getName(),
                        0   //default bgColor value
                );
                DBManager.getInstance().insert(p);
                //MeetFragment에 브로드캐스트
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("type", Config.PUSH_TYPE_NOTIFICATION);
                pushNotification.putExtra("isFirst", true);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                //알람이 on/off인지 체크
                int alarmAll = PropertyManager.getInstance().getAlarmAll();
                Log.e("alarmAll:", ""+alarmAll);
                if(alarmAll > 0){
                    if(p.message_id == 1 && PropertyManager.getInstance().getAlarmLike() == 0){
                        return;
                    }
                    if(p.message_id == 3 && PropertyManager.getInstance().getAlarmReply() == 0){
                        return;
                    }
                    if(p.message_id == 5 && PropertyManager.getInstance().getAlarmFriend() == 0){
                        return;
                    }

                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    // check for push notification image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        if(message.getId() == 3){   //reply_anonymous
                            //case 3인 경우 누군가 회원님의 게시글에 댓글을 남겼습니다. 라고 푸시가 옴
                            showNotificationMessage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent);
                        } else {
                            showNotificationMessage(getApplicationContext(), title, user.getName()+message.getMessage(), message.getCreatedAt(), resultIntent);
                        }
                    } else {
                        // push notification contains image then show it with the image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent, imageUrl);
                    }
                }

//                case 4일때는 백그라운드 여부와 상관없이 무조건 알림 트레이에 띄움
//                // verifying whether the app is in background or foreground
//                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//
//                    // app is in foreground, broadcast the push message
//                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                    pushNotification.putExtra("type", Config.PUSH_TYPE_USER);
//                    pushNotification.putExtra("message", message);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                    // play notification sound
//                    NotificationUtils notificationUtils = new NotificationUtils();
//                    notificationUtils.playNotificationSound();
//                } else {
//                    // app is in background. show the message in notification try
//                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//                    // check for push notification image attachment
//                    if (TextUtils.isEmpty(imageUrl)) {
//                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
//                    } else {
//                        // push notification contains image
//                        // show it with the image
//                        showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent, imageUrl);
//                    }
//                }
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }


    /**
     * Showing notification with text only
     * */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if( tab != null) {
            Log.d(TAG, "showNotificationMessage: " + tab);
            intent.putExtra("tab", tab);
        }
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     * */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if( tab != null) {
            Log.d(TAG, "showNotificationMessage: " + tab);
            intent.putExtra("tab", tab);
        }
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void addMessageToDB(String chatRoomId, Message message){
        long num = DBManager.getInstance().insertMsg(message);
        Log.d(TAG, "addMessageToDB: " + num);
    }

    private void addRoomAndMessage(String chatRoomId, Message message){
        ChatRoom cr = new ChatRoom(Integer.parseInt(chatRoomId), message.user.name,/*네임*/ message.message, /* 라스트메시지*/ message.createdAt, /* 타임스탬프*/ 0, /* 언리드카운트,*/ "", /*image url*/ 0/* bgColor*/, Integer.parseInt(PropertyManager.getInstance().getUserId()), message.getCreatedAt());
        ArrayList<ChatRoom> list = (ArrayList<ChatRoom>)DBManager.getInstance().searchRoom(Integer.parseInt(chatRoomId));
        if(list.size() == 0){   //없으면 룸생성
            cr.unreadCount = 0;
            DBManager.getInstance().insertRoom(cr);
//            MainActivity.setChatCount(cr.unreadCount); //insertMsg하고 unreadCount 증가 시키는게 맞지
        } else {    //있으면 업데이트
            if(list.size() == 1 && list.get(0).id == Integer.parseInt(chatRoomId)){
                cr = list.get(0);
                cr.unreadCount = list.get(0).unreadCount;
                cr.lastMessage = message.message;
                cr.timestamp = message.createdAt;
                DBManager.getInstance().updateRoom(cr);
//                MainActivity.setChatCount(cr.unreadCount++);
            }
        }
        if(DBManager.getInstance().insertMsg(message) > 0 ){    //insertMsg 성공하면 updateRoom
            cr.unreadCount = cr.unreadCount++;
            if(DBManager.getInstance().updateRoom(cr) > 0 ){
                MainActivity.setChatCount(cr.unreadCount);
                Log.d(TAG, "addRoomAndMessage: "+cr.unreadCount);
            }
        }
        Log.e("invisible", "chatroom");
        Log.e("aaaaaaaa", ""+DBManager.getInstance().searchRoom(Integer.parseInt(chatRoomId)).get(0).toString());
    }
}
