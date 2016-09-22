package kr.me.ansr.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-09-04.
 */
public class ColorButton extends AppCompatButton{
    public ColorButton(final Context context) {
        super(context);
        init();
    }

    public ColorButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
//        Drawable tempDrawable = ContextCompat.getDrawable(getContext(), R.drawable.abc_ic_clear_mtrl_alpha);
////        Drawable tempDrawable = ContextCompat.getDrawable(getContext(), R.drawable.e__cancle_2);
//        clearDrawable = DrawableCompat.wrap(tempDrawable);
//        DrawableCompat.setTintList(clearDrawable,getHintTextColors());
//        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());
//
//        super.setOnTouchListener(this);
//        super.setOnFocusChangeListener(this);
//        addTextChangedListener(this);
    }

}
