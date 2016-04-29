package kr.me.ansr.tab.friends;

public class ChildItem {
//	public String title;
	
	public int iconId;
	public String name;
	public String univ;
	public int studentId;
	public String job;
	
	public ChildItem(){}
	public ChildItem(int iconId, String name, String univ, int studentId,
			String job) {
		super();
		this.iconId = iconId;
		this.name = name;
		this.univ = univ;
		this.studentId = studentId;
		this.job = job;
	}
	
	
}
