package kr.me.ansr.tab.mypage;

import kr.me.ansr.PagerFragment;
import kr.me.ansr.tab.friends.recycler.FriendsDataManager;
import kr.me.ansr.tab.friends.recycler.SectionAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MypageFragment extends PagerFragment {

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		TextView tv = new TextView(getActivity());
		tv.setText("Fragment 5");
		return tv;
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
//			FriendsDataManager.getInstance().items.clear();
//			SectionAdapter.items.clear();
		}
	}
}
