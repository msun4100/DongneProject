package kr.me.ansr.tab.board;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChildTwoFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		TextView tv = new TextView(getActivity());
		tv.setText("Fragment child 2");
		return tv;
	}

}
