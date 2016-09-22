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
import kr.me.ansr.tab.friends.model.FriendsResult;


/**
 * Created by KMS on 2016-09-01.
 */
public class PhotoChangeFragment extends DialogFragment {
    public PhotoChangeFragment(){

    }
    Button btn1, btn2, btn3;
    FriendsResult data;

    public static PhotoChangeFragment newInstance() {
        PhotoChangeFragment f = new PhotoChangeFragment();
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            data = (FriendsResult)b.getSerializable("userInfo");
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

        View view = inflater.inflate(R.layout.dialog_photo_change_layout, container, false);

        btn1 = (Button)view.findViewById(R.id.btn_photo_change_1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        btn2 = (Button)view.findViewById(R.id.btn_photo_change_2);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				Toast.makeText(getActivity(), "dialog ok", Toast.LENGTH_SHORT).show();
//                Bundle b = new Bundle();
//                UnsignPWDialogFragment f = new UnsignPWDialogFragment();
//                f.setArguments(b);
//                f.show(getActivity().getSupportFragmentManager(), "unsignpwdialog");
                Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
                dismiss();

            }//onClick
        });
        btn3 = (Button)view.findViewById(R.id.btn_photo_change_3);
        btn3.setOnClickListener(new View.OnClickListener() {
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
