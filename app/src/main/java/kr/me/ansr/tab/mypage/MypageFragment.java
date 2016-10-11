package kr.me.ansr.tab.mypage;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.image.MediaStoreActivity;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.recycler.FriendsDataManager;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import kr.me.ansr.tab.friends.recycler.SectionAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

public class MypageFragment extends PagerFragment {

	AppCompatActivity activity;

	@Bind(R.id.image_my_0_0) ImageView menu00;
	@Bind(R.id.image_my_0_1) ImageView menu01;
	@Bind(R.id.image_my_0_2) ImageView menu02;
	@Bind(R.id.image_my_1_0) ImageView menu10;
	@Bind(R.id.image_my_1_1) ImageView menu11;
//	@Bind(R.id.image_my_1_2) ImageView menu12;
	@Bind(R.id.frame_my_1_2) FrameLayout menu12;

	@Bind(R.id.image_my_next) ImageView nextIcon;
	@Bind(R.id.image_my_thumb) ImageView thumbIcon;
	@Bind(R.id.text_my_username) TextView usernameView;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mypage, container, false);
		ButterKnife.bind(this, view);
		activity = (AppCompatActivity) getActivity();
		nextIcon.setOnClickListener(mListener);
		nextIcon.setImageResource(R.drawable.f_icon_next_1);
		thumbIcon.setOnClickListener(mListener);
		menu00.setOnClickListener(mListener);
		menu01.setOnClickListener(mListener);
		menu02.setOnClickListener(mListener);
		menu10.setOnClickListener(mListener);
		menu11.setOnClickListener(mListener);
		menu12.setOnClickListener(mListener);

		initData();
		return view;
	}

	private void initData(){
		String username = PropertyManager.getInstance().getUserName();
		if(username != null && !username.equals("")){
			usernameView.setText(username);
		} else {
			usernameView.setText("유저명");
		}

		String userId = PropertyManager.getInstance().getUserId();
		String url = Config.FILE_GET_URL.replace(":userId", "" + userId).replace(":size", "small");
		Glide.with(getContext()).load(url).placeholder(R.drawable.e__who_icon)
				.centerCrop()
				.signature(new StringSignature(MyApplication.getInstance().getCurrentTimeStampString()))//매번 새로고침
				.into(thumbIcon);
	}

	public View.OnClickListener mListener = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()){
				case R.id.image_my_0_0:
					Toast.makeText(getActivity(), "00", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_0_1:
					Toast.makeText(getActivity(), "01", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_0_2:
					Toast.makeText(getActivity(), "02", Toast.LENGTH_SHORT).show();
					intent = new Intent(getActivity(), AlarmActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_1_0:
					Toast.makeText(getActivity(), "10", Toast.LENGTH_SHORT).show();
					intent = new Intent(getActivity(), FriendStatusActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_1_1:
					Toast.makeText(getActivity(), "11", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_1_2:
					Toast.makeText(getActivity(), "12", Toast.LENGTH_SHORT).show();
					break;

				case R.id.frame_my_1_2:
					Toast.makeText(getActivity(), "12", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_thumb:
					Toast.makeText(getActivity(), "thumb", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_next:
					Toast.makeText(getActivity(), "next", Toast.LENGTH_SHORT).show();
					intent = new Intent(getActivity(), MediaStoreActivity.class);
					intent.putExtra("mItem", FriendsSectionFragment.getUserInfo());
					intent.putExtra("tag", MediaStoreActivity.TAG_MY_PAGE);
//					startActivityForResult(intent, 125);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
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
				activity.getSupportActionBar().setTitle("");
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false); //챗프래그먼트에서 백버튼 자리 메뉴를 사용하기 때문에
				activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				activity.getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.z_titlebar_more));
				((MainActivity)getActivity()).getToolbarTitle().setText("");
			}
		}
	}
}
