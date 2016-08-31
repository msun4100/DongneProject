package kr.me.ansr.tab.friends;


import kr.me.ansr.MainActivity;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import kr.me.ansr.tab.friends.tabtwo.FriendsTwoFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;

public class FriendsFragment extends PagerFragment {


	AppCompatActivity activity;
	FragmentTabHost tabHost;
	Spinner spinner;
	MySpinnerAdapter mySpinnerAdapter;
	String mSpinnerItem;
	public int mSearchOption = -1;
	InputMethodManager imm;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container, false);
		activity = (AppCompatActivity) getActivity();
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

//		======================================
		spinner = (Spinner) view.findViewById(R.id.spinner);
		mySpinnerAdapter = new MySpinnerAdapter();
		spinner.setAdapter(mySpinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				String str = (String)mySpinnerAdapter.getItem(position);
				mSearchOption = position;
				Log.e("clicked position: ", ""+position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				spinner.setSelection(0);
			}
		});
		initData();
		return view;
	}

	private void initData(){
		String[] searchArray = getResources().getStringArray(R.array.search_items);
		for (int i = 0; i < searchArray.length; i++) {
			mySpinnerAdapter.add(searchArray[i]);
		}
		spinner.setSelection(0);
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
//				activity.getSupportActionBar().setTitle("       Friends Fragment");
//				activity.getSupportActionBar().setBackgroundDrawable(R.drawable.d_write_bg);
				activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				String str = PropertyManager.getInstance().getDeptName();
				if(str != null && !str.equals("")){
					((MainActivity)getActivity()).getToolbarTitle().setText(str);
				} else {
					((MainActivity)getActivity()).getToolbarTitle().setText("학교 사람들");
				}

			}
		}
	}
}
