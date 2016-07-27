package kr.me.ansr.tab.board.one;

import java.io.Serializable;
import java.util.ArrayList;

import kr.me.ansr.tab.board.preview.PreReply;

/**
 * Created by KMS on 2016-07-27.
 */
public class BoardResult implements Serializable {
    public int userId;
    public String email;
    public String username;
    public String pic;
    public String content;
    ArrayList<PreReply> replies;


    public BoardResult(){}
    public BoardResult(int userId, String email, String username, String pic, String content, ArrayList<PreReply> replies) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.pic = pic;
        this.content = content;
        this.replies = replies;
    }

}
