package kr.me.ansr.tab.board.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.BoardReportDialogFragment;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.board.CommentInfo;
import kr.me.ansr.tab.board.like.LikeInfo;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.board.reply.CommentThread;
import kr.me.ansr.tab.board.reply.ReplyResult;
import okhttp3.Request;

public class BoardDetailActivity extends AppCompatActivity implements IDataReturned
{
    private static final String TAG = BoardDetailActivity.class.getSimpleName();
    TextView toolbarTitle;
    ImageView toolbarMenu;

    ProgressDialog dialog = null;
    public BoardResult mItem = null;
    private CommentThread ct;
    private int reqBoardId, mPosition;

    ImageView iconThumb;
    TextView nameView;
    TextView stuIdView;
    TextView deptView;
    TextView timeStampView;

    TextView bodyView;
    ImageView iconReply;
    TextView replyCountView;
    ImageView iconLike;
    TextView likeCountView;
    ImageView bodyImage;

    ListView listView;
    DetailReplyAdapter mAdapter;
    LinearLayout likeLayout;
    //reply input Views
    EditText inputReply;
    CheckBox checkBox;
    String currentTab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.e__titlebar_2));
        getSupportActionBar().setElevation(0);	//6.0이상 음영효과 제거
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("");
        toolbarMenu = (ImageView)toolbar.findViewById(R.id.toolbar_menu1);
        toolbarMenu.setImageResource(R.drawable.e__more_2_modify);
        toolbarMenu.setOnClickListener(viewListener);
        Intent intent = getIntent();
        if (intent != null) {
//            mItem = (BoardResult)intent.getSerializableExtra(BoardInfo.BOARD_DETAIL_OBJECT);
            reqBoardId = intent.getIntExtra(BoardInfo.BOARD_DETAIL_BOARD_ID, -1);
            mPosition = intent.getIntExtra(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, -1);
            currentTab = intent.getStringExtra("currentTab");
            if(reqBoardId == -1) forcedFinish();
        } else {
            forcedFinish();
        }
        fetchViews();
        mAdapter.setOnAdapterItemClickListener(new DetailReplyAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(DetailReplyAdapter adapter, View view, ReplyResult item, int type) {
                switch (type){
                    case 100:
                        Toast.makeText(BoardDetailActivity.this,"reply name click", Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(BoardDetailActivity.this,"reply body click", Toast.LENGTH_SHORT).show();
                        break;
                    case 300:
                        mAdapter.setLike(item, Integer.valueOf(PropertyManager.getInstance().getUserId()));
                        Toast.makeText(BoardDetailActivity.this, ""+item._id, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReplyResult data = (ReplyResult)listView.getItemAtPosition(position);
                Toast.makeText(BoardDetailActivity.this,"onItem click"+data.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        listView.setAdapter(mAdapter);

        likeLayout.setOnClickListener(viewListener);
        bodyView.setOnClickListener(viewListener);

        //implements reply
        inputReply = (EditText)findViewById(R.id.edit_detail_input);
        checkBox = (CheckBox)findViewById(R.id.check_detail_anonymous);
        checkBox.setChecked(false);
        Button btn = (Button)findViewById(R.id.btn_detail_send);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendReply();
            }
        });

        initData();
    }
    private void sendReply(){
        final String content = inputReply.getText().toString();
        if(content.equals("") || content == null){
            return;
        }
        final String type = getBoardType();
        String to = ""+mItem.writer;
        NetworkManager.getInstance().postDongneCommentAdd(this, reqBoardId, mItem.commentId, content, type, to, new NetworkManager.OnResultListener<CommentInfo>() {
            @Override
            public void onSuccess(Request request, CommentInfo result) {
                if(result.error.equals(false)){
                    inputReply.setText("");
//                    int userId = Integer.valueOf(PropertyManager.getInstance().getUserId());
//                    String username = PropertyManager.getInstance().getUserName();
//                    ReplyResult rr = new ReplyResult("", content, userId, username, type, new ArrayList<Integer>(), 0, new ArrayList<ReplyResult>(), MyApplication.getInstance().getCurrentTimeStampString());
                    if(result.result != null) {
                        if(mAdapter.getCount() == 0){
                            listView.setVisibility(View.VISIBLE);
                        }
                        mAdapter.add(result.result);
                        mItem.repCount = mAdapter.getCount();   //디테일 빠져나갔을 때 갱신위해
                        int cnt = Integer.valueOf(replyCountView.getText().toString());
                        replyCountView.setText(""+(cnt+1));

                        if (mAdapter.getCount() > 1) {
                            listView.smoothScrollToPosition(mAdapter.getCount() - 1);
                        }
                    }
                } else {
                    inputReply.setText("");
                    Toast.makeText(BoardDetailActivity.this, "error: true" +  result.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(BoardDetailActivity.this, "onFailure:"+ cause, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData(){
        NetworkManager.getInstance().getDongneBoardDetail(this, reqBoardId, new NetworkManager.OnResultListener<BoardInfo>() {
            @Override
            public void onSuccess(Request request, BoardInfo result) {
                if(result.error.equals(false)){
                    if(!result.message.equals("DOCS_LENGTH_ERROR")  && result.result != null ){
                        Log.e(TAG, result.comment.toString());
                        if (result.comment != null && result.comment.size() >= 1) {
                            ct = result.comment.get(0);
//                            mAdapter.setTotalCount(result."repTotal");
                            replyCountView.setText("" + ct.replies.size());
                            listView.setVisibility(View.VISIBLE);
                            mAdapter.clear();
                            mAdapter.addAll(ct.replies);
                        } else {
                            replyCountView.setText("0");
                            listView.setVisibility(View.GONE);
                            mAdapter.clear();
                        }

                        if (mAdapter.getCount() > 1) {
                            listView.setSelection(mAdapter.getCount() - 1);
                        }
                        mItem = result.result.get(0);
                        mItem.repCount = mAdapter.getCount();   //client용 repCount 초기화
                        setBoardItem(mItem);
                    } else {
                        //DOCS_LENGTH_ERROR;
                        Toast.makeText(getApplicationContext(), ""+result.message, Toast.LENGTH_SHORT).show();
                        forcedFinish();
                    }
                } else {
                    Toast.makeText(BoardDetailActivity.this, "error: true" + result.message, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(BoardDetailActivity.this, "onFailure" + cause, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading....");
        dialog.show();
    }

    public void setBoardItem(BoardResult item) {
//        titleView.setTextSize(item.fontSize);
        this.mItem = item;
        if(item.type.equals("00") || item.type.equals("10")){
            nameView.setText(getResources().getString(R.string.board_anonymous_name));
            iconThumb.setImageResource(R.drawable.e__who_icon);
        } else {
            nameView.setText(item.user.username);
            String url = Config.FILE_GET_URL.replace(":userId", ""+item.writer).replace(":size", "small");
            Glide.with(this).load(url).placeholder(R.drawable.e__who_icon).centerCrop().signature(new StringSignature(item.updatedAt)).into(iconThumb);
        }
        if (item.pic.size() > 0){
            bodyImage.setVisibility(View.VISIBLE);
            String url = Config.BOARD_FILE_GET_URL.replace(":imgKey", ""+item.pic.get(0));
            Glide.with(this).load(url).centerCrop().signature(new StringSignature(item.updatedAt)).into(bodyImage);
        } else {
            bodyImage.setVisibility(View.GONE);
        }

        bodyView.setText(item.body);
        deptView.setText(item.user.deptname);

        String stuId = String.valueOf(item.user.enterYear);
        if(stuId.length()==4){
            stuIdView.setText(stuId.substring(2,4));    //2016 --> 16
        } else {
            stuIdView.setText("17");
        }

        timeStampView.setText(MyApplication.getTimeStamp(item.createdAt));
        likeCountView.setText(""+item.likeCount);
    }

    public View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                BoardResult data = mAdapter.getItem(position);
                case R.id.linear_like_layout:
                    int likeMode =2;    //likeMode가 2면 요청 안하고 리턴
                    int mUserId = Integer.valueOf(PropertyManager.getInstance().getUserId());
                    if(mItem.likes.contains(mUserId)) likeMode = LikeInfo.DISLIKE; else likeMode = LikeInfo.LIKE;
                    String to = ""+mItem.writer;
                    postLike(likeMode, String.valueOf(mItem.boardId), PropertyManager.getInstance().getUserId(), to, Integer.MAX_VALUE);
                    break;
                case R.id.text_board_body:
                    Toast.makeText(BoardDetailActivity.this,"detail body click", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.toolbar_menu1:
                    BoardReportDialogFragment mDialogFragment = BoardReportDialogFragment.newInstance();
                    Bundle b = new Bundle();
                    b.putSerializable("boardInfo", mItem);
                    b.putString("tag", BoardReportDialogFragment.TAG_BOARD_DETAIL);
                    mDialogFragment.setArguments(b);
                    mDialogFragment.show(getSupportFragmentManager(), "boardReportDialog");
                    break;
                default:
                    break;
            }
        }
    };

    private void postLike(int like, String boardId, final String userId, String to, final int position){
        if(like>1){Toast.makeText(BoardDetailActivity.this,"invalid like type",Toast.LENGTH_SHORT).show(); return;}
        if(boardId == null || userId == null) { Toast.makeText(BoardDetailActivity.this,"arg is null",Toast.LENGTH_SHORT).show(); return; }
        NetworkManager.getInstance().postDongneBoardLike(BoardDetailActivity.this, like, boardId, userId, to, new NetworkManager.OnResultListener<LikeInfo>() {
            @Override
            public void onSuccess(Request request, LikeInfo result) {
                if(result.error.equals(false)){
//                    Toast.makeText(BoardDetailActivity.this,""+result.message, Toast.LENGTH_SHORT).show();
                    setLike(Integer.MAX_VALUE, Integer.valueOf(userId));    //MAX_VALUE == 'position is never used'
                } else {
                    //error: true
                    Toast.makeText(BoardDetailActivity.this,"error: true\n"+result.message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(BoardDetailActivity.this,"postLike() onFailure: "+cause, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setLike(int position, int userId){
        if(mItem.likes.contains(userId)){
            if(mItem.likeCount == 0) return;
            else {
                mItem.likeCount--;
                int idx = mItem.likes.lastIndexOf(userId);
                mItem.likes.remove(idx);

                likeCountView.setText(""+mItem.likes.size());
//                iconLike.setImageResource(R.drawable.b_main_view_contents_icon_05_off);
            }
        } else {
            mItem.likeCount++;
            mItem.likes.add(userId);    //add는 indexOf 하면 현재 배열에 없으니까 -1 리턴 됨.

            likeCountView.setText(""+mItem.likes.size());
//            iconLike.setImageResource(R.drawable.b_main_view_contents_icon_05_on);
        }
//        notifyDataSetChanged();
    }


    //==============================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            finishAndReturnData();
        }
        return super.onOptionsItemSelected(item);
    }
    private void fetchViews(){
        iconThumb = (ImageView)findViewById(R.id.image_board_thumb);
        nameView = (TextView)findViewById(R.id.text_board_name);
        stuIdView = (TextView)findViewById(R.id.text_board_stuid);
        deptView = (TextView)findViewById(R.id.text_board_dept);
        timeStampView = (TextView)findViewById(R.id.text_board_timestamp);
        bodyView = (TextView)findViewById(R.id.text_board_body);
//        iconReply = (ImageView)findViewById(R.id.image_board_reply);
        replyCountView = (TextView)findViewById(R.id.text_board_reply_count);
//        iconLike = (ImageView)findViewById(R.id.image_board_like);
        likeCountView = (TextView)findViewById(R.id.text_board_like_count);

        likeLayout = (LinearLayout)findViewById(R.id.linear_like_layout);
        bodyImage = (ImageView)findViewById(R.id.image_board_body);

        listView = (ListView)findViewById(R.id.listView_board);
        mAdapter = new DetailReplyAdapter(this);
    }

    private void finishAndReturnData(){
        if(mPosition != -1){
            //board tab에서 요청한 상세보기면 setResult해줌
            Intent intent = new Intent();
            intent.putExtra(BoardInfo.BOARD_DETAIL_MODIFIED_ITEM, mItem);
            intent.putExtra(BoardInfo.BOARD_DETAIL_MODIFIED_POSITION, mPosition);
            setResult(RESULT_OK, intent);//RESULT_OK를 BoardFragment의 온리절트에서 받음
            finish();
        } else {
            //새소식 탭에서 요청한 상세보기면
            finish();
        }


    }
    private void forcedFinish(){
        Toast.makeText(getApplicationContext(), "게시글을 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
        finish();
    }

    public static final String TYPE_ANONYMOUS = ""+0;
    public static final String TYPE_PUBLIC = ""+1;
    private String getBoardType(){
        String type = currentTab;
        if(checkBox.isChecked()){
            type += TYPE_ANONYMOUS; //체크했으면 비공개 type == "0"
        } else {
            type += TYPE_PUBLIC;
        }
        return type;
    }
    @Override
    public void onBackPressed() {
        finishAndReturnData();
        super.onBackPressed();
    }
    @Override
    public void onDataReturned(String choice) {
        //Use the returned value
        if(choice != null){
            Log.e("choice", choice);
            switch (choice){
                case "0":
                case "1":
                case "2":
                case "3":
                    break;
            }
        }
    }
}
