package kr.me.ansr.tab.board;


import java.util.List;

import kr.me.ansr.R;
import kr.me.ansr.tab.board.detail.BoardDetailActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ChildOneFragment extends Fragment {
	
	ListView listView;
	MyAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_board_one, container, false);
	
		listView = (ListView)view.findViewById(R.id.boardOne_listView1);
		mAdapter = new MyAdapter(getActivity());
		listView.setClickable(false);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Object data = listView.getItemAtPosition(position);
				ItemData item = (ItemData) data;
				Toast.makeText(getActivity(), "list clicked : " + item.title,	Toast.LENGTH_SHORT).show();
				
				
				Intent intent = new Intent(getActivity(), BoardDetailActivity.class);
//				intent.putExtra(CommonResultItem.REQUEST_NUMBER, "" + item.num);
				startActivity(intent);
				
			}
		});
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Object data = listView.getItemAtPosition(position);
//				if (data instanceof String) {
//					String text = (String) data;
//					Toast.makeText(getActivity(), "header click : " + text,
//							Toast.LENGTH_SHORT).show();
//				} else {
//					ItemData item = (ItemData) data;
//					Toast.makeText(getActivity(),
//							"item click : " + item.title, Toast.LENGTH_SHORT)
//							.show();
//				}
//			}
//		});

		initData();
		
		
		
		
		return view;
	}
	
	private void initData() {

		List<ItemData> items = DataManager.getInstance().getItemDataDummyList();
		for (ItemData id : items) {
			mAdapter.add(id);
		}
		// for (int i = 0; i < 10; i++) {
		// ItemData id = new ItemData();
		// id.iconId = R.drawable.ic_launcher;
		// id.title = "title" + i;
		// id.desc = "desc" + i;
		// id.like = i;
		// mAdapter.add(id);
		// }
	}
	
	
}
