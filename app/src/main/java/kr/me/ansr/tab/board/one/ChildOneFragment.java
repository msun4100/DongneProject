package kr.me.ansr.tab.board.one;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Random;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.tab.board.ActivityResultEvent;
import kr.me.ansr.tab.board.EventBus;
import kr.me.ansr.tab.board.PagerFragment;
import kr.me.ansr.tab.board.detail.BoardDetailActivity;
import kr.me.ansr.tab.board.like.LikeInfo;
import kr.me.ansr.tab.board.reply.CommentThread;
import kr.me.ansr.tab.board.reply.ReplyResult;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-25.
 */
public class ChildOneFragment extends PagerFragment {

    private static final String TAG = ChildOneFragment.class.getSimpleName();
    RecyclerView recyclerView;
    BoardAdapter mAdapter;
    //    RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_one, container, false);

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
                        initBoard();
                    }
                }, 2000);
            }
        });
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
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
        mAdapter = new BoardAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BoardResult data = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), BoardDetailActivity.class);
//                intent.putExtra(BoardInfo.BOARD_DETAIL_OBJECT, data);
                intent.putExtra(BoardInfo.BOARD_DETAIL_BOARD_ID, data.boardId);
                intent.putExtra(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, position);
                getParentFragment().startActivityForResult(intent, BoardInfo.BOARD_RC_NUM); //tabHost가 있는 BoardFragment에서 리절트를 받음
            }
        });
        mAdapter.setOnAdapterItemClickListener(new BoardAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(BoardAdapter adapter, View view, int position, BoardResult item, int type) {
                BoardResult data = mAdapter.getItem(position);
                switch (type) {
                    case 100:
                        Toast.makeText(getActivity(), "nameView click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(getActivity(), "imageView click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 300:
                        Toast.makeText(getActivity(), "listViewLayout click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 400:
                        Toast.makeText(getActivity(), "like view click:\n"+data.likes.toString(), Toast.LENGTH_SHORT).show();
                        int likeMode =2;    //likeMode가 2면 요청 안하고 리턴
                        int mUserId = Integer.valueOf(PropertyManager.getInstance().getUserId());
                        if(data.likes.contains(mUserId)) likeMode = LikeInfo.DISLIKE; else likeMode = LikeInfo.LIKE;
                        postLike(likeMode, String.valueOf(data.boardId), PropertyManager.getInstance().getUserId(), position);
                        break;
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                BoardResult data = mAdapter.getItem(position);
                Toast.makeText(getActivity(), "Long click data\n" + data.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new BoardDecoration(getActivity()));
        start = 0;
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        initBoard();
//        initData();
        return view;
    }
    private void initBoard(){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        String tab = ""+0;  //재학생 탭 == 0
        if(mUnivId == ""){
            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
            mUnivId = "1";
            return;
        }
//        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        NetworkManager.getInstance().postDongneBoardList(getActivity(),
                mUnivId,
                tab,
                ""+start,
                ""+DISPLAY_NUM,
                ""+reqDate,
                new NetworkManager.OnResultListener<BoardInfo>() {
                    @Override
                    public void onSuccess(Request request, BoardInfo result) {
                        if (result.error.equals(false)) {
                            if(!result.message.equals("HAS_NO_BOARD_ITEM")  && result.result != null ){
//                                mAdapter.clearAllFriends();   //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
                                mAdapter.items.clear();
                                Log.e(TAG+"total:", ""+result.total);
                                mAdapter.setTotalCount(result.total);
                                ArrayList<BoardResult> items = result.result;
                                ArrayList<CommentThread> comment = result.comment;
                                Log.e(TAG+"comment:", ""+comment.toString());
                                Random r = new Random();
                                for(int i=0; i < items.size(); i++){
                                    BoardResult child = items.get(i);
                                    if(comment != null){
                                        for(CommentThread ct : comment){
                                            if(ct._id.equals(child.commentId)){
                                                child.repCount = ct.replies.size();
                                                int sum = 0;
                                                for(ReplyResult rr : ct.replies){
                                                    if(sum++ == 3) break;
                                                    child.preReplies.add(rr);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    mAdapter.add(child);
                                }
                                start++;
                            } else {
                                //has no board item
                                mAdapter.items.clear();
                                Log.e(TAG, result.message);
                                Toast.makeText(getActivity(), "" + result.message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                            mAdapter.clearAllFriends(); //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
                            //error: true
                            mAdapter.items.clear();
                            Log.e(TAG, result.message);
                            Toast.makeText(getActivity(), "result.error: true" + result.message, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(getActivity(), TAG + "Board init() onFailure:" + cause, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading....");
        dialog.show();
    }
    //request values
    boolean isMoreData = false;
    ProgressDialog dialog = null;
    private static final int DISPLAY_NUM = BoardInfo.BOARD_DISPLAY_NUM;
    private int start=0;
    private String reqDate = null;

    private void getMoreItem(){
        if (isMoreData) return;
        isMoreData = true;
        String tab = "0";
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > mAdapter.getItemCount()) {
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount()) {
            //기존 코드에서 +DIS_NUM 한 것은 마지막 디스플레이가 리프레쉬 안되서 디스플레이 수만큼 +시킴.
//            int start = mAdapter.getItemCount() + 1;
//            int display = 50;
            NetworkManager.getInstance().postDongneBoardList(getActivity(),
                    PropertyManager.getInstance().getUnivId(),
                    tab,
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    new NetworkManager.OnResultListener<BoardInfo>() {
                        @Override
                        public void onSuccess(Request request, BoardInfo result) {
                            Log.e(TAG+"getMore:", ""+result.message);
                            if(!result.message.equals("HAS_NO_BOARD_ITEM")){
                                Log.e(TAG+"getMore:", result.result.toString());
                                ArrayList<CommentThread> comment = result.comment;
                                Log.e(TAG+"getMore-comment:", comment.toString());
                                mAdapter.addAll(result.result);
                            } else {
                                Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            }
                            isMoreData = false;
//                            dialog.dismiss();
//                            refreshLayout.setRefreshing(false);
                            start++;
                            Log.e(TAG+"getMoreItem() start=", ""+start);
                        }
                        @Override
                        public void onFailure(Request request, int code, Throwable cause) {
                            isMoreData =false;
//                            dialog.dismiss();
//                            refreshLayout.setRefreshing(false);
                        }
                    });
//            dialog = new ProgressDialog(getActivity());
//            dialog.setTitle("Loading....");
//            dialog.show();
        }
    }

    private void postLike(int like, String boardId, final String userId, final int position){
        if(like>1){Toast.makeText(getActivity(),"invalid like type",Toast.LENGTH_SHORT).show(); return;}
        if(boardId == null || userId == null) { Toast.makeText(getActivity(),"arg is null",Toast.LENGTH_SHORT).show(); return; }
        NetworkManager.getInstance().postDongneBoardLike(getActivity(), like, boardId, userId, new NetworkManager.OnResultListener<LikeInfo>() {
            @Override
            public void onSuccess(Request request, LikeInfo result) {
                if(result.error.equals(false)){
                    Toast.makeText(getActivity(),""+result.message, Toast.LENGTH_SHORT).show();
                    mAdapter.setLike(position, Integer.valueOf(userId));
                } else {
                    //error: true
                    Toast.makeText(getActivity(),"error: true\n"+result.message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(getActivity(),"postLike() onFailure: "+cause, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void ModifiedSetItem(int position, BoardResult result){
        if(position == -1 || result == null) return;
        mAdapter.getItem(position).likes = result.likes;
        mAdapter.getItem(position).likeCount = result.likeCount;
        mAdapter.getItem(position).repCount = result.repCount;
        mAdapter.notifyDataSetChanged();
    }

    private void refreshList(){
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mAdapter.clearAll();
                start = 0;
                reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                initBoard();
            }
        }, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extraBundle;
//        Toast.makeText(getActivity(),"fragment reqCode:"+requestCode+"\nresultCode:"+resultCode, Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case BoardInfo.BOARD_RC_NUM:
                if (resultCode == getActivity().RESULT_OK) {
                    extraBundle = data.getExtras();
                    int position = extraBundle.getInt(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, -1);
                    BoardResult result = (BoardResult)extraBundle.getSerializable(BoardInfo.BOARD_DETAIL_MODIFIED_ITEM);
                    Toast.makeText(getActivity(),""+ result.toString() , Toast.LENGTH_SHORT).show();
                    ModifiedSetItem(position, result);
                    break;
                }
            case 100:
                refreshList();
                break;
        }
    }
    //EventBus의 post를 통해 ActivityResultEvent 를 매개변수로 받아서 현재 프래그먼트의 (오버라이드한)onActivityResult를 호출
    //register/unregister 하는 과정은 baseFragment인 PagerFragment에서
    @Subscribe
    public void onEvent(ActivityResultEvent activityResultEvent){
       onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
    }



    public ChildOneFragment(){}
    //보드 프래그먼트의 커스텀뷰페이저 오버라이드
    @Override
    public void onPageCurrent() {
        super.onPageCurrent();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            if(activity != null){
//                activity.getSupportActionBar().setTitle("Board Fragment");
//            }
        }
    }
}
