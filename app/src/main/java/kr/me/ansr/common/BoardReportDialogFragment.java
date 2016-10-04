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

import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-09-01.
 */
public class BoardReportDialogFragment extends DialogFragment {

    public static final String TAG_TAB_THREE = "tabThree";
    public static final String TAG_BOARD_DETAIL = "BoardDetail";

    public BoardReportDialogFragment(){

    }
    Button btn1, btn2, btn3;
    BoardResult data;
    String tag = null;
    int userId = Integer.parseInt(PropertyManager.getInstance().getUserId());

    public static BoardReportDialogFragment newInstance() {
        BoardReportDialogFragment f = new BoardReportDialogFragment();
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            data = (BoardResult)b.getSerializable("boardInfo");
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

        View view = inflater.inflate(R.layout.dialog_board_report_layout, container, false);

        btn1 = (Button)view.findViewById(R.id.btn_board_report_top);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_THREE)) {
                        Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    if(tag.equals(TAG_BOARD_DETAIL)) {
                        Toast.makeText(getActivity(), "detail top", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }
        });

        btn2 = (Button)view.findViewById(R.id.btn_board_report_mid);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_THREE)) {
                        Toast.makeText(getActivity(), "tab three mid", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    if(tag.equals(TAG_BOARD_DETAIL)) {
                        Toast.makeText(getActivity(), "detail mid", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }//onClick
        });

        btn3 = (Button)view.findViewById(R.id.btn_board_report_bottom);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_THREE)) {
                        //...
                    }
                }
                dismiss();
            }
        });

		initData();
        return view;
    }
    private void initData(){
        switch (tag){
            case TAG_TAB_THREE:
                btn1.setVisibility(View.GONE);
                btn2.setBackgroundResource(R.drawable.e_popup_top_selector);
                if(data.writer == userId){
                    btn2.setText("수 정 하 기");
                } else {
                    btn2.setText("신 고 하 기");
                }
                break;
            case TAG_BOARD_DETAIL:
                if(data.writer == userId){
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setBackgroundResource(R.drawable.e_popup_mid_selector);
                    btn2.setText("수 정 하 기");
                } else {
                    btn1.setVisibility(View.GONE);
                    btn2.setBackgroundResource(R.drawable.e_popup_top_selector);
                    btn2.setText("신 고 하 기");
                }

                break;
        }

    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
//		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
//		getDialog().setTitle("Custom Dialog");
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
