package kr.me.ansr.tab.board;


import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.common.event.ActivityResultEvent;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.board.one.ChildOneFragment;
import kr.me.ansr.tab.board.two.ChildTwoFragment;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class BoardFragment extends PagerFragment {

	private static final String TAG = BoardFragment.class.getSimpleName();
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
		checkBoardSize();
		activity = (AppCompatActivity) getActivity();
		tabs = (TabWidget)v.findViewById(android.R.id.tabs);
		tabHost = (TabHost)v.findViewById(android.R.id.tabhost);
		tabHost.setup();
		pager = (BoardViewPager)v.findViewById(R.id.pager);
		pager.setOffscreenPageLimit(PAGER_OFFSET_LIMIT);
		mAdapter = new TabsAdapter(getActivity(), getChildFragmentManager(), tabHost, pager);
		ImageView iv1 = new ImageView(getActivity()); iv1.setImageResource(R.drawable.e_board_tab1_selector);
		ImageView iv2 = new ImageView(getActivity()); iv2.setImageResource(R.drawable.e_board_tab2_selector);
		mAdapter.addTab(tabHost.newTabSpec("ttab1").setIndicator(iv1), ChildOneFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("ttab2").setIndicator(iv2), ChildTwoFragment.class, null);

		mAdapter.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
//				Log.e("current tabId:", tabId);
//				Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
				switch (tabId){
					case "ttab2":
						currentTab = "1";
//						t.send(new HitBuilders.EventBuilder().setCategory("TAB3_"+getClass().getSimpleName()).setAction("Press Tab").setLabel("Tab2 Click").build());
						break;
					case "ttab1":
					default:
						currentTab = "0";
//						t.send(new HitBuilders.EventBuilder().setCategory("TAB3_"+getClass().getSimpleName()).setAction("Press Tab").setLabel("Tab1 Click").build());
						break;
				}
			}
		});
		mAdapter.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		//	on 1118, child 에 달아봄
//		Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
//		t.setScreenName("TAB3_"+getClass().getSimpleName());
//		t.send(new HitBuilders.AppViewBuilder().build());

		//custom viewpager 때 수정한 코드
		if (savedInstanceState != null) {
			mAdapter.onRestoreInstanceState(savedInstanceState);
			String tag = savedInstanceState.getString("tabTag2");	//tabTag == MainActivity,
			tabHost.setCurrentTabByTag(tag);
		}


		return v;
	}

	MenuItem menuNext;
	ImageView imageNext;
//	public static final String BOARD_TAB_ONE = "studentTab";
//	public static final String BOARD_TAB_TWO = "graduateTab";
	public String currentTab = "0";
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_f_board, menu);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		menuNext = menu.findItem(R.id.menu_board_write);
		imageNext = new ImageView(getActivity());
		imageNext.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		imageNext.setPadding(16, 0, 16, 0);
		imageNext.setImageResource(R.drawable.e__write_2);
//		imageNext.setImageResource(R.drawable.a_write_menu_selector);
		imageNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				((WriteActivity)getActivity()).imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				Toast.makeText(getActivity(), "currentTab: " + currentTab, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getActivity(), BoardWriteActivity.class);
				intent.putExtra("currentTab", currentTab);
				intent.putExtra("type", "new");
				startActivityForResult(intent, BoardWriteActivity.BOARD_WRITE_RC_NEW); //tabHost가 있는 BoardFragment에서 리절트를 받음
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
				activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				activity.getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.e__titlebar_1));
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false); //챗프래그먼트에서 백버튼 자리 메뉴를 사용하기 때문에
				((MainActivity)getActivity()).getToolbarTitle().setText("");
				MainActivity.setStatusBarColor(activity, 0xffffff);
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
			case FriendsSectionFragment.FRIENDS_RC_NUM:	//프로필 이미지클릭
				if (resultCode == getActivity().RESULT_OK) {
					Bundle extraBundle = data.getExtras();
					FriendsResult result = (FriendsResult)extraBundle.getSerializable(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM);
					if (result != null) {
						EventBus.getInstance().post((FriendsResult)result);
//						Log.e(TAG, "onActivityResult: "+ result);
					}
				}
				break;
			case BoardInfo.BOARD_RC_NUM:
				if (resultCode == getActivity().RESULT_OK) {
					Bundle extraBundle = data.getExtras();
//					int position = extraBundle.getInt(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, -1);
					BoardResult result = (BoardResult)extraBundle.getSerializable(BoardInfo.BOARD_DETAIL_MODIFIED_ITEM);
					if (result != null) {
						EventBus.getInstance().post((BoardResult)result);
					}
					String next = extraBundle.getString("_NEXT_");	//on 1130
					if(next != null){
						EventBus.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
					}
				}
				break;
			case BoardWriteActivity.BOARD_WRITE_RC_NEW:
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
			case BoardWriteActivity.BOARD_WRITE_RC_EDIT:
				if (resultCode == getActivity().RESULT_OK) {
					Bundle extraBundle = data.getExtras();
					String returnString = extraBundle.getString("return");
					Toast.makeText(getActivity(), "return key== "+returnString, Toast.LENGTH_LONG).show();
					if(returnString.equals("success")){
						Log.e("afterEdit", "success");
						BoardResult result = (BoardResult)extraBundle.getSerializable("mItem");
						if (result != null) {
							EventBus.getInstance().post((BoardResult)result);
						}
					} else {
						Log.e("afterEdit", "failure");
						Toast.makeText(getActivity(), "return key== "+returnString, Toast.LENGTH_SHORT).show();
					}
				}
				break;
		}


	}

	private void setBoardResizePixel(){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		Config.resizeValue = width;
		Log.e(TAG, "setBoardResizePixel: "+"width:"+width+" height:"+height );
	}
	private void checkBoardSize(){
		if(Config.resizeValue == 0 || Config.resizeValue == -1){
			setBoardResizePixel();
		}
	}
}
