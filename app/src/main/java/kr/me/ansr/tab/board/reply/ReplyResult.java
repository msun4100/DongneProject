package kr.me.ansr.tab.board.reply;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-27.
 */
public class ReplyResult implements Serializable {
    public String _id;
    public String body;
    public int userId;
    public String username;
    public String type;
    public ArrayList<Integer> likes;
    public int likeCount;
    public int boardId; //MyWriting value

    public ArrayList<ReplyResult> replies;
    public String updatedAt;


    public ReplyResult(){}

    public ReplyResult(String _id, String body, int userId, String username) {
        this._id = _id;
        this.body = body;
        this.userId = userId;
        this.username = username;
    }

    public ReplyResult(String _id, String body, int userId, String username, String type, ArrayList<Integer> likes, int likeCount, ArrayList<ReplyResult> replies, String updatedAt) {
        this._id = _id;
        this.body = body;
        this.userId = userId;
        this.username = username;
        this.type = type;
        this.likes = likes;
        this.likeCount = likeCount;
        this.replies = replies;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ReplyResult{" +
                ", body='" + body + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", boardId=" + boardId +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
