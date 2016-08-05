package kr.me.ansr.tab.board.one;

import java.util.ArrayList;

import kr.me.ansr.tab.board.reply.CommentThread;
import kr.me.ansr.tab.board.reply.ReplyResult;

/**
 * Created by KMS on 2016-07-27.
 */
public class BoardInfo {
    public static final int BOARD_RC_NUM = 222;
    public static final int BOARD_DISPLAY_NUM = 3;
    public static final String BOARD_DETAIL_BOARD_ID = "detailBoardId";
    public static final String BOARD_DETAIL_OBJECT = "detailObject";
    public static final String BOARD_DETAIL_MODIFIED_ITEM = "detailModifiedItem";
    public static final String BOARD_DETAIL_MODIFIED_POSITION = "detailModifiedPosition";
    public Boolean error;
    public String message;
    public int total;
    public ArrayList<CommentThread> comment;
    public ArrayList<BoardResult> result;

}
