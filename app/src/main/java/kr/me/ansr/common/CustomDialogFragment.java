package kr.me.ansr.common;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import kr.me.ansr.tab.friends.list.FriendsListActivity;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import kr.me.ansr.tab.mypage.status.BlockFragment;

/**
 * Created by KMS on 2016-08-04.
 */
public class CustomDialogFragment extends DialogFragment {
    public static final String TAG_BOARD_WRITE = "boardWrite";
    public static final String TAG_STATUS_BLOCK = "statusBlock";
    public static final String TAG_TAB_ONE_UNIV = "tabOneUniv";
    public static final String TAG_TAB_ONE_MY = "tabOneMy";
    public static final String TAG_LIST = "list";
    public CustomDialogFragment(){

    }
    public static CustomDialogFragment newInstance() {
        CustomDialogFragment f = new CustomDialogFragment();
        return f;
    }
    TextView textView1, textView2;
    String tag = null;

    String title, body;

    Handler mHandler = new Handler(Looper.getMainLooper());

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
//        getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);
//		getDialog().getWindow().setLayout(289, 134);
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
                if(tag != null){
                    if(tag.equals(TAG_BOARD_WRITE)) {
                        ((BoardWriteActivity) (getActivity())).nextProcess();
                        dismiss();
                    }
                    if(tag.equals(TAG_STATUS_BLOCK)) {
                        BlockFragment.removeStatus();
                        dismiss();
//                        ReceiveFragment.updateStatus(Integer.valueOf(FriendsInfo.STATUS_ACCEPT), mItem.userId, "Removed", mItem);
                    }
                    if(tag.equals(TAG_TAB_ONE_UNIV)) {
//                        BlockFragment.removeStatus();
                        //updateStatus 성공하면 리스트에서 삭제하고 blockCount++; 해줌.
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), tag+"Runnable", Toast.LENGTH_SHORT).show();
                                FriendsSectionFragment.updateStatus(3);
                                dismiss();
                            }
                        }, 1000);

                    }
                    if(tag.equals(TAG_LIST)) {
//                        BlockFragment.removeStatus();
                        //updateStatus 성공하면 리스트에서 삭제하고 blockCount++; 해줌.
                        Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
//                        FriendsListActivity.updateStatus(3);
                        dismiss();
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
