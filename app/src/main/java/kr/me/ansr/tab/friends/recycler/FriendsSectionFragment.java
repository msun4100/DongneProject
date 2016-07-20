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

import java.util.Random;

import kr.me.ansr.PagerFragment;
import kr.me.ansr.R;

/**
 * Created by KMS on 2016-07-20.
 */
public class FriendsSectionFragment extends PagerFragment{

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
                ChildItem data = mAdapter.getItem(position);
                Toast.makeText(getActivity(), "data : " + data.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnAdapterItemClickListener(new SectionAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(SectionAdapter adapter, View view, ChildItem item, int type) {
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
                ChildItem data = mAdapter.getItem(position);
                Toast.makeText(getActivity(), "Long click data\n" + data.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
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
        mAdapter.put("내 프로필", new ChildItem(SectionAdapter.GROUP_PROFILE, true, "http://10.0.3.2:3000/getPic/0","user0","inha univ",16,"jobname"));
        recyclerView.addItemDecoration(new MyDecoration(getActivity()));
        for(int i=0; i<10; i++){
            boolean isFriend = true;
            if(i % 3 == 0) isFriend = false;
            mAdapter.put("학교 사람들", new ChildItem(SectionAdapter.GROUP_FRIENDS, isFriend, ""+ someUrls[r.nextInt(4)],("user"+i),"univ",i,"job"));

        }
    }

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
