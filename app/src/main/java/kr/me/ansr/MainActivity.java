package kr.me.ansr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kr.me.ansr.image.upload.Config;
import kr.me.ansr.login.SplashActivity;
import kr.me.ansr.tab.board.BoardFragment;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.chat.GcmChatFragment;
import kr.me.ansr.tab.friends.FriendsFragment;
import kr.me.ansr.tab.meet.MeetFragment;
import kr.me.ansr.tab.mypage.MypageFragment;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	TabHost tabHost;
	ViewPager pager;
	TabsAdapter mAdapter;
	TabWidget tabs;
	private static final int PAGER_OFFSET_LIMIT = 3;
	public ImageView toolbarIcon;
	public TextView toolbarTitle;

	public static TextView userCount;
	public static TextView chatCount;
	public static TextView pushCount;

	public static void setUserCount(final int count) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(count == 0){
					userCount.setVisibility(View.GONE);
					userCount.setText(""+count);
				} else {
					userCount.setVisibility(View.VISIBLE);
					userCount.setText(""+count);
				}
			}
		}, 100);
	}

	public static void setChatCount(final int count) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(count == 0){
					chatCount.setVisibility(View.GONE);
					chatCount.setText("N");
				} else {
					chatCount.setVisibility(View.VISIBLE);
					chatCount.setText("N");
				}
			}
		}, 100);
	}

	public static void setPushCount(final int count) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(count == 0){
					pushCount.setVisibility(View.GONE);
					pushCount.setText("N");
				} else {
					pushCount.setVisibility(View.VISIBLE);
					pushCount.setText("N");
				}
			}
		}, 100);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setElevation(0);	//6.0이상 음영효과 제거
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
		toolbarTitle.setText("학 교 사 람 들");
		tabs = (TabWidget)findViewById(android.R.id.tabs);
		tabHost = (TabHost)findViewById(android.R.id.tabhost);
		tabHost.setup();
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setOffscreenPageLimit(PAGER_OFFSET_LIMIT);
		mAdapter = new TabsAdapter(this, getSupportFragmentManager(), tabHost, pager);
		ImageView iv1 = new ImageView(this); iv1.setImageResource(R.drawable.z_tab_1_selector);
		ImageView iv2 = new ImageView(this); iv2.setImageResource(R.drawable.z_tab_2_selector);
		ImageView iv3 = new ImageView(this); iv3.setImageResource(R.drawable.z_tab_3_selector);
		ImageView iv4 = new ImageView(this); iv4.setImageResource(R.drawable.z_tab_4_selector);
		ImageView iv5 = new ImageView(this); iv5.setImageResource(R.drawable.z_tab_5_selector);
		mAdapter.addTab(tabHost.newTabSpec("tab1").setIndicator(iv1), FriendsFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab2").setIndicator(iv2), GcmChatFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab3").setIndicator(iv3), BoardFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab4").setIndicator(iv4), MeetFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab5").setIndicator(iv5), MypageFragment.class, null);
		
		mAdapter.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {

			}
		});
		mAdapter.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {

			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}
			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		userCount = (TextView) findViewById(R.id.tab_user_count);
		chatCount = (TextView) findViewById(R.id.tab_chat_count);
		pushCount = (TextView) findViewById(R.id.tab_push_count);

		Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("MainActivity");
		t.send(new HitBuilders.AppViewBuilder().build());

		if (savedInstanceState != null) {
			mAdapter.onRestoreInstanceState(savedInstanceState);
			String tag = savedInstanceState.getString("tabTag");
			tabHost.setCurrentTabByTag(tag);
		}

	} //onCreate

	public ImageView getToolbarIcon() {
		return toolbarIcon;
	}

	public TextView getToolbarTitle() {
		if(toolbarTitle != null){
			return toolbarTitle;
		}
		return null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAdapter.onSaveInstanceState(outState);
		String tag = tabHost.getCurrentTabTag();
		outState.putString("tabTag", tag);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		getMenuInflater().inflate(R.menu.menu_empty_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			t.send(new HitBuilders.EventBuilder().setCategory(getClass().getSimpleName()).setAction("Press Menu").setLabel("setting Click").build());
//			Toast.makeText(getApplicationContext(), "UsingLocation: "+PropertyManager.getInstance().getUsingLocation()
//					+"\nlatitude "+PropertyManager.getInstance().getLatitude()
//					+"\nlongitude "+PropertyManager.getInstance().getLongitude()
//					+"\nemail "+PropertyManager.getInstance().getEmail()
//					+"\nuserId "+PropertyManager.getInstance().getUserId()
//					+"\nusername "+PropertyManager.getInstance().getUserName()
//					+"\nunivId "+PropertyManager.getInstance().getUnivId()
//					+"\nunivName "+PropertyManager.getInstance().getUnivName()
//					+"\ndeptName "+PropertyManager.getInstance().getDeptName()
//					+"\nprofile "+PropertyManager.getInstance().getProfile()
//							+"\nisTab2visible "+PropertyManager.getInstance().getIsTab2Visible()
//							+"\nlastUpdate "+PropertyManager.getInstance().getLastUpdate()
//							+"\njobname "+PropertyManager.getInstance().getJobName()
//							+"\njobteam "+PropertyManager.getInstance().getJobTeam()
//							+"\nnewCount "+PropertyManager.getInstance().getNewCount()
//							+"\nresizeValue "+ Config.resizeValue
//					, Toast.LENGTH_LONG).show();
			return true;
		}
        if (id == R.id.logout) {
			t.send(new HitBuilders.EventBuilder().setCategory(getClass().getSimpleName()).setAction("Press Menu").setLabel("logout Click").build());
//            PropertyManager.getInstance().clearProperties();
//            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	public void setMenuVisibility() {
		Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_anim);
		if (tabs.getVisibility() == View.GONE) {
			tabs.setVisibility(View.VISIBLE);
			tabs.startAnimation(anim);
		}
	}

	public void setMenuInvisibility() {
		Animation anim = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.hide_anim);
		if (tabs.getVisibility() == View.VISIBLE) {
			tabs.setVisibility(View.GONE);
			tabs.startAnimation(anim);
		}
	}

	static Handler mHandler = new Handler(Looper.getMainLooper());
	//	========BackPress======
	public static final int MESSAGE_TIME_OUT_BACK_KEY = 0;
	public static final int TIME_BACK_KEY = 2000;
	boolean isBackPressed = false;
	Handler mBackHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
				case MESSAGE_TIME_OUT_BACK_KEY:
					isBackPressed = false;
					break;
			}
		}
	};
	@Override
	public void onBackPressed() {
		if( !isBackPressed ){
			isBackPressed = true;
			Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
			mBackHandler.sendEmptyMessageDelayed(MESSAGE_TIME_OUT_BACK_KEY, TIME_BACK_KEY);
		} else {
			mBackHandler.removeMessages(MESSAGE_TIME_OUT_BACK_KEY);
			super.onBackPressed();
		}
	}

	public static void setStatusBarColor(Activity activity, int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = activity.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(color);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent: ");	//invoke newIntent -> onResume. (onCreate -> onStart -> onResume X)
		Log.d(TAG, "tab: " + intent.getStringExtra("tab"));
		String tab = intent.getStringExtra("tab");
		if( tab != null){
			switch (tab){
				case "tab1":
					break;
				case "tab2":
					tabHost.setCurrentTab(1);	//chat
//					tabHost.setCurrentTabByTag(tab);
					break;
				case "tab3":
				case "tab4":
					tabHost.setCurrentTab(3);	//push
					break;
				default:
					break;
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
//		Log.d(TAG, "onResume: ");
//		Intent intent = getIntent();
//		Log.d(TAG, "tab: " + intent.getStringExtra("tab"));
//		String tab = intent.getStringExtra("tab");
//		if( tab != null){
//			switch (tab){
//				case "tab1":
//					break;
//				case "tab2":
//					tabHost.setCurrentTab(1);	//chat
////					tabHost.setCurrentTabByTag(tab);
//					break;
//				case "tab3":
//				case "tab4":
//					tabHost.setCurrentTab(3);	//push
//					break;
//				default:
//					break;
//			}
//		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		super.onStop();
	}
}
