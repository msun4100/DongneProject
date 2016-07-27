package kr.me.ansr.tab.board.reply;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-27.
 */
public class CommentThread implements Serializable {
    public String _id;
    public String title;
    public int boardId;
    public ArrayList<ReplyResult> replies;

    public CommentThread(){}

    public CommentThread(String _id, String title, int boardId, ArrayList<ReplyResult> replies) {
        this._id = _id;
        this.title = title;
        this.boardId = boardId;
        this.replies = replies;
    }
}
