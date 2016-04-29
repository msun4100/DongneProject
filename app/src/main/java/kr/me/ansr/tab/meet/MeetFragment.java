package kr.me.ansr.tab.meet;

import kr.me.ansr.MainActivity;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MeetFragment extends PagerFragment {

	
	boolean toggle = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meet, container,
				false);
		
		TextView tv = (TextView)view.findViewById(R.id.textView1);
		tv.setText("fragment 4");
		
		Button btn = (Button)view.findViewById(R.id.button1);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(toggle){
					((MainActivity) getActivity()).setMenuVisibility();
					toggle = !toggle;
				} else {
					((MainActivity) getActivity()).setMenuInvisibility();
					toggle = !toggle;
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
			// ...
		}
	}
}
