package kr.me.ansr.tab.friends.detail;

import java.io.Serializable;

/**
 * Created by KMS on 2016-09-06.
 */
public class StatusResult implements Serializable{
    public int from;
    public int to;
    public int status;
    public int actionUser;
    public String updatedAt;
    public String msg;

    public StatusResult(){}
    public StatusResult(int from, int to, int status, int actionUser, String updatedAt, String msg) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.actionUser = actionUser;
        this.updatedAt = updatedAt;
        this.msg = msg;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getActionUser() {
        return actionUser;
    }

    public void setActionUser(int actionUser) {
        this.actionUser = actionUser;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
