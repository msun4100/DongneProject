package kr.me.ansr.common;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import kr.me.ansr.MyApplication;
import kr.me.ansr.R;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.detail.FriendsDetailActivity;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-08-05.
 */
public class InputDialogFragment extends DialogFragment {
    public static final String TAG_FRIENDS_DETAIL = "friendsDetail";

    public InputDialogFragment(){

    }
    TextView stuidView, deptnameView, jobnameView, jobteamView;
    EditText inputView;
    ImageView thumbIcon, closeIcon;

    String tag = null;
    FriendsResult mItem=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            tag = b.getString("tag", "0");
            mItem = (FriendsResult)b.getSerializable("item");
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
        Button btn = (Button)view.findViewById(R.id.btn_dialog_input_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_FRIENDS_DETAIL)) {
                        String str = inputView.getText().toString();
                        ((FriendsDetailActivity) (getActivity())).nextProcess(str);
                    }
                }
                dismiss();
            }//onClick
        });
        stuidView = (TextView)view.findViewById(R.id.text_dialog_input_stuid);
        deptnameView = (TextView)view.findViewById(R.id.text_dialog_input_deptname);
        jobnameView = (TextView)view.findViewById(R.id.text_dialog_input_jobname);
        jobteamView = (TextView)view.findViewById(R.id.text_dialog_input_jobteam);
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
        if(mItem != null){
            stuidView.setText(""+(mItem.univ.get(0).enterYear));
            deptnameView.setText(mItem.univ.get(0).deptname);
            jobnameView.setText(mItem.job.getName());
            jobteamView.setText(mItem.job.getTeam());
            if (!TextUtils.isEmpty(mItem.pic.small)) {
                String url = Config.FILE_GET_URL.replace(":userId", ""+mItem.userId).replace(":size", "small");
                Glide.with(getContext()).load(url).placeholder(R.drawable.ic_stub)
                .signature(new StringSignature(mItem.getUpdatedAt())).into(thumbIcon);
            } else {
                thumbIcon.setImageResource(R.mipmap.ic_launcher);
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
