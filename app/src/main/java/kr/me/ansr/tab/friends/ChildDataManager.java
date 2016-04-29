package kr.me.ansr.tab.friends;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.R;

public class ChildDataManager {
	private static ChildDataManager instance;
	public static ChildDataManager getInstance() {
		if (instance == null) {
			instance = new ChildDataManager();
		}
		return instance;
	}
	
	private ChildDataManager() {	
	}
	
	public List<ChildItem> getItemDataDummyList() {
		ArrayList<ChildItem> items = new ArrayList<ChildItem>();
		for (int i = 0; i < 8 ; i++) {
//			ItemData id = new ItemData();
//			id.timeStamp = new TimeStamp();
//			id.no = i;
//			id.title = "Title " + i;
//			id.author = "익명" + i;
//			id.timeStamp.time = "2016.03.1" + i;
//			id.timeStamp.value = i;
//			id.count = i;
//			items.add(id);
			ChildItem child = new ChildItem();
			child.iconId = i;
			child.name = "username" + i;
			child.univ = "univ" + i;
			child.studentId = i+1;
			child.job = "job" + i;
			items.add(child);
		}
		return items;
	}
	
	
//	public List<ItemData> getItemDataList() {
//		ArrayList<ItemData> items = new ArrayList<ItemData>();
//		for (int i = 0; i < 20 ; i++) {
//			ItemData id = new ItemData();
//			id.iconId = R.drawable.ic_launcher;
//			id.title = "title" + i;
//			id.desc = "desc" + i;
//			id.like = i;
//			items.add(id);
//		}
//		return items;
//	}
	
	
}
