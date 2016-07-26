package kr.me.ansr.tab.board.two;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import kr.me.ansr.R;
import kr.me.ansr.tab.board.PagerFragment;

/**
 * Created by KMS on 2016-07-25.
 */
public class ChildTwoFragment extends PagerFragment {

//    MyAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_two, container, false);

        Toast.makeText(getActivity(),"Two create", Toast.LENGTH_SHORT).show();

        initData();
        return view;
    }

    private void initData() {

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public ChildTwoFragment(){}
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