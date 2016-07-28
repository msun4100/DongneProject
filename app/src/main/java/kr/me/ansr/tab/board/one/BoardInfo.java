package kr.me.ansr.tab.board.one;

import java.util.ArrayList;

import kr.me.ansr.tab.board.reply.CommentThread;
import kr.me.ansr.tab.board.reply.ReplyResult;

/**
 * Created by KMS on 2016-07-27.
 */
public class BoardInfo {
    public Boolean error;
    public String message;
    public int total;
    public ArrayList<CommentThread> comment;
    public ArrayList<BoardResult> result;

}
