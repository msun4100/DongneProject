package kr.me.ansr.tab.friends.recycler;

import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;

import kr.me.ansr.common.InputDialogFragment;
import kr.me.ansr.common.event.ActivityResultEvent;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.common.ReportDialogFragment;
import kr.me.ansr.common.event.FriendsFragmentResultEvent;
import kr.me.ansr.tab.friends.FriendsFragment;
import kr.me.ansr.tab.friends.MySpinnerAdapter;
import kr.me.ansr.tab.friends.PagerFragment;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.detail.StatusInfo;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-20.
 */
public class FriendsSectionFragment extends PagerFragment
{
    private static final String TAG = FriendsSectionFragment.class.getSimpleName();
    public static final int FRIENDS_RC_NUM = 199;
    public static final int DIALOG_RC_NUM = 200;
    public static final int DIALOG_RC_CUT_OFF = 201;
    public static final int DIALOG_RC_BLOCK = 202;
    public static final int DIALOG_RC_REPORT = 203;
    public static final int DIALOG_RC_SEND_REQ = 204;

    private static final String F1_TAG = "searchTab";

    AppCompatActivity activity;

    RecyclerView recyclerView;
    public static SectionAdapter mAdapter;
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
    InputMethodManager imm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_section, container, false);
        activity = (AppCompatActivity) getActivity();
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                }, 1000);
            }
        });   //this로 하려면 implements 하고 오버라이드 코드 작성하면 됨.
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Log.e(TAG+"onstatechanged","aaaaaaaa");
                if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onScrollStateChanged: isSearching" + isSearching);
                    if(isSearching == true){
                        getMoreSearchUsers(username, enterYear, deptname, job);
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
                Log.e("lastVisibleItemPosition",""+lastVisibleItemPosition);
                Log.e("222",""+(lastVisibleItemPosition != RecyclerView.NO_POSITION));
                Log.e("333",""+(totalItemCount - 1 <= lastVisibleItemPosition));
                if (totalItemCount > 0 && lastVisibleItemPosition != RecyclerView.NO_POSITION && (totalItemCount - 1 <= lastVisibleItemPosition)) {
                    isLast = true;
                } else {
                    isLast = false;
                }
                Log.e("444", ""+isLast);
            }
        });
        mAdapter = new SectionAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                selectedItem = data;    //디테일에서 관리 누를 경우사용될 변수
                Log.e("sectionFragment->data", data.toString());
                Intent intent = new Intent(getActivity(), FriendsDetailActivity.class);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
                intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, position);
                intent.putExtra("tag", InputDialogFragment.TAG_FRIENDS_DETAIL);
                getParentFragment().startActivityForResult(intent, FriendsSectionFragment.FRIENDS_RC_NUM); //tabHost가 있는 FriendsFragment에서 리절트를 받음
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
                selectedItem = data;
                ReportDialogFragment mDialogFragment = ReportDialogFragment.newInstance();
                Bundle b = new Bundle();
//                b.putString("tag", ReportDialogFragment.TAG_BOARD_WRITE);
                b.putSerializable("userInfo", data);
                b.putString("tag", ReportDialogFragment.TAG_TAB_ONE_UNIV);
                mDialogFragment.setArguments(b);
                mDialogFragment.setTargetFragment(FriendsSectionFragment.this, DIALOG_RC_NUM);
                mDialogFragment.show(getActivity().getSupportFragmentManager(), "reportDialog");
            }
        });
        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(getActivity()));

        start = 0;
//        reqDate = getCurrentTimeStamp();
        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
//        initUnivUsers();  //spinner 리스너에서 호출. 최초 0번째 position 리스너가 호출되기 때문에 initUnivUsers() 호출이 중복 됨.

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
                if(isSearching == true){
                    frameSearch.setVisibility(View.GONE);
                    searchIcon.setImageResource(R.drawable.b_list_search_icon_selector);
//                    initUnivUsers();
                    restoreList();
                    isSearching = false;
                    clearSearchInput();
                } else if(isSearching == false){
                    setContainerVisibility();
                }
            }
        });

        inputName = (EditText)view.findViewById(R.id.edit_search_name);
        inputYear = (EditText)view.findViewById(R.id.edit_search_enteryear);
        inputDept = (EditText)view.findViewById(R.id.edit_search_dept);
        inputJob = (EditText)view.findViewById(R.id.edit_search_job);
        inputJob.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchProcess();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        confirmView = (TextView)view.findViewById(R.id.text_search_confirm);
        confirmView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                searchProcess();
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        cancelView = (TextView)view.findViewById(R.id.text_search_cancel);
        cancelView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                clearSearchInput(); //또는 아래 코드처럼 검색값 입력창 닫히면서 기본값 정렬되게?
                mSearchOption = spinner.getSelectedItemPosition();
                start = 0;
                if(true){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isSearching = false;
                            searchIcon.setImageResource(R.drawable.b_list_search_icon_selector);
                            setContainerVisibility();
                            restoreList();
//                            initUnivUsers();
                        }
                    }, 500);
                }
            }
        });

        return view;
    }

    public boolean isSearching = false;
    public String username = null;
    public String enterYear = null;
    public String deptname = null;
    public String job = null;

    EditText inputName, inputYear, inputDept, inputJob;
    TextView confirmView, cancelView;
    public String headerString = null;
    public void searchProcess(){
        username = inputName.getText().toString();
        enterYear = inputYear.getText().toString();
        deptname = inputDept.getText().toString();
        job = inputJob.getText().toString();
        if(username.equals("") && enterYear.equals("") && deptname.equals("") && job.equals("")){
            Toast.makeText(getActivity(), "검색 조건을 하나 이상 입력해주세요", Toast.LENGTH_SHORT).show();
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
        if(!TextUtils.isEmpty(job)){
            headerString += "회사/직무:" + job +", ";
        }
        if(!TextUtils.isEmpty(headerString)){
            headerString = headerString.substring(0, headerString.length() - 2);
        }

        Log.e("s_username", username);
        Log.e("s_enterYear", enterYear);
        Log.e("s_deptname", deptname);
        Log.e("s_job", job);

        storeList();    //기존 데이타 백업
        mSearchOption = 0; //이름정렬
        start = 0;
        if(true){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isSearching = true;
                    searchIcon.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
                    setContainerVisibility();
                    initSearchUsers(username, enterYear, deptname, job);
                }
            }, 500);
        }
    }

    public void clearSearchInput(){
        inputName.setText(""); inputYear.setText(""); inputDept.setText(""); inputJob.setText("");
        username = null; enterYear = null; deptname = null; job = null;
    }
    public void setContainerVisibility(){
        if(frameSearch == null){
            return;
        }
        if(frameSearch.getVisibility() == View.VISIBLE){
//            Fragment f = getActivity().getSupportFragmentManager().findFragmentByTag(F1_TAG);
//            if(f != null){
//                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                ft.remove(f);
//                ft.commit();
//            }
            frameSearch.setVisibility(View.GONE);
        } else {
//            searchIcon.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
//            Bundle bundle = new Bundle();
//            bundle.putString("filePath", "");
//            Fragment f = getActivity().getSupportFragmentManager().findFragmentByTag(F1_TAG);
//            if (f == null) {
//                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                SearchFragment newFragment = SearchFragment.newInstance();
//                newFragment.setArguments(bundle);
////                ft.setCustomAnimations(R.anim.slide_top_in, R.anim.slide_top_out);
//                ft.replace(R.id.containerforsearch, newFragment, F1_TAG);
//                ft.commit();
//            }
            frameSearch.setVisibility(View.VISIBLE);
        }
    }
    private void initData() {
        String[] searchArray = getResources().getStringArray(R.array.search_items);
        for (int i = 0; i < searchArray.length; i++) {
            mySpinnerAdapter.add(searchArray[i]);
        }
        spinner.setSelection(0);
//        initUnivUsers();
    }
    private void initUnivUsers(){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        if(mUnivId.equals("")){
            Toast.makeText(getActivity(),"대학교를 등록해주세요.", Toast.LENGTH_SHORT).show();
//            return;
            mUnivId = ""+1; //테스트 위해 임시로 (회원가입안하고 테스트 해보기 위해)
            Log.d(TAG, "initUnivUsers: "+ "대학교를 등록해주세요.");
            return;
        }
//        reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        NetworkManager.getInstance().postDongneUnivUsers(getActivity(),
                0,//mode
                null,   //친구리스트 보기에서만 userId를 지정
                mSearchOption,//req.body.sort
                mUnivId,
                ""+start,
                ""+DISPLAY_NUM,
                ""+reqDate,
                new NetworkManager.OnResultListener<FriendsInfo>() {
                    @Override
                    public void onSuccess(Request request, FriendsInfo result) {
//                        if(!result.message.equals("HAS_NO_BOARD_ITEM")  && result.result != null ){
                        int mTotal = 0;
                        if (result.error.equals(false)) {
                            if(result.result != null){
                                mAdapter.blockCount = 0;
                                mAdapter.items.clear();
                                mAdapter.setTotalCount(result.total);
                                setTabTotalCount(result.total);
                                ArrayList<FriendsResult> items = result.result;

                                if(PropertyManager.getInstance().getNewCount() < result.total){
                                    MainActivity.setUserCount(result.total - PropertyManager.getInstance().getNewCount());
                                    PropertyManager.getInstance().setNewCount(result.total);
                                }
                                if(result.user != null){
                                    Log.e(TAG+" user:", result.user.toString());
                                    result.user.status = 1; //내프로필 이름 표시되게
                                    //last update 값이 다르면 프로퍼티매니저에 새로 저장.
                                    if(!PropertyManager.getInstance().getLastUpdate().equals(result.user.updatedAt)){
                                        PropertyManager.getInstance().storeUser(result.user);
                                    }
                                    if(result.user.job != null){
                                        if(result.user.job.name != null) {
                                            PropertyManager.getInstance().setJobName(result.user.job.name);
                                        }
                                        if(result.user.job.team != null) {
                                            PropertyManager.getInstance().setJobTeam(result.user.job.team);
                                        }
                                    }
                                    mAdapter.put("내 프로필", result.user);
                                }
                                for(int i=0; i < items.size(); i++){
                                    FriendsResult child = items.get(i);
                                    Log.e(TAG, ""+child);
                                    if(child.status == 3){
                                        //block 관계면 리스트엔 보여주지 않고 아답터 토탈 카운트는 증가 시키기 위해.
                                        //나중에 children.size() + blockCount로 토탈카운트를 리턴함.
                                        //현시점에서 겟모어 하는 리스너봤을때 큰 문제가 없을 것 같음.
                                        //아답터의 갯수보다는 리스트에 보이는 갯수와 포지션으로 호출 하니까
                                        //겟모어 할때 (result.total == mAdapter.getTotalCount()) 와 비교할때
                                        //mAdapter.getItemCount()와 block 카운트를 더해서 비교 하면 기존 토탈과 갯수를 맞춰 비교할 수 있음.
                                        mAdapter.blockCount++;
                                        continue;
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
    private String searchReqDate = null;


    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount() + mAdapter.blockCount ) {
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > (mAdapter.getItemCount() + mAdapter.blockCount) ) {
            NetworkManager.getInstance().postDongneUnivUsers(getActivity(),
                    0, //mode
                    null,   //친구리스트 보기에서만 userId를 지정
                    mSearchOption, //sort
                    PropertyManager.getInstance().getUnivId(),
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+reqDate,
                    new NetworkManager.OnResultListener<FriendsInfo>() {
                        @Override
                        public void onSuccess(Request request, FriendsInfo result) {
                            Log.e(TAG+"getMore:", result.result.toString());
//                            mAdapter.addAllFriends(result.result);
                            //기존 getMore의 addAll 대신에 포문 사용(블락친구 핸들링 위해)
                            ArrayList<FriendsResult> items = result.result;
                            for(int i=0; i < items.size(); i++){
                                FriendsResult child = items.get(i);
                                Log.e(TAG, ""+child);
                                if(child.status == 3){
                                    mAdapter.blockCount++;
                                    continue;
                                }
                                mAdapter.put("학교 사람들", child);
                            }
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

    private void ModifiedSetItem(int position, FriendsResult result){
        if(position == -1 || result == null) return;
//        mAdapter.getItem(position).status = result.status;
        mAdapter.getItem(position).status = result.status;
        mAdapter.getItem(position).desc = result.desc;
        mAdapter.getItem(position).sns = result.sns;
        mAdapter.getItem(position).job = result.job;

        mAdapter.notifyDataSetChanged();
    }

    private void setTabTotalCount(int total){
        if(FriendsFragment.totalUniv != null){
            FriendsFragment.totalUniv.setText(""+(++total));
        }
    }

    private void refreshList(){
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mAdapter.clearAll();
                start = 0;
                reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                initUnivUsers();
            }
        }, 500);
    }
    static public FriendsResult getUserInfo(){
        //mypage에서 mItem가져오기 위해
        if(mAdapter != null && mAdapter.getItemCount() >= 2){
            FriendsResult item = mAdapter.getItem(1);
//            Log.d(TAG, "getUserInfo: cnt"+ mAdapter.getItemCount());
            return item;
        }
        return null;
    }


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

    private void initSearchUsers(String username, String enterYear, String deptname, String job){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        searchReqDate = MyApplication.getInstance().getCurrentTimeStampString();
        NetworkManager.getInstance().postDongneUnivUsersSearch(getActivity(),
                0,//mode
                mSearchOption,//req.body.sort
                mUnivId,
                ""+start,
                ""+DISPLAY_NUM,
                ""+searchReqDate,
                username,
                enterYear,
                deptname,
                job,
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

    private void getMoreSearchUsers(String username, String enterYear, String deptname, String job){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        if (isMoreData) return;
        isMoreData = true;

        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount() + mAdapter.blockCount ) {
//        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > (mAdapter.getItemCount() + mAdapter.blockCount) ) {
            NetworkManager.getInstance().postDongneUnivUsersSearch(getActivity(),
                    0,//mode
                    mSearchOption,//req.body.sort
                    mUnivId,
                    ""+start,
                    ""+DISPLAY_NUM,
                    ""+searchReqDate,
                    username,
                    enterYear,
                    deptname,
                    job,
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
        }
    }
    //EventBus의 post를 통해 ActivityResultEvent 를 매개변수로 받아서 현재 프래그먼트의 (오버라이드한)onActivityResult를 호출
    //register/unregister 하는 과정은 baseFragment인 PagerFragment에서
    @Subscribe
    public void onEvent(FriendsFragmentResultEvent activityResultEvent){
        Log.e("onEvent:", "FriendsSection event");
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
    }
    @Subscribe
    public void onEvent(FriendsResult fr){
        Log.e("onEvent:", "FriendsSection fr");
        findOneAndModify(fr);
    }

    public FriendsSectionFragment() {
        // Required empty public constructor
    }
    //프랜드프래그먼트의 커스텀뷰페이저 오버라이드
    @Override
    public void onPageCurrent() {
        super.onPageCurrent();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void restoreList(){
        start = FriendsDataManager.getInstance().getStart();
        reqDate = FriendsDataManager.getInstance().getReqDate();
        mSearchOption = FriendsDataManager.getInstance().getmSearchOption();
        spinner.setSelection(mSearchOption);
        mAdapter.clearAll();
        mAdapter.setTotalCount(FriendsDataManager.getInstance().getTotalCount());
        mAdapter.addAll(FriendsDataManager.getInstance().getList());
        int lastVisibleItemPosition = FriendsDataManager.getInstance().getLastVisibleItemPosition();
//        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, lastVisibleItemPosition );
        recyclerView.scrollToPosition(lastVisibleItemPosition);
        FriendsDataManager.getInstance().clearAll();
    }

    private void storeList() {
        //기존 데이타 백업
        FriendsDataManager.getInstance().setmSearchOption(mSearchOption);
        //+3해주는건 검색필터 입력창이 열릴때 lastVisiblePosition이 3~4정도 줄어듬
        FriendsDataManager.getInstance().setLastVisibleItemPosition(layoutManager.findLastCompletelyVisibleItemPosition() + 3);
        FriendsDataManager.getInstance().setTotalCount(mAdapter.getTotalCount());
        FriendsDataManager.getInstance().setStart(start);
        FriendsDataManager.getInstance().setReqDate(reqDate);
        FriendsDataManager.getInstance().clearAll();
        FriendsDataManager.getInstance().addAll(mAdapter.getGroupItems());
    }
}
