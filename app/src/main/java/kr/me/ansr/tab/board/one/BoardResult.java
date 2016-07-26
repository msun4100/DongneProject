package kr.me.ansr.tab.board.one;

import java.io.Serializable;
import java.util.ArrayList;

import kr.me.ansr.tab.board.reply.ReplyResult;

/**
 * Created by KMS on 2016-07-27.
 */
public class BoardResult implements Serializable {
    public int userId;
    public String email;
    public String username;
    public String pic;
    public String content;
    ArrayList<ReplyResult> replies;
    public ArrayList<String> repArr;

    public BoardResult(){}
    public BoardResult(int userId, String email, String username, String pic) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.pic = pic;
    }
}
