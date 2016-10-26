package kr.me.ansr.common.account;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.common.IPWReturned;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.friends.MySpinnerAdapter;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-10-21.
 */
public class PWDialogFragment extends DialogFragment{
    public static final String TAG_PW_CHANGE = "pwChange";

    public PWDialogFragment() {

    }
    Button btn2, btn3;
    String tag = null;
    CustomEditText inputChk, inputConfirm1, inputConfirm2;
    TextView emailView;
    public static PWDialogFragment newInstance() {
        PWDialogFragment f = new PWDialogFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if (b != null) {
            tag = b.getString("tag", "0");
        }
    }
    InputMethodManager imm;
    //0831_@nullable 어노테이션 삭제함
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getDialog().getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

        View view = inflater.inflate(R.layout.dialog_pw_layout, container, false);

        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        btn2 = (Button) view.findViewById(R.id.btn_report_form_mid);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    if (tag.equals(TAG_PW_CHANGE)) {
                        if(check1() && check2()){
                            pCallback.onPWReturned(inputConfirm2.getText().toString());
                            dismiss();
                        }
                    }
                }
            }//onClick
        });

        btn3 = (Button) view.findViewById(R.id.btn_report_form_bottom);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        inputChk = (CustomEditText)view.findViewById(R.id.edit_pw_chk);
        inputConfirm1 = (CustomEditText)view.findViewById(R.id.edit_pw_confirm1);
        inputConfirm2 = (CustomEditText)view.findViewById(R.id.edit_pw_confirm2);
        inputConfirm2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    //....
                    if (tag.equals(TAG_PW_CHANGE)) {
                        if(check1() && check2()){
                            pCallback.onPWReturned(inputConfirm2.getText().toString());
                            dismiss();
                        }
                    }
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        emailView = (TextView)view.findViewById(R.id.text_pw_email);
        emailView.setText(PropertyManager.getInstance().getEmail());

        initData();

        return view;
    }

    private void initData() {

    }

    private boolean check1(){
        if(inputChk.getText().toString().equals("")){
            Toast.makeText(getActivity(), "비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!PropertyManager.getInstance().getPassword().equals(inputChk.getText().toString())){
            Toast.makeText(getActivity(), "기존 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean check2(){
        if(inputConfirm1.getText().toString().equals("") || inputConfirm2.getText().toString().equals("")){
            Toast.makeText(getActivity(), "변경하실 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!inputConfirm1.getText().toString().equals(inputConfirm2.getText().toString())){
            Toast.makeText(getActivity(), "변경하실 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(inputConfirm2.getText().toString().equals(inputChk.getText().toString())){
            Toast.makeText(getActivity(), "기존 비밀번호와 변경할 비밀번호가 일치합니다.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private IDataReturned mCallback;
    private IPWReturned pCallback;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (IDataReturned) activity;
            pCallback = (IPWReturned) activity;
        } catch (ClassCastException e){
            Log.d("MyDialog", "Activity doesn't implements 'IDataReturned interface'");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try{
            mCallback = null;
            pCallback = null;
        } catch (ClassCastException e){
            Log.d("MyDialog", "Exception occurred while onDetach");
        }
    }

}
