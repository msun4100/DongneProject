package kr.me.ansr.tab.mypage;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.tab.friends.recycler.FriendsDataManager;
import kr.me.ansr.tab.friends.recycler.SectionAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MypageFragment extends PagerFragment {

	AppCompatActivity activity;
	@Bind(R.id.image_my_next) ImageView nextIcon;
	@Bind(R.id.image_my_thumb) ImageView thumbIcon;
	@Bind(R.id.image_my_0_0) ImageView menu00;
	@Bind(R.id.image_my_0_1) ImageView menu01;
	@Bind(R.id.image_my_0_2) ImageView menu02;
	@Bind(R.id.image_my_1_0) ImageView menu10;
	@Bind(R.id.image_my_1_1) ImageView menu11;
	@Bind(R.id.image_my_1_2) ImageView menu12;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mypage, container, false);
		ButterKnife.bind(this, view);
		activity = (AppCompatActivity) getActivity();
		nextIcon.setOnClickListener(mListener);
		thumbIcon.setOnClickListener(mListener);
		menu00.setOnClickListener(mListener);
		menu01.setOnClickListener(mListener);
		menu02.setOnClickListener(mListener);
		menu10.setOnClickListener(mListener);
		menu11.setOnClickListener(mListener);
		menu12.setOnClickListener(mListener);


		return view;
	}

	public View.OnClickListener mListener = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.image_my_0_0:
					Toast.makeText(getActivity(), "00", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_0_1:
					Toast.makeText(getActivity(), "01", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_0_2:
					Toast.makeText(getActivity(), "02", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getActivity(), AlarmActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_1_0:
					Toast.makeText(getActivity(), "10", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_1_1:
					Toast.makeText(getActivity(), "11", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_1_2:
					Toast.makeText(getActivity(), "12", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_thumb:
					Toast.makeText(getActivity(), "thumb", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_next:
					Toast.makeText(getActivity(), "next", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};

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
