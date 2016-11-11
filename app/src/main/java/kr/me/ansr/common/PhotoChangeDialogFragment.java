package kr.me.ansr.common;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
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
public class PhotoChangeDialogFragment extends DialogFragment {

    public static final String TAG_IMAGE_HOME = "imageHome";

    public PhotoChangeDialogFragment(){

    }
    Button btn1, btn2, btn3;
    FriendsResult data;
    String choice = null;
    String tag = null;

    public static PhotoChangeDialogFragment newInstance() {
        PhotoChangeDialogFragment f = new PhotoChangeDialogFragment();
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            data = (FriendsResult)b.getSerializable("userInfo");
            tag = b.getString("tag");
        }
    }
    //0831_@nullable 어노테이션 삭제함
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

        View view = inflater.inflate(R.layout.dialog_photo_change_layout, container, false);

        btn1 = (Button)view.findViewById(R.id.btn_photo_change_1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag.equals(TAG_IMAGE_HOME)) {
                    Intent intent = new Intent();
                    intent.putExtra("next", "_START_ALBUM_");
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
                dismiss();
            }
        });
        btn2 = (Button)view.findViewById(R.id.btn_photo_change_2);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag.equals(TAG_IMAGE_HOME)) {
                    Intent intent = new Intent();
                    intent.putExtra("next", "_DEFAULT_IMG_");
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
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
