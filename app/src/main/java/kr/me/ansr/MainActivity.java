package kr.me.ansr;

import kr.me.ansr.login.LoginActivity;
import kr.me.ansr.login.SplashActivity;
import kr.me.ansr.tab.board.BoardFragment;
import kr.me.ansr.tab.chat.GcmChatFragment;
import kr.me.ansr.tab.friends.FriendsFragment;
import kr.me.ansr.tab.meet.MeetFragment;
import kr.me.ansr.tab.mypage.MypageFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	TabHost tabHost;
	ViewPager pager;
	TabsAdapter mAdapter;
	TabWidget tabs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		tabs = (TabWidget)findViewById(android.R.id.tabs);
		tabHost = (TabHost)findViewById(android.R.id.tabhost);
		tabHost.setup();
		//git 내용 변경위해
		pager = (ViewPager)findViewById(R.id.pager);
		mAdapter = new TabsAdapter(this, getSupportFragmentManager(), tabHost, pager);
		mAdapter.addTab(tabHost.newTabSpec("tab1").setIndicator("TAB1"), FriendsFragment.class, null);
//		mAdapter.addTab(tabHost.newTabSpec("tab2").setIndicator("TAB2"), MainFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab2").setIndicator("TAB2"), GcmChatFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab3").setIndicator("TAB3"), BoardFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab4").setIndicator("TAB4"), MeetFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab5").setIndicator("TAB5"), MypageFragment.class, null);
		
		
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
			Toast.makeText(getApplicationContext(), "UsingLocation: "+PropertyManager.getInstance().getUsingLocation() +"\nlatitude: "+PropertyManager.getInstance().getLatitude()+"\nlongitude"+PropertyManager.getInstance().getLongitude(), Toast.LENGTH_LONG).show();
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
		Animation anim = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.show_anim);
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
