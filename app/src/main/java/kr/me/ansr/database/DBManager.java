package kr.me.ansr.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.MyApplication;

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
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
}
