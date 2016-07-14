package kr.me.ansr.login.autocomplete.ex.dept;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-14.
 */
public class DeptResult implements Serializable {
    public int univId;
    public int deptId;
    public String deptname;
    public int total;

    @Override
    public String toString() {
        return "DeptResult{" +
                "univId=" + univId +
                ", deptId=" + deptId +
                ", deptname='" + deptname + '\'' +
                ", total=" + total +
                '}';
    }
}
