package kr.me.ansr.database;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.gcmchat.gcm.GcmIntentService;
import kr.me.ansr.gcmchat.model.ChatRoom;
import kr.me.ansr.gcmchat.model.Message;

/**
 * Created by KMS on 2016-09-22.
 */
public class DBManager extends SQLiteOpenHelper {
    private static final String TAG = DBManager.class.getSimpleName();
    private static DBManager instance;
    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;

    private DBManager() {
        super(MyApplication.getContext(), DB_NAME, null,  DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+ DBConstant.PushTable.TABLE_NAME+"(" +
                DBConstant.PushTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBConstant.PushTable.COLUMN_IMAGE+" TEXT," +
                DBConstant.PushTable.COLUMN_CHAT_ROOM_ID+" INTEGER," +
                DBConstant.PushTable.COLUMN_MESSAGE_ID+" INTEGER," +
                DBConstant.PushTable.COLUMN_MESSAGE+" TEXT," +
                DBConstant.PushTable.COLUMN_CREATED_AT+" TEXT," +
                DBConstant.PushTable.COLUMN_USER_ID+" INTEGER," +
                DBConstant.PushTable.COLUMN_NAME+" TEXT," +
                DBConstant.PushTable.COLUMN_BG+" INTEGER);";

        String sql2 = "CREATE TABLE "+ DBConstant.ChatRoomsTable.TABLE_NAME+"(" +
                DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID+" INTEGER," +
                DBConstant.ChatRoomsTable.COLUMN_IMAGE+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_CREATED_AT+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_LAST_MSG+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT+" INTEGER," +
                DBConstant.ChatRoomsTable.COLUMN_NAME+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER+" INTEGER," +
                DBConstant.ChatRoomsTable.COLUMN_LAST_JOIN+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_BG+" INTEGER," +
                "PRIMARY KEY (" + DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID + ", "+ DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER+ ")" + ");";

        String sql3 = "CREATE TABLE "+ DBConstant.MessagesTable.TABLE_NAME+"(" +
//                DBConstant.MessagesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                DBConstant.MessagesTable.COLUMN_MESSAGE_ID+" INTEGER," +
                DBConstant.MessagesTable.COLUMN_MESSAGE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBConstant.MessagesTable.COLUMN_IMAGE+" TEXT," +
                DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID+" INTEGER," +
                DBConstant.MessagesTable.COLUMN_CREATED_AT+" TEXT," +
                DBConstant.MessagesTable.COLUMN_MESSAGE+" TEXT," +
                DBConstant.MessagesTable.COLUMN_USER_ID+" INTEGER," +
                DBConstant.MessagesTable.COLUMN_USER_NAME+" TEXT," +
                DBConstant.MessagesTable.COLUMN_BG+" INTEGER);";

        db.execSQL(sql);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //on upgrade older tables
        db.execSQL("DROP TABLE IF EXISTS " + DBConstant.ChatRoomsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstant.MessagesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstant.PushTable.TABLE_NAME);
        // create new tables
        onCreate(db);
    }

    ContentValues values = new ContentValues();
    public void insert(Push p) {
        values.clear();
        values.put(DBConstant.PushTable.COLUMN_IMAGE, p.image);
        values.put(DBConstant.PushTable.COLUMN_CHAT_ROOM_ID, p.chat_room_id);
        values.put(DBConstant.PushTable.COLUMN_MESSAGE_ID, p.message_id);
        values.put(DBConstant.PushTable.COLUMN_MESSAGE, p.message);
        values.put(DBConstant.PushTable.COLUMN_CREATED_AT, p.created_at);
        values.put(DBConstant.PushTable.COLUMN_USER_ID, p.user_id);
        values.put(DBConstant.PushTable.COLUMN_NAME, p.name);
        values.put(DBConstant.PushTable.COLUMN_BG, p.bgColor);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(DBConstant.PushTable.TABLE_NAME, null, values);
    }

    public void update(Push p) {
        values.clear();
        values.put(DBConstant.PushTable.COLUMN_IMAGE, p.image);
        values.put(DBConstant.PushTable.COLUMN_CHAT_ROOM_ID, p.chat_room_id);
        values.put(DBConstant.PushTable.COLUMN_MESSAGE_ID, p.message_id);
        values.put(DBConstant.PushTable.COLUMN_MESSAGE, p.message);
        values.put(DBConstant.PushTable.COLUMN_CREATED_AT, p.created_at);
        values.put(DBConstant.PushTable.COLUMN_USER_ID, p.user_id);
        values.put(DBConstant.PushTable.COLUMN_NAME, p.name);
        values.put(DBConstant.PushTable.COLUMN_BG, p.bgColor);

        String where = DBConstant.PushTable._ID + " = ?";
        String[] args = {""+p.id};

        SQLiteDatabase db = getWritableDatabase();
        db.update(DBConstant.PushTable.TABLE_NAME, values, where, args);
    }

    public void delete(Push p) {
        String where = DBConstant.PushTable._ID + " = ?";
        String[] args = {""+p.id};

        SQLiteDatabase db = getWritableDatabase();
        db.delete(DBConstant.PushTable.TABLE_NAME, where, args);
    }

    public Cursor selectCursor(String keyword) {
        String[] columns = {DBConstant.PushTable._ID,
                DBConstant.PushTable.COLUMN_IMAGE,
                DBConstant.PushTable.COLUMN_CHAT_ROOM_ID,
                DBConstant.PushTable.COLUMN_MESSAGE_ID,
                DBConstant.PushTable.COLUMN_MESSAGE,
                DBConstant.PushTable.COLUMN_CREATED_AT,
                DBConstant.PushTable.COLUMN_USER_ID,
                DBConstant.PushTable.COLUMN_NAME,
                DBConstant.PushTable.COLUMN_BG
        };
        String where = null;
        String[] whereArgs = null;
        if (!TextUtils.isEmpty(keyword)) {
            where = DBConstant.PushTable.COLUMN_NAME + " LIKE ?";
            whereArgs = new String[]{"%" + keyword + "%"};
        }
        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.PushTable.COLUMN_NAME + " COLLATE LOCALIZED ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBConstant.PushTable.TABLE_NAME, columns, where, whereArgs, groupBy, having, orderBy);
        return c;
    }

    public List<Push> search(String keyword) {
        Cursor c = selectCursor(keyword);
        List<Push> list = new ArrayList<Push>();
        while(c.moveToNext()) {
            Push p = new Push();
            p.id = c.getLong(c.getColumnIndex(DBConstant.PushTable._ID));
            p.image = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_IMAGE));
            p.chat_room_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_CHAT_ROOM_ID));
            p.message_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_MESSAGE_ID));
            p.message = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_MESSAGE));
            p.created_at = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_CREATED_AT));
            p.user_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_USER_ID));
            p.name = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_NAME));
            p.bgColor = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_BG));
            list.add(p);
        }
        c.close();
        return list;
    }

    public List<Push> searchAll() {
        String[] columns = {DBConstant.PushTable._ID,
                DBConstant.PushTable.COLUMN_IMAGE,
                DBConstant.PushTable.COLUMN_CHAT_ROOM_ID,
                DBConstant.PushTable.COLUMN_MESSAGE_ID,
                DBConstant.PushTable.COLUMN_MESSAGE,
                DBConstant.PushTable.COLUMN_CREATED_AT,
                DBConstant.PushTable.COLUMN_USER_ID,
                DBConstant.PushTable.COLUMN_NAME,
                DBConstant.PushTable.COLUMN_BG
        };
        String where = null;
        String[] whereArgs = null;
//        if (!TextUtils.isEmpty(keyword)) {
//            where = DBConstant.PushTable.COLUMN_NAME + " LIKE ?";
//            whereArgs = new String[]{"%" + keyword + "%"};
//        }
        where = DBConstant.PushTable._ID + " > ?";
        whereArgs = new String[]{"0"};  //SELECT * FROM PushTable WHERE id > 0

        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.PushTable.COLUMN_CREATED_AT + " COLLATE LOCALIZED DESC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBConstant.PushTable.TABLE_NAME, columns, where, whereArgs, groupBy, having, orderBy);  //return c 대신 풀코드 복사

        List<Push> list = new ArrayList<Push>();
        while(c.moveToNext()) {
            Push p = new Push();
            p.id = c.getLong(c.getColumnIndex(DBConstant.PushTable._ID));
            p.image = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_IMAGE));
            p.chat_room_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_CHAT_ROOM_ID));
            p.message_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_MESSAGE_ID));
            p.message = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_MESSAGE));
            p.created_at = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_CREATED_AT));
            p.user_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_USER_ID));
            p.name = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_NAME));
            p.bgColor = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_BG));
            list.add(p);
        }
        c.close();
        return list;
    }

    public List<Push> searchPush(int start, int display){
//        private final String MY_QUERY = "SELECT * FROM table_a a INNER JOIN table_b b ON a.id=b.other_id WHERE b.property_id=?";
//        db.rawQuery(MY_QUERY, new String[]{String.valueOf(propertyId)});
        final String MY_QUERY = "SELECT "+DBConstant.PushTable._ID +", image, chat_room_id, message_id, message, created_at, user_id, name, bg"
                + " FROM "+ DBConstant.PushTable.TABLE_NAME
                + " ORDER BY "+ DBConstant.PushTable.COLUMN_CREATED_AT + " COLLATE LOCALIZED DESC"
                + " LIMIT ?"
                + " OFFSET ?;";
        SQLiteDatabase db = getReadableDatabase();
//        Log.e(TAG, "searchPush: "+MY_QUERY);
        Cursor c = db.rawQuery(MY_QUERY, new String[]{String.valueOf(display), String.valueOf(start * display)});

        List<Push> list = new ArrayList<Push>();
        while(c.moveToNext()) {
            Push p = new Push();
            p.id = c.getLong(c.getColumnIndex(DBConstant.PushTable._ID));
            p.image = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_IMAGE));
            p.chat_room_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_CHAT_ROOM_ID));
            p.message_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_MESSAGE_ID));
            p.message = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_MESSAGE));
            p.created_at = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_CREATED_AT));
            p.user_id = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_USER_ID));
            p.name = c.getString(c.getColumnIndex(DBConstant.PushTable.COLUMN_NAME));
            p.bgColor = c.getInt(c.getColumnIndex(DBConstant.PushTable.COLUMN_BG));
            list.add(p);
        }
        c.close();
        return list;
    }




    //chat_room_table
    public long insertRoom(ChatRoom cr) {
        values.clear();
        values.put(DBConstant.ChatRoomsTable.COLUMN_IMAGE, cr.image);
        values.put(DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID, cr.id);
        values.put(DBConstant.ChatRoomsTable.COLUMN_NAME, cr.name);
        values.put(DBConstant.ChatRoomsTable.COLUMN_LAST_MSG, cr.lastMessage);
        values.put(DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT, cr.unreadCount);
        values.put(DBConstant.ChatRoomsTable.COLUMN_CREATED_AT, cr.timestamp);
        values.put(DBConstant.ChatRoomsTable.COLUMN_BG, cr.bgColor);
        values.put(DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER, Integer.parseInt(PropertyManager.getInstance().getUserId()) );
        values.put(DBConstant.ChatRoomsTable.COLUMN_LAST_JOIN, cr.lastJoin );
        SQLiteDatabase db = getWritableDatabase();
        long num = 0;
        try{
            num = db.insert(DBConstant.ChatRoomsTable.TABLE_NAME, null, values);
            //subscribe topic_#
            Intent intent = new Intent(MyApplication.getContext(), GcmIntentService.class);
            intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
            intent.putExtra(GcmIntentService.TOPIC, "topic_" + cr.id);
            MyApplication.getContext().startService(intent);
        } catch (SQLException e){
            e.printStackTrace();
            num = -1;
        }
        return num;
    }

    public int updateRoom(ChatRoom cr) {
        values.clear();
        values.put(DBConstant.ChatRoomsTable.COLUMN_IMAGE, cr.image);
        values.put(DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID, cr.id);
        values.put(DBConstant.ChatRoomsTable.COLUMN_NAME, cr.name);
        values.put(DBConstant.ChatRoomsTable.COLUMN_LAST_MSG, cr.lastMessage);
        values.put(DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT, cr.unreadCount);
        values.put(DBConstant.ChatRoomsTable.COLUMN_CREATED_AT, cr.timestamp);
        values.put(DBConstant.ChatRoomsTable.COLUMN_BG, cr.bgColor);
//        values.put(DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER, cr.activeUser );
        values.put(DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER, Integer.parseInt(PropertyManager.getInstance().getUserId()) );
        values.put(DBConstant.ChatRoomsTable.COLUMN_LAST_JOIN, cr.lastJoin);

        String where = DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID + " = ?";
        String[] args = {""+cr.id};

        SQLiteDatabase db = getWritableDatabase();
        int num;
        try{
            num = db.update(DBConstant.ChatRoomsTable.TABLE_NAME, values, where, args);
        }catch (SQLException e){
            e.printStackTrace();
            num = -1;
        }
        return num;
    }

//    public void deleteRoom(ChatRoom cr) {
    public int deleteRoom(int chatRoomId) {
        String where = DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID + " = ?";
//        String[] args = {""+cr.id};
        String[] args = {""+chatRoomId};
        SQLiteDatabase db = getWritableDatabase();
        int num = 0;
        try {
            num = db.delete(DBConstant.ChatRoomsTable.TABLE_NAME, where, args);
        } catch (SQLException e) {
            num = -1;
            e.printStackTrace();
        }
        Log.d("DB", "deleteRoom: "+num);
        return num;
    }

    public Cursor selectCursorRoom(int chatRoomId) {
        String[] columns = {
//                DBConstant.ChatRoomsTable._ID,
                DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID,
                DBConstant.ChatRoomsTable.COLUMN_NAME,
                DBConstant.ChatRoomsTable.COLUMN_IMAGE,
                DBConstant.ChatRoomsTable.COLUMN_LAST_MSG,
                DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT,
                DBConstant.ChatRoomsTable.COLUMN_CREATED_AT,
                DBConstant.ChatRoomsTable.COLUMN_BG,
                DBConstant.ChatRoomsTable.COLUMN_IMAGE,
                DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER,
                DBConstant.ChatRoomsTable.COLUMN_LAST_JOIN
        };
        String where = null;
        String[] whereArgs = null;
        if (!TextUtils.isEmpty(""+chatRoomId)) {
            where = DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID + " = ? AND " + DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER + " = ?";
            whereArgs = new String[]{ (""+chatRoomId), PropertyManager.getInstance().getUserId() };
        }
        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.ChatRoomsTable.COLUMN_CREATED_AT + " COLLATE LOCALIZED ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBConstant.ChatRoomsTable.TABLE_NAME, columns, where, whereArgs, groupBy, having, orderBy);
        return c;
    }

    public boolean isRoomExists(int chatRoomId){
        int size = searchRoom(chatRoomId).size();
        Log.d(TAG, "isRoomExists: "+size);
        if(size == 1){
            return true; //존재하면 트루
        }
        return false;
    }



    public int searchRoomName(String username){ //username == roomname
        String[] columns = {
                DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID,
        };
        String where = null;
        String[] whereArgs = null;
        if (!TextUtils.isEmpty(username)) {
            where = DBConstant.ChatRoomsTable.COLUMN_NAME + " = ? AND "+ DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER + " = ?";
            whereArgs = new String[]{ username, PropertyManager.getInstance().getUserId() };
        }
        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID + " COLLATE LOCALIZED ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBConstant.ChatRoomsTable.TABLE_NAME, columns, where, whereArgs, groupBy, having, orderBy);
        List<Integer> list = new ArrayList<Integer>();
        Log.e("before while1", ""+where+username);
        Log.e("before while2", ""+list.size());
        while(c.moveToNext()) {
            list.add(c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID)));
            Log.e("while", ""+c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID)));
        }
        c.close();
        if(list.size() > 0 && searchRoom(list.get(0)).size() == 1){
            return list.get(0); //존재하는 채팅방아이디
        }
        return -1;
    }


    public List<ChatRoom> searchRoom(int chatRoomId) {
        Cursor c = selectCursorRoom(chatRoomId);
        List<ChatRoom> list = new ArrayList<ChatRoom>();
        while(c.moveToNext()) {
            ChatRoom cr = new ChatRoom();
//            cr.id = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable._ID));
            cr.name = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_NAME));
            cr.id = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID));
            cr.lastMessage = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_LAST_MSG));
            cr.unreadCount = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT));
            cr.timestamp = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_CREATED_AT));
            cr.bgColor = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_BG));
            cr.image = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_IMAGE));
            cr.activeUser = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER));
            cr.lastJoin = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_LAST_JOIN));
            list.add(cr);
        }
        c.close();
        return list;
    }


    public List<ChatRoom> searchAllRoom() {
        String[] columns = {
//                DBConstant.ChatRoomsTable._ID,
                DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID,
                DBConstant.ChatRoomsTable.COLUMN_NAME,
                DBConstant.ChatRoomsTable.COLUMN_IMAGE,
                DBConstant.ChatRoomsTable.COLUMN_LAST_MSG,
                DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT,
                DBConstant.ChatRoomsTable.COLUMN_CREATED_AT,
                DBConstant.ChatRoomsTable.COLUMN_BG,
                DBConstant.ChatRoomsTable.COLUMN_IMAGE,
                DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER,
                DBConstant.ChatRoomsTable.COLUMN_LAST_JOIN
        };
        String where = null;
        String[] whereArgs = null;
//        if (!TextUtils.isEmpty(keyword)) {
//            where = DBConstant.PushTable.COLUMN_NAME + " LIKE ?";
//            whereArgs = new String[]{"%" + keyword + "%"};
//        }
        where = DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID + " > ? AND " + DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER + " = ?";
        whereArgs = new String[]{"0", PropertyManager.getInstance().getUserId()};  //SELECT * FROM PushTable WHERE id > 0

        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.ChatRoomsTable.COLUMN_CREATED_AT + " COLLATE LOCALIZED DESC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBConstant.ChatRoomsTable.TABLE_NAME, columns, where, whereArgs, groupBy, having, orderBy);  //return c 대신 풀코드 복사

        List<ChatRoom> list = new ArrayList<ChatRoom>();
        while(c.moveToNext()) {
            ChatRoom cr = new ChatRoom();
//            cr.id = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable._ID));
            cr.name = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_NAME));
            cr.id = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID));
            cr.lastMessage = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_LAST_MSG));
            cr.unreadCount = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT));
            cr.timestamp = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_CREATED_AT));
            cr.bgColor = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_BG));
            cr.image = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_IMAGE));
            cr.activeUser = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_ACTIVE_USER));
            cr.lastJoin = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_LAST_JOIN));
            list.add(cr);
        }
        Log.e(TAG, "searchAllRoom: "+list.size() );
        c.close();
        return list;
    }

    // messages_table
    public long insertMsg(Message message) {
        values.clear();
//        values.put(DBConstant.MessagesTable.COLUMN_MESSAGE_ID, message.id);   //auto increment
        values.put(DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID, message.chat_room_id);
        values.put(DBConstant.MessagesTable.COLUMN_USER_ID, message.user.id);
        values.put(DBConstant.MessagesTable.COLUMN_USER_NAME, message.user.name);
        values.put(DBConstant.MessagesTable.COLUMN_MESSAGE, message.message);
        values.put(DBConstant.MessagesTable.COLUMN_CREATED_AT, message.createdAt);
        values.put(DBConstant.MessagesTable.COLUMN_BG, message.bgColor);
        values.put(DBConstant.MessagesTable.COLUMN_IMAGE, message.image);

        SQLiteDatabase db = getWritableDatabase();
        long num = 0;
        try{
            num = db.insert(DBConstant.MessagesTable.TABLE_NAME, null, values);
        } catch (SQLException e){
            e.printStackTrace();
            num = -1;
        }
        return num;
    }

    public int updateMsg(Message message) {
        values.clear();
        values.put(DBConstant.MessagesTable.COLUMN_MESSAGE_ID, message.id);
        values.put(DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID, message.chat_room_id);
        values.put(DBConstant.MessagesTable.COLUMN_USER_ID, message.user.getId());
        values.put(DBConstant.MessagesTable.COLUMN_USER_NAME, message.user.name);
        values.put(DBConstant.MessagesTable.COLUMN_MESSAGE, message.message);
        values.put(DBConstant.MessagesTable.COLUMN_CREATED_AT, message.createdAt);
        values.put(DBConstant.MessagesTable.COLUMN_BG, message.bgColor);
        values.put(DBConstant.MessagesTable.COLUMN_IMAGE, message.image);

        String where = DBConstant.MessagesTable.COLUMN_MESSAGE_ID + " = ?";
        String[] args = {""+message.id};

        SQLiteDatabase db = getWritableDatabase();
        int num =0;
        try{
            num = db.update(DBConstant.MessagesTable.TABLE_NAME, values, where, args);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return num;
    }

    public int deleteMsg(int messageId) {
        String where = DBConstant.MessagesTable.COLUMN_MESSAGE_ID + " = ?";
        String[] args = {""+messageId};
        SQLiteDatabase db = getWritableDatabase();
        int num = 0;
        try {
            num = db.delete(DBConstant.MessagesTable.TABLE_NAME, where, args);
        } catch (SQLException e) {
            e.printStackTrace();
            num = -1;
        }
        Log.e("deleteMsg", ""+num);
        return num;
    }
    public int deleteRoomMsg(int chatRoomId) {
        String where = DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID + " = ?";
        String[] args = {""+chatRoomId};
        SQLiteDatabase db = getWritableDatabase();
        int num = 0;
        try {
            num = db.delete(DBConstant.MessagesTable.TABLE_NAME, where, args);
        } catch (SQLException e) {
            e.printStackTrace();
            num = -1;
        }
        Log.e("deleteRoomMsg", ""+num);
        return num;
    }

    public Cursor selectCursorMsg(int chatRoomdId, String message) {
        String[] columns = {
//                DBConstant.MessagesTable._ID,
                DBConstant.MessagesTable.COLUMN_MESSAGE_ID,
                DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID,
                DBConstant.MessagesTable.COLUMN_USER_ID,
                DBConstant.MessagesTable.COLUMN_USER_NAME,
                DBConstant.MessagesTable.COLUMN_MESSAGE,
                DBConstant.MessagesTable.COLUMN_CREATED_AT,
                DBConstant.MessagesTable.COLUMN_BG,
                DBConstant.MessagesTable.COLUMN_IMAGE
        };
        String where = null;
        String[] whereArgs = null;
        if (!TextUtils.isEmpty(message)) {
            where = "";
            where += DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID + " = ? AND"; //띄어쓰기
            where += DBConstant.MessagesTable.COLUMN_MESSAGE + " LIKE ?";
            whereArgs = new String[]{ (""+chatRoomdId), ("%" + message + "%") };
        }
        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.MessagesTable.COLUMN_MESSAGE_ID + " COLLATE LOCALIZED ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBConstant.MessagesTable.TABLE_NAME, columns, where, whereArgs, groupBy, having, orderBy);
        return c;
    }

    public List<Message> searchMsg(int chatRoomId, String message) {
        Cursor c = selectCursorMsg(chatRoomId, message);
        List<Message> list = new ArrayList<Message>();

        while(c.moveToNext()) {
            Message m = new Message();
            m.id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_MESSAGE_ID));
            m.chat_room_id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID));
            m.user.id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_USER_ID));
            m.user.name = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_USER_NAME));
            m.message = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_MESSAGE));
            m.createdAt = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_CREATED_AT));
            m.bgColor = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_BG));
            m.image = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_IMAGE));
            list.add(m);
        }
        c.close();
        return list;
    }

    //return All of chat room's messages
    public List<Message> searchAllMsg(int chatRoomId) {
        String[] columns = {
//                DBConstant.MessagesTable._ID,
                DBConstant.MessagesTable.COLUMN_MESSAGE_ID,
                DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID,
                DBConstant.MessagesTable.COLUMN_USER_ID,
                DBConstant.MessagesTable.COLUMN_USER_NAME,
                DBConstant.MessagesTable.COLUMN_MESSAGE,
                DBConstant.MessagesTable.COLUMN_CREATED_AT,
                DBConstant.MessagesTable.COLUMN_BG,
                DBConstant.MessagesTable.COLUMN_IMAGE
        };
        String where = null;
        String[] whereArgs = null;
//        if (!TextUtils.isEmpty(keyword)) {
//            where = DBConstant.PushTable.COLUMN_NAME + " LIKE ?";
//            whereArgs = new String[]{"%" + keyword + "%"};
//        }
        where = DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID + " = ?";
        whereArgs = new String[]{""+chatRoomId};  //SELECT * FROM messages WHERE chat_room_id = ?

        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.MessagesTable.COLUMN_MESSAGE_ID + " COLLATE LOCALIZED ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBConstant.MessagesTable.TABLE_NAME, columns, where, whereArgs, groupBy, having, orderBy);  //return c 대신 풀코드 복사

        List<Message> list = new ArrayList<Message>();
        while(c.moveToNext()) {
            Message m = new Message();
            m.id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_MESSAGE_ID));
            m.chat_room_id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID));
            m.user.id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_USER_ID));
            m.user.name = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_USER_NAME));
            m.message = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_MESSAGE));
            m.createdAt = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_CREATED_AT));
            m.bgColor = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_BG));
            m.image = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_IMAGE));
            list.add(m);
        }
        c.close();
        return list;
    }

    public List<Message> getLastMessages(){
//        private final String MY_QUERY = "SELECT * FROM table_a a INNER JOIN table_b b ON a.id=b.other_id WHERE b.property_id=?";
//        db.rawQuery(MY_QUERY, new String[]{String.valueOf(propertyId)});
        final String MY_QUERY = "SELECT chat_room_id, message, message_id, m.created_at"
                + " FROM "+ DBConstant.MessagesTable.TABLE_NAME +" as m"
                + " WHERE m.created_at = (SELECT MAX(m2.created_at)"
                                    + " FROM "+ DBConstant.MessagesTable.TABLE_NAME +" as m2"
                                    + " GROUP BY m2.chat_room_id HAVING m2.chat_room_id = m.chat_room_id)"
                                    + " ORDER BY m.created_at DESC;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(MY_QUERY, null);
        List<Message> list = new ArrayList<>();
        while(c.moveToNext()) {
            Message m = new Message();
            m.id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_MESSAGE_ID));
            m.chat_room_id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID));
//            m.user.id = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_USER_ID));
//            m.user.name = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_USER_NAME));
            m.message = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_MESSAGE));
            m.createdAt = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_CREATED_AT));
//            m.bgColor = c.getInt(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_BG));
//            m.image = c.getString(c.getColumnIndex(DBConstant.MessagesTable.COLUMN_IMAGE));
            list.add(m);
        }
        c.close();
        return list;
    }

    public int getUnreadCount(ChatRoom cr){
        String timeStamp = "";
        if(isRoomExists(cr.id)){
            List<ChatRoom> list = searchRoom(cr.id);
            if(list.size() == 1 && list.get(0).id == cr.id){
                timeStamp = list.get(0).timestamp;
                Log.e(TAG, "getUnreadCount: timeStamp" + timeStamp);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
        final String MY_QUERY = "SELECT COUNT(message_id) AS unread_count"
                + " FROM "+ DBConstant.MessagesTable.TABLE_NAME +" as m"
                + " WHERE m.created_at > ? AND chat_room_id = ?;";

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(MY_QUERY, new String[]{ timeStamp, String.valueOf(cr.id) });
        int unread_count = 0;
        while(c.moveToNext()) {
            unread_count = c.getInt(c.getColumnIndex("unread_count"));
            Log.e(TAG, "getUnreadCount: unread_count " + unread_count);
        }
        c.close();
        return unread_count;
    }

}
