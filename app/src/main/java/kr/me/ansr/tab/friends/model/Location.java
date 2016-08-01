package kr.me.ansr.tab.friends.model;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-21.
 */
public class Location implements Serializable{
    public double lon;
    public double lat;

    public Location(){}

    public Location(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
