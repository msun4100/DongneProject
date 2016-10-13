package kr.me.ansr.tab.friends;


import kr.me.ansr.MainActivity;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.event.FriendsFragmentResultEvent;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.login.autocomplete.univ.UnivInfo;
import kr.me.ansr.login.autocomplete.univ.UnivResult;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import kr.me.ansr.tab.friends.tabtwo.FriendsTwoFragment;
import okhttp3.Request;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FriendsFragment extends PagerFragment {


	AppCompatActivity activity;
	TabHost tabHost;
	FriendsViewPager pager;   //	ViewPager pager;
	TabsAdapter mAdapter;
	TabWidget tabs;
	private static final int PAGER_OFFSET_LIMIT = 1;
	public static TextView totalUniv, totalFriends;

	InputMethodManager imm;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container, false);
		activity = (AppCompatActivity) getActivity();
//		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		tabs = (TabWidget)view.findViewById(android.R.id.tabs);
		tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		tabHost.setup();
		pager = (FriendsViewPager)view.findViewById(R.id.pager);
		pager.setOffscreenPageLimit(PAGER_OFFSET_LIMIT);
		mAdapter = new TabsAdapter(getActivity(), getChildFragmentManager(), tabHost, pager);
		ImageView iv1 = new ImageView(getActivity()); iv1.setImageResource(R.drawable.z_friends_tab_1_selector);
		ImageView iv2 = new ImageView(getActivity()); iv2.setImageResource(R.drawable.z_friends_tab_2_selector);
		mAdapter.addTab(tabHost.newTabSpec("friends1").setIndicator(iv1), FriendsSectionFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("friends2").setIndicator(iv2), FriendsTwoFragment.class, null);
		mAdapter.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				switch (tabId){
					case "friends1":
						currentTab = "f1";
						totalUniv.setTextColor(0xff606a72);
						totalFriends.setTextColor(0xff99999a);
						break;
					case "friends2":
					default:
						currentTab = "f2";
						totalUniv.setTextColor(0xff99999a);	//ffcbc8c6 보다 진한 색
						totalFriends.setTextColor(0xff606a72);
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

		totalUniv = (TextView)view.findViewById(R.id.text_total_univ);
		totalFriends = (TextView)view.findViewById(R.id.text_total_friends);

		//custom viewpager 때 수정한 코드
		if (savedInstanceState != null) {
			mAdapter.onRestoreInstanceState(savedInstanceState);
			String tag = savedInstanceState.getString("tabTagFriends");
			tabHost.setCurrentTabByTag(tag);
		}
		return view;
	}



	//custom viewpager 때 수정한 코드
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAdapter.onSaveInstanceState(outState);
		String tag = tabHost.getCurrentTabTag();
		outState.putString("tabTagFriends", tag);
	}
	public String currentTab = null;	//board에서 사용하던 변수



	@Override
	public void onPageCurrent() {
		super.onPageCurrent();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if(activity != null){
				activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				activity.getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.b_list_titlebar));
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false); //챗프래그먼트에서 백버튼 자리 메뉴를 사용하기 때문에

				String str = PropertyManager.getInstance().getUnivName();
				if(str != null && !str.equals("")){
					makeTitleString(str);
				} else {
					//univname이 없으면 univItem요청해서 프로퍼티매니저에 이름 저장하고 툴바 타이틀 만듬.
					getUnivItem();
				}
			}
		}
	}

	public void makeTitleString(String str){
		String m = "";
		for(int i=0; i<str.length(); i++){
			m += str.substring(i, i+1) + " ";
		}
		m = m.substring(0, m.length()-1);//마지막 여백 제외
		((MainActivity)getActivity()).getToolbarTitle().setText(m);
	}

	public void getUnivItem(){
		String univId = PropertyManager.getInstance().getUnivId();
		if(univId == null)
			return;
		NetworkManager.getInstance().getDongneUnivItem(getActivity(), univId, new NetworkManager.OnResultListener<UnivInfo>() {
			@Override
			public void onSuccess(Request request, UnivInfo result) {
				if (result.error.equals(false)) {
					if(result.result != null && result.result.size() == 1){
						String univName = result.result.get(0).univname;
						String univId = ""+result.result.get(0).univId;
						PropertyManager.getInstance().setUnivName(univName);
						PropertyManager.getInstance().setUnivId(univId);
						makeTitleString(univName);
					} else {
						((MainActivity)getActivity()).getToolbarTitle().setText("학 교 사 람 들");
					}
				} else { //error: true
					((MainActivity)getActivity()).getToolbarTitle().setText("학 교 사 람 들");
					Toast.makeText(getActivity(), "result.error:" + result.message, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				((MainActivity)getActivity()).getToolbarTitle().setText("학 교 사 람 들");
				Toast.makeText(getActivity(), "onFailure: " + cause, Toast.LENGTH_SHORT).show();
			}
		});
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case FriendsSectionFragment.FRIENDS_RC_NUM:
				if (resultCode == getActivity().RESULT_OK) {
					EventBus.getInstance().post(new FriendsFragmentResultEvent(requestCode, resultCode, data));
				}
				break;
//			case 100:
//				//서버 요청 성공하면 새로 고치는 코드
//				if (resultCode == getActivity().RESULT_OK) {
//					Bundle extraBundle = data.getExtras();
//					String returnString = extraBundle.getString("return");
//					if(returnString.equals("success")){
//						Log.e("afterWrite", "success");
//						Toast.makeText(getActivity(), "return key== "+returnString, Toast.LENGTH_LONG).show();
//						EventBus.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
//					} else {
//						Log.e("afterWrite", "failure");
//						Toast.makeText(getActivity(), "return key== "+returnString, Toast.LENGTH_SHORT).show();
//					}
//				}
//				break;
		}

	}

}
