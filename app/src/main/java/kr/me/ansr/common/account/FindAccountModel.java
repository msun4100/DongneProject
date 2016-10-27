package kr.me.ansr.common.account;

/**
 * Created by KMS on 2016-10-27.
 */
public class FindAccountModel {

    public String username;
    public String univname;
    public String deptname;
    public String enterYear;

    public FindAccountModel(){}

    public FindAccountModel(String username, String univname, String deptname, String enterYear) {
        this.username = username;
        this.univname = univname;
        this.deptname = deptname;
        this.enterYear = enterYear;
    }

    @Override
    public String toString() {
        return "FindAccountModel{" +
                "username='" + username + '\'' +
                ", deptname='" + deptname + '\'' +
                ", enterYear='" + enterYear + '\'' +
                '}';
    }
}
