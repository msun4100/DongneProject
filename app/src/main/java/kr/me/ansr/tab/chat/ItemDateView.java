package kr.me.ansr.tab.chat;

import kr.me.ansr.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ItemDateView extends FrameLayout {

	public ItemDateView(Context context) {
		super(context);
		init();
	}
	
	TextView dateView;
	ItemData mItem;
	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.item_chat_date_layout, this);
		dateView = (TextView)findViewById(R.id.text_date);
	}
	
	public void setItemData(ItemData item) {
		mItem = item;
		dateView.setText(item.message);
	}

}
