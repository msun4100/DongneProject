package kr.me.ansr.tab.board;


import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.tab.board.one.ChildOneFragment;
import kr.me.ansr.tab.board.two.ChildTwoFragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

public class BoardFragment extends PagerFragment {

//	Fragment[] list = { new ChildOneFragment(), new ChildTwoFragment()};
	AppCompatActivity activity;
	TabHost tabHost;
	BoardViewPager pager;
//	ViewPager pager;
	TabsAdapter mAdapter;
	TabWidget tabs;
	private static final int PAGER_OFFSET_LIMIT = 1;
//	FragmentManager fm;
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_board, container, false);
		activity = (AppCompatActivity) getActivity();
		tabs = (TabWidget)v.findViewById(android.R.id.tabs);
		tabHost = (TabHost)v.findViewById(android.R.id.tabhost);
		tabHost.setup();
		pager = (BoardViewPager)v.findViewById(R.id.pager);
		pager.setOffscreenPageLimit(PAGER_OFFSET_LIMIT);
		mAdapter = new TabsAdapter(getActivity(), getChildFragmentManager(), tabHost, pager);
//		mAdapter.addTab(tabHost.newTabSpec("tab1").setIndicator("TAB1"), FriendsSectionFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("ttab1").setIndicator("TTAB1"), ChildOneFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("ttab2").setIndicator("TTAB2"), ChildTwoFragment.class, null);

		mAdapter.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
			}
		});
		mAdapter.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});

		//custom viewpager 때 수정한 코드
		if (savedInstanceState != null) {
			mAdapter.onRestoreInstanceState(savedInstanceState);
			String tag = savedInstanceState.getString("tabTag2");
			tabHost.setCurrentTabByTag(tag);
		}


		return v;
	}
	//custom viewpager 때 수정한 코드
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAdapter.onSaveInstanceState(outState);
		String tag = tabHost.getCurrentTabTag();
		outState.putString("tabTag2", tag);
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
				activity.getSupportActionBar().setTitle("Board Fragment");
			}
		}
	}
}
