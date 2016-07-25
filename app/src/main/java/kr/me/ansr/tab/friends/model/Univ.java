package kr.me.ansr.tab.friends.model;

/**
 * Created by KMS on 2016-07-21.
 */
public class Univ {
    public int univId;
    public int deptId;
    public String deptname;
    public int isGraduate; //boolean으로 안하는건 나중에 졸업 휴학 재학 등 여러 상태 표시를 위해
    public int enterYear;

    public Univ(){}

    public Univ(int univId, int deptId, String deptname, int isGraduate, int enterYear) {
        this.univId = univId;
        this.deptId = deptId;
        this.deptname = deptname;
        this.isGraduate = isGraduate;
        this.enterYear = enterYear;
    }

    public int getUnivId() {
        return univId;
    }

    public void setUnivId(int univId) {
        this.univId = univId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public int getIsGraduate() {
        return isGraduate;
    }

    public void setIsGraduate(int isGraduate) {
        this.isGraduate = isGraduate;
    }

    public int getEnterYear() {
        return enterYear;
    }

    public void setEnterYear(int enterYear) {
        this.enterYear = enterYear;
    }
}
