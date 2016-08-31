package kr.me.ansr.tab.friends;

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
        LayoutInflater.from(getContext()).inflate( R.layout.view_search_item, this);

        tv = (TextView) findViewById(R.id.text_search);
    }
    public void setItemData(String text){
        tv.setText(text);
    }
}
