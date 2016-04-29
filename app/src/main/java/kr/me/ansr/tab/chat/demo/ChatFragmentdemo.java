package kr.me.ansr.tab.chat.demo;

import kr.me.ansr.MyApplication;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.tab.chat.demo.ChatArrayAdapter;
import kr.me.ansr.tab.chat.demo.ChatMessage;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatFragmentdemo extends PagerFragment {

	private static final String TAG = "ChatFragment";
	
	private ChatArrayAdapter adp;
	private ListView list;
	private EditText chatText;
	private Button send;
	
	Intent intent;
	private boolean side = false;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//		TextView tv = new TextView(getActivity());
//		tv.setText("Fragment 2");
//		return tv;
		View view = inflater.inflate(R.layout.fragment_chat_demo, container, false);

		Intent i = getActivity().getIntent();
		
		send = (Button) view.findViewById(R.id.btn);
		list = (ListView) view.findViewById(R.id.listview);
		
		adp = new ChatArrayAdapter(MyApplication.getContext(), R.layout.item_chat_demo);
		list.setAdapter(adp);
		
		chatText = (EditText)view.findViewById(R.id.chat_text);
		chatText.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
			}
		});
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendChatMessage();
			}
		});
		
		
		list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		list.setAdapter(adp);
		
		adp.registerDataSetObserver(new DataSetObserver() {
			public void OnChanged(){
	        	super.onChanged();
	        		
	        	list.setSelection(adp.getCount() -1);
	        		
	       	}
		});
		
		return view;
	}
	
	private boolean sendChatMessage(){
        adp.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        side = !side;
        return true;
    }
	
	@Override
	public void onPageCurrent() {
		super.onPageCurrent();
		
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// ...
		}
	}
}
