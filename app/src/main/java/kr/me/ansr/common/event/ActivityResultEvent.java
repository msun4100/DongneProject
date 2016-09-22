package kr.me.ansr.common.event;

import android.content.Intent;

/**
 * Created by KMS on 2016-07-29.
 * ResultEvent Class를 따로 두는 이유는 같은 클래스로 계속 사용하면
 * EventBus.getInstance().post(new ..)할때 같은 클래스명이면 모두 이벤트를 sub 하기 때문에
 * Friends, board 별로 리절트이벤트 클래스를 따로 나눔
 */
public class ActivityResultEvent {

    private int requestCode;
    private int resultCode;
    private Intent data;

    public ActivityResultEvent(){}
    public ActivityResultEvent(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }
}