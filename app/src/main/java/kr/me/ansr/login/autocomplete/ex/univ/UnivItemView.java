package kr.me.ansr.login.autocomplete.ex.univ;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.me.ansr.R;

/**
 * Created by dongja94 on 2016-01-18.
 */
public class UnivItemView extends FrameLayout {
    public UnivItemView(Context context) {
        super(context);
        init();
    }

//    ImageView iconView;
    TextView univIdView, univnameView, totalView;
    private void init() {
        inflate(getContext(), R.layout.view_item_auto_complete, this);
//        iconView = (ImageView)findViewById(R.id.image_icon);
        univnameView = (TextView)findViewById(R.id.text_univname);
        totalView = (TextView)findViewById(R.id.text_total);
//        univIdView = (TextView)findViewById(R.id.text_univId);
    }

    public void setData(UnivResult data) {
//        iconView.setImageDrawable(data.icon);
        univnameView.setText(data.univname);
        totalView.setText(""+data.total);
//        univIdView.setText(""+data.univId);
    }
}
