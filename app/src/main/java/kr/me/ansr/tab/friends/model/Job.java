package kr.me.ansr.tab.friends.model;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-21.
 */
public class Job implements Serializable{
    public String team;
    public String name;

    public Job(){}
    public Job(String team, String name) {
        this.team = team;
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
