package kr.me.ansr.tab.board;


import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.board.one.ChildOneFragment;
import kr.me.ansr.tab.board.two.ChildTwoFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

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
		setHasOptionsMenu(true);
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
				Log.e("current tabId:", tabId);
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

	MenuItem menuNext;
	ImageView imageNext;
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_f_board, menu);
		menuNext = menu.findItem(R.id.menu_board_write);
		imageNext = new ImageView(getActivity());
		imageNext.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		imageNext.setPadding(16, 0, 16, 0);
		imageNext.setImageResource(R.drawable.a_write_menu_selector);
		imageNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				((WriteActivity)getActivity()).imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				Toast.makeText(getActivity(), "custom img menu", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getActivity(), BoardWriteActivity.class);
				intent.putExtra("putkey", "passed key");
				startActivityForResult(intent, 100); //tabHost가 있는 BoardFragment에서 리절트를 받음
			}
		});
		menuNext.setActionView(imageNext);
		super.onCreateOptionsMenu(menu, inflater);
	}
	//onOptionsItemSelected()
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//		if(id == R.id.menu_board_write){
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

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

	/**
	 * NestedFragment에서 startactivityForresult실행시 fragment에 들어오지 않는 문제
	 * param : ActivityResultEvent
	 */

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case BoardInfo.BOARD_RC_NUM:
				if (resultCode == getActivity().RESULT_OK) {
					EventBus.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
				}
				break;
			case 100:
				if (resultCode == getActivity().RESULT_OK) {
					Bundle extraBundle = data.getExtras();
					String returnString = extraBundle.getString("return");
					if(returnString.equals("success")){
						Log.e("afterWrite", "success");
						Toast.makeText(getActivity(), "return key== "+returnString, Toast.LENGTH_LONG).show();
						EventBus.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
					} else {
						Log.e("afterWrite", "failure");
						Toast.makeText(getActivity(), "return key== "+returnString, Toast.LENGTH_SHORT).show();
					}

				}
				break;
		}


	}


}
