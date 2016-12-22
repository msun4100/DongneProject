package kr.me.ansr.common;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import kr.me.ansr.MyApplication;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.detail.StatusResult;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.mypage.status.ReceiveFragment;
import kr.me.ansr.tab.mypage.status.SendFragment;

/**
 * Created by KMS on 2016-08-05.
 */
public class InputDialogFragment extends DialogFragment {
    public static final String TAG_FRIENDS_DETAIL = "friendsDetail";
    public static final String TAG_STATUS_RECEIVE = "statusReceive";
    public static final String TAG_STATUS_SEND = "statusSend";
    public static final String TAG_STATUS_BLOCK = "statusBlock";
    public static final String TAG_TAB_ONE_UNIV = "tabOneUniv";

    public InputDialogFragment(){

    }
    public static InputDialogFragment newInstance() {
        InputDialogFragment f = new InputDialogFragment();
        return f;
    }
    TextView stuidView, deptnameView, jobnameView, jobteamView, statusView;
    EditText inputView;
    ImageView thumbIcon, closeIcon;
    LinearLayout btnLayout1, btnLayout2;
    Button btnSend, btnCancel, btnOk;
    String tag = null;

    StatusResult mStatus = null;
    FriendsResult mItem=null;
    int userId = Integer.parseInt(PropertyManager.getInstance().getUserId());
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            tag = b.getString("tag", null);
            mStatus = (StatusResult)b.getSerializable("mStatus");
            mItem = (FriendsResult)b.getSerializable("mItem");
//            Log.e("inputDialog-mStatus", mStatus.toString());
//            Log.e("inputDialog-mItem", mItem.toString());
            Log.e("inputDialog-tag", tag);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

        View view = inflater.inflate(R.layout.dialog_input_layout, container, false);
        inputView = (EditText)view.findViewById(R.id.edit_dialog_input_input);
        thumbIcon = (ImageView)view.findViewById(R.id.image_dialog_input_thumb);
        thumbIcon.setImageResource(R.mipmap.ic_launcher);
        closeIcon = (ImageView)view.findViewById(R.id.image_dialog_input_close);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnSend= (Button)view.findViewById(R.id.btn_dialog_input_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(tag != null){
                    if(tag.equals(TAG_FRIENDS_DETAIL)) {
                        String str = inputView.getText().toString();
                        if(str == null || str.equals("")){
                            str = getResources().getString(R.string.status_greeting_msg);
                        }
                        if(mStatus.status == 0 && mStatus.from == userId){
                            ((FriendsDetailActivity) (getActivity())).nextProcess("_cancel_");
                        } else{
                            //mStatus.status == -1
                            ((FriendsDetailActivity) (getActivity())).nextProcess(str); //str==msg와 함께 친구 요청함 nextProcess의 else
                        }
                    }
                    if(tag.equals(TAG_TAB_ONE_UNIV)) {
                        String str = inputView.getText().toString();
                        if(str == null || str.equals("")){
                            str = getResources().getString(R.string.status_greeting_msg);
                        }
                        if(mStatus.status == 0 && mStatus.from == userId){
//                            FriendsSection에서는 0이면 토스트 처리.
                        } else{
                            //mStatus.status == -1
                            intent.putExtra("mItem", mItem);
                            intent.putExtra("msg", str);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                    if(tag.equals(TAG_STATUS_SEND)) {
                        intent.putExtra("next", "_REQ_CANCEL_");
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        dismiss();
                    }
                }
                dismiss();
            }//onClick
        });
        btnOk = (Button)view.findViewById(R.id.btn_dialog_input_ok);
        btnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //요청 수락 프로세스
                Intent intent = new Intent();
                if(tag != null){
                    if(tag.equals(TAG_FRIENDS_DETAIL)) {
                        ((FriendsDetailActivity) (getActivity())).nextProcess("_ok_");// 수락버튼 클릭
                        dismiss();
                    }
                    if(tag.equals(TAG_STATUS_RECEIVE)) {
                        intent.putExtra("next", "_OK_");
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        dismiss();
                    }
                }

            }
        });
        btnCancel = (Button)view.findViewById(R.id.btn_dialog_input_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(tag != null){
                    if(tag.equals(TAG_FRIENDS_DETAIL)) {
                        ((FriendsDetailActivity) (getActivity())).nextProcess("_cancel_");
                        dismiss();
                    }
                    if(tag.equals(TAG_STATUS_RECEIVE)) {
                        intent.putExtra("next", "_CANCEL_");
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        dismiss();
                    }
                }
            }
        });
        stuidView = (TextView)view.findViewById(R.id.text_dialog_input_stuid);
        deptnameView = (TextView)view.findViewById(R.id.text_dialog_input_deptname);
        jobnameView = (TextView)view.findViewById(R.id.text_dialog_input_jobname);
        jobteamView = (TextView)view.findViewById(R.id.text_dialog_input_jobteam);
        statusView = (TextView)view.findViewById(R.id.text_dialog_input_status);
        //친구 요청 현황에 따라 버튼 노출
        btnLayout1 = (LinearLayout)view.findViewById(R.id.dialog_btn_layout1);
        btnLayout2 = (LinearLayout)view.findViewById(R.id.dialog_btn_layout2);
		initData();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
//		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
//		getDialog().setTitle("Custom Dialog");
    }
    private void initData(){
        if(mStatus != null){
            String msg = mStatus.msg;
            if(msg == null || msg.equals("")){
                msg = getResources().getString(R.string.status_greeting_msg);
            }

            if(mStatus.status == 0 && mStatus.from == userId){
                inputView.setFocusable(false);
                inputView.setFocusableInTouchMode(false);
                inputView.setText(msg);

                btnLayout1.setVisibility(View.VISIBLE);
                btnLayout2.setVisibility(View.INVISIBLE);
                btnSend.setText("요청 취소");
                statusView.setVisibility(View.VISIBLE);
                statusView.setText("친구 신청 완료");
            } else if(mStatus.status == 0 && mStatus.to == userId){
                inputView.setFocusable(false);
                inputView.setFocusableInTouchMode(false);
                inputView.setText(msg);

                btnLayout1.setVisibility(View.INVISIBLE);
                btnLayout2.setVisibility(View.VISIBLE);
                btnOk.setText("수 락");
                btnCancel.setText("거 절");
                statusView.setVisibility(View.VISIBLE);
                statusView.setText("받은 요청");
            } else {
                //FriendsDetail에서 열때 status == -1 인 경우
                inputView.setFocusable(true);
                inputView.setFocusableInTouchMode(true);
                inputView.setText("");

                btnLayout1.setVisibility(View.VISIBLE);
                btnLayout2.setVisibility(View.INVISIBLE);
                btnSend.setText("보내기");
                statusView.setVisibility(View.INVISIBLE);
            }
            String stuId = ""+(mItem.univ.get(0).enterYear);
            stuidView.setText(stuId.substring(2,4));
            deptnameView.setText(mItem.univ.get(0).deptname);
            jobnameView.setText(mItem.job.getName());
            jobteamView.setText(mItem.job.getTeam());
            if (!TextUtils.isEmpty(mItem.pic.small)) {
                String url = Config.FILE_GET_URL.replace(":userId", ""+mItem.userId).replace(":size", "small");
                Glide.with(getContext()).load(url).placeholder(R.drawable.e__who_icon).centerCrop()
                .signature(new StringSignature(mItem.getUpdatedAt())).into(thumbIcon);
            } else {
                thumbIcon.setImageResource(R.drawable.e__who_icon);
            }
        }
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
