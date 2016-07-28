package kr.me.ansr.tab.friends.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-21.
 */
public class FriendsResult implements Serializable{

    public int groupIndex;

    public int userId;
    public String email;
    public String username;
    public String pic;
    public String provider;
    public boolean isFriend;
    public String temp;
    public Location location;
    public ArrayList<String> sns;
    public ArrayList<String> desc;
    public Job job;
    public ArrayList<Univ> univ;

    public FriendsResult(){}

    public FriendsResult(int userId, String username, String pic) {
        this.userId = userId;
        this.username = username;
        this.pic = pic;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<String> getSns() {
        return sns;
    }

    public void setSns(ArrayList<String> sns) {
        this.sns = sns;
    }

    public ArrayList<String> getDesc() {
        return desc;
    }

    public void setDesc(ArrayList<String> desc) {
        this.desc = desc;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }


    public ArrayList<Univ> getUniv() {
        return univ;
    }

    public void setUniv(ArrayList<Univ> univ) {
        this.univ = univ;
    }

    public FriendsResult(int groupIndex, int userId, String email, String username, String pic, String provider, boolean isFriend, String temp, Location location, ArrayList<String> sns, ArrayList<String> desc, Job job, ArrayList<Univ> univ) {
        this.groupIndex = groupIndex;
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.pic = pic;
        this.provider = provider;
        this.isFriend = isFriend;
        this.temp = temp;
        this.location = location;
        this.sns = sns;
        this.desc = desc;
        this.job = job;
        this.univ = univ;
    }

    @Override
    public String toString() {
        return "FriendsResult{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", pic='" + pic + '\'' +
                ", isFriend=" + isFriend +
                ", temp='" + temp + '\'' +
                ", univ=" + univ +
                '}';
    }
}
