package kr.me.ansr.tab.friends;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by KMS on 2016-10-04.
 */
public class FriendsViewPager extends ViewPager {
    public FriendsViewPager(Context context)
    {
        super(context);
    }
    public FriendsViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FriendsViewPager(Context context, AttributeSet attrs, boolean isSwipeEnable)
    {
        super(context, attrs);
        this.isSwipeEnable = isSwipeEnable;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0)
    {
        return isSwipeEnable;   //false return하면 스와이프 안됨
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return isSwipeEnable;   //false return하면 스와이프 안됨
    }

    boolean isSwipeEnable = false;

    public void setSwipeEnable(boolean isSwipeEnable){
        this.isSwipeEnable = isSwipeEnable;
    }
}
