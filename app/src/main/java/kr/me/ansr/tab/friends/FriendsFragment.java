package kr.me.ansr.tab.friends;


import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.tab.board.ChildOneFragment;
import kr.me.ansr.tab.board.ChildTwoFragment;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class FriendsFragment extends PagerFragment {


	AppCompatActivity activity;
	FragmentTabHost tabHost;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container, false);
		activity = (AppCompatActivity) getActivity();

		tabHost = (FragmentTabHost) view.findViewById(R.id.tabhost);
		tabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		tabHost.addTab(tabHost.newTabSpec("friends1").setIndicator("학교사람들"), FriendsSectionFragment.class, null);
		tabHost.addTab(tabHost.newTabSpec("friends2").setIndicator("일촌"), ChildTwoFragment.class, null);

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if(tabId == "friends1"){
					Toast.makeText(getActivity(), "f1", Toast.LENGTH_SHORT).show();
//					tabHost.getCurrentTab(0)
				} else if(tabId == "friends2"){
					Toast.makeText(getActivity(), "f2", Toast.LENGTH_SHORT).show();
				}
			}
		});

		tabHost.setCurrentTabByTag("friends1");

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
			if(activity != null){
				activity.getSupportActionBar().setTitle("Friends Fragment");
			}
		}
	}
}
