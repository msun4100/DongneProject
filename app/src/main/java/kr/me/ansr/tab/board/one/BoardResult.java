package kr.me.ansr.tab.board.one;

import java.io.Serializable;
import java.util.ArrayList;

import kr.me.ansr.tab.board.reply.ReplyResult;
import kr.me.ansr.tab.friends.model.Pic;

/**
 * Created by KMS on 2016-07-27.
 */
public class BoardResult implements Serializable {
    public String _id;
    public int boardId;
    public int univId;
    public int writer;
    public int pageId;
    public String title;
    public String commentId;
    public String updatedAt;
    public String createdAt;
    public ArrayList<Integer> likes;
    public int likeCount;
    public int viewCount;
    public int repCount;    //client용 value. 댓글 수 표시를 위해
    public String body;
    public String type;
    public BoardUser user;
    public ArrayList<String> pic;
//    public ArrayList<PreReply> preReplies;
    public ArrayList<ReplyResult> preReplies;
    public BoardResult(){
//        this.user = new FriendsResult(-1, "name", "");
        this.user = new BoardUser();
    }
    public class BoardUser implements Serializable{
        public int enterYear;
        public String username;
        public String deptname;
        public BoardUser(){}
    }

    @Override
    public String toString() {
        return "BoardResult{" +
                "_id='" + _id + '\'' +
                ", boardId=" + boardId +
                ", writer=" + writer +
                ", commentId='" + commentId + '\'' +
                ", likeCount=" + likeCount +
                ", repCount=" + repCount +
                '}';
    }
}
