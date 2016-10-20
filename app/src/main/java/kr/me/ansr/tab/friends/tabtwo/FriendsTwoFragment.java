package kr.me.ansr.tab.friends.tabtwo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.event.FriendsFragmentResultEvent;
import kr.me.ansr.tab.friends.FriendsFragment;
import kr.me.ansr.tab.friends.MySpinnerAdapter;
import kr.me.ansr.tab.friends.PagerFragment;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.FriendsDataManager;
import kr.me.ansr.tab.friends.recycler.MyDecoration;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-25.
 */
public class FriendsTwoFragment extends PagerFragment {

    private static final String TAG = FriendsTwoFragment.class.getSimpleName();
    public static final int FRIENDS_RC_NUM = 201;
    AppCompatActivity activity;

    RecyclerView recyclerView;
    MyFriendsAdapter mAdapter;
    //    RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());


    Spinner spinner;
    MySpinnerAdapter mySpinnerAdapter;
    String mSpinnerItem;
    public int mSearchOption = 0;
    ImageView searchIcon;
    FrameLayout frameSearch;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_section, container, false);
        activity = (AppCompatActivity) getActivity();
//        getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(!dialog.isShowing()){
//                            refreshLayout.setRefreshing(false);
//                        }
//                    }
//                }, 2000);
            }
        });   //this로 하려면 implements 하고 오버라이드 코드 작성하면 됨.
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
        recyclerView.addOnItemTouchListener(new MyFriendsAdapter.RecyclerTouchListener(getActivity(), recyclerView, new MyFriendsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                FriendsResult data = mAdapter.getItem(position);
                switch (view.getId()) {
                    case 100:
                        Toast.makeText(getActivity(), "nameView click" + data.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(getActivity(), "imageView click" + data.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getActivity(), "data Click: " + data.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                Toast.makeText(getActivity(), "data Long Click\n: " + data.toString(), Toast.LENGTH_SHORT).show();
            }
        }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e(TAG+"onstatechanged","aaaaaaaa");
                if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    getMoreItem();
                }

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mAdapter.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
//                Log.e("111lastVisiblePosition",""+lastVisibleItemPosition);
//                Log.e("222 != NO_POSITION",""+(lastVisibleItemPosition != RecyclerView.NO_POSITION));
//                Log.e("333 itemCount<lastPos",""+(totalItemCount - 1 <= lastVisibleItemPosition));
                if (totalItemCount > 0 && lastVisibleItemPosition != RecyclerView.NO_POSITION && (totalItemCount - 1 <= lastVisibleItemPosition)) {
                    isLast = true;
                } else {
                    isLast = false;
                }
//                Log.e("444 isLast:",""+isLast);
            }
        });
        mAdapter = new MyFriendsAdapter();
        mAdapter.setOnAdapterItemClickListener(new MyFriendsAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(MyFriendsAdapter adapter, View view, FriendsResult item, int type) {
                switch (type) {
                    case 100:
                        Toast.makeText(getActivity(), "nameView click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(getActivity(), "imageView click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
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
        reqDate = getCurrentTimeStamp();
//        initUnivUsers();

        //        implements common_search_bar_spinner======================================
        frameSearch = (FrameLayout)view.findViewById(R.id.containerforsearch);
        frameSearch.setVisibility(View.GONE);
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
                setContainerVisibility();
            }
        });

        inputName = (EditText)view.findViewById(R.id.edit_search_name);
        inputYear = (EditText)view.findViewById(R.id.edit_search_enteryear);
        inputDept = (EditText)view.findViewById(R.id.edit_search_dept);
        inputJob = (EditText)view.findViewById(R.id.edit_search_job);
        confirmView = (TextView)view.findViewById(R.id.text_search_confirm);
        confirmView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e("confirm","clicked");
            }
        });
        cancelView = (TextView)view.findViewById(R.id.text_search_cancel);
        cancelView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                clearSearchInput();
                setContainerVisibility();
            }
        });

        return view;
    }

    EditText inputName, inputYear, inputDept, inputJob;
    TextView confirmView, cancelView;
    public void clearSearchInput(){
        inputName.setText(""); inputYear.setText(""); inputDept.setText(""); inputJob.setText("");
    }
    public void setContainerVisibility(){
        if(frameSearch == null){
            return;
        }
        if(frameSearch.getVisibility() == View.VISIBLE){
            frameSearch.setVisibility(View.GONE);
        } else {
            frameSearch.setVisibility(View.VISIBLE);
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
        reqDate = getCurrentTimeStamp();
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
//                                mAdapter.clearAllFriends();   //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
                                mAdapter.items.clear();
                                mAdapter.blockCount = 0;
                                Log.e(TAG+"total:", ""+result.total);
                                mAdapter.setTotalCount(result.total);
                                setTabTotalCount(result.total);
                                ArrayList<FriendsResult> items = result.result;
                                FriendsDataManager.getInstance().clearFriends();
                                FriendsDataManager.getInstance().getList().addAll(items);

                                if(result.user != null){
                                    Log.e(TAG+" user:", result.user.toString());
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
//                            mAdapter.clearAllFriends(); //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
                            mAdapter.items.clear();
                            FriendsDataManager.getInstance().clearFriends();
                            Log.e(TAG, result.message);
                            Toast.makeText(getActivity(), TAG + "result.error: true\nresult.message:" + result.message, Toast.LENGTH_SHORT).show();
                        }
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    boolean isMoreData = false;
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
                            if(!result.message.equals("has no more accepted friends")){
                                Log.e(TAG+"getMore:", result.result.toString());
                                mAdapter.addAllFriends(result.result);
                                FriendsDataManager.getInstance().getList().addAll(result.result);
                                start++;
                            } else {
                                Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            }
                            isMoreData = false;
                            refreshLayout.setRefreshing(false);
                            Log.e(TAG+"getMoreItem() start=", ""+start);
                        }
                        @Override
                        public void onFailure(Request request, int code, Throwable cause) {
                            isMoreData =false;
                            refreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    private void setTabTotalCount(int total){
        if(FriendsFragment.totalFriends != null){
            FriendsFragment.totalFriends.setText(""+total);
        }
    }

    public String getCurrentTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    private void findOneAndModify(FriendsResult fr){
        if(mAdapter == null || mAdapter.getItemCount() < 1) return;
        mAdapter.findOneAndModify(fr);
        int num;
        switch (fr.status){
            case -1:
                mAdapter.removeItem(fr);
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
                mAdapter.blockCount++;
                num = Integer.parseInt(FriendsFragment.totalFriends.getText().toString());
                setTabTotalCount(--num);
                break;
            default:break;
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
