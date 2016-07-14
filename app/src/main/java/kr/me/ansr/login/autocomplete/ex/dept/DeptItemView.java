package kr.me.ansr.login.autocomplete.ex.dept;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-07-14.
 */
public class DeptItemView extends FrameLayout{
    public DeptItemView(Context context) {
        super(context);
        init();
    }

    TextView deptnameView, totalView;
    private void init() {
        inflate(getContext(), R.layout.view_item_auto_complete, this);
        deptnameView = (TextView)findViewById(R.id.text_univname);
        totalView = (TextView)findViewById(R.id.text_total);
    }

    public void setData(DeptResult data) {
        deptnameView.setText(""+data.univId + " " +data.deptname);
//        deptnameView.setText(data.deptname);
        totalView.setText(""+data.total);
    }
}

