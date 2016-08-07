package kr.me.ansr.tab.mypage;

import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.tab.friends.recycler.FriendsDataManager;
import kr.me.ansr.tab.friends.recycler.SectionAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MypageFragment extends PagerFragment {

	AppCompatActivity activity;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_mypage, container, false);
		activity = (AppCompatActivity) getActivity();



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
			if(activity != null){
				activity.getSupportActionBar().setTitle("더 보 기");
			}
		}
	}
}
