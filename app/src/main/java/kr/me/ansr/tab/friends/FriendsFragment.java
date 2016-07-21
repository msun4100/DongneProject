package kr.me.ansr.tab.friends;


import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class FriendsFragment extends PagerFragment {

//	item_friends_child_layout, fragment_friends, item_friends_group_layout 지워

	ExpandableListView listView;
	MyAdapter mAdapter;
	AppCompatActivity activity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_friends, container,
				false);

		activity = (AppCompatActivity) getActivity();
		TextView tv = (TextView) view.findViewById(R.id.textView1);
		tv.setText("Fragment 1");

		listView = (ExpandableListView) view
				.findViewById(R.id.expandableListView1);
		mAdapter = new MyAdapter(getActivity());
		listView.setAdapter(mAdapter);

		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				ChildItem child = (ChildItem) mAdapter.getChild(groupPosition,
						childPosition);
				Toast.makeText(getActivity(), "child : " + child.name,
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		listView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
//				Toast.makeText(getActivity(), "Group collapse", Toast.LENGTH_SHORT).show();
				listView.expandGroup(groupPosition);
			}
		});

		listView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
//				Toast.makeText(getActivity(), "Group expand", Toast.LENGTH_SHORT).show();
			}
		});

		initData();

		for (int i = 0; i < mAdapter.getGroupCount(); i++) {
			listView.expandGroup(i);
		}

		return view;
	}

	private void initData() {
		mAdapter.put("내 프로필", new ChildItem(0,"user01","univ",16,"job"));
		for(int i=0; i<10; i++){
			mAdapter.put("친구목록", new ChildItem(i,("user"+i),"univ",i+1,"job"));		
		}
//		for (int i = 0; i < 3; i++) {
//			for (int j = 0; j < 5; j++) {
//				
//				mAdapter.put("group"+i, new ChildItem(i,("user"+i),"univ",i+1,"job"));
////				mAdapter.put("group"+i, "child"+i+":"+j);
//			}
//		}
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
//			Toast.makeText(getActivity(), ""+ PropertyManager.getInstance().getIsTab2Visible(), Toast.LENGTH_SHORT).show();
			if(activity != null){
				activity.getSupportActionBar().setTitle("Friends Fragment");
			}

		}
	}
}
