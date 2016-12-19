package kr.me.ansr.tab.friends.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-08-31.
 */
public class MySpinnerItemView extends FrameLayout{
    public MySpinnerItemView(Context context) {
        super(context);
        init();
    }

    public MySpinnerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    TextView tv;
    private void init(){
        LayoutInflater.from(getContext()).inflate( R.layout.view_item_enter_year_spinner_search, this);
        tv = (TextView) findViewById(R.id.text_spinner_enter_year);
    }
    public void setItemData(String text, int position){
        if(position == 0){
            tv.setTextColor(0xff9c9c9d);
        } else {
            tv.setTextColor(0xff252525);
        }
        tv.setText(text);
    }
    public void setTextColor(int color){
        tv.setTextColor(color);
    }

}
