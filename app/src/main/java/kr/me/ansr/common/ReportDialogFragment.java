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
import kr.me.ansr.tab.friends.detail.StatusResult;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;
import kr.me.ansr.tab.friends.tabtwo.FriendsTwoFragment;

/**
 * Created by KMS on 2016-09-01.
 */
public class ReportDialogFragment extends DialogFragment {
    public static final String TAG_FRIENDS_DETAIL = "friendsDetail";
    public static final String TAG_TAB_ONE_UNIV = "tabOneUniv";
    public static final String TAG_TAB_TWO_UNIV = "tabTwoUniv";
    public static final String TAG_TAB_ONE_MY = "tabOneMy";
    public static final String TAG_TAB_THREE = "tabThree";
    public static final String TAG_LIST = "list";   //친구리스트에서

    public static final String TAG_TAB_MY_WRITING_ONE = "tabMyWritingOne";

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
            tag = b.getString("tag", null);
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
                    if(tag.equals(TAG_TAB_ONE_UNIV)) {
                        if(data.status == 1){
                            CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", CustomDialogFragment.TAG_TAB_ONE_UNIV);
                            b.putString("title","정말 친구를 끊으시겠습니까?");
                            b.putString("body", "해당 유저와 대화하기 기능을 사용할 수 없습니다.");
                            b.putSerializable("mItem", data);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.setTargetFragment(getTargetFragment(), FriendsSectionFragment.DIALOG_RC_CUT_OFF);
                            mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
                            dismiss();
                        } else if(data.status == -1){
                            int userId =Integer.parseInt(PropertyManager.getInstance().getUserId());
                            StatusResult sr = new StatusResult(
                                    userId, //from
                                    data.userId, //to
                                    -1, //status
                                    userId, //actionUser
                                    "", //updatedAt
                                    ""  //msg
                            );
                            InputDialogFragment mDialogFragment = InputDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", tag);
                            b.putSerializable("mStatus", sr);
                            b.putSerializable("mItem", data);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.setTargetFragment(getTargetFragment(), FriendsSectionFragment.DIALOG_RC_SEND_REQ);
                            mDialogFragment.show(getActivity().getSupportFragmentManager(), "inputDialog");
                            dismiss();
                        } else {
                            //이미 신청한경우
                            Toast.makeText(getActivity(), "이미 친구 신청을 한 상태 입니다.", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                    if(tag.equals(TAG_TAB_TWO_UNIV)) {
                        if(data.status == 1){
                            CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", CustomDialogFragment.TAG_TAB_ONE_UNIV);
                            b.putString("title","정말 친구를 끊으시겠습니까?");
                            b.putString("body", "해당 유저와 대화하기 기능을 사용할 수 없습니다.");
                            b.putSerializable("mItem", data);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.setTargetFragment(getTargetFragment(), FriendsTwoFragment.DIALOG_RC_CUT_OFF);
                            mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
                            dismiss();
                        } else if(data.status == -1){
                            int userId =Integer.parseInt(PropertyManager.getInstance().getUserId());
                            StatusResult sr = new StatusResult(
                                    userId, //from
                                    data.userId, //to
                                    -1, //status
                                    userId, //actionUser
                                    "", //updatedAt
                                    ""  //msg
                            );
                            InputDialogFragment mDialogFragment = InputDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", tag);
                            b.putSerializable("mStatus", sr);
                            b.putSerializable("mItem", data);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.setTargetFragment(getTargetFragment(), FriendsTwoFragment.DIALOG_RC_SEND_REQ);
                            mDialogFragment.show(getActivity().getSupportFragmentManager(), "inputDialog");
                            dismiss();
                        } else {
                            //이미 신청한경우
                            Toast.makeText(getActivity(), "이미 친구 신청을 한 상태 입니다.", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }

                    if(tag.equals(TAG_LIST)) {
                        Toast.makeText(getActivity(), "cut off", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    if(tag.equals(TAG_FRIENDS_DETAIL)) {
                        CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                        Bundle b = new Bundle();
                        switch (data.status){
                            case -1:
                                Toast.makeText(getActivity(), "친구 신청 버튼을 이용해 주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            case -100:
                            case 0:
                                Toast.makeText(getActivity(), "이미 친구 신청을 한 상태 입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                b.putString("tag", CustomDialogFragment.TAG_FRIENDS_DETAIL);
                                b.putString("title","정말 친구를 끊으시겠습니까?");
                                b.putString("body", "해당 유저와 대화하기 기능을 사용할 수 없습니다.");
                                b.putSerializable("choice","cutOff");
                                b.putSerializable("mItem", data);
                                mDialogFragment.setArguments(b);
                                mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
                                break;
                            case 3:
                                b.putString("tag", CustomDialogFragment.TAG_FRIENDS_DETAIL);
                                b.putString("title","친구 차단을 해제 하시겠습니까?");
                                b.putString("body", "학교 사람들 리스트에 노출됩니다.");
                                b.putString("choice","blockOff");
                                b.putSerializable("mItem", data);
                                mDialogFragment.setArguments(b);
                                mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
                                break;
                        }
                        dismiss();
                    }


                }
            }
        });
        if(data.status == 1){
            btn1.setText("친 구 끊 기");
        } else if(data.status == 3){
            btn1.setText("차 단 해 제");
        } else {
            btn1.setText("친 구 신 청");
        }
        btn2 = (Button)view.findViewById(R.id.btn_report_block);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_ONE_UNIV)) {
                        if(data.status == 3){
                            Toast.makeText(getActivity(), "이미 차단한 유저입니다.", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", CustomDialogFragment.TAG_TAB_ONE_UNIV);
                            b.putString("title","정말 차단 하시겠습니까?");
                            b.putString("body", "더보기 - 친구관리에서 확인하실 수 있습니다.");
                            b.putSerializable("mItem", data);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.setTargetFragment(getTargetFragment(), FriendsSectionFragment.DIALOG_RC_BLOCK);
                            mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
                            dismiss();
                        }
                    }
                    if(tag.equals(TAG_TAB_TWO_UNIV)) {
                        if(data.status == 3){
                            Toast.makeText(getActivity(), "이미 차단한 유저입니다.", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", CustomDialogFragment.TAG_TAB_TWO_UNIV);
                            b.putString("title","정말 차단 하시겠습니까?");
                            b.putString("body", "더보기 - 친구관리에서 확인하실 수 있습니다.");
                            b.putSerializable("mItem", data);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.setTargetFragment(getTargetFragment(), FriendsTwoFragment.DIALOG_RC_BLOCK);
                            mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
                            dismiss();
                        }
                    }
                    if(tag.equals(TAG_LIST)) {
                        dismiss();
                    }
                    if(tag.equals(TAG_FRIENDS_DETAIL)) {
                        if(data.status == 3){
                            Toast.makeText(getActivity(), "이미 차단한 유저입니다.", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", CustomDialogFragment.TAG_FRIENDS_DETAIL);
                            b.putString("title","정말 차단 하시겠습니까?");
                            b.putString("body", "더보기 - 친구관리에서 확인하실 수 있습니다.");
                            b.putString("choice", "block");
                            b.putSerializable("mItem", data);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.show(getActivity().getSupportFragmentManager(), "customDialog");
                            dismiss();
                        }
                    }
                }


            }//onClick
        });
        btn3 = (Button)view.findViewById(R.id.btn_report_report);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_TAB_ONE_UNIV)) {
                        ReportFormDialogFragment mDialogFragment = ReportFormDialogFragment.newInstance();
                        Bundle b = new Bundle();
                        b.putString("tag", ReportFormDialogFragment.TAG_TAB_ONE_UNIV);
                        b.putSerializable("fItem", data);
                        b.putSerializable("bItem", null);
                        mDialogFragment.setArguments(b);
                        mDialogFragment.setTargetFragment(getTargetFragment(), FriendsSectionFragment.DIALOG_RC_REPORT);
                        mDialogFragment.show(getActivity().getSupportFragmentManager(), "reportFormDialog");
                        dismiss();
                    }

                    if(tag.equals(TAG_TAB_TWO_UNIV)) {
                        ReportFormDialogFragment mDialogFragment = ReportFormDialogFragment.newInstance();
                        Bundle b = new Bundle();
                        b.putString("tag", ReportFormDialogFragment.TAG_TAB_TWO_UNIV);
                        b.putSerializable("fItem", data);
                        b.putSerializable("bItem", null);
                        mDialogFragment.setArguments(b);
                        mDialogFragment.setTargetFragment(getTargetFragment(), FriendsTwoFragment.DIALOG_RC_REPORT);
                        mDialogFragment.show(getActivity().getSupportFragmentManager(), "reportFormDialog");
                        dismiss();
                    }
                    if(tag.equals(TAG_FRIENDS_DETAIL)) {
                        ReportFormDialogFragment mDialogFragment = ReportFormDialogFragment.newInstance();
                        Bundle b = new Bundle();
                        b.putString("tag", ReportFormDialogFragment.TAG_FRIENDS_DETAIL);
                        b.putSerializable("fItem", data);
                        b.putSerializable("bItem", null);
                        mDialogFragment.setArguments(b);
                        mDialogFragment.show(getActivity().getSupportFragmentManager(), "reportFormDialog");
                        dismiss();
                    }
                    if(tag.equals(TAG_LIST)) {
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
