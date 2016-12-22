package kr.me.ansr.tab.friends.list;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.InputDialogFragment;
import kr.me.ansr.common.ReportDialogFragment;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.common.event.FriendsFragmentResultEvent;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.detail.StatusInfo;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.MyDecoration;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;
import kr.me.ansr.tab.friends.tabtwo.MyFriendsAdapter;
import okhttp3.Request;

public class FriendsListActivity extends AppCompatActivity {
    private static final String TAG = FriendsListActivity.class.getSimpleName();
    public static final int FRIENDS_RC_NUM = 202;
    TextView toolbarTitle;
    int userId;

    RecyclerView recyclerView;
    FriendsListAdapter mAdapter;
    //    RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());

    public int mSearchOption = 0;
    FriendsResult selectedItem = null;

    TextView sameCnt;
    FriendsResult mItem;

    RelativeLayout emptyLayout;
    ImageView emptyIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title); toolbarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.c_artboard_final_list_title));

        emptyLayout = (RelativeLayout)findViewById(R.id.rl_empty);
        emptyIcon = (ImageView) findViewById(R.id.iv_empty_img);
        emptyIcon.setImageResource(R.drawable.z_empty_friends_3);
        Intent intent = getIntent();
        if (intent != null) {
            mItem = (FriendsResult) intent.getSerializableExtra("mItem");
            userId = mItem.userId;
            Log.e("targetId :", ""+userId);
        }

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        start = 0;
//                        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
//                        initUnivUsers();
                        if(!dialog.isShowing()){
                            refreshLayout.setRefreshing(false);
                        }
                    }
                }, 2000);
            }
        });   //this로 하려면 implements 하고 오버라이드 코드 작성하면 됨.
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
//        recyclerView.addOnItemTouchListener(new MyFriendsAdapter.RecyclerTouchListener(FriendsListActivity.this, recyclerView, new MyFriendsAdapter.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                FriendsResult data = mAdapter.getItem(position);
//                switch (view.getId()) {
//                    case 100:
//                    default:
//                        Toast.makeText(FriendsListActivity.this, "data Click: " + data.toString(), Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//                FriendsResult data = mAdapter.getItem(position);
//                Toast.makeText(FriendsListActivity.this, "data Long Click\n: " + data.toString(), Toast.LENGTH_SHORT).show();
//            }
//        }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    getMoreItem();
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
        mAdapter = new FriendsListAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                selectedItem = data;    //디테일에서 관리 누를 경우사용될 변수
                Intent intent = new Intent(FriendsListActivity.this, FriendsDetailActivity.class);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, position);
                intent.putExtra("tag", InputDialogFragment.TAG_FRIENDS_DETAIL);
                startActivityForResult(intent, FriendsListActivity.FRIENDS_RC_NUM); //tabHost가 있는 FriendsFragment에서 리절트를 받음
            }
        });
        mAdapter.setOnAdapterItemClickListener(new FriendsListAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(FriendsListAdapter adapter, View view, int position, FriendsResult item, int type) {
                switch (type) {
                    case 100:   //name
                    case 200:   //image
                        default:
                            FriendsResult data = mAdapter.getItem(position);
                            selectedItem = data;    //디테일에서 관리 누를 경우사용될 변수
                            Intent intent = new Intent(FriendsListActivity.this, FriendsDetailActivity.class);
                            intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
                            intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
                            intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, position);
                            intent.putExtra("tag", InputDialogFragment.TAG_FRIENDS_DETAIL);
                            startActivityForResult(intent, FriendsListActivity.FRIENDS_RC_NUM); //tabHost가 있는 FriendsFragment에서 리절트를 받음
                            break;
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                selectedItem = data;
//                ReportDialogFragment mDialogFragment = ReportDialogFragment.newInstance();
//                Bundle b = new Bundle();
////                b.putString("tag", ReportDialogFragment.TAG_BOARD_WRITE);
//                b.putSerializable("userInfo", data);
//                b.putString("tag", ReportDialogFragment.TAG_TAB_ONE_UNIV);
//                mDialogFragment.setArguments(b);
//                mDialogFragment.show(getSupportFragmentManager(), "reportDialog");
            }
        });

        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(FriendsListActivity.this);
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(FriendsListActivity.this));


        //FriendListActivity init datas.
        sameCnt = (TextView)findViewById(R.id.text_same_cnt);

        start = 0;
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        initUnivUsers();
        EventBus.getInstance().register(this);
    }

    private void initUnivUsers(){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        if(mUnivId == ""){
            Toast.makeText(FriendsListActivity.this,"대학교 등록할 것", Toast.LENGTH_SHORT).show();
//            return;
            mUnivId = ""+ (0); //테스트 위해 임시로 (회원가입안하고 테스트 해보기 위해)
        }
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        NetworkManager.getInstance().postDongneUnivUsers(FriendsListActivity.this,
                1, //mode
                ""+userId,
                mSearchOption,
                mUnivId,
                ""+start,
                ""+DISPLAY_NUM,
                ""+reqDate,
                new NetworkManager.OnResultListener<FriendsInfo>() {
                    @Override
                    public void onSuccess(Request request, FriendsInfo result) {
                        if (result.error.equals(false)) {
                            if(result.result != null && !result.message.equals("has no more accepted friends")){
                                mAdapter.blockCount = 0;    //1인애들만 불러오니까 블락카운트가 증가 안하겟지
                                mAdapter.items.clear();
                                mAdapter.setTotalCount(result.total);
                                ArrayList<FriendsResult> items = result.result;

                                if(result.user != null){
                                    if(mItem !=null){
                                        mAdapter.put("친구 프로필", mItem);
                                    } else {
                                        result.user.status = 1;
                                        mAdapter.put("친구 프로필", result.user);
                                    }
                                }
                                for(int i=0; i < items.size(); i++){
                                    FriendsResult child = items.get(i);
                                    if(child.status == 3){
                                        mAdapter.blockCount++;
                                        continue;
                                    }
                                    mAdapter.put("학교 사람들", child);
                                }
                                if(start == 0){
                                    sameCnt.setText(""+result.sameCnt);
                                }
                                start++;
                            }
                        } else {
                            mAdapter.items.clear();
                            sameCnt.setText("0");
                            Log.e(TAG, result.message);
                            Toast.makeText(FriendsListActivity.this, TAG + " " + result.message, Toast.LENGTH_SHORT).show();
                        }
                        showLayout();
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(FriendsListActivity.this, getString(R.string.res_err_msg), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: " + cause );
                        showLayout();
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(FriendsListActivity.this);
        dialog.setTitle("Loading....");
        dialog.show();
    }

    boolean isMoreData = false;
    ProgressDialog dialog = null;
    private static final int DISPLAY_NUM = FriendsInfo.FRIEND_DISPLAY_NUM;
    private int start=0;
    private String reqDate = null;

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getMoreItem() {
        if (isMoreData) return;
        isMoreData = true;
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > mAdapter.getItemCount()) {
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount()) {
            //기존 코드에서 +DIS_NUM 한 것은 마지막 디스플레이가 리프레쉬 안되서 디스플레이 수만큼 +시킴.
//            int start = mAdapter.getItemCount() + 1;
//            int display = 50;
            NetworkManager.getInstance().postDongneUnivUsers(FriendsListActivity.this,
                    1, //mode
                    ""+userId,
                    mSearchOption,
                    PropertyManager.getInstance().getUnivId(),
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    new NetworkManager.OnResultListener<FriendsInfo>() {
                        @Override
                        public void onSuccess(Request request, FriendsInfo result) {
                            if(!result.message.equals("has no more accepted friends")){
                                mAdapter.addAllFriends(result.result);
                                start++;
//                                FriendsDataManager.getInstance().getList().addAll(result.result);
                            } else {
                                Toast.makeText(FriendsListActivity.this, result.message, Toast.LENGTH_SHORT).show();
                            }
                            isMoreData = false;
                            refreshLayout.setRefreshing(false);
                        }
                        @Override
                        public void onFailure(Request request, int code, Throwable cause) {
                            Toast.makeText(FriendsListActivity.this, getString(R.string.res_err_msg), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onFailure: " + cause );
                            isMoreData =false;
                            refreshLayout.setRefreshing(false);
                        }
                    });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishAndReturnData(boolean result){
        Intent intent = new Intent();
        if(result){
            intent.putExtra("return", "success");
        } else {
            intent.putExtra("return", "false");
        }
        this.setResult(RESULT_OK, intent);//RESULT_OK를 BoardFragment의 온리절트에서 받음
        finish();
    }
    private void forcedFinish(){
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /*
	To get EventBus's posting events. children are inherited overriding functions
	*/

    //onCreate 마지막에 register()함
    @Override
    public void onDestroy() {
        EventBus.getInstance().unregister(this);
        super.onDestroy();
    }
    @Subscribe
    public void onEvent(FriendsResult fr){
        Log.e("onEvent:", "FriendsListActivity fr");
//        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
        //번갈아 계속 호출되는 버그
        findOneAndModify(fr);
    }
    private void findOneAndModify(FriendsResult fr){
        if(mAdapter == null || mAdapter.getItemCount() < 1) return;
        mAdapter.findOneAndModify(fr);
        switch (fr.status){
            case -1:
                break;
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                mAdapter.removeItem(fr);
                mAdapter.blockCount++;
                break;
            default:break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extraBundle;
        switch (requestCode) {
            case FriendsListActivity.FRIENDS_RC_NUM:
                if (resultCode == RESULT_OK) {
                    extraBundle = data.getExtras();
                    FriendsResult result = (FriendsResult)extraBundle.getSerializable(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM);
//                    EventBus.getInstance().post(new FriendsFragmentResultEvent(requestCode, resultCode, data));
                    EventBus.getInstance().post(result);
                    break;
                }
        }
    }

    private void showLayout(){
        if (mAdapter.getItemCount() > 0){
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

}
