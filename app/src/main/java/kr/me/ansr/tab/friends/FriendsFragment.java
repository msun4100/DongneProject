package kr.me.ansr.tab.friends;


import kr.me.ansr.MainActivity;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.common.event.FriendsFragmentResultEvent;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import kr.me.ansr.tab.friends.tabtwo.FriendsTwoFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

public class FriendsFragment extends PagerFragment {


	AppCompatActivity activity;
	FragmentTabHost tabHost;

	InputMethodManager imm;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container, false);
		activity = (AppCompatActivity) getActivity();
		((MainActivity)getActivity()).getToolbarTitle().setText("학 교 사 람 들");
//		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		tabHost = (FragmentTabHost) view.findViewById(R.id.tabhost);
		tabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		ImageView iv1 = new ImageView(getActivity()); iv1.setImageResource(R.drawable.z_friends_tab_1_selector);
		ImageView iv2 = new ImageView(getActivity()); iv2.setImageResource(R.drawable.z_friends_tab_2_selector);
		tabHost.addTab(tabHost.newTabSpec("friends1").setIndicator(iv1), FriendsSectionFragment.class, null);
		tabHost.addTab(tabHost.newTabSpec("friends2").setIndicator(iv2), FriendsTwoFragment.class, null);
		//activity group을 extends해야 쓸 수 있는 듯--> 메인액티비티가
//		tabHost.addTab(tabHost.newTabSpec("friends1").setIndicator("학교사람들")
//				.setContent(new Intent(getActivity(), FriendsSectionFragment.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
//		tabHost.addTab(tabHost.newTabSpec("friends2").setIndicator("일촌")
//				.setContent(new Intent(getActivity(), FriendsTwoFragment.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
//		tabHost.setCurrentTabByTag("friends1");
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if(tabId == "friends1"){
//					Toast.makeText(getActivity(), "f1", Toast.LENGTH_SHORT).show();
				} else if(tabId == "friends2"){
//					Toast.makeText(getActivity(), "f2", Toast.LENGTH_SHORT).show();
//					tabHost.getCurrentTab();
				}
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
			if(activity != null){
				activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				activity.getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.b_list_titlebar));
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false); //챗프래그먼트에서 백버튼 자리 메뉴를 사용하기 때문에

				String str = "인하대학교";
				String m = "";
				for(int i=0; i<str.length(); i++){
					m += str.substring(i, i+1) + " ";
				}
				m = m.substring(0, m.length()-1);
				((MainActivity)getActivity()).getToolbarTitle().setText(m);

//				String str = PropertyManager.getInstance().getUnivName();
//				if(str != null && !str.equals("")){
//					String m = "";
//					for(int i=0; i<str.length(); i++){
//						m += str.substring(i, i+1) + " ";
//					}
//					m = m.substring(0, m.length()-1);
//					((MainActivity)getActivity()).getToolbarTitle().setText(m);
//				} else {
//					((MainActivity)getActivity()).getToolbarTitle().setText("학 교 사 람 들");
//				}
			}
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case FriendsInfo.FRIENDS_RC_NUM:
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
