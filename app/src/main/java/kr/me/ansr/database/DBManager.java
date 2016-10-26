package kr.me.ansr.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.MyApplication;
import kr.me.ansr.gcmchat.model.ChatRoom;

/**
 * Created by KMS on 2016-09-22.
 */
public class DBManager extends SQLiteOpenHelper {
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
                DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID+" INTEGER PRIMARY KEY," +
                DBConstant.ChatRoomsTable.COLUMN_IMAGE+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_CREATED_AT+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_LAST_MSG+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT+" INTEGER," +
                DBConstant.ChatRoomsTable.COLUMN_NAME+" TEXT," +
                DBConstant.ChatRoomsTable.COLUMN_BG+" INTEGER);";

        String sql3 = "CREATE TABLE "+ DBConstant.MessagesTable.TABLE_NAME+"(" +
                DBConstant.MessagesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBConstant.MessagesTable.COLUMN_IMAGE+" TEXT," +
                DBConstant.MessagesTable.COLUMN_MESSAGE_ID+" INTEGER," +
                DBConstant.MessagesTable.COLUMN_CHAT_ROOM_ID+" INTEGER," +
                DBConstant.MessagesTable.COLUMN_CREATED_AT+" TEXT," +
                DBConstant.MessagesTable.COLUMN_MESSAGE+" TEXT," +
                DBConstant.MessagesTable.COLUMN_USER_ID+" INTEGER," +
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
        String orderBy = DBConstant.PushTable.COLUMN_NAME + " COLLATE LOCALIZED ASC";
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
        SQLiteDatabase db = getWritableDatabase();
        long num = 0;
        try{
            num = db.insert(DBConstant.ChatRoomsTable.TABLE_NAME, null, values);
        } catch (SQLException e){
            e.printStackTrace();
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

        String where = DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID + " = ?";
        String[] args = {""+cr.id};

        SQLiteDatabase db = getWritableDatabase();
        int num =0;
        try{
            num = db.update(DBConstant.ChatRoomsTable.TABLE_NAME, values, where, args);
        }catch (SQLException e){
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return num;
    }

    public Cursor selectCursorRoom(String roomName) {
        String[] columns = {
//                DBConstant.ChatRoomsTable._ID,
                DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID,
                DBConstant.ChatRoomsTable.COLUMN_NAME,
                DBConstant.ChatRoomsTable.COLUMN_IMAGE,
                DBConstant.ChatRoomsTable.COLUMN_LAST_MSG,
                DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT,
                DBConstant.ChatRoomsTable.COLUMN_CREATED_AT,
                DBConstant.ChatRoomsTable.COLUMN_BG,
                DBConstant.ChatRoomsTable.COLUMN_IMAGE
        };
        String where = null;
        String[] whereArgs = null;
        if (!TextUtils.isEmpty(roomName)) {
            where = DBConstant.ChatRoomsTable.COLUMN_NAME + " LIKE ?";
            whereArgs = new String[]{"%" + roomName + "%"};
        }
        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.ChatRoomsTable.COLUMN_NAME + " COLLATE LOCALIZED ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBConstant.ChatRoomsTable.TABLE_NAME, columns, where, whereArgs, groupBy, having, orderBy);
        return c;
    }

    public List<ChatRoom> searchRoom(String roomName) {
        Cursor c = selectCursorRoom(roomName);
        List<ChatRoom> list = new ArrayList<ChatRoom>();
        while(c.moveToNext()) {
            ChatRoom cr = new ChatRoom();
            cr.id = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable._ID));
            cr.name = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_NAME));
            cr.id = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID));
            cr.lastMessage = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_LAST_MSG));
            cr.unreadCount = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_UNREAD_CNT));
            cr.timestamp = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_CREATED_AT));
            cr.bgColor = c.getInt(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_BG));
            cr.image = c.getString(c.getColumnIndex(DBConstant.ChatRoomsTable.COLUMN_IMAGE));
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
                DBConstant.ChatRoomsTable.COLUMN_IMAGE
        };
        String where = null;
        String[] whereArgs = null;
//        if (!TextUtils.isEmpty(keyword)) {
//            where = DBConstant.PushTable.COLUMN_NAME + " LIKE ?";
//            whereArgs = new String[]{"%" + keyword + "%"};
//        }
        where = DBConstant.ChatRoomsTable.COLUMN_CHAT_ROOM_ID + " > ?";
        whereArgs = new String[]{"0"};  //SELECT * FROM PushTable WHERE id > 0

        String groupBy = null;
        String having = null;
        String orderBy = DBConstant.ChatRoomsTable.COLUMN_NAME + " COLLATE LOCALIZED ASC";
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
            list.add(cr);
        }
        c.close();
        return list;
    }




}
