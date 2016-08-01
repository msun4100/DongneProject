package kr.me.ansr.tab.board;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

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
}
