package kr.me.ansr.login.autocomplete.ex;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import kr.me.ansr.R;

/**
 * Created by dongja94 on 2016-01-18.
 */
public class ItemView extends FrameLayout {
    public ItemView(Context context) {
        super(context);
        init();
    }

    ImageView iconView;
    TextView titleView;
    private void init() {
        inflate(getContext(), R.layout.view_item, this);
        iconView = (ImageView)findViewById(R.id.image_icon);
        titleView = (TextView)findViewById(R.id.text_title);
    }

    public void setData(MyData data) {
        iconView.setImageDrawable(data.icon);
        titleView.setText(data.title);
    }
}
