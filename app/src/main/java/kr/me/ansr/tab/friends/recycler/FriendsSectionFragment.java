package kr.me.ansr.tab.friends.recycler;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import kr.me.ansr.NetworkManager;
import kr.me.ansr.PagerFragment;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.login.autocomplete.ex.univ.UnivInfo;
import kr.me.ansr.tab.friends.recycler.model.FriendsInfo;
import kr.me.ansr.tab.friends.recycler.model.FriendsResult;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-20.
 */
public class FriendsSectionFragment extends PagerFragment{
    private static final String TAG = FriendsSectionFragment.class.getSimpleName();
    AppCompatActivity activity;

    RecyclerView recyclerView;
    SectionAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_section, container, false);
        activity = (AppCompatActivity) getActivity();

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
        mAdapter = new SectionAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FriendsResult data = mAdapter.getItem(position);
                Toast.makeText(getActivity(), "data : " + data.toString(), Toast.LENGTH_SHORT).show();
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
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(getActivity()));
        initData();

        return view;
    }

    private void initData() {
        Random r = new Random();
        String [] someUrls = new String[4];
        someUrls[0]="http://10.0.3.2:3000/getPic/0";
        someUrls[1]="http://10.0.3.2:3000/getPic/1";
        someUrls[2]="http://10.0.3.2:3000/getPic/2";
        someUrls[3]="";
        getUnivUsers();
//        mAdapter.put("내 프로필", new ChildItem(SectionAdapter.GROUP_PROFILE, true, "http://10.0.3.2:3000/getPic/0","user0","inha univ",16,"jobname"));
//        recyclerView.addItemDecoration(new MyDecoration(getActivity()));
//        for(int i=0; i<10; i++){
//            boolean isFriend = true;
//            if(i % 3 == 0) isFriend = false;
//            mAdapter.put("학교 사람들", new ChildItem(SectionAdapter.GROUP_FRIENDS, isFriend, ""+ someUrls[r.nextInt(4)],("user"+i),"univ",i,"job"));
//        }
    }

    public void getUnivUsers(){
        String mUnivId = PropertyManager.getInstance().getUnivId();
        if(mUnivId == ""){
            Toast.makeText(getActivity(),"대학교 등록할 것", Toast.LENGTH_SHORT).show();
//            return;
            mUnivId = ""+ (0); //테스트 위해 임시로 (회원가입안하고 테스트 해보기 위해)
        }
        NetworkManager.getInstance().getDongneUnivUsers(getActivity(),
//                Integer.parseInt(PropertyManager.getInstance().getUnivId()),
                mUnivId,
                new NetworkManager.OnResultListener<FriendsInfo>() {
            @Override
            public void onSuccess(Request request, FriendsInfo result) {
                if (result.error.equals(false)) {
                    if(result.result != null){
                        ArrayList<FriendsResult> items = result.result;
                        for(int i=0; i < items.size(); i++){
                            FriendsResult child = items.get(i);
                            Log.e(TAG, ""+child);
                            if(i==0){
                                mAdapter.put("내 프로필", child); //내 정보 불러와서
                            }
                            mAdapter.put("학교 사람들", child);
                        }
                    }
                } else {
//                    mAdapter.clearAll();
                    Log.e(TAG, result.message);
                    Toast.makeText(getActivity(), TAG + "result.error: true\nresult.message:" + result.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, int code, Throwable cause) {

            }
        });
    }
//    ==============================
    @Override
    public void onPageCurrent() {
        super.onPageCurrent();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // ...
            if(activity != null){
                activity.getSupportActionBar().setTitle("Friends Fragment");
            }
        }
    }
}
