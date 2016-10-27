package kr.me.ansr.common.account;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kr.me.ansr.R;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-10-27.
 */
public class ConfirmDialogFragment extends DialogFragment{

    public static final String TAG_FIND_ID = "findID";
    public static final String TAG_FIND_PW = "findPW";

    public ConfirmDialogFragment(){

    }
    public static ConfirmDialogFragment newInstance() {
        ConfirmDialogFragment f = new ConfirmDialogFragment();
        return f;
    }
    TextView textView1, textView2;
    String tag = null;

    String title, body;
    String choice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            tag = b.getString("tag", null);
            title = b.getString("title","default title");
            body = b.getString("body","default body");
            choice = b.getString("choice", null);   //디테일액티비티 callback위해
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT );
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

        View view = inflater.inflate(R.layout.dialog_confirm_layout, container, false);
        textView1 = (TextView)view.findViewById(R.id.textView1);
        textView2 = (TextView)view.findViewById(R.id.textView2);
        textView1.setText(title);
        textView2.setText(body);

        Button btn = (Button)view.findViewById(R.id.btn_dialog_cancel);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn = (Button)view.findViewById(R.id.btn_dialog_ok);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_FIND_ID)) {

                    }
                    if(tag.equals(TAG_FIND_PW)) {

                    }
                }
                dismiss();
            }//onClick
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
//		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
//		getDialog().setTitle("Custom Dialog");
    }
    private void initData(){
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onResume() {
        super.onResume();
        initData();
    }


}
