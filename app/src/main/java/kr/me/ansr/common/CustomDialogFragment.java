package kr.me.ansr.common;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.me.ansr.R;
import kr.me.ansr.tab.board.BoardWriteActivity;

/**
 * Created by KMS on 2016-08-04.
 */
public class CustomDialogFragment extends DialogFragment {
    public static final String TAG_BOARD_WRITE = "boardWrite";
    public CustomDialogFragment(){

    }
    TextView textView1, textView2;
    String tag = null;

    String title, body;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            tag = b.getString("tag", "0");
            title = b.getString("title","default title");
            body = b.getString("body","default body");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);
//		getDialog().getWindow().setLayout(300,300);
//		getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
//		getDialog().getWindow().getAttributes().x = 120;
//		getDialog().getWindow().getAttributes().y = -18;

        View view = inflater.inflate(R.layout.dialog_custom_layout, container, false);
        textView1 = (TextView)view.findViewById(R.id.textView1);
        textView2 = (TextView)view.findViewById(R.id.textView2);
        textView1.setText(title);
        textView2.setText(body);

        Button btn = (Button)view.findViewById(R.id.btn_dialog_cancel);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "dialog cancel", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        btn = (Button)view.findViewById(R.id.btn_dialog_ok);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				Toast.makeText(getActivity(), "dialog ok", Toast.LENGTH_SHORT).show();
//                Bundle b = new Bundle();
//                UnsignPWDialogFragment f = new UnsignPWDialogFragment();
//                f.setArguments(b);
//                f.show(getActivity().getSupportFragmentManager(), "unsignpwdialog");
                dismiss();
                if(tag != null){
                    if(tag.equals(TAG_BOARD_WRITE)) {
                        ((BoardWriteActivity) (getActivity())).nextProcess();
                    }
                }
            }//onClick
        });

//		initData(); //여기에서 알람 데이터목록 추가함
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
