package kr.me.ansr.database;

import android.provider.BaseColumns;

/**
 * Created by KMS on 2016-09-22.
 */
public class DBConstant {

    public interface PushTable extends BaseColumns {
        public static final String TABLE_NAME = "push_table";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_CHAT_ROOM_ID = "chat_room_id";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BG = "bg";
    }

    public interface ChatRoomsTable extends BaseColumns {
        public static final String TABLE_NAME = "chat_rooms";
        public static final String COLUMN_CHAT_ROOM_ID = "chat_room_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAST_MSG = "last_msg";
        public static final String COLUMN_UNREAD_CNT = "unread_cnt";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_BG = "bg";
        public static final String COLUMN_IMAGE = "image";
    }

    public interface MessagesTable extends BaseColumns {
        public static final String TABLE_NAME = "messages_table";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_CHAT_ROOM_ID = "chat_room_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_BG = "bg";
        public static final String COLUMN_IMAGE = "image";
    }

}
