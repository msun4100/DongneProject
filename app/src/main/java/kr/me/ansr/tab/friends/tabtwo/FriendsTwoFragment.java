package kr.me.ansr.tab.friends.tabtwo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.InputDialogFragment;
import kr.me.ansr.common.ReportDialogFragment;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.common.event.FriendsFragmentResultEvent;
import kr.me.ansr.tab.friends.FriendsFragment;
import kr.me.ansr.tab.friends.MySpinnerAdapter;
import kr.me.ansr.tab.friends.PagerFragment;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.detail.StatusInfo;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import kr.me.ansr.tab.friends.recycler.MyDecoration;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;
import kr.me.ansr.tab.friends.search.FriendsSearchDialogFragment;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-25.
 */
public class FriendsTwoFragment extends PagerFragment {

    private static final String TAG = FriendsTwoFragment.class.getSimpleName();
    public static final int FRIENDS_RC_NUM = 299;
    public static final int DIALOG_RC_NUM = 300;
    public static final int DIALOG_RC_CUT_OFF = 301;
    public static final int DIALOG_RC_BLOCK = 302;
    public static final int DIALOG_RC_REPORT = 303;
    public static final int DIALOG_RC_SEND_REQ = 304;
    public static final int DIALOG_RC_SEARCH = 305;
    AppCompatActivity activity;

    RecyclerView recyclerView;
    public static MyFriendsAdapter mAdapter;
    //    RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());


    Spinner spinner;
    MySpinnerAdapter mySpinnerAdapter;
    public int mSearchOption = 0;
    ImageView searchIcon;

    InputMethodManager imm;
    public FloatingActionButton fab;

    RelativeLayout emptyLayout;
    ImageView emptyIcon;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_section, container, false);
        activity = (AppCompatActivity) getActivity();
        emptyLayout = (RelativeLayout)view.findViewById(R.id.rl_empty);
        emptyIcon = (ImageView)view.findViewById(R.id.iv_empty_img);
        emptyIcon.setImageResource(R.drawable.z_empty_friends_2);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProcess();
            }
        });
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start = 0;
                        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                        initUnivUsers();
                    }
                }, 1000);
            }
        });   //this로 하려면 implements 하고 오버라이드 코드 작성하면 됨.
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
//        recyclerView.addOnItemTouchListener(new MyFriendsAdapter.RecyclerTouchListener(getActivity(), recyclerView, new MyFriendsAdapter.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                // when chat is clicked, launch full chat thread activity
//                FriendsResult data = mAdapter.getItem(position);
//                switch (view.getId()) {
//                    case 100:
//                        Toast.makeText(getActivity(), "nameView click" + data.toString(), Toast.LENGTH_SHORT).show();
//                        break;
//                    case 200:
//                        Toast.makeText(getActivity(), "imageView click" + data.toString(), Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        Toast.makeText(getActivity(), "data Click: " + data.toString(), Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//                FriendsResult data = mAdapter.getItem(position);
//                Toast.makeText(getActivity(), "data Long Click\n: " + data.toString(), Toast.LENGTH_SHORT).show();
//            }
//        }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(isSearching == true){
                        getMoreSearchUsers(username, enterYear, deptname, jobname, jobteam);
                    } else {
                        getMoreItem();
                    }
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
                if (dy > 0) { // Scroll Down
                    if (fab.isShown()) fab.hide();
                } else if (dy < 0) { // Scroll Up
                    if (!fab.isShown()) fab.show();
                }
            }
        });
        mAdapter = new MyFriendsAdapter();
        mAdapter.setOnAdapterItemClickListener(new MyFriendsAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(MyFriendsAdapter adapter, View view, int position, FriendsResult item, int type) {
                switch (type) {
                    case 100:   //nameView
                    case 200:   //imageView
                        default:
                            showDetail(position);
                            break;

                }
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDetail(position);
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                selectedItem = data;
                if(PropertyManager.getInstance().getUserId().equals(""+data.userId)) return;
                ReportDialogFragment mDialogFragment = ReportDialogFragment.newInstance();
                Bundle b = new Bundle();
//                b.putString("tag", ReportDialogFragment.TAG_BOARD_WRITE);
                b.putSerializable("userInfo", data);
                b.putString("tag", ReportDialogFragment.TAG_TAB_TWO_UNIV);
                mDialogFragment.setArguments(b);
                mDialogFragment.setTargetFragment(FriendsTwoFragment.this, DIALOG_RC_NUM);
                mDialogFragment.show(getActivity().getSupportFragmentManager(), "reportDialog");
            }
        });

        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(getActivity()));
//        initUnivUsers(); //onResume에서 호출

        start = 0;
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
//        initUnivUsers();

        //        implements common_search_bar_spinner======================================
//        frameSearch = (FrameLayout)view.findViewById(R.id.containerforsearch);
//        frameSearch.setVisibility(View.GONE);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        mySpinnerAdapter = new MySpinnerAdapter();
        spinner.setAdapter(mySpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				String str = (String)mySpinnerAdapter.getItem(position);
                Log.e("clicked position: ", ""+position);
                /*
                onCreateView에서 초기화할때 최초 호출하는 initUnivUsers()도 여기서 처리
                onItemSelected == 0 으로 세팅 되기 때문에
                */
                mSearchOption = position;
                start = 0;
//                if(mSearchOption != 0){
                if(true){
                    //핸들러로 몇초 있다가 요청
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initUnivUsers();
                        }
                    }, 500);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
            }
        });
        initData(); //spinnerItem init
        searchIcon = (ImageView)view.findViewById(R.id.image_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                searchProcess();
            }
        });


        //		on 1118
        Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName("TAB1_ChildTwoFragment");
        t.send(new HitBuilders.AppViewBuilder().build());

        return view;
    }

    private void searchProcess(){
        Log.e(TAG, "onClick: "+mSearchOption );
        if(mSearchOption == 3){
            spinner.setSelection(0);    // ==서치옵션도 0 이 됨
        }
        if(isSearching == true){
            isSearching = false;
            searchIcon.setImageResource(R.drawable.b_list_search_icon_selector);
            fab.setImageResource(R.drawable.z_fab_search);
//            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            start = 0;
            reqDate = MyApplication.getInstance().getCurrentTimeStampString();
            initUnivUsers();
//            restoreList();
        } else if(isSearching == false){
            //프래그먼트 호출
            showSearchFragment();
        }
    }

    public boolean isSearching = false;
    public String headerString = null;
    String username = "", enterYear = "", deptname = "", jobname = "", jobteam = "";

    public void doSearch(){
        if(username.equals("") && enterYear.equals("") && deptname.equals("") && jobname.equals("") && jobteam.equals("")){
            Toast.makeText(getActivity(), "검색 조건을 하나 이상 입력해주세요", Toast.LENGTH_SHORT).show();
            isSearching = false;
            return;
        }
        headerString = "";
        if(!TextUtils.isEmpty(username)){
            headerString += "이름:" + username +", ";
        }
        if(!TextUtils.isEmpty(enterYear)){
            headerString += "학번:" + enterYear +", ";
        }
        if(!TextUtils.isEmpty(deptname)){
            headerString += "학과:" + deptname +", ";
        }
        if(!TextUtils.isEmpty(jobname)){
            headerString += "회사:" + jobname +", ";
        }
        if(!TextUtils.isEmpty(jobteam)){
            headerString += "직무:" + jobname +", ";
        }
        if(!TextUtils.isEmpty(headerString)){
            headerString = headerString.substring(0, headerString.length() - 2);
        }

        Log.e("s_username", username);
        Log.e("s_enterYear", enterYear);
        Log.e("s_deptname", deptname);
        Log.e("s_jobname", jobname);
        Log.e("s_jobteam", jobteam);

//        storeList();    //기존 데이타 백업
        mSearchOption = spinner.getSelectedItemPosition();
        start = 0;
        if(true){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
                    t.send(new HitBuilders.EventBuilder().setCategory("TAB1_ChildTwoFragment").setAction("Press Button").setLabel("Search view Click").build());
                    isSearching = true;
                    searchIcon.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
                    fab.setImageResource(R.drawable.z_fab_close);
                    initSearchUsers(username, enterYear, deptname, jobname, jobteam);
                }
            }, 500);
        }
    }

    private void initData() {
        String[] searchArray = getResources().getStringArray(R.array.search_items);
        for (int i = 0; i < searchArray.length; i++) {
            mySpinnerAdapter.add(searchArray[i]);
        }
        spinner.setSelection(0);
    }
    private void initUnivUsers(){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        if(mUnivId == ""){
            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
//            return;
            mUnivId = ""+ (0); //테스트 위해 임시로 (회원가입안하고 테스트 해보기 위해)
        }
//        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        NetworkManager.getInstance().postDongneUnivUsers(getActivity(),
                1, //mode
                null,
                mSearchOption,
                mUnivId,
                ""+start,
                ""+DISPLAY_NUM,
                ""+reqDate,
                new NetworkManager.OnResultListener<FriendsInfo>() {
                    @Override
                    public void onSuccess(Request request, FriendsInfo result) {
                        if (result.error.equals(false)) {
                            if(result.result != null && !result.message.equals("has no more accepted friends") ){
                                mAdapter.items.clear();
                                mAdapter.blockCount = 0;
                                mAdapter.setTotalCount(result.total);
                                setTabTotalCount(result.total);
                                ArrayList<FriendsResult> items = result.result;
                                if(result.user != null){
                                    result.user.status = 1;
                                    mAdapter.put("내 프로필", result.user);
                                }

                                for(int i=0; i < items.size(); i++){
                                    FriendsResult child = items.get(i);
                                    Log.e(TAG, ""+child);
                                    if(i==0 && mAdapter.getItem(1) == null){ //임시코드
                                        mAdapter.put("내 프로필", child); //내 정보 불러와서
                                    }
                                    mAdapter.put("학교 사람들", child);
                                }
                                start++;
                            }
                        } else {
                            mAdapter.items.clear();
                            Log.e(TAG, result.message);
                            Toast.makeText(getActivity(), TAG + "result.error: true\nresult.message:" + result.message, Toast.LENGTH_SHORT).show();
                        }
                        showLayout();
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        showLayout();
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Friends list Loading...");
        dialog.show();
    }

    boolean isMoreData = false;
    private static final int DISPLAY_NUM = FriendsInfo.FRIEND_DISPLAY_NUM;
    private int start=0;
    private String reqDate = null;
    private String searchReqDate = null;

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );

    }

    private void getMoreItem() {
        if (isMoreData) return;
        isMoreData = true;
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > mAdapter.getItemCount()) {
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount() + mAdapter.blockCount) {
            //기존 코드에서 +DIS_NUM 한 것은 마지막 디스플레이가 리프레쉬 안되서 디스플레이 수만큼 +시킴.
//            int start = mAdapter.getItemCount() + 1;
//            int display = 50;
            NetworkManager.getInstance().postDongneUnivUsers(getActivity(),
                    1, //mode
                    null,
                    mSearchOption,
                    PropertyManager.getInstance().getUnivId(),
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    new NetworkManager.OnResultListener<FriendsInfo>() {
                        @Override
                        public void onSuccess(Request request, FriendsInfo result) {
                            Log.e(TAG+"getMore:", ""+result.message);
                            if(result.error.equals(false)){
                                if(!result.message.equals("has no more accepted friends")){
                                    Log.e(TAG+"getMore:", result.result.toString());
                                    mAdapter.addAllFriends(result.result);
                                    start++;
                                } else {
                                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            }

                            isMoreData = false;
                            dialog.dismiss();
                            refreshLayout.setRefreshing(false);
                            Log.e(TAG+"getMoreItem() start=", ""+start);
                        }
                        @Override
                        public void onFailure(Request request, int code, Throwable cause) {
                            isMoreData =false;
                            dialog.dismiss();
                            refreshLayout.setRefreshing(false);
                        }
                    });
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Friends list Loading...");
            dialog.show();
        }
    }

    private void setTabTotalCount(int total){
        if(total > 0 && FriendsFragment.totalFriends != null){
            FriendsFragment.totalFriends.setText(""+total);
        } else {
            FriendsFragment.totalFriends.setText("");
        }
    }

    private void initSearchUsers(String username, String enterYear, String deptname, String jobname, String jobteam){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        searchReqDate = MyApplication.getInstance().getCurrentTimeStampString();
        start = 0;
        NetworkManager.getInstance().postDongneUnivUsersSearch(getActivity(),
                1,//mode
                mSearchOption,//req.body.sort
                mUnivId,
                ""+start,
                ""+DISPLAY_NUM,
                ""+searchReqDate,
                username,
                enterYear,
                deptname,
                jobname,
                jobteam,
                new NetworkManager.OnResultListener<FriendsInfo>() {
                    @Override
                    public void onSuccess(Request request, FriendsInfo result) {
                        if (result.error.equals(false)) {
                            if(!result.message.equals("has no more friends")){
                                mAdapter.blockCount = 0;
                                mAdapter.items.clear();
                                mAdapter.setTotalCount(result.total);
                                Log.d(TAG, "onSuccess: total"+result.total);
                                ArrayList<FriendsResult> items = result.result;
                                if(result.user != null){
                                    Log.e(TAG+" user:", result.user.toString());
                                    result.user.status = 1;
                                    mAdapter.put("내 프로필", result.user);
                                }
                                for(int i=0; i < items.size(); i++){
                                    FriendsResult child = items.get(i);
                                    Log.e(TAG, ""+child);
                                    if(child.status == 3){
                                        mAdapter.blockCount++;
                                        continue;
                                    }
//                                    mAdapter.put("검색 결과", child);
                                    mAdapter.put(headerString, child);
                                }
                                start++;
                            } else {    //has no more friends
                                mAdapter.blockCount = 0;
                                mAdapter.items.clear();
                                mAdapter.setTotalCount(0);
                                if(result.user != null){
                                    Log.e(TAG+" user:", result.user.toString());
                                    result.user.status = 1;
                                    mAdapter.put("내 프로필", result.user);
                                }
                                Toast.makeText(getActivity(), result.message, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            mAdapter.items.clear();
                            Log.e(TAG, result.message);
                            Toast.makeText(getActivity(), TAG + "result.error: true\nresult.message:" + result.message, Toast.LENGTH_SHORT).show();
                        }
                        showLayout();
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        showLayout();
                        refreshLayout.setRefreshing(false);
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading....");
        dialog.show();
    }

    private void getMoreSearchUsers(String username, String enterYear, String deptname, String jobname, String jobteam){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        if (isMoreData) return;
        isMoreData = true;

        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount() + mAdapter.blockCount ) {
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > (mAdapter.getItemCount() + mAdapter.blockCount) ) {
            NetworkManager.getInstance().postDongneUnivUsersSearch(getActivity(),
                    1,//mode
                    mSearchOption,//req.body.sort
                    mUnivId,
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+searchReqDate,
                    username,
                    enterYear,
                    deptname,
                    jobname,
                    jobteam,
                    new NetworkManager.OnResultListener<FriendsInfo>() {
                        @Override
                        public void onSuccess(Request request, FriendsInfo result) {
                            if (result.error.equals(false)) {
                                Log.e(TAG, "onSuccess: " + result.message);
                                if(!result.message.equals("has no more friends")){
                                    ArrayList<FriendsResult> items = result.result;
                                    for(int i=0; i < items.size(); i++){
                                        FriendsResult child = items.get(i);
                                        Log.e(TAG, ""+child);
                                        if(child.status == 3){
                                            mAdapter.blockCount++;
                                            continue;
                                        }
                                        mAdapter.put(headerString, child);
                                    }
                                    start++;
                                } else {
                                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e(TAG, "onSuccess: "+result.message);
                                Toast.makeText(getActivity(), TAG + "result.error: true\nresult.message:" + result.message, Toast.LENGTH_SHORT).show();
                            }
                            Log.e(TAG+"getMoreItem() start=", ""+start);
                            dialog.dismiss();
                            isMoreData = false;
                            refreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Request request, int code, Throwable cause) {
                            dialog.dismiss();
                            isMoreData =false;
                            refreshLayout.setRefreshing(false);
                        }
                    });
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("get more items...");
            dialog.show();
        }
    }


    private void findOneAndModify(FriendsResult fr){
        if(mAdapter == null || mAdapter.getItemCount() < 1) return;
        mAdapter.findOneAndModify(fr);
        int num;
        switch (fr.status){
            case -1:
                mAdapter.removeItem(fr);
//                mAdapter.setTotalCount(mAdapter.getTotalCount()-1);
                num = Integer.parseInt(FriendsFragment.totalFriends.getText().toString());
                setTabTotalCount(--num);
                break;
            case 0:
                break;
            case 1:
                break;
            case 2:
            case 3:
                mAdapter.removeItem(fr);
//                mAdapter.setTotalCount(mAdapter.getTotalCount()-1);
                mAdapter.blockCount++;
                num = Integer.parseInt(FriendsFragment.totalFriends.getText().toString());
                setTabTotalCount(--num);
                break;
            default:break;
        }
    }

    ProgressDialog dialog = null;
    FriendsResult selectedItem = null;
    public void updateStatus(final int status, int to, String msg){
        if(selectedItem == null){
            Log.e("selectedItem is null","");
            return;
        }
        Log.e("selectedItem ", selectedItem.toString());
        final FriendsResult mItem = selectedItem;
        //세번째 파라미터 mItem은 요청 성공시 아답터에서 삭제하기 위해 remove(object) 호출 용
        NetworkManager.getInstance().postDongneFriendsUpdate(MyApplication.getContext(),
                status, //3 report -> CustomDialog 를 통해 온 요청이면 status == 3, ReportFormDialog에서 온 요청이면 status = 2, msg=reportType
                to, //to == 선택된 아이템의 userId
                msg, //블락하는데 메세지는 상관없음.
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        if (result.error.equals(false)) {
                            Toast.makeText(MyApplication.getContext(), ""+result.message, Toast.LENGTH_LONG).show();
                            mItem.status = status;
                            EventBus.getInstance().post(mItem); //수정된 체로 보냄
                        } else {
                            Toast.makeText(MyApplication.getContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(MyApplication.getContext(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("서버 요청 중..");
        dialog.show();
    }

    public void removeStatus(int userId, final FriendsResult mItem){
        //파라미터 mItem은 요청 성공시 아답터에서 삭제하기 위해 remove(object) 호출 용
        NetworkManager.getInstance().postDongneFriendsRemove(MyApplication.getContext(),
                userId,
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        if (result.error.equals(false)) {
                            mItem.status = -1;
                            EventBus.getInstance().post(mItem); //수정된 체로 보냄
                        } else {
                            Toast.makeText(MyApplication.getContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(MyApplication.getContext(), "onFailure\n"+cause, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("서버 요청 중..");
        dialog.show();
    }

    public void reportUser(int type){
        if(selectedItem == null){
            return;
        }
        final int to = selectedItem.userId;
        String msg = "friends";
        final FriendsResult mItem = selectedItem;
        //세번째 파라미터 mItem은 요청 성공시 아답터에서 삭제하기 위해 remove(object) 호출 용
        NetworkManager.getInstance().postDongneReportUpdate(MyApplication.getContext(),
                type, //reportType
                to, //to == 선택된 아이템의 userId
                msg, //블락하는데 메세지는 상관없음.
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        if (result.error.equals(false)) {
                            Toast.makeText(MyApplication.getContext(), ""+result.message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            updateStatus(3, to, "reported");    //신고처리 성공시 차단친구로 변경
                        } else {
                            Toast.makeText(MyApplication.getContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(MyApplication.getContext(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("서버 요청 중..");
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extraBundle;
        switch (requestCode) {
            case FriendsSectionFragment.FRIENDS_RC_NUM: //디테일에서 리턴받은 값
                if (resultCode == getActivity().RESULT_OK) {
//                    extraBundle = data.getExtras();
//                    int position = extraBundle.getInt(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, -1);
//                    FriendsResult result = (FriendsResult)extraBundle.getSerializable(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM);
//                    Toast.makeText(getActivity(),""+ result.toString() , Toast.LENGTH_SHORT).show();
//                    ModifiedSetItem(position, result);
//                    findOneAndModify(result);
                    break;
                }
            case DIALOG_RC_BLOCK:
                if (resultCode == getActivity().RESULT_OK) {
                    Log.e("DIALOG_RC_BLOCK","aaaaaaaa");
                    extraBundle = data.getExtras();
                    FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
                    updateStatus(StatusInfo.STATUS_BLOCKED, result.userId, "blocked");
                }
                break;
            case DIALOG_RC_CUT_OFF:
                if (resultCode == getActivity().RESULT_OK) {
                    Log.e("DIALOG_RC_CUT_OFF","aaaaaaaa");
                    extraBundle = data.getExtras();
                    FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
                    removeStatus(result.userId, result);
                }
                break;
            case DIALOG_RC_REPORT:
                if (resultCode == getActivity().RESULT_OK) {
                    Log.e("DIALOG_RC_REPORT","aaaaaaaa");
                    extraBundle = data.getExtras();
                    int reportType = extraBundle.getInt("type", -1);
                    if(reportType != -1){
                        reportUser(reportType);
                    }
                }
                break;
            case DIALOG_RC_SEND_REQ:
                if (resultCode == getActivity().RESULT_OK) {
                    Log.e("DIALOG_RC_SEND_REQ","aaaaaaaa");
                    extraBundle = data.getExtras();
                    String msg = extraBundle.getString("msg");
                    FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
                    if(msg != null && result != null){
                        updateStatus(StatusInfo.STATUS_PENDING, result.userId, msg);
                    }
                }
                break;
            case DIALOG_RC_SEARCH:
                if (resultCode == getActivity().RESULT_OK) {
                    extraBundle = data.getExtras();
                    username = extraBundle.getString("username");
                    enterYear = extraBundle.getString("enterYear");
                    deptname = extraBundle.getString("deptname");
                    jobname = extraBundle.getString("jobname");
                    jobteam = extraBundle.getString("jobteam");
                    doSearch();
                }
                break;
        }
    }

    private void restoreList(){
        if(FriendsTwoDataManager.getInstance().items.size() > 0){
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Loading data...");
            dialog.show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    start = FriendsTwoDataManager.getInstance().getStart();
                    reqDate = FriendsTwoDataManager.getInstance().getReqDate();
                    mSearchOption = FriendsTwoDataManager.getInstance().getmSearchOption();
                    spinner.setSelection(mSearchOption);
                    mAdapter.clearAll();
                    mAdapter.setTotalCount(FriendsTwoDataManager.getInstance().getTotalCount());
                    mAdapter.addAll(FriendsTwoDataManager.getInstance().getList());
                    int lastVisibleItemPosition = FriendsTwoDataManager.getInstance().getLastVisibleItemPosition();
                    recyclerView.scrollToPosition(lastVisibleItemPosition);
                    FriendsTwoDataManager.getInstance().clearAll();
                    if(dialog.isShowing() == true){
                        dialog.dismiss();
                    }
                }
            }, 100);
        } else {
            reqDate = MyApplication.getInstance().getCurrentTimeStampString();
            start = 0;
            initUnivUsers();
        }
    }

    private void storeList() {
        //기존 데이타 백업
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FriendsTwoDataManager.getInstance().setmSearchOption(mSearchOption);
                //+3해주는건 검색필터 입력창이 열릴때 lastVisiblePosition이 3~4정도 줄어듬
                FriendsTwoDataManager.getInstance().setLastVisibleItemPosition(layoutManager.findLastCompletelyVisibleItemPosition() + 3);
                FriendsTwoDataManager.getInstance().setTotalCount(mAdapter.getTotalCount());
                FriendsTwoDataManager.getInstance().setStart(start);
                FriendsTwoDataManager.getInstance().setReqDate(reqDate);
                FriendsTwoDataManager.getInstance().clearAll();
                FriendsTwoDataManager.getInstance().addAll(mAdapter.getGroupItems());
            }
        }, 100);

    }

    private void showDetail(int position){
        FriendsResult data = mAdapter.getItem(position);
        selectedItem = data;    //디테일에서 관리 누를 경우사용될 변수
        Log.e("FriendsTwoFragment", data.toString());
        Intent intent = new Intent(getActivity(), FriendsDetailActivity.class);
        intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
        intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
        intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, position);
        intent.putExtra("tag", InputDialogFragment.TAG_FRIENDS_DETAIL);
        getParentFragment().startActivityForResult(intent, FRIENDS_RC_NUM); //tabHost가 있는 FriendsFragment에서 리절트를 받음
    }

    private void showSearchFragment(){
        FriendsSearchDialogFragment mDialogFragment = FriendsSearchDialogFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("tag", FriendsSearchDialogFragment.TAG_TAB_ONE_MY);
        mDialogFragment.setArguments(b);
        mDialogFragment.setTargetFragment(FriendsTwoFragment.this, DIALOG_RC_SEARCH);
        mDialogFragment.show(getActivity().getSupportFragmentManager(), "friendsSearchDialog");
    }

    private void showLayout(){
        if (mAdapter != null && mAdapter.getItemCount() > 2){
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    public FriendsTwoFragment(){}
    @Override
    public void onPageCurrent() {
        super.onPageCurrent();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "setUserVisibleHint: " );
    }
    @Subscribe
    public void onEvent(FriendsFragmentResultEvent activityResultEvent){
        Log.e("onEvent:", "FriendsTwoFragment event");
//        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
    }
    @Subscribe
    public void onEvent(FriendsResult fr){
        Log.e("onEvent:", "FriendsTwoFragment fr");
        findOneAndModify(fr);
    }

}
