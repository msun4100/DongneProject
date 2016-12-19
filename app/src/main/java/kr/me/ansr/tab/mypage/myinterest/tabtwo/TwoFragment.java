package kr.me.ansr.tab.mypage.myinterest.tabtwo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.event.ActivityResultEvent;
import kr.me.ansr.tab.board.detail.BoardDetailActivity;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.board.reply.ReplyResult;

import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;
import kr.me.ansr.tab.mypage.PagerFragment;
import kr.me.ansr.tab.mypage.mywriting.tabtwo.CommentAdapter;
import kr.me.ansr.tab.mypage.mywriting.tabtwo.MyCommentInfo;
import okhttp3.Request;

/**
 * Created by KMS on 2016-10-18.
 * mywriting.tabtwo package의 아답터,뷰홀더, 인포, 데코 그대로 사용
 */
public class TwoFragment extends PagerFragment {
    private static final String TAG = TwoFragment.class.getSimpleName();

    RecyclerView recyclerView;
    CommentAdapter mAdapter;
    //    RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());
    ImageView searchIcon;
    CustomEditText searchInput;
    public String word = null;
    RelativeLayout emptyLayout;
    ImageView emptyIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_writing_two, container, false);
        emptyLayout = (RelativeLayout)view.findViewById(R.id.rl_empty);
        emptyIcon = (ImageView) view.findViewById(R.id.iv_empty_img);
        emptyIcon.setImageResource(R.drawable.z_empty_board);
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
                        initReplies();
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
                    if(word != null && !word.equals("") && word.length() > 0){
//                        getMoreSearchItem();
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
            }
        });
        mAdapter = new CommentAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ReplyResult data = mAdapter.getItem(position);
                selectedItem = data;
                Intent intent = new Intent(getActivity(), BoardDetailActivity.class);
//                intent.putExtra(BoardInfo.BOARD_DETAIL_OBJECT, data);
                intent.putExtra(BoardInfo.BOARD_DETAIL_BOARD_ID, data.boardId);
                intent.putExtra(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, position);
                intent.putExtra("currentTab", "0"); //재학생 탭 == 0
//                startActivity(intent);
                getActivity().startActivityForResult(intent, BoardInfo.BOARD_RC_NUM); //tabHost가 있는 BoardFragment에서 리절트를 받음
            }
        });
        mAdapter.setOnAdapterItemClickListener(new CommentAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(CommentAdapter adapter, View view, ReplyResult item, int type) {
                ReplyResult data = item;
                switch (type) {
                    case 100:
                        default:
                            Toast.makeText(getActivity(), "View click"+ item.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                ReplyResult data = mAdapter.getItem(position);
                selectedItem = data;
                Toast.makeText(getActivity(), "Long click"+ data.toString(), Toast.LENGTH_SHORT).show();
//                BoardReportDialogFragment mDialogFragment = BoardReportDialogFragment.newInstance();
//                Bundle b = new Bundle();
//                b.putSerializable("boardInfo", data);
//                b.putString("tag", BoardReportDialogFragment.TAG_TAB_THREE_STUDENT);
//                mDialogFragment.setArguments(b);
//                mDialogFragment.setTargetFragment(TwoFragment.this, DIALOG_RC_NUM);
//                mDialogFragment.show(getActivity().getSupportFragmentManager(), "boardReportDialog");
            }
        });
        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new BoardDecoration(getActivity()));


//        search views
        searchInput = (CustomEditText)view.findViewById(R.id.custom_editText1);
        searchInput.setHint("댓글 검색(작성자/내용)");
        searchIcon = (ImageView)view.findViewById(R.id.image_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                word = searchInput.getText().toString();
                if(!word.equals("") && word != null){
//                    searchInput.setText("");
                    searchInput.setText(word + " (취소:검색어 초기화 후 검색 버튼 클릭.)");
//                    Toast.makeText(getActivity(), "searchInput:"+word, Toast.LENGTH_SHORT).show();
                    start = 0;
                    reqDate = MyApplication.getInstance().getCurrentTimeStampString();
//                    searchBoard();
                } else {
//                    searchInput.setHint("게시글 검색 (작성자/내용)");
                    word = "";
                    start = 0;
                    reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                    initReplies();
                }
            }
        });

        start = 0;
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        initReplies();
        return view;
    }

    private void initReplies(){
        String userId = PropertyManager.getInstance().getUserId();
        NetworkManager.getInstance().postDongneCommentsMyWriting(getActivity(),
                userId,
                ""+start,
                ""+DISPLAY_NUM,
                ""+reqDate,
                new NetworkManager.OnResultListener<MyCommentInfo>() {
                    @Override
                    public void onSuccess(Request request, MyCommentInfo result) {
                        if (!result.message.equals("HAS_NO_REPLIES_ITEM") && result.error.equals(false)) {
                            if(result.result != null ){
                                mAdapter.items.clear();
                                Log.e(TAG+"total:", ""+result.total);
                                mAdapter.setTotalCount(result.total);
                                ArrayList<ReplyResult> items = result.result;
                                for(int i=0; i < items.size(); i++){
                                    ReplyResult child = items.get(i);
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
                            //error: true
                            mAdapter.items.clear();
                            Log.e(TAG, result.message);
                            Toast.makeText(getActivity(), "result.error: true" + result.message, Toast.LENGTH_SHORT).show();
                        }
                        showLayout();
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(getActivity(), TAG + "Replies init() onFailure:" + cause, Toast.LENGTH_LONG).show();
                        showLayout();
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Replies List Loading....");
        dialog.show();
    }

//    private void searchBoard(){
//        if (word == null || word.equals("")) return;
//        String mUnivId = PropertyManager.getInstance().getUnivId();
//        String tab = ""+0;  //재학생 탭 == 0
//        if(mUnivId == ""){
//            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
//            mUnivId = "1";
//            return;
//        }
//        NetworkManager.getInstance().postDongneBoardListMyWritingSearch(getActivity(),
//                mUnivId,
//                PropertyManager.getInstance().getUserId(),
//                ""+start,
//                ""+DISPLAY_NUM,
//                ""+reqDate,
//                word,
//                new NetworkManager.OnResultListener<BoardInfo>() {
//                    @Override
//                    public void onSuccess(Request request, BoardInfo result) {
//                        if (result.error.equals(false)) {
//                            if(!result.message.equals("HAS_NO_BOARD_ITEM")  && result.result != null ){
////                                mAdapter.clearAllFriends();   //이 시점에 호출하면 IndexBound exception. why? 내 프로필도 등록안했으니 칠드런의 사이즈가 0임.
//                                mAdapter.items.clear();
//                                Log.e(TAG+"total:", ""+result.total);
//                                mAdapter.setTotalCount(result.total);
//                                ArrayList<BoardResult> items = result.result;
//                                ArrayList<CommentThread> comment = result.comment;
//                                for(int i=0; i < items.size(); i++){
//                                    BoardResult child = items.get(i);
//                                    if(comment != null){
//                                        for(CommentThread ct : comment){
//                                            if(ct._id.equals(child.commentId)){
//                                                child.repCount = ct.replies.size();
//                                                int sum = 0;
//                                                for(ReplyResult rr : ct.replies){
//                                                    if(sum++ == 3) break;
//                                                    child.preReplies.add(rr);
//                                                }
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    mAdapter.add(child);
//                                }
//                                start++;
//                            } else {
//                                //has no board item
//                                mAdapter.items.clear();
//                                Log.e(TAG, result.message);
//                                Toast.makeText(getActivity(), "" + result.message, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            mAdapter.items.clear();
//                            Log.e(TAG, result.message);
//                            Toast.makeText(getActivity(), "result.error: true" + result.message, Toast.LENGTH_SHORT).show();
//                        }
//                        dialog.dismiss();
//                        refreshLayout.setRefreshing(false);
//                    }
//
//                    @Override
//                    public void onFailure(Request request, int code, Throwable cause) {
//                        Toast.makeText(getActivity(), TAG + " Board search onFailure:" + cause, Toast.LENGTH_LONG).show();
//                        dialog.dismiss();
//                        refreshLayout.setRefreshing(false);
//                    }
//                });
//        dialog = new ProgressDialog(getActivity());
//        dialog.setTitle("Board List Loading....");
//        dialog.show();
//    }


    //request values
    boolean isMoreData = false;
    ProgressDialog dialog = null;
    private static final int DISPLAY_NUM = BoardInfo.BOARD_DISPLAY_NUM;
    private int start=0;
    private String reqDate = null;

    private void getMoreItem(){
        if (isMoreData) return;
        isMoreData = true;
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > mAdapter.getItemCount()) {
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount()) {
            //기존 코드에서 +DIS_NUM 한 것은 마지막 디스플레이가 리프레쉬 안되서 디스플레이 수만큼 +시킴.
//            int start = mAdapter.getItemCount() + 1;
//            int display = 50;
            NetworkManager.getInstance().postDongneCommentsMyWriting(getActivity(),
                    PropertyManager.getInstance().getUserId(),
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    new NetworkManager.OnResultListener<MyCommentInfo>() {
                        @Override
                        public void onSuccess(Request request, MyCommentInfo result) {
                            Log.e(TAG+"getMore:", ""+result.message);
                            if(!result.message.equals("HAS_NO_REPLIES_ITEM")){
                                Log.e(TAG+"getMore:", result.result.toString());
                                mAdapter.addAll(result.result);
                                start++;
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
            dialog.setTitle("Replies List Loading....");
            dialog.show();
        }
    }

//    private void getMoreSearchItem(){
//        if (isMoreData || word == null || word.equals("")) return;
//        isMoreData = true;
//        String tab = "0";
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount()) {
//            NetworkManager.getInstance().postDongneBoardListMyWritingSearch(getActivity(),
//                    PropertyManager.getInstance().getUnivId(),
//                    PropertyManager.getInstance().getUserId(),
//                    ""+start,
//                    ""+DISPLAY_NUM,
//                    ""+reqDate,
//                    word,
//                    new NetworkManager.OnResultListener<BoardInfo>() {
//                        @Override
//                        public void onSuccess(Request request, BoardInfo result) {
//                            Log.e(TAG+"getMoreSearch:", ""+result.message);
//                            if(!result.message.equals("HAS_NO_BOARD_ITEM")){
//                                Log.e(TAG+"getMoreSearch:", result.result.toString());
//                                ArrayList<CommentThread> comment = result.comment;
//                                Log.e(TAG+"getMoreSearch-comment:", comment.toString());
//                                mAdapter.addAll(result.result);
//                                start++;
//                            } else {
//                                Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
//                            }
//                            isMoreData = false;
//                            dialog.dismiss();
//                            refreshLayout.setRefreshing(false);
//                            Log.e(TAG+"getMoreSearch() start=", ""+start);
//                        }
//                        @Override
//                        public void onFailure(Request request, int code, Throwable cause) {
//                            isMoreData =false;
//                            dialog.dismiss();
//                            refreshLayout.setRefreshing(false);
//                        }
//                    });
//            dialog = new ProgressDialog(getActivity());
//            dialog.setTitle("Board List Loading....");
//            dialog.show();
//        }
//    }


    private void refreshList(){
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mAdapter.clearAll();
                start = 0;
                reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                initReplies();
            }
        }, 1000);
    }

//    private void removeItem(String boardId, String writer){
//        NetworkManager.getInstance().postDongneBoardRemove(MyApplication.getContext(),
//                boardId,
//                writer,
//                new NetworkManager.OnResultListener<BoardInfo>() {
//                    @Override
//                    public void onSuccess(Request request, BoardInfo result) {
//                        if (result.error.equals(false)) {
//                            Toast.makeText(MyApplication.getContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_LONG).show();
//                            if (mAdapter != null && mAdapter.getItemCount() > 0){
//                                mAdapter.remove(selectedItem);
//                            }
//                            dialog.dismiss();
//                        } else {
//                            Toast.makeText(MyApplication.getContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Request request, int code, Throwable cause) {
//                        Toast.makeText(MyApplication.getContext(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                });
//        dialog = new ProgressDialog(getActivity());
//        dialog.setTitle("게시글 삭제 중..");
//        dialog.show();
//    }
//
//    public void reportBoard(int type){
//        if(selectedItem == null){
//            return;
//        }
//        final int to = selectedItem.boardId;
//        String msg = "board";
//        final BoardResult mItem = selectedItem;
//        //세번째 파라미터 mItem은 요청 성공시 아답터에서 삭제하기 위해 remove(object) 호출 용
//        NetworkManager.getInstance().postDongneReportUpdate(MyApplication.getContext(),
//                type, //reportType
//                to, //to == 선택된 아이템의 userId
//                msg, //게시판인지 친구인지
//                new NetworkManager.OnResultListener<StatusInfo>() {
//                    @Override
//                    public void onSuccess(Request request, StatusInfo result) {
//                        if (result.error.equals(false)) {
//                            Toast.makeText(MyApplication.getContext(), ""+result.message, Toast.LENGTH_LONG).show();
//                            dialog.dismiss();
////                            updateStatus(3, to, "reported");    //신고처리 성공시 차단친구로 변경
//                        } else {
//                            Toast.makeText(MyApplication.getContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Request request, int code, Throwable cause) {
//                        Toast.makeText(MyApplication.getContext(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                });
//        dialog = new ProgressDialog(getActivity());
//        dialog.setTitle("서버 요청 중..");
//        dialog.show();
//    }

    private void findOneAndModify(ReplyResult rr){
        if(mAdapter == null || mAdapter.getItemCount() < 1) return;
        mAdapter.findOneAndModify(rr);
    }


    public static final int DIALOG_RC_NUM = 301;
    public static final int DIALOG_RC_REPORT = 302;
    public static final int DIALOG_RC_DELETE = 303;
    public ReplyResult selectedItem = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extraBundle;
//        Toast.makeText(getActivity(),"fragment reqCode:"+requestCode+"\nresultCode:"+resultCode, Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case DIALOG_RC_NUM:
                if(resultCode == getActivity().RESULT_OK) {

                }
                break;

        }
    }
    //EventBus의 post를 통해 ActivityResultEvent 를 매개변수로 받아서 현재 프래그먼트의 (오버라이드한)onActivityResult를 호출
    //register/unregister 하는 과정은 baseFragment인 PagerFragment에서
    @Subscribe
    public void onEvent(ActivityResultEvent activityResultEvent){
        Log.e("onEvent:", "MyTwo");
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
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




    public TwoFragment(){}

    @Override
    public void onPageCurrent() {
        super.onPageCurrent();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }
}
