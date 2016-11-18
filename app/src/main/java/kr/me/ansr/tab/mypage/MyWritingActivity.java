package kr.me.ansr.tab.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import kr.me.ansr.*;
import kr.me.ansr.TabsAdapter;
import kr.me.ansr.common.event.ActivityResultEvent;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.tab.board.BoardWriteActivity;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.mypage.mywriting.tabone.OneFragment;
import kr.me.ansr.tab.mypage.mywriting.tabtwo.TwoFragment;

public class MyWritingActivity extends AppCompatActivity {

    private static final String TAG = MyWritingActivity.class.getSimpleName();
    TabHost tabHost;
    ViewPager pager;
    kr.me.ansr.TabsAdapter mAdapter;
    TabWidget tabs;
    private static final int PAGER_OFFSET_LIMIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_writing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        toolbar.setBackgroundResource(R.drawable.z_titlebar_mywrite);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        tabs = (TabWidget)findViewById(android.R.id.tabs);
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setOffscreenPageLimit(PAGER_OFFSET_LIMIT);
        mAdapter = new TabsAdapter(this, getSupportFragmentManager(), tabHost, pager);
//        ImageView iv1 = new ImageView(this); iv1.setImageResource(R.drawable.e_board_tab1_selector);
//        ImageView iv2 = new ImageView(this); iv2.setImageResource(R.drawable.e_board_tab2_selector);
        mAdapter.addTab(tabHost.newTabSpec("myTab1").setIndicator("게 시 글"), OneFragment.class, null);
        mAdapter.addTab(tabHost.newTabSpec("myTab2").setIndicator("댓 글"), TwoFragment.class, null);

        mAdapter.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
            }
        });
        mAdapter.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        if (savedInstanceState != null) {
            mAdapter.onRestoreInstanceState(savedInstanceState);
            String tag = savedInstanceState.getString("tabTagMyWriting");
            tabHost.setCurrentTabByTag(tag);
        }

        EventBus.getInstance().register(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
        String tag = tabHost.getCurrentTabTag();
        outState.putString("tabTagMyWriting", tag);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            finish();
//            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BoardInfo.BOARD_RC_NUM:
                if (resultCode == RESULT_OK) {
                    Bundle extraBundle = data.getExtras();
                    BoardResult result = (BoardResult)extraBundle.getSerializable(BoardInfo.BOARD_DETAIL_MODIFIED_ITEM);
                    if(result != null){
                        Log.e(TAG, "onActivityResult: "+ result.toString() );
                        EventBus.getInstance().post(result);
                    }
                }
                break;
            case BoardWriteActivity.BOARD_WRITE_RC_NEW:
                if (resultCode == RESULT_OK) {
                    Bundle extraBundle = data.getExtras();
                    String returnString = extraBundle.getString("return");
                    if(returnString.equals("success")){
                        Log.e("afterWrite", "success");
                        Toast.makeText(MyWritingActivity.this, "return key== "+returnString, Toast.LENGTH_LONG).show();
                        EventBus.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
                    } else {
                        Log.e("afterWrite", "failure");
                        Toast.makeText(MyWritingActivity.this, "return key== "+returnString, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case BoardWriteActivity.BOARD_WRITE_RC_EDIT:
                if (resultCode == RESULT_OK) {
                    Bundle extraBundle = data.getExtras();
                    String returnString = extraBundle.getString("return");
                    Toast.makeText(MyWritingActivity.this, "return key== "+returnString, Toast.LENGTH_LONG).show();
                    if(returnString.equals("success")){
                        Log.e("afterEdit", "success");
//                        EventBus.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
                        BoardResult result = (BoardResult)extraBundle.getSerializable("mItem");
                        if(result != null){
                            Log.e(TAG, "onActivityResult: "+ result.toString() );
                            EventBus.getInstance().post(result);
                        }
                    } else {
                        Log.e("afterEdit", "failure");
                        Toast.makeText(MyWritingActivity.this, "return key== "+returnString, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }


    }


    @Override
    public void onDestroy() {
        EventBus.getInstance().unregister(this);
        super.onDestroy();
    }

}
