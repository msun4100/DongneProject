package kr.me.ansr.tab.friends.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by KMS on 2016-08-05.
 */
public class Desc implements Serializable, ProfileItem{
//    public Drawable icon; //모델안에 Drawable 있으면 쿠드 낫 파인드 클래스 에러 발생..
    public String desc;
    public Desc(){}

    public Desc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Desc{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
