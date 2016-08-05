package kr.me.ansr.tab.friends.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by KMS on 2016-08-04.
 */
public class Sns implements Serializable{
    public String sns;
    public String url;
//    public Drawable icon;

    public Sns(){}

    public Sns(String sns, String url) {
        this.sns = sns;
        this.url = url;
    }
}
