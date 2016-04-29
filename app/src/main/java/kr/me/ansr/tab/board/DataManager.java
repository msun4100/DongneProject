package kr.me.ansr.tab.board;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.R;

public class DataManager {
	private static DataManager instance;
	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}
	
	private DataManager() {	
	}
	
	public List<ItemData> getItemDataDummyList() {
		ArrayList<ItemData> items = new ArrayList<ItemData>();
		for (int i = 0; i < 20 ; i++) {
			ItemData id = new ItemData();
			id.timeStamp = new TimeStamp();
			id.no = i;
			id.title = "Title " + i;
			id.author = "익명" + i;
			id.timeStamp.time = "2016.03.1" + i;
			id.timeStamp.value = i;
			id.count = i;
			items.add(id);
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
