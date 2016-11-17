package kr.me.ansr.tab.mypage;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CommonInfo;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.common.event.FriendsFragmentResultEvent;
import kr.me.ansr.image.MediaStoreActivity;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.model.Sns;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import kr.me.ansr.tab.mypage.account.ManagementActivity;
import kr.me.ansr.tab.mypage.setting.SettingActivity;
import okhttp3.Request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

public class MypageFragment extends PagerFragment {
	private static final String TAG = MypageFragment.class.getSimpleName();
	AppCompatActivity activity;
//	@Bind(R.id.image_my_0_0) ImageButton menu00;
	@Bind(R.id.frame_my_0_0) FrameLayout menu00;
	@Bind(R.id.frame_my_0_1) FrameLayout menu01;
	@Bind(R.id.frame_my_0_2) FrameLayout menu02;
	@Bind(R.id.frame_my_1_0) FrameLayout menu10;
	@Bind(R.id.frame_my_1_1) FrameLayout menu11;
	@Bind(R.id.frame_my_1_2) FrameLayout menu12;

	@Bind(R.id.LinearLayout1) LinearLayout nextLayout;
	@Bind(R.id.image_my_thumb) ImageView thumbIcon;
	@Bind(R.id.text_my_username) TextView usernameView;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mypage, container, false);
		ButterKnife.bind(this, view);
		activity = (AppCompatActivity) getActivity();
		nextLayout.setOnClickListener(mListener);

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
				case R.id.frame_my_0_0:
					intent = new Intent(getActivity(), MyWritingActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_0_1:
				case R.id.frame_my_0_1:
					intent = new Intent(getActivity(), MyInterestActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_0_2:
				case R.id.frame_my_0_2:
					intent = new Intent(getActivity(), AlarmActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_1_0:
				case R.id.frame_my_1_0:
					intent = new Intent(getActivity(), FriendStatusActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_1_1:
				case R.id.frame_my_1_1:
					intent = new Intent(getActivity(), ManagementActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_1_2:
				case R.id.frame_my_1_2:
					intent = new Intent(getActivity(), SettingActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
					break;
				case R.id.image_my_thumb:
					Toast.makeText(getActivity(), "thumb", Toast.LENGTH_SHORT).show();
					break;
				case R.id.image_my_next:
				case R.id.LinearLayout1:
					Toast.makeText(getActivity(), "next", Toast.LENGTH_SHORT).show();
					intent = new Intent(getActivity(), MediaStoreActivity.class);
					FriendsResult item = FriendsSectionFragment.getUserInfo();
					if(item != null){
						intent.putExtra("mItem", item);
						intent.putExtra("tag", MediaStoreActivity.TAG_MY_PAGE);
						startActivityForResult(intent, 125);
					} else {
						Log.d(TAG, "onClick: " + "item is null");
					}
					break;
				default:
					break;
			}
		}
	};


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 125:
				if (resultCode == Activity.RESULT_OK ) {
					Log.d("TAG", "onActivityResult: "+resultCode);
					Bundle extraBundle = data.getExtras();
					FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
					result.updatedAt = MyApplication.getInstance().getCurrentTimeStampString();	//TimeStamp만 변경 --> 프로필 이미지 갱신
					// 사실상 여기서 result는 사용 안함
					EventBus.getInstance().post(result);	//post가 호출 안되네.. pause??라서 그런가?
					if(result != null){
						//OK로 오는 경우는 이미 ImageHomeFragment에서 EditUser 를 하고 넘어온 값.	//캔슬일 경우만 updatedAt 갱신함.(프로필 갱신을 위해서)
						initData();
					}
				}
				else if(resultCode == Activity.RESULT_CANCELED){
					Log.d("TAG", "onActivityResult: "+resultCode);
					Bundle extraBundle = data.getExtras();
					FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
					result.updatedAt = MyApplication.getInstance().getCurrentTimeStampString();	//TimeStamp만 변경 --> 프로필 이미지 갱신
					// 사실상 여기서 result는 사용 안함
					EventBus.getInstance().post(result);	//post가 호출 안되네.. pause??라서 그런가?
					if(result != null){
						editUser(); 	//전부다 null로 요청하고 updatedAt만 수정
//						initData();		//editUser Success에서 처리
					}
				}
				break;
		}
	}
	ProgressDialog dialog = null;
	private void editUser(){
		NetworkManager.getInstance().putDongnePutEditUser(getActivity(), PropertyManager.getInstance().getDeptName(), null, null, null, null, null, null, new NetworkManager.OnResultListener<CommonInfo>() {
			@Override
			public void onSuccess(Request request, CommonInfo result) {
				if (result.error.equals(true)) {
					Toast.makeText(getActivity(), "회원정보 수정에 실패하였습니다. 다시 요청해주세요.", Toast.LENGTH_SHORT).show();
					Log.e("error on edit:", result.message);
				} else {
					Toast.makeText(getActivity(), "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
					initData();
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(getActivity(), "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Loading....");
		dialog.show();
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
				activity.getSupportActionBar().setTitle("");
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false); //챗프래그먼트에서 백버튼 자리 메뉴를 사용하기 때문에
				activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				activity.getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.z_titlebar_more));
				((MainActivity)getActivity()).getToolbarTitle().setText("");
			}
		}
	}
}
