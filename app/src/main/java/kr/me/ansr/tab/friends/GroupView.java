package kr.me.ansr.tab.friends;

import kr.me.ansr.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GroupView extends FrameLayout {

	public GroupView(Context context) {
		super(context);
		init();
	}
	
	TextView titleView;
	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.item_friends_group_layout, this);
		titleView = (TextView)findViewById(R.id.text_title);
	}
	
	public void setText(String text) {
		titleView.setText(text);
	}
}
