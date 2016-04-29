package kr.me.ansr.tab.board;

import kr.me.ansr.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemView extends FrameLayout {

	public ItemView(Context context) {
		super(context);
		init();
	}

	public ItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
//	ImageView iconView;
//	TextView titleView;
//	TextView descView;
//	TextView likeView;
//	ItemData mItem;
	
	TextView noView;
	TextView titleView;
	TextView authorView;
	TextView timeStampView;
	TextView countView;
	ItemData mItem;
	
	public interface OnLikeClickListener {
		public void onLikeClick(View v, ItemData item);
	}
	
	OnLikeClickListener mListener;
	
	public void setOnLikeClickListener(OnLikeClickListener listener) {
		mListener = listener;
	}
	
	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.item_board_layout, this);
//		iconView = (ImageView)findViewById(R.id.image_icon);
//		titleView = (TextView)findViewById(R.id.text_title);
//		descView = (TextView)findViewById(R.id.text_desc);
//		likeView = (TextView)findViewById(R.id.text_like);
		
		noView = (TextView)findViewById(R.id.text_board_no);
		titleView = (TextView)findViewById(R.id.text_board_title);
		authorView = (TextView)findViewById(R.id.text_board_author);
		timeStampView = (TextView)findViewById(R.id.text_board_date);
		countView = (TextView)findViewById(R.id.text_board_count);
//		likeView.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (mListener != null) {
//					mListener.onLikeClick(ItemView.this, mItem);
//				}
//			}
//		});
	}
	
	public void setItemData(ItemData item) {
		mItem = item;
//		iconView.setImageResource(item.iconId);
//		descView.setText(item.desc);
//		likeView.setText(""+item.like);
		
		noView.setText(""+item.no);
		titleView.setText(item.title);
		authorView.setText(item.author);
		timeStampView.setText(""+item.timeStamp.time);
//		if(item.timeStamp != null) {
//			timeStampView.setText(item.timeStamp.time);
//		}
		countView.setText(""+item.count);
	}

	
}
