package kr.me.ansr.common;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import kr.me.ansr.R;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-09-01.
 */
public class ReportDialogFragment extends DialogFragment {

    public static final String TAG_TAB_ONE = "tabOne";

    public ReportDialogFragment(){

    }
    Button btn1, btn2, btn3, btn4;
    FriendsResult data;
    String tag = null;

    public static ReportDialogFragment newInstance() {
        ReportDialogFragment f = new ReportDialogFragment();
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            data = (FriendsResult)b.getSerializable("userInfo");
            tag = b.getString("tag", "0");
        }
    }
    //0831_@nullable 어노테이션 삭제함
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);
//		getDialog().getWindow().setLayout(300,300);
//		getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
//		getDialog().getWindow().getAttributes().x = 120;
//		getDialog().getWindow().getAttributes().y = -18;

        View view = inflater.inflate(R.layout.dialog_report_layout, container, false);

        btn1 = (Button)view.findViewById(R.id.btn_report_cut_off);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_ONE)) {
//                        ((BoardWriteActivity) (getActivity())).nextProcess();
                        Toast.makeText(getActivity(), "cut off", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }
        });
        if(data.status == 1){
            btn1.setText("친 구 끊 기");
        } else {
            btn1.setText("친 구 신 청");
        }
        btn2 = (Button)view.findViewById(R.id.btn_report_block);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_ONE)) {
                        CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                        Bundle b = new Bundle();
                        b.putString("tag", CustomDialogFragment.TAG_TAB_ONE);
                        b.putString("title","정말 차단 하시겠습니까?");
                        b.putString("body", "더보기 - 친구관리에서 확인하실 수 있습니다.");
                        mDialogFragment.setArguments(b);
                        mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
                        dismiss();
                    }
                }


            }//onClick
        });
        btn3 = (Button)view.findViewById(R.id.btn_report_report);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_ONE)) {
                        Toast.makeText(getActivity(), "report", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }

            }//onClick
        });
        btn4 = (Button)view.findViewById(R.id.btn_report_cancel);
        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
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
