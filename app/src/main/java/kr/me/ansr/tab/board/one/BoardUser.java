package kr.me.ansr.tab.board.one;

import java.io.Serializable;

import kr.me.ansr.tab.friends.model.Pic;

/**
 * Created by KMS on 2016-12-03.
 */
public class BoardUser implements Serializable {
    public int userId;
    public String username;
    public Pic pic;
    public String updatedAt;
    public int enterYear;
    public String deptname;

    public BoardUser(){}

    public BoardUser(int userId, String username, Pic pic, String updatedAt, int enterYear, String deptname) {
        this.userId = userId;
        this.username = username;
        this.pic = pic;
        this.updatedAt = updatedAt;
        this.enterYear = enterYear;
        this.deptname = deptname;
    }
}
