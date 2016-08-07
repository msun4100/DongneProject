package kr.me.ansr.tab.friends.recycler;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;

import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-20.
 */
public class FriendsSectionFragment extends Fragment
{
    private static final String TAG = FriendsSectionFragment.class.getSimpleName();
    AppCompatActivity activity;

    RecyclerView recyclerView;
    SectionAdapter mAdapter;
//    RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_section, container, false);
        activity = (AppCompatActivity) getActivity();
//        getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
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
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
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
                Log.e("111",""+lastVisibleItemPosition);
                Log.e("222",""+(lastVisibleItemPosition != RecyclerView.NO_POSITION));
                Log.e("333",""+(totalItemCount - 1 <= lastVisibleItemPosition));
                if (totalItemCount > 0 && lastVisibleItemPosition != RecyclerView.NO_POSITION && (totalItemCount - 1 <= lastVisibleItemPosition)) {
                    isLast = true;
                } else {
                    isLast = false;
                }
                Log.e(TAG+"onscrolled","");
            }
        });
        mAdapter = new SectionAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                Log.e("sectionFragment->data", data.toString());
                Intent intent = new Intent(getActivity(), FriendsDetailActivity.class);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, position);
                getParentFragment().startActivityForResult(intent, FriendsInfo.FRIENDS_RC_NUM); //tabHost가 있는 FriendsFragment에서 리절트를 받음
            }
        });
        mAdapter.setOnAdapterItemClickListener(new SectionAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(SectionAdapter adapter, View view, FriendsResult item, int type) {
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
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                Toast.makeText(getActivity(), "Long click data\n" + data.toString(), Toast.LENGTH_SHORT).show();
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
//        reqDate = getCurrentTimeStamp();
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        initUnivUsers();


        return view;
    }

    private void initData() {
//        initUnivUsers();   //get
//        mAdapter.put("내 프로필", new ChildItem(SectionAdapter.GROUP_PROFILE, true, "http://10.0.3.2:3000/getPic/0","user0","inha univ",16,"jobname"));
//        for(int i=0; i<10; i++){
//            boolean isFriend = true;
//            if(i % 3 == 0) isFriend = false;
//            mAdapter.put("학교 사람들", new ChildItem(SectionAdapter.GROUP_FRIENDS, isFriend, ""+ someUrls[r.nextInt(4)],("user"+i),"univ",i,"job"));
//        }
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
                0,
                mUnivId,
                ""+start,
                ""+DISPLAY_NUM,
                ""+reqDate,
                new NetworkManager.OnResultListener<FriendsInfo>() {
                    @Override
                    public void onSuccess(Request request, FriendsInfo result) {
//                        if(!result.message.equals("HAS_NO_BOARD_ITEM")  && result.result != null ){
                        if (result.error.equals(false)) {
                            if(result.result != null){
//                                mAdapter.clearAllFriends();   //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
                                mAdapter.items.clear();
                                mAdapter.setTotalCount(result.total);
                                ArrayList<FriendsResult> items = result.result;
                                for(int i=0; i < items.size(); i++){
                                    FriendsResult child = items.get(i);
                                    Log.e(TAG, ""+child);
                                    if(i==0 && mAdapter.getItem(1) == null){ //임시코드
                                        mAdapter.put("내 프로필", child); //내 정보 불러와서
                                        FriendsDataManager.getInstance().items.add(child);
                                    }
                                    mAdapter.put("학교 사람들", child);
                                    FriendsDataManager.getInstance().items.add(child);
                                }
                                start++;
                            }
                        } else {
//                            mAdapter.clearAllFriends(); //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
                            mAdapter.items.clear();
                            FriendsDataManager.getInstance().items.clear();

                            Log.e(TAG, result.message);
                            Toast.makeText(getActivity(), TAG + "result.error: true\nresult.message:" + result.message, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }
                });
        dialog = new ProgressDialog(getActivity());
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
//        if(mAdapter.getItemCount() > 0){
//            start = mAdapter.getItemCount() / DISPLAY_NUM;
//            isLast = false;
//            Log.e("onResume", ""+start+"\n"+mAdapter.getItemCount());
//            return;
//        }
//        start = 0;
//        reqDate = getCurrentTimeStamp();
//        initUnivUsers();
    }

    private void getMoreItem() {
        if (isMoreData) return;
        isMoreData = true;
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > mAdapter.getItemCount()) {
//            int start = mAdapter.getItemCount() + 1;
//            int display = 50;
            NetworkManager.getInstance().postDongneUnivUsers(getActivity(),
                    0, //mode
                    PropertyManager.getInstance().getUnivId(),
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    new NetworkManager.OnResultListener<FriendsInfo>() {
                        @Override
                        public void onSuccess(Request request, FriendsInfo result) {
                            Log.e(TAG+"getMore:", result.result.toString());
                            mAdapter.addAllFriends(result.result);
                            FriendsDataManager.getInstance().items.addAll(result.result);
                            isMoreData = false;
                            dialog.dismiss();
                            refreshLayout.setRefreshing(false);
                            start++;
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
            dialog.setTitle("Loading....");
            dialog.show();
        }
    }

    public String getCurrentTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public FriendsSectionFragment() {
        // Required empty public constructor
    }
}
