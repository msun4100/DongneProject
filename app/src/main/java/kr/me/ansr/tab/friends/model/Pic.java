package kr.me.ansr.tab.friends.model;

import java.io.Serializable;

/**
 * Created by KMS on 2016-08-01.
 */
public class Pic implements Serializable{
    public String small;
    public String large;

    public Pic(){}

    public Pic(String small, String large) {
        this.small = small;
        this.large = large;
    }

    @Override
    public String toString() {
        return "Pic{" +
                "small='" + small + '\'' +
                ", large='" + large + '\'' +
                '}';
    }
}
