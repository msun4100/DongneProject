package kr.me.ansr.tab.board.one;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Random;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.BoardReportDialogFragment;
import kr.me.ansr.common.CustomDialogFragment;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.InputDialogFragment;
import kr.me.ansr.common.ReportDialogFragment;
import kr.me.ansr.common.ReportFormDialogFragment;
import kr.me.ansr.common.event.ActivityResultEvent;
import kr.me.ansr.tab.board.BoardWriteActivity;
import kr.me.ansr.tab.board.PagerFragment;
import kr.me.ansr.tab.board.PreLoadLayoutManager;
import kr.me.ansr.tab.board.detail.BoardDetailActivity;
import kr.me.ansr.tab.board.like.LikeInfo;
import kr.me.ansr.tab.board.reply.CommentThread;
import kr.me.ansr.tab.board.reply.ReplyResult;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.detail.StatusInfo;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-25.
 */
public class ChildOneFragment extends PagerFragment {

    private static final String TAG = ChildOneFragment.class.getSimpleName();

    RecyclerView recyclerView;
    BoardAdapter mAdapter;
    PreLoadLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    boolean isLast = false;
    Handler mHandler = new Handler(Looper.getMainLooper());
    ImageView searchIcon;
    CustomEditText searchInput;
    public String word = null;
    InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_one, container, false);
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
                        initBoard();
                    }
                }, 1000);
            }
        });
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(word != null && !word.equals("") && word.length() > 0 && isSearching == true){
                        getMoreSearchItem();
                    } else {
                        getMoreItem();
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mAdapter.getItemCount();
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();  //Completely로 하면 -1리턴 되는 경우가 있음
                if(lastCompletelyVisibleItemPosition == RecyclerView.NO_POSITION && ((totalItemCount - 1) == lastVisibleItemPosition) ){
                    lastCompletelyVisibleItemPosition = lastVisibleItemPosition;
                }
                if (totalItemCount > 0 && lastCompletelyVisibleItemPosition != RecyclerView.NO_POSITION && (totalItemCount - 1 <= lastCompletelyVisibleItemPosition)) {
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
                selectedItem = data;    //신고할 때 필요한걸 텐데
                showBoardDetail(data, position);
            }
        });
        mAdapter.setOnAdapterItemClickListener(new BoardAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(BoardAdapter adapter, View view, int position, BoardResult item, int type) {
                BoardResult data = mAdapter.getItem(position);
                selectedItem = data;
                switch (type) {
                    case 100:   //nameView
                    case 200:   //imageView
                        getUserInfo(data.writer);
                        break;
                    case 300:
                        BoardReportDialogFragment mDialogFragment = BoardReportDialogFragment.newInstance();
                        Bundle b = new Bundle();
                        b.putSerializable("boardInfo", data);
                        b.putString("tag", BoardReportDialogFragment.TAG_TAB_THREE_STUDENT);
                        mDialogFragment.setArguments(b);
                        mDialogFragment.setTargetFragment(ChildOneFragment.this, ChildOneFragment.DIALOG_RC_NUM);
                        mDialogFragment.show(getActivity().getSupportFragmentManager(), "boardReportDialog");
                        break;
                    case 400:
                        Toast.makeText(getActivity(), "like view click:\n"+data.likes.toString(), Toast.LENGTH_SHORT).show();

                        Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder().setCategory("TAB3_ChildOne").setAction("Press Button").setLabel("Like view Click").build());
                        int likeMode =2;    //likeMode가 2면 요청 안하고 리턴
                        int mUserId = Integer.valueOf(PropertyManager.getInstance().getUserId());
                        if(data.likes.contains(mUserId)) likeMode = LikeInfo.DISLIKE; else likeMode = LikeInfo.LIKE;
                        String to = ""+data.writer;
                        postLike(likeMode, String.valueOf(data.boardId), PropertyManager.getInstance().getUserId(), to, position);
                        break;
                    case 500:
                        showBoardDetail(data, position);
                        break;
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                BoardResult data = mAdapter.getItem(position);
                selectedItem = data;
                BoardReportDialogFragment mDialogFragment = BoardReportDialogFragment.newInstance();
                Bundle b = new Bundle();
                b.putSerializable("boardInfo", data);
                b.putString("tag", BoardReportDialogFragment.TAG_TAB_THREE_STUDENT);
                mDialogFragment.setArguments(b);
                mDialogFragment.setTargetFragment(ChildOneFragment.this, ChildOneFragment.DIALOG_RC_NUM);
                mDialogFragment.show(getActivity().getSupportFragmentManager(), "boardReportDialog");
            }
        });
        recyclerView.setAdapter(mAdapter);
        layoutManager = new PreLoadLayoutManager(getActivity());
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new BoardDecoration(getActivity()));


//        search views
        searchInput = (CustomEditText)view.findViewById(R.id.custom_editText1);
        searchInput.setHint("게시글 검색 (작성자/학과/내용)");
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    word = searchInput.getText().toString();
                    if(!word.equals("") && word != null && isSearching == false){
//                    searchInput.setText("");
                        isSearching = true;
                        searchIcon.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        start = 0;
                        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                        searchBoard();
                    } else {
                        isSearching = false;
                        searchIcon.setImageResource(R.drawable.b_list_search_icon_selector);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        word = "";
                        start = 0;
                        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                        initBoard();
                    }
                    return true;
                }
                return false;
            }
        });
        searchIcon = (ImageView)view.findViewById(R.id.image_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                word = searchInput.getText().toString();
                if(!word.equals("") && word != null && isSearching == false){
                    isSearching = true;
                    searchIcon.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                    searchInput.setText("검색어:"+word);
                    start = 0;
                    reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                    searchBoard();
                } else {
                    isSearching = false;
                    searchIcon.setImageResource(R.drawable.b_list_search_icon_selector);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    word = "";
                    searchInput.setText("");
                    start = 0;
                    reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                    initBoard();
                }
            }
        });

        start = 0;
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        initBoard();

        Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName("TAB3_"+getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());

        return view;
    }

    public boolean isSearching = false;

    private void showBoardDetail(BoardResult data, int position){
        Log.e(TAG, "showBoardDetail: "+data.toString() );
        selectedItem = data;
        Intent intent = new Intent(getActivity(), BoardDetailActivity.class);
//                intent.putExtra(BoardInfo.BOARD_DETAIL_OBJECT, data);
        intent.putExtra(BoardInfo.BOARD_DETAIL_BOARD_ID, data.boardId);
        intent.putExtra(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, position);
        intent.putExtra("currentTab", "0"); //재학생 탭 == 0
        getParentFragment().startActivityForResult(intent, BoardInfo.BOARD_RC_NUM); //tabHost가 있는 BoardFragment에서 리절트를 받음
    }
    private void getUserInfo(int writer) {
        NetworkManager.getInstance().getDongneUserInfo(getActivity(), writer,
                new NetworkManager.OnResultListener<FriendsInfo>() {
                    @Override
                    public void onSuccess(Request request, FriendsInfo result) {
                        if (result.error.equals(false)) {
                            FriendsResult data;
                            if(result.result != null && result.result.size() == 1){
                                data = result.result.get(0);
//                                selectedItem = data;    //디테일에서 관리 누를 경우사용될 변수
                                Log.e(TAG, "onSuccess: "+data.toString() );
                                Intent intent = new Intent(getActivity(), FriendsDetailActivity.class);
                                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
                                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
                                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, 0);   //position은 이제 필요 없는데..
                                intent.putExtra("tag", InputDialogFragment.TAG_FRIENDS_DETAIL);
                                getParentFragment().startActivityForResult(intent, FriendsSectionFragment.FRIENDS_RC_NUM); //tabHost가 있는 FriendsFragment에서 리절트를 받음
                            } else {
                                Log.e(TAG, result.message);
                                Toast.makeText(getActivity(), "result.error: false" + result.message, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.e(TAG, result.message);
                            Toast.makeText(getActivity(), "result.error: true" + result.message, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(getActivity(), TAG + " getUser onFailure:" + cause, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("유저 정보를 가져오는 중...");
        dialog.show();
    }

    private void initBoard(){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        String tab = ""+0;  //재학생 탭 == 0
        if(mUnivId == ""){
            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
            mUnivId = "1";
            return;
        }
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
                                if (mAdapter.getItemCount() > 0){
                                    recyclerView.scrollToPosition(0);
                                }
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
        dialog.setTitle("Board List Loading....");
        dialog.show();
    }

    private void searchBoard(){
        if (word == null || word.equals("")) return;
        String mUnivId = PropertyManager.getInstance().getUnivId();
        String tab = ""+0;  //재학생 탭 == 0
        if(mUnivId == ""){
            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
            mUnivId = "1";
            return;
        }
        NetworkManager.getInstance().postDongneBoardListSearch(getActivity(),
                mUnivId,
                tab,
                ""+start,
                ""+DISPLAY_NUM,
                ""+reqDate,
                word,
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
                                if (mAdapter.getItemCount() > 0){
                                    recyclerView.scrollToPosition(0);
                                }
                            } else {
                                //has no board item
                                mAdapter.items.clear();
                                Log.e(TAG, result.message);
                                Toast.makeText(getActivity(), "" + result.message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mAdapter.items.clear();
                            Log.e(TAG, result.message);
                            Toast.makeText(getActivity(), "result.error: true" + result.message, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(getActivity(), TAG + " Board search onFailure:" + cause, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }
                });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Board List Loading....");
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
            NetworkManager.getInstance().postDongneBoardList(getActivity(),
                    PropertyManager.getInstance().getUnivId(),
                    tab,
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    new NetworkManager.OnResultListener<BoardInfo>() {
                        @Override
                        public void onSuccess(Request request, BoardInfo result) {
                            Log.e(TAG, "onSuccess: getMore"+result.message );
                            if(!result.message.equals("HAS_NO_BOARD_ITEM")){
                                Log.e(TAG+"getMore:", result.result.toString());
                                ArrayList<BoardResult> items = result.result;
                                ArrayList<CommentThread> comment = result.comment;
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
//                                Log.e(TAG+"getMore-comment:", comment.toString());
//                                mAdapter.addAll(result.result);
                                start++;
                            } else {
                                Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            }
                            isMoreData = false;
                            dialog.dismiss();
                            refreshLayout.setRefreshing(false);
                            Log.e(TAG+ "getMoreItem() start=", ""+start);
                        }
                        @Override
                        public void onFailure(Request request, int code, Throwable cause) {
                            isMoreData =false;
                            dialog.dismiss();
                            refreshLayout.setRefreshing(false);
                        }
                    });
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Board List Loading....");
            dialog.show();
        }
    }

    private void getMoreSearchItem(){
        if (isMoreData || word == null || word.equals("")) return;
        isMoreData = true;
        String tab = "0";
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount()) {
            NetworkManager.getInstance().postDongneBoardListSearch(getActivity(),
                    PropertyManager.getInstance().getUnivId(),
                    tab,
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    word,
                    new NetworkManager.OnResultListener<BoardInfo>() {
                        @Override
                        public void onSuccess(Request request, BoardInfo result) {
                            Log.e(TAG+"getMoreSearch:", ""+result.message);
                            if(!result.message.equals("HAS_NO_BOARD_ITEM")){
                                ArrayList<BoardResult> items = result.result;
                                ArrayList<CommentThread> comment = result.comment;
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
                                Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            }
                            isMoreData = false;
                            dialog.dismiss();
                            refreshLayout.setRefreshing(false);
                            Log.e(TAG+"getMoreSearch() start=", ""+start);
                        }
                        @Override
                        public void onFailure(Request request, int code, Throwable cause) {
                            isMoreData =false;
                            dialog.dismiss();
                            refreshLayout.setRefreshing(false);
                        }
                    });
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Board List Loading....");
            dialog.show();
        }
    }

    private void postLike(int like, String boardId, final String userId, String to, final int position){
        if(like>1){Toast.makeText(getActivity(),"invalid like type",Toast.LENGTH_SHORT).show(); return;}
        if(boardId == null || userId == null) { Toast.makeText(getActivity(),"arg is null",Toast.LENGTH_SHORT).show(); return; }
        NetworkManager.getInstance().postDongneBoardLike(getActivity(), like, boardId, userId, to, new NetworkManager.OnResultListener<LikeInfo>() {
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

    private void removeItem(final BoardResult br){
        NetworkManager.getInstance().postDongneBoardRemove(MyApplication.getContext(),
                ""+br.boardId,
                ""+br.writer,
                new NetworkManager.OnResultListener<BoardInfo>() {
                    @Override
                    public void onSuccess(Request request, BoardInfo result) {
                        if (result.error.equals(false)) {
                            Toast.makeText(MyApplication.getContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_LONG).show();
                            if (mAdapter != null && mAdapter.getItemCount() > 0){
                                mAdapter.removeItem(br);
                            }
                            dialog.dismiss();
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
        dialog.setTitle("게시글 삭제 중..");
        dialog.show();
    }

    public void reportBoard(int type){
        if(selectedItem == null){
            return;
        }
        final int to = selectedItem.boardId;
        String msg = "board";
        final BoardResult mItem = selectedItem;
        //세번째 파라미터 mItem은 요청 성공시 아답터에서 삭제하기 위해 remove(object) 호출 용
        NetworkManager.getInstance().postDongneReportUpdate(MyApplication.getContext(),
                type, //reportType
                to, //to == 선택된 아이템의 userId
                msg, //게시판인지 친구인지
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        if (result.error.equals(false)) {
                            Toast.makeText(MyApplication.getContext(), ""+result.message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
//                            updateStatus(3, to, "reported");    //신고처리 성공시 차단친구로 변경
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

    private void findOneAndModify(BoardResult br){
//        Log.e(TAG, "findOneAndModify: before "+br.toString() );
        if(mAdapter == null || mAdapter.getItemCount() < 1) {
            Log.e(TAG, "findOneAndModify: null or itemCount error");
            return;
        }
        mAdapter.findOneAndModify(br);
    }


    public static final int DIALOG_RC_NUM = 301;
    public static final int DIALOG_RC_REPORT = 302;
    public static final int DIALOG_RC_DELETE = 303;
    public BoardResult selectedItem = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extraBundle;
        switch (requestCode) {
            case BoardInfo.BOARD_RC_NUM:
//                BoardDetail에서 신고, 수정, 삭제 등을 했을 때 여기서 처리
//                NEXT에 따라서 신고, 수정, 삭제 등을 해줌
                if (resultCode == getActivity().RESULT_OK) {
                    extraBundle = data.getExtras();
//                    int position = extraBundle.getInt(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, -1);
//                    BoardResult result = (BoardResult)extraBundle.getSerializable(BoardInfo.BOARD_DETAIL_MODIFIED_ITEM);
//                    Log.e("ChildOne", "result_ok");
//                    ModifiedSetItem(position, result);
                    String next =  extraBundle.getString("_NEXT_");
                    if(next != null){
                        switch (next){
                            case "_REPORT_":
                                callReport();
                                break;
                            case "_EDIT_":
                                callEdit();
                                break;
                            case "_DELETE_":
                                callDelete();
                                break;
                        }
                    }

                    break;
                }
            case BoardWriteActivity.BOARD_WRITE_RC_NEW:
                refreshList();
                break;
//                //1116 이제 여기로 안타고 BoardFragment에서 Post(new BoardResult) 해서 @subscribe(BoardResult br) 에서 받고,  findOneAndModify함
//            case BoardWriteActivity.BOARD_WRITE_RC_EDIT:
//                if(resultCode == getActivity().RESULT_OK){
//                    extraBundle = data.getExtras();
//                    BoardResult br = (BoardResult)extraBundle.getSerializable("mItem");
//                    findOneAndModify(br);
//                }
//                break;
            case DIALOG_RC_NUM:
                if(resultCode == getActivity().RESULT_OK){
                    extraBundle = data.getExtras();
                    String next = extraBundle.getString("next");
                    if(next == null || next.equals("")) break;
                    switch (next){
                        case "_REPORT_":
                            callReport();
                            break;
                        case "_EDIT_":
                            callEdit();
                            break;
                        case "_DELETE_":
                            callDelete();
                            break;
                    }
                }
                break;
            case DIALOG_RC_REPORT:
                if (resultCode == getActivity().RESULT_OK) {
                    extraBundle = data.getExtras();
                    int reportType = extraBundle.getInt("type", -1);
                    Log.e("DIALOG_RC_REPORT",""+reportType);
                    if(reportType != -1){
                        reportBoard(reportType);
                    }
                }
                break;
            case DIALOG_RC_DELETE:
                if (resultCode == getActivity().RESULT_OK) {
//                    removeItem(""+selectedItem.boardId, ""+selectedItem.writer);
                    Log.e("DIALOG_RC_DELETE",""+data.getExtras().getString("choice"));
                    BoardResult br = (BoardResult) data.getExtras().getSerializable("bItem");
                    if(br != null) {
                        Log.e("DIALOG_RC_DELETE",""+data.getExtras().getSerializable("bItem"));
                        removeItem(br);
                    }
                }
                break;
        }
    }

    private void callReport(){
        ReportFormDialogFragment mDialogFragment = ReportFormDialogFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("tag", ReportFormDialogFragment.TAG_TAB_THREE_STUDENT);
        b.putSerializable("fItem", null);
        b.putSerializable("bItem", selectedItem);
        mDialogFragment.setArguments(b);
        mDialogFragment.setTargetFragment(ChildOneFragment.this, DIALOG_RC_REPORT);
        mDialogFragment.show(getActivity().getSupportFragmentManager(), "reportFormDialog");
        Log.e("childOne", "report");
    }
    private void callEdit(){
        Log.e("childOne", "edit");
        Intent intent = new Intent(getActivity(), BoardWriteActivity.class);
        intent.putExtra("currentTab", "0"); // 0 = 재학생, 1 = 졸업생
        intent.putExtra("type", "edit");
        intent.putExtra("mItem", selectedItem);
        getParentFragment().startActivityForResult(intent, BoardWriteActivity.BOARD_WRITE_RC_EDIT); //tabHost가 있는 BoardFragment에서 리절트를 받음
    }
    private void callDelete(){
        CustomDialogFragment cDialogFragment = CustomDialogFragment.newInstance();
        Bundle bb = new Bundle();
        bb.putString("tag", CustomDialogFragment.TAG_TAB_THREE_STU);
        bb.putString("title","게시글을 삭제 하시겠습니까?");
        bb.putString("body", "해당 게시글이 삭제됩니다.");
        bb.putString("choice","delete");
        bb.putSerializable("bItem", selectedItem);
        cDialogFragment.setTargetFragment(ChildOneFragment.this, DIALOG_RC_DELETE);
        cDialogFragment.setArguments(bb);
        cDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
    }

    //EventBus의 post를 통해 ActivityResultEvent 를 매개변수로 받아서 현재 프래그먼트의 (오버라이드한)onActivityResult를 호출
    //register/unregister 하는 과정은 baseFragment인 PagerFragment에서
    @Subscribe
    public void onEvent(ActivityResultEvent activityResultEvent){
        Log.e("onEvent:", "ChildOne ARE");
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
    }

    @Subscribe
    public void onEvent(BoardResult br){
        Log.e("onEvent:", "ChildOne br");
        findOneAndModify(br);
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

        }
    }

}
