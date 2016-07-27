package kr.me.ansr.tab.board.preview;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-27.
 */

public class PreReply implements Serializable{
    public int userId;
    public String username;
    public String body;

    public PreReply(){}
    public PreReply(int userId, String username, String body) {
        this.userId = userId;
        this.username = username;
        this.body = body;
    }
}
