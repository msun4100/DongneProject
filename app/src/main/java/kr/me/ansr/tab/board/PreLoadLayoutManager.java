package kr.me.ansr.tab.board;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by KMS on 2016-11-18.
 */
public class PreLoadLayoutManager extends LinearLayoutManager {
    private final int mDisplayHeight;

    public PreLoadLayoutManager(Context context){
        super(context);
        mDisplayHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
//        return mDisplayHeight;
        return 300;
    }
}
