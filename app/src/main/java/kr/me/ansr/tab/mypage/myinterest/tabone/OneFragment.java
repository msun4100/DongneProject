package kr.me.ansr.tab.mypage.myinterest.tabone;

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
import java.util.Random;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.BoardReportDialogFragment;
import kr.me.ansr.common.CustomDialogFragment;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.ReportFormDialogFragment;
import kr.me.ansr.common.event.ActivityResultEvent;
import kr.me.ansr.tab.board.BoardWriteActivity;
import kr.me.ansr.tab.board.detail.BoardDetailActivity;
import kr.me.ansr.tab.board.like.LikeInfo;
import kr.me.ansr.tab.board.one.BoardAdapter;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.board.one.OnItemClickListener;
import kr.me.ansr.tab.board.one.OnItemLongClickListener;
import kr.me.ansr.tab.board.reply.CommentThread;
import kr.me.ansr.tab.board.reply.ReplyResult;
import kr.me.ansr.tab.friends.detail.StatusInfo;
import kr.me.ansr.tab.mypage.PagerFragment;
import okhttp3.Request;

/**
 * Created by KMS on 2016-10-18.
 */
public class OneFragment extends PagerFragment{
    private static final String TAG = OneFragment.class.getSimpleName();

    RecyclerView recyclerView;
    BoardAdapter mAdapter;
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
        View view = inflater.inflate(R.layout.fragment_my_writing_one, container, false);

        emptyLayout = (RelativeLayout) view.findViewById(R.id.rl_empty);
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
                    if(word != null && !word.equals("") && word.length() > 0){
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
                selectedItem = data;
                Intent intent = new Intent(getActivity(), BoardDetailActivity.class);
//                intent.putExtra(BoardInfo.BOARD_DETAIL_OBJECT, data);
                intent.putExtra(BoardInfo.BOARD_DETAIL_BOARD_ID, data.boardId);
                intent.putExtra(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, position);
                intent.putExtra("currentTab", "0"); //재학생 탭 == 0
                getActivity().startActivityForResult(intent, BoardInfo.BOARD_RC_NUM); //tabHost가 있는 BoardFragment에서 리절트를 받음
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
                        selectedItem = data;
                        BoardReportDialogFragment mDialogFragment = BoardReportDialogFragment.newInstance();
                        Bundle b = new Bundle();
                        b.putSerializable("boardInfo", data);
                        b.putString("tag", BoardReportDialogFragment.TAG_TAB_MY_WRITING_ONE);
                        mDialogFragment.setArguments(b);
                        mDialogFragment.setTargetFragment(OneFragment.this, DIALOG_RC_NUM);
                        mDialogFragment.show(getActivity().getSupportFragmentManager(), "boardReportDialog");
                        break;
                    case 400:
                        Toast.makeText(getActivity(), "like view click:\n"+data.likes.toString(), Toast.LENGTH_SHORT).show();
                        int likeMode =2;    //likeMode가 2면 요청 안하고 리턴
                        int mUserId = Integer.valueOf(PropertyManager.getInstance().getUserId());
                        if(data.likes.contains(mUserId)) likeMode = LikeInfo.DISLIKE; else likeMode = LikeInfo.LIKE;
                        String to = ""+data.writer;
                        postLike(likeMode, String.valueOf(data.boardId), PropertyManager.getInstance().getUserId(), to, position);
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
                b.putString("tag", BoardReportDialogFragment.TAG_TAB_MY_WRITING_ONE);
                mDialogFragment.setArguments(b);
                mDialogFragment.setTargetFragment(OneFragment.this, DIALOG_RC_NUM);
                mDialogFragment.show(getActivity().getSupportFragmentManager(), "boardReportDialog");
            }
        });
        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new BoardDecoration(getActivity()));
//        search views
        searchInput = (CustomEditText)view.findViewById(R.id.custom_editText1);
        searchIcon = (ImageView)view.findViewById(R.id.image_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                word = searchInput.getText().toString();
                if(!word.equals("") && word != null){
                    searchInput.setText(word + " (취소:검색어 초기화 후 검색 버튼 클릭.)");
                    start = 0;
                    reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                    searchBoard();
                } else {
                    word = "";
                    start = 0;
                    reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                    initBoard();
                }
            }
        });

        start = 0;
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        initBoard();
        return view;
    }

    private void initBoard(){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        String userId = PropertyManager.getInstance().getUserId();
        if(mUnivId == ""){
            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
            mUnivId = "1";
            return;
        }
        NetworkManager.getInstance().postDongneBoardListMyInterest(getActivity(),
                mUnivId,
                userId,
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
                        showLayout();
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(getActivity(), TAG + "Board init() onFailure:" + cause, Toast.LENGTH_LONG).show();
                        showLayout();
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
        String userId = PropertyManager.getInstance().getUserId();
        if(mUnivId == ""){
            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
            mUnivId = "1";
            return;
        }
        NetworkManager.getInstance().postDongneBoardListMyInterestSearch(getActivity(),
                mUnivId,
                userId,
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
                        showLayout();
                        dialog.dismiss();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(getActivity(), TAG + " Board search onFailure:" + cause, Toast.LENGTH_LONG).show();
                        showLayout();
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
    private static final int DISPLAY_NUM = 100;
    private int start=0;
    private String reqDate = null;

    private void getMoreItem(){
        if (isMoreData) return;
        isMoreData = true;
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount()) {
            NetworkManager.getInstance().postDongneBoardListMyInterest(getActivity(),
                    PropertyManager.getInstance().getUnivId(),
                    PropertyManager.getInstance().getUserId(),
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
            dialog.setTitle("Board List Loading....");
            dialog.show();
        }
    }

    private void getMoreSearchItem(){
        if (isMoreData || word == null || word.equals("")) return;
        isMoreData = true;
        String tab = "0";
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount()) {
            NetworkManager.getInstance().postDongneBoardListMyInterestSearch(getActivity(),
                    PropertyManager.getInstance().getUnivId(),
                    PropertyManager.getInstance().getUserId(),
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    word,
                    new NetworkManager.OnResultListener<BoardInfo>() {
                        @Override
                        public void onSuccess(Request request, BoardInfo result) {
                            Log.e(TAG+"getMoreSearch:", ""+result.message);
                            if(!result.message.equals("HAS_NO_BOARD_ITEM")){
                                Log.e(TAG+"getMoreSearch:", result.result.toString());
                                ArrayList<CommentThread> comment = result.comment;
                                Log.e(TAG+"getMoreSearch-comment:", comment.toString());
                                mAdapter.addAll(result.result);
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

    private void findOneAndModify(BoardResult br, String mode){
        if(mAdapter == null || mAdapter.getItemCount() < 1) return;
        mAdapter.findOneAndModify(br);
        switch (mode){
            case "_REMOVE_":
                break;
            case "_EDIT_":
                break;
            default:break;
        }
    }


    public static final int DIALOG_RC_NUM = 301;
    public static final int DIALOG_RC_REPORT = 302;
    public static final int DIALOG_RC_DELETE = 303;
    public BoardResult selectedItem = null;

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
                    Log.e("MyOne", "result_ok");
                    ModifiedSetItem(position, result);
                    break;
                }
            case BoardWriteActivity.BOARD_WRITE_RC_NEW:
                refreshList();
                break;
            case BoardWriteActivity.BOARD_WRITE_RC_EDIT:
                if(resultCode == getActivity().RESULT_OK){
                    extraBundle = data.getExtras();
                    BoardResult br = (BoardResult)extraBundle.getSerializable("mItem");
                    findOneAndModify(br, "_EDIT_");
                }
                break;
            case DIALOG_RC_NUM:
                if(resultCode == getActivity().RESULT_OK){
                    extraBundle = data.getExtras();
                    String next = extraBundle.getString("next");
                    if(next == null || next.equals("")) break;
                    switch (next){
                        case "_REPORT_":
                            ReportFormDialogFragment mDialogFragment = ReportFormDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", ReportFormDialogFragment.TAG_TAB_MY_WRITING_ONE);
                            b.putSerializable("fItem", null);
                            b.putSerializable("bItem", selectedItem);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.setTargetFragment(OneFragment.this, DIALOG_RC_REPORT);
                            mDialogFragment.show(getActivity().getSupportFragmentManager(), "reportFormDialog");
                            Log.e("childOne", "report");
                            break;
                        case "_EDIT_":
                            Log.e("childOne", "edit");
                            Intent intent = new Intent(getActivity(), BoardWriteActivity.class);
                            intent.putExtra("currentTab", "0"); // 0 = 재학생, 1 = 졸업생
                            intent.putExtra("type", "edit");
                            intent.putExtra("mItem", selectedItem);
                            getActivity().startActivityForResult(intent, BoardWriteActivity.BOARD_WRITE_RC_EDIT); //tabHost가 있는 BoardFragment에서 리절트를 받음
                            break;
                        case "_DELETE_":
                            CustomDialogFragment cDialogFragment = CustomDialogFragment.newInstance();
                            Bundle bb = new Bundle();
                            bb.putString("tag", CustomDialogFragment.TAG_TAB_MY_WRITING_ONE);
                            bb.putString("title","게시글을 삭제 하시겠습니까?");
                            bb.putString("body", "해당 게시글이 삭제됩니다.");
                            bb.putString("choice","delete");
                            bb.putSerializable("bItem", selectedItem);
                            cDialogFragment.setTargetFragment(OneFragment.this, DIALOG_RC_DELETE);
                            cDialogFragment.setArguments(bb);
                            cDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
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
    //EventBus의 post를 통해 ActivityResultEvent 를 매개변수로 받아서 현재 프래그먼트의 (오버라이드한)onActivityResult를 호출
    //register/unregister 하는 과정은 baseFragment인 PagerFragment에서
    @Subscribe
    public void onEvent(ActivityResultEvent activityResultEvent){
        Log.e("onEvent:", "MyOne");
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



    public OneFragment(){}
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
