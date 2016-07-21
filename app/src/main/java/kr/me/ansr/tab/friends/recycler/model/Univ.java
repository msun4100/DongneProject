package kr.me.ansr.tab.friends.recycler.model;

/**
 * Created by KMS on 2016-07-21.
 */
public class Univ {
    int univId;
    int deptId;
    int isGraduate; //boolean으로 안하는건 나중에 졸업 휴학 재학 등 여러 상태 표시를 위해
    int enterYear;

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
