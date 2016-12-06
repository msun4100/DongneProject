package kr.me.ansr.common;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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

    public static final String TAG_TAB_THREE_STUDENT = "tabThreeStudent";
    public static final String TAG_TAB_THREE_GRADUATE = "tabThreeGraduate";
    public static final String TAG_BOARD_DETAIL = "BoardDetail";

    public static final String TAG_TAB_MY_WRITING_ONE = "tabMyWritingOne";

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

        View view = inflater.inflate(R.layout.dialog_board_report_layout, container, false);

        btn1 = (Button)view.findViewById(R.id.btn_board_report_top);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_THREE_STUDENT)) {
                        Toast.makeText(getActivity(), "top 삭제하기", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        if(data.writer == Integer.parseInt(PropertyManager.getInstance().getUserId())){
                            intent.putExtra("next", "_DELETE_");
                        } else {
                            intent.putExtra("next", "");
                        }
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        dismiss();
                    }
                    if(tag.equals(TAG_TAB_MY_WRITING_ONE)) {
                        Intent intent = new Intent();
                        if(data.writer == Integer.parseInt(PropertyManager.getInstance().getUserId())){
                            intent.putExtra("next", "_DELETE_");
                        } else {
                            intent.putExtra("next", "");
                        }
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        dismiss();
                    }

                    if(tag.equals(TAG_BOARD_DETAIL)) {
                        Toast.makeText(getActivity(), "detail top 삭제하기", Toast.LENGTH_SHORT).show();
                        mCallback.onDataReturned("0");
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
                    if(tag.equals(TAG_TAB_THREE_STUDENT)) {
                        Toast.makeText(getActivity(), "tab three mid "+ getTargetRequestCode(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        if(data.writer == Integer.parseInt(PropertyManager.getInstance().getUserId())){
                            intent.putExtra("next", "_EDIT_");
                        } else {
                            intent.putExtra("next", "_REPORT_");
                        }
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        dismiss();
                    }
                    if(tag.equals(TAG_TAB_MY_WRITING_ONE)) {
                        Toast.makeText(getActivity(), "tab three mid "+ getTargetRequestCode(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        if(data.writer == Integer.parseInt(PropertyManager.getInstance().getUserId())){
                            intent.putExtra("next", "_EDIT_");
                        } else {
                            intent.putExtra("next", "_REPORT_");
                        }
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        dismiss();
                    }
                    if(tag.equals(TAG_BOARD_DETAIL)) {
                        Toast.makeText(getActivity(), "detail mid", Toast.LENGTH_SHORT).show();
                        mCallback.onDataReturned("1");
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
                    if(tag.equals(TAG_TAB_THREE_STUDENT)) {
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
//            case TAG_TAB_THREE_STUDENT:
//                btn1.setVisibility(View.GONE);
//                btn2.setBackgroundResource(R.drawable.e_popup_top_selector);
//                if(data.writer == userId){
//                    btn2.setText("수 정 하 기");
//                } else {
//                    btn2.setText("신 고 하 기");
//                }
//                break;
            case TAG_TAB_THREE_STUDENT:
            case TAG_TAB_MY_WRITING_ONE:
            case TAG_BOARD_DETAIL:
                default:
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

    private IDataReturned mCallback;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (IDataReturned) activity;
        } catch (ClassCastException e){
            Log.d("MyDialog", "Activity doesn't implements 'IDataReturned interface'");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try{
            mCallback = null;
        } catch (ClassCastException e){
            Log.d("MyDialog", "Exception occurred while onDetach");
        }
    }

}
