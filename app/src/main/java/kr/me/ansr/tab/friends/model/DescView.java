package kr.me.ansr.tab.friends.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-08-05.
 */
public class DescView extends FrameLayout{
    public DescView(Context context) {
        super(context);
        init();
    }

    TextView descView;
    ImageView iconView;
    private void init() {
//        LayoutInflater.from(getContext()).inflate(R.layout.view_profile_desc_layout, this);
        inflate(getContext(), R.layout.view_profile_desc_layout, this);
//        iconView = (ImageView)findViewById(R.id.image_profile_desc_icon);
        descView = (TextView)findViewById(R.id.text_profile_desc);
    }

    public void setData(Desc data) {
        descView.setText(data.desc);
//        iconView.setImageResource(R.drawable.ic_stub);
    }
}
