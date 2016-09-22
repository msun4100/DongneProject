package kr.me.ansr.tab.board;

import java.util.ArrayList;

import kr.me.ansr.common.CommonInfo;
import kr.me.ansr.tab.board.reply.CommentThread;
import kr.me.ansr.tab.board.reply.ReplyResult;

/**
 * Created by KMS on 2016-08-01.
 */
public class CommentInfo extends CommonInfo{
    public ArrayList<CommentThread> comment;
    public ReplyResult result;  //BoardDetailActivity에서 새로 코멘트 추가했을때 리스트에 추가하기 위해.
}
