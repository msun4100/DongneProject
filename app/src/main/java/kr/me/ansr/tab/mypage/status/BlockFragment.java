package kr.me.ansr.tab.mypage.status;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomDialogFragment;
import kr.me.ansr.common.InputDialogFragment;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.detail.StatusInfo;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.MyDecoration;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.mypage.recycler.StatusAdapter;
import okhttp3.Request;


/**
 * Created by KMS on 2016-08-31.
 */


public class BlockFragment extends Fragment {
    private String TAG = BlockFragment.class.getSimpleName();

    public static BlockFragment newInstance() {
        BlockFragment f = new BlockFragment();
        return f;
    }


    RecyclerView recyclerView;
    static StatusAdapter mAdapter;

    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());

    public int mSearchOption = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_block, container, false);

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
        mAdapter = new StatusAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                Log.e("receiveFragment->data", data.toString());
                Intent intent = new Intent(getActivity(), FriendsDetailActivity.class);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, position);
                intent.putExtra("tag", InputDialogFragment.TAG_STATUS_BLOCK);
                startActivity(intent);
            }
        });
        mAdapter.setOnAdapterItemClickListener(new StatusAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(StatusAdapter adapter, View view, FriendsResult item, int type) {
                switch (type) {
                    case 100:
                        Toast.makeText(getActivity(), "nameView click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(getActivity(), "imageView click"+ item.toString(), Toast.LENGTH_SHORT).show();
                    case 300:
                        selectedItem = item;

                        CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                        Bundle b = new Bundle();
                        b.putString("tag", CustomDialogFragment.TAG_STATUS_BLOCK);
                        b.putString("title","친구 차단을 해제 하시겠습니까?");
                        b.putString("body", "학교 사람들 리스트에 노출됩니다.");
                        mDialogFragment.setArguments(b);
                        mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
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


        return view;
    }

    private void initData(){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        if(mUnivId == ""){
            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
//            return;
            mUnivId = ""+ (0); //테스트 위해 임시로 (회원가입안하고 테스트 해보기 위해)
        }
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        NetworkManager.getInstance().getDongneFriendsStatus(getActivity(),
                FriendsInfo.STATUS_BLOCK, //mode(each status)
                new NetworkManager.OnResultListener<FriendsInfo>() {
                    @Override
                    public void onSuccess(Request request, FriendsInfo result) {
                        if (result.error.equals(false)) {
                            if(result.result != null && !result.message.equals("has no blocked-3 friends") ){
//                                mAdapter.clearAllFriends();   //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
                                mAdapter.items.clear();
                                Log.e(TAG+"total:", ""+result.total);
                                mAdapter.setTotalCount(result.total);
                                ArrayList<FriendsResult> items = result.result;
                                for(int i=0; i < items.size(); i++){
                                    FriendsResult child = items.get(i);
                                    Log.e(TAG, ""+child);
//                                    if(i==0 && mAdapter.getItem(1) == null){ //임시코드
//                                        mAdapter.put("내 프로필", child); //내 정보 불러와서
//                                    }
//                                    mAdapter.put("받은 신청", child);
                                    mAdapter.put(child);
                                }
                                start++;
                            }
                        } else {
//                            mAdapter.clearAllFriends(); //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
                            mAdapter.items.clear();
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
    private static final int DISPLAY_NUM = 4;
    private int start=0;
    private String reqDate = null;

//    private void getMoreItem() {
//        if (isMoreData) return;
//        isMoreData = true;
////        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > mAdapter.getItemCount()) {
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount()) {
//            //기존 코드에서 +DIS_NUM 한 것은 마지막 디스플레이가 리프레쉬 안되서 디스플레이 수만큼 +시킴.
////            int start = mAdapter.getItemCount() + 1;
////            int display = 50;
//            NetworkManager.getInstance().postDongneUnivUsers(getActivity(),
//                    1, //mode
//                    mSearchOption,
//                    PropertyManager.getInstance().getUnivId(),
//                    ""+start,
//                    ""+DISPLAY_NUM,
//                    ""+reqDate,
//                    new NetworkManager.OnResultListener<FriendsInfo>() {
//                        @Override
//                        public void onSuccess(Request request, FriendsInfo result) {
//                            Log.e(TAG+"getMore:", ""+result.message);
//                            if(!result.message.equals("has no more accepted friends")){
//                                Log.e(TAG+"getMore:", result.result.toString());
//                                mAdapter.addAllFriends(result.result);
//                            } else {
//                                Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
//                            }
//                            isMoreData = false;
//                            refreshLayout.setRefreshing(false);
//                            start++;
//                            Log.e(TAG+"getMoreItem() start=", ""+start);
//                        }
//                        @Override
//                        public void onFailure(Request request, int code, Throwable cause) {
//                            isMoreData =false;
//                            refreshLayout.setRefreshing(false);
//                        }
//                    });
//        }
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
    @Override
    public void onResume() {
        super.onResume();
        start = 0;
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        initData();
    }


    private void sendImageToOnActivityResult(String path){
        String filePath = path;
        Intent intent = new Intent();
        intent.putExtra("filePath", filePath);

//        getActivity().setResult(MediaStoreActivity.RC_SELECT_PROFILE_CODE, intent);
    }

    static FriendsResult selectedItem = null;
    public static void removeStatus(){
        //파라미터 mItem은 요청 성공시 아답터에서 삭제하기 위해 remove(object) 호출 용
        if(selectedItem == null){
            return;
        }

        NetworkManager.getInstance().postDongneFriendsRemove(MyApplication.getContext(),
                selectedItem.userId,
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        if (result.error.equals(false)) {
                            Toast.makeText(MyApplication.getContext(), ""+result.message, Toast.LENGTH_LONG).show();
                            mAdapter.removeItem(selectedItem);
                        } else {
                            Toast.makeText(MyApplication.getContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
//                            Log.e(TAG+" error: true", result.message);
                        }
//                        dialog.dismiss();
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(MyApplication.getContext(), "onFailure\n"+cause, Toast.LENGTH_LONG).show();
//                        dialog.dismiss();
                    }
                });
//        dialog = new ProgressDialog(getActivity());
//        dialog.setTitle("서버 요청 중..");
//        dialog.show();
    }

}

