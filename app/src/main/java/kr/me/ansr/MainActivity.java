package kr.me.ansr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import kr.me.ansr.login.SplashActivity;
import kr.me.ansr.tab.board.BoardFragment;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.chat.GcmChatFragment;
import kr.me.ansr.tab.friends.FriendsFragment;
import kr.me.ansr.tab.meet.MeetFragment;
import kr.me.ansr.tab.mypage.MypageFragment;

public class MainActivity extends AppCompatActivity {

	TabHost tabHost;
	ViewPager pager;
	TabsAdapter mAdapter;
	TabWidget tabs;
	private static final int PAGER_OFFSET_LIMIT = 3;
	public ImageView toolbarIcon;
	public TextView toolbarTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setElevation(0);	//6.0이상 음영효과 제거
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
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
				// TODO Auto-generated method stub
			}
		});
		mAdapter.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});

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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Toast.makeText(getApplicationContext(), "UsingLocation: "+PropertyManager.getInstance().getUsingLocation()
					+"\nlatitude "+PropertyManager.getInstance().getLatitude()
					+"\nlongitude "+PropertyManager.getInstance().getLongitude()
					+"\nemail "+PropertyManager.getInstance().getEmail()
					+"\nuserId "+PropertyManager.getInstance().getUserId()
					+"\nusername "+PropertyManager.getInstance().getUserName()
					+"\nunivId "+PropertyManager.getInstance().getUnivId()
					+"\nunivName "+PropertyManager.getInstance().getUnivName()
					+"\nprofile "+PropertyManager.getInstance().getProfile()
					, Toast.LENGTH_LONG).show();
			return true;
		}
        if (id == R.id.logout) {
            PropertyManager.getInstance().clearProperties();
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            Toast.makeText(getApplicationContext(), "property email: "+PropertyManager.getInstance().getEmail(), Toast.LENGTH_LONG).show();
//            finish();
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
}
