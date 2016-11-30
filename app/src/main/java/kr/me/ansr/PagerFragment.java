package kr.me.ansr;

import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.GoogleAnalytics;

public class PagerFragment extends Fragment {
	public void onPageCurrent() {}

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
