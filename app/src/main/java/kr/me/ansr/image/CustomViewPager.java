package kr.me.ansr.image;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by KMS on 2016-12-02.
 * Viewpager에 PhotoView를 이용하여 슬라이드 구현할 때, viewpager와 photoview간의 버그
 * java.lang.IllegalArgumentException: pointerIndex out of range 에러발생시 해결
 * --> viewpager를 상속받는 CustomViewPager class를 생성한 뒤 예외처리.
 */
public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        try {
            return super.onTouchEvent(me);
        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent me) {
        try {
            return super.onInterceptTouchEvent(me);
        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
        }
        return false;
    }

}
