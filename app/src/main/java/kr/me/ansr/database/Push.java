package kr.me.ansr.database;

/**
 * Created by KMS on 2016-09-22.
 */
public class Push {

    public int bgColor = 0; //백그라운드 칼라

    public long id = -1;
    public String image;
    public int chat_room_id;
    public int message_id;
    public String message;
    public String created_at;

    public int user_id;
    public String name;

    public Push(){}
    public Push(String image, int chat_room_id, int message_id, String message, String created_at, int user_id, String name, int bgColor) {
        this.image = image;
        this.chat_room_id = chat_room_id;
        this.message_id = message_id;
        this.message = message;
        this.created_at = created_at;
        this.user_id = user_id;
        this.name = name;
        this.bgColor = bgColor;
    }

    @Override
    public String toString() {
        return "Push{" +
                " id=" + id +
                ", bgColor=" + bgColor +
                ", image='" + image + '\'' +
                ", chat_room_id=" + chat_room_id +
                ", message_id=" + message_id +
                ", message='" + message + '\'' +
                ", created_at='" + created_at + '\'' +
                ", user_id=" + user_id +
                ", name='" + name + '\'' +
                '}';
    }
}
