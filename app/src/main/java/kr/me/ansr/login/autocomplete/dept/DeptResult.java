package kr.me.ansr.login.autocomplete.dept;

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
        return deptname;
    }
}
