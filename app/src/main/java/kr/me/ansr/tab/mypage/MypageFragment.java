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
import android.text.TextUtils;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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

		//	on 1118
		Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("TAB5_"+getClass().getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());
		return view;
	}

	private void initData(){
		FriendsResult item = FriendsSectionFragment.getUserInfo();
		if(item == null){
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
		} else {
			usernameView.setText(item.username);
			if(!TextUtils.isEmpty(item.pic.small) && item.pic.small.equals("1")){
				String url = Config.FILE_GET_URL.replace(":userId", "" + item.userId).replace(":size", "small");
				Glide.with(getContext()).load(url).placeholder(R.drawable.e__who_icon)
						.centerCrop()
						.signature(new StringSignature(item.updatedAt))//매번 새로고침
						.into(thumbIcon);
			} else {
				thumbIcon.setImageResource(R.drawable.e__who_icon);
			}

		}

	}

	// override init func.
	private void changeData(FriendsResult item){
		if(item == null){
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
		} else {
			usernameView.setText(item.username);
			if(!TextUtils.isEmpty(item.pic.small) && item.pic.small.equals("1")){
				String url = Config.FILE_GET_URL.replace(":userId", "" + item.userId).replace(":size", "small");
				Glide.with(getContext()).load(url).placeholder(R.drawable.e__who_icon)
						.centerCrop()
						.signature(new StringSignature(item.updatedAt))//매번 새로고침
						.into(thumbIcon);
			} else {
				thumbIcon.setImageResource(R.drawable.e__who_icon);
			}
		}
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
					break;
				case R.id.image_my_next:
				case R.id.LinearLayout1:
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
					Log.d(TAG, "onActivityResult: OK");
					Bundle extraBundle = data.getExtras();
					FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
					Log.d(TAG, "onActivityResult: "+result.toString());
					EventBus.getInstance().post(result);	//post가 호출 안되네.. pause??라서 그런가?
					if(result != null){
						changeData(result);
					}
				}
				else if(resultCode == Activity.RESULT_CANCELED){
					Log.d(TAG, "onActivityResult: CANCEL");
					Bundle extraBundle = data.getExtras();
					FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
					Log.d(TAG, "onActivityResult: "+result.toString());
					EventBus.getInstance().post(result);	//post가 호출 안되네.. pause??라서 그런가?
					if(result != null){
						changeData(result);
					}
				}
				break;
		}
	}
	ProgressDialog dialog = null;
	private void editUser(){
		String reqDate = MyApplication.getInstance().getCurrentTimeStampString();
		NetworkManager.getInstance().putDongnePutEditUser(getActivity(), reqDate, PropertyManager.getInstance().getDeptName(), null, null, null, null, null, null, new NetworkManager.OnResultListener<CommonInfo>() {
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
