package kr.me.ansr.login.autocomplete.ex.univ;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-14.
 */
public class UnivResult implements Serializable {
    public int univId;
    public String univname;
    public int total;

    @Override
    public String toString() {
        return "UnivResult{" +
                "univId=" + univId +
                ", univname='" + univname + '\'' +
                ", total=" + total +
                '}';
    }

    public String getUnivname() {
        return univname;
    }
}
