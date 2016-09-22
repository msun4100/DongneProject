package kr.me.ansr.tab.meet;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;
import kr.me.ansr.database.DBConstant;
import kr.me.ansr.database.DBManager;
import kr.me.ansr.database.Push;
import kr.me.ansr.gcmchat.app.Config;
import kr.me.ansr.gcmchat.model.Message;
import kr.me.ansr.tab.board.detail.BoardDetailActivity;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;
import kr.me.ansr.tab.mypage.FriendStatusActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MeetFragment extends PagerFragment {
	private static final String TAG = MeetFragment.class.getSimpleName();

	AppCompatActivity activity;
	TextView emptyMsg;

	RecyclerView recyclerView;
	PushAdapter mAdapter;

	LinearLayoutManager layoutManager;
	SwipeRefreshLayout refreshLayout;
	boolean isLast = false;
	Handler mHandler = new Handler(Looper.getMainLooper());



	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meet, container, false);
		activity = (AppCompatActivity) getActivity();
		emptyMsg = (TextView)view.findViewById(R.id.text_empty_msg);
		emptyMsg.setText(getResources().getString(R.string.empty_feed_msg));
		//================================================
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
		refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
			@Override
			public void onRefresh() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
//                        if(!dialog.isShowing()){
//                            refreshLayout.setRefreshing(false);
//                        }
//                        일단 그냥 1초 있다가 사라지게, 현재 구현한 것은 getMoreItem() 같은 것 없이 전체리스트 불러오도록 처리
						refreshLayout.setRefreshing(false);
					}
				}, 1000);
			}
		});   //this로 하려면 implements 하고 오버라이드 코드 작성하면 됨.
		recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    getMoreItem();
				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int totalItemCount = mAdapter.getItemCount();
				int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
				if (totalItemCount > 0 && lastVisibleItemPosition != RecyclerView.NO_POSITION && (totalItemCount - 1 <= lastVisibleItemPosition)) {
					isLast = true;
				} else {
					isLast = false;
				}
			}
		});
		mAdapter = new PushAdapter();
		mAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Push data = mAdapter.getItem(position);
				//여기서 안해도 액티비티들어갔다 나오면서 onResume 될 때 갱신됨.
				//message_id가 5라서 StatusActivity로 들어가는 경우 말고 바로 색바뀌어야 되는 케이스가 있나?
//				mAdapter.setItemBackGround(position);
				data.bgColor = 1;
				DBManager.getInstance().update(data);

				Intent intent;
				switch (data.message_id){
					case 1:	//"님이 회원님의 게시글을 좋아합니다.",
					case 2://"님이 회원님의 게시글에 댓글을 남겼습니다.",
					case 3:	//"누군가 회원님의 게시글에 댓글을 남겼습니다.",
//						boardId랑 position을 같이 넘겨야 하는데, boardId는 푸시의 chat_room_id와 같음.
//							포지션이 안정해져있을때 처리를해야함. 여기서나 보드디테일에서나
//						BoardResult data = mAdapter.getItem(position);
//						Intent intent = new Intent(getActivity(), BoardDetailActivity.class);
//						intent.putExtra(BoardInfo.BOARD_DETAIL_BOARD_ID, data.boardId);
//						intent.putExtra(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, position);
//						intent.putExtra("currentTab", "0"); //재학생 탭 == 0
//						getParentFragment().startActivityForResult(intent, BoardInfo.BOARD_RC_NUM); //tabHost가 있는 BoardFragment에서 리절트를 받음
						break;
					case 4:	//"님이 회원님의 친구 신청을 수락하였습니다.",
						break;
					case 5:
						intent = new Intent(getActivity(), FriendStatusActivity.class);
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
						break;
				}

			}
		});
		mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public void onItemLongClick(View view, int position) {
				Push data = mAdapter.getItem(position);
				mAdapter.removeItem(data);
				DBManager.getInstance().delete(data);
				if(mAdapter.getItemCount() == 0){
					showLayout();
				}
			}
		});
		mAdapter.setOnAdapterItemClickListener(new PushAdapter.OnAdapterItemClickListener() {
			@Override
			public void onAdapterItemClick(PushAdapter adapter, View view, Push item, int type) {
				switch (type) {
					case 100:
						Toast.makeText(getActivity(), "iconView click"+ item.toString(), Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});

		recyclerView.setAdapter(mAdapter);
		layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.addItemDecoration(new MyDecoration(getActivity()));


		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
					// new push notification is received
					handlePushNotification(intent);
				}
			}
		};

		initData();

		return view;
	}

	private BroadcastReceiver mRegistrationBroadcastReceiver;

	private void initData(){
		mAdapter.clear();
		List<Push> items = DBManager.getInstance().searchAll();
		for (Push p : items) {
			Log.e("feed: ", p.toString());
			mAdapter.add(p);
		}
		showLayout();
	}
	private void showLayout(){
		if (mAdapter.getItemCount() > 0){
			emptyMsg.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
		} else {
			emptyMsg.setVisibility(View.VISIBLE);
			recyclerView.setVisibility(View.GONE);
		}
	}
	private void handlePushNotification(Intent intent) {
		int type = intent.getIntExtra("type", -1);
		if (type == Config.PUSH_TYPE_NOTIFICATION){
			initData();
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		initData();
		// register new push message receiver
		// by doing this, the activity will be notified each time a new message arrives
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(Config.PUSH_NOTIFICATION));
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}

	//====================================================================================
	@Override
	public void onPageCurrent() {
		super.onPageCurrent();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if(activity != null){
				activity.getSupportActionBar().setTitle("MeetFragment");
				activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false); //챗프래그먼트에서 백버튼 자리 메뉴를 사용하기 때문에
				activity.getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.e__titlebar_4));
				((MainActivity)getActivity()).getToolbarTitle().setText("");
			}
		}
	}
	//하단 메인메뉴 스크롤에 따라 숨기기 할때 테스트 해본 코드
//	Button btn = (Button)view.findViewById(R.id.button1);
//	btn.setOnClickListener(new View.OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			if(toggle){
//				((MainActivity) getActivity()).setMenuVisibility();
//				toggle = !toggle;
//			} else {
//				((MainActivity) getActivity()).setMenuInvisibility();
//				toggle = !toggle;
//			}
//
//		}
//	});

}
