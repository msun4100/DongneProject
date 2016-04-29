package kr.me.ansr.tab.friends;

import kr.me.ansr.R;
import kr.me.ansr.tab.board.ItemData;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ChildView extends FrameLayout {

	public ChildView(Context context) {
		super(context);
		init();
	}
	
//	TextView titleView;
	ImageView iconView;
	TextView nameView;
	TextView univView;
	TextView stuIdView;
	TextView jobView;
	
	ChildItem mItem;
	
	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.item_friends_child_layout, this);
//		titleView = (TextView)findViewById(R.id.text_title);
		iconView = (ImageView)findViewById(R.id.image_friends_icon);
		nameView = (TextView)findViewById(R.id.text_friends_name);
		univView = (TextView)findViewById(R.id.text_friends_univ);
		stuIdView = (TextView)findViewById(R.id.text_friedns_stuid);
		jobView = (TextView)findViewById(R.id.text_friends_job);
		
	}
	
	public void setItemData(ChildItem item) {
		mItem = item;
//		iconView.setImageResource(item.iconId);
		nameView.setText(item.name);
		univView.setText(item.univ);
		stuIdView.setText(""+item.studentId);
		jobView.setText(""+item.job);
	}
	
	
	public void setText(String text) {
//		titleView.setText(text);
		
//		iconView.setImageResource(R.id.ic);
		nameView.setText(text);
		univView.setText(text);
		stuIdView.setText(text);
		jobView.setText(text);
		
	}

}
