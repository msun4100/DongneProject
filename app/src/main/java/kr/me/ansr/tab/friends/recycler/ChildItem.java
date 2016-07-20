package kr.me.ansr.tab.friends.recycler;

/**
 * Created by KMS on 2016-07-20.
 */
public class ChildItem {
    public int groupIndex;

    public int iconId;
    public String thumbnail;
    public boolean isFriend;
    public String name;
    public String univ;
    public int studentId;
    public String job;


    public ChildItem(){}
    public ChildItem(int groupIndex, boolean isFriend, String thumbnail, String name, String univ, int studentId, String job) {
        super();
        this.groupIndex = groupIndex;
        this.isFriend = isFriend;
        this.thumbnail = thumbnail;
        this.name = name;
        this.univ = univ;
        this.studentId = studentId;
        this.job = job;
    }

    @Override
    public String toString() {
        return "ChildItem{" +
                "groupIndex=" + groupIndex +
                ", iconId=" + iconId +
                ", thumbnail='" + thumbnail + '\'' +
                ", isFriend=" + isFriend +
                ", name='" + name + '\'' +
                ", univ='" + univ + '\'' +
                ", studentId=" + studentId +
                ", job='" + job + '\'' +
                '}';
    }
}
