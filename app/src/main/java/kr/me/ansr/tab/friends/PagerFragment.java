package kr.me.ansr.tab.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;

import kr.me.ansr.common.event.EventBus;

public class PagerFragment extends Fragment {
	public void onPageCurrent() { }
	/*
	To get EventBus's posting events. children are inherited overriding functions
	*/
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		EventBus.getInstance().register(this);
	}
	@Override
	public void onDestroyView() {
		EventBus.getInstance().unregister(this);
		super.onDestroyView();
	}
	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
	}

	@Override
	public void onStop() {
		GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
		super.onStop();
	}
}
