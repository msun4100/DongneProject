package kr.me.ansr.common.account;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kr.me.ansr.R;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-10-21.
 */
public class LogoutDialogFragment extends DialogFragment {
    public static final String TAG_LOGOUT = "logout";
    public static final String TAG_WITHDRAW = "withdraw";
    public static final String TAG_ROOM_DELETE = "roomDelete";

    public LogoutDialogFragment(){

    }
    public static LogoutDialogFragment newInstance() {
        LogoutDialogFragment f = new LogoutDialogFragment();
        return f;
    }
    TextView textView1, textView2;
    String tag = null;

    String title, body;
    String choice;


    FriendsResult mItem;
    BoardResult bItem;
    int chatRoomId; //chat room delete
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            tag = b.getString("tag", null);
            title = b.getString("title","default title");
            body = b.getString("body","default body");
            mItem = (FriendsResult) b.getSerializable("mItem");
            choice = b.getString("choice", null);   //디테일액티비티 callback위해
            bItem = (BoardResult) b.getSerializable("bItem");
            if(mItem != null){

            }
            if(bItem != null){
//                Log.e("CustomDialogF", bItem.toString());
            }
            chatRoomId = b.getInt("chatRoomId",-1); //remove chat room
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT );
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

        View view = inflater.inflate(R.layout.dialog_custom_layout, container, false);
        textView1 = (TextView)view.findViewById(R.id.textView1);
        textView2 = (TextView)view.findViewById(R.id.textView2);
        textView1.setText(title);
        textView2.setText(body);

        Button btn = (Button)view.findViewById(R.id.btn_dialog_cancel);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag.equals(TAG_ROOM_DELETE)) {
                    Intent intent = new Intent();
                    intent.putExtra("chatRoomId", chatRoomId);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, intent);
                }
                dismiss();
            }
        });
        btn = (Button)view.findViewById(R.id.btn_dialog_ok);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tag != null){
                    if(tag.equals(TAG_LOGOUT)) {
                        mCallback.onDataReturned("_LOGOUT_"); //창 닫힘
                    }
                    if(tag.equals(TAG_WITHDRAW)) {
                        mCallback.onDataReturned("_WITHDRAW_"); //탈퇴 확인. 창 닫힘
                    }
                    if(tag.equals(TAG_ROOM_DELETE)) {
                        Intent intent = new Intent();
                        intent.putExtra("chatRoomId", chatRoomId);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    }
                }
                dismiss();
            }//onClick
        });

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
