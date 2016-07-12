package kr.me.ansr.tab.board;


import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BoardFragment extends PagerFragment {

	FragmentTabHost tabHost;
	AppCompatActivity activity;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_board, container, false);
		activity = (AppCompatActivity) getActivity();
//		activity.getSupportActionBar().setTitle("Board Fragment");
		tabHost = (FragmentTabHost)v.findViewById(R.id.tabhost);
		tabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		tabHost.addTab(tabHost.newTabSpec("ct1").setIndicator("재학생"), ChildOneFragment.class, null);
		tabHost.addTab(tabHost.newTabSpec("ct2").setIndicator("졸업생"), ChildTwoFragment.class, null);
		
		
		return v;
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
				activity.getSupportActionBar().setTitle("Board Fragment");
			}
		}
	}
}
