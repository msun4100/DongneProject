package kr.me.ansr.tab.chat;

import java.util.Date;
import java.util.concurrent.Callable;

import kr.me.ansr.tab.chat.socket.LoginActivity;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.tab.chat.socket.MainActivity;
import kr.me.ansr.tab.chat.socket.MainFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnHoverListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ChatFragment extends PagerFragment {

	ListView listView;
	RadioGroup group;
	EditText inputView;
	MyAdapter mAdapter;
	Button btn;
	
	InputMethodManager imm;
	Boolean toggle =true;
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TextView tv = new TextView(getActivity());
		// tv.setText("Fragment 2");
		// return tv;
		View view = inflater.inflate(R.layout.fragment_chat, container,
				false);
		
		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		listView = (ListView) view.findViewById(R.id.listView1);
		group = (RadioGroup) view.findViewById(R.id.radioGroup1);
		inputView = (EditText) view.findViewById(R.id.edit_input);
		mAdapter = new MyAdapter(getActivity());
		listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listView.setAdapter(mAdapter);
		
//		listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//		listView.setAdapter(mAdapter);
		
		mAdapter.registerDataSetObserver(new DataSetObserver() {
			public void OnChanged(){
	        	super.onChanged();
	        		
	        	listView.setSelection(mAdapter.getCount() -1);
	        		
	       	}
		});

		
		btn = (Button) view.findViewById(R.id.btn_chat_add);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String keyword = inputView.getText().toString();
				ItemData data = new ItemData();
				switch (group.getCheckedRadioButtonId()) {
				case R.id.radio_date:
					data.message = new Date().toString();
					data.type = ItemData.TYPE_DATE;
					mAdapter.add(data);
					break;
				case R.id.radio_send:
					if (keyword != null && !keyword.equals("")) {
						data.message = keyword;
						data.type = ItemData.TYPE_SEND;
						mAdapter.add(data);
						inputView.setText("");
					}
					break;
				case R.id.radio_receive:
				default:
					if (keyword != null && !keyword.equals("")) {
						data.message = keyword;
						data.type = ItemData.TYPE_RECEIVE;
						mAdapter.add(data);
						inputView.setText("");
					}
					break;
				}
			}
		});

		Button btn2 = (Button) view.findViewById(R.id.btn_chat_socket);
		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);
			}
		});

		
//		============tabs메뉴 에딧텍스트 상태에 따라 쇼/하이드 하기=========================
		inputView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
//				if(hasFocus){
//					Toast.makeText(getActivity(), "hasFocus: " + hasFocus, Toast.LENGTH_SHORT).show();	
//				} else {
//					Toast.makeText(getActivity(), "hasFocus: " + hasFocus, Toast.LENGTH_SHORT).show();
//				}
				
//				if(hasFocus){
//					((MainActivity) getActivity()).setMenuInvisibility();
//				} else {
//					Toast.makeText(getActivity(), "onFocus else..", Toast.LENGTH_SHORT).show();
//					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//					((MainActivity) getActivity()).setMenuVisibility();
//				}
//				if(toggle){
//					((MainActivity) getActivity()).setMenuVisibility();
//				} else {
//					((MainActivity) getActivity()).setMenuInvisibility();
//				}
			}
			
		});
		
		return view;
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
