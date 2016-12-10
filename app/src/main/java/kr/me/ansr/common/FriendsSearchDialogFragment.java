package kr.me.ansr.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-12-10.
 */
public class FriendsSearchDialogFragment extends DialogFragment{
    public static final String TAG_TAB_ONE_UNIV = "tabOneUniv";

    public FriendsSearchDialogFragment() {

    }
    Button btnCancel, btnOk;
    String tag = null;
    CustomEditText inputName, inputEnterYear, inputDept, inputJobname, inputJobteam ;
    public static FriendsSearchDialogFragment newInstance() {
        FriendsSearchDialogFragment f = new FriendsSearchDialogFragment();
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

        View view = inflater.inflate(R.layout.dialog_search_friends, container, false);

        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    if (tag.equals(TAG_TAB_ONE_UNIV)) {
                        returnData();
                    }
                    dismiss();
                }
            }//onClick
        });
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        inputName = (CustomEditText)view.findViewById(R.id.edit_search_name);
        inputDept = (CustomEditText)view.findViewById(R.id.edit_search_dept);
        inputJobname = (CustomEditText)view.findViewById(R.id.edit_search_jobname);
        inputJobteam = (CustomEditText)view.findViewById(R.id.edit_search_jobteam);
        inputEnterYear = (CustomEditText)view.findViewById(R.id.edit_search_enteryear);
        inputEnterYear.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    //....
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (tag.equals(TAG_TAB_ONE_UNIV)) {
                        returnData();
                    }
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initData();

        return view;
    }

    private void initData() {

    }
    private void returnData(){
        String username = inputName.getText().toString().trim();
        String enterYear = inputEnterYear.getText().toString().trim();
        String dept = inputDept.getText().toString().trim();
        String jobname = inputJobname.getText().toString().trim();
        String jobteam = inputJobteam.getText().toString().trim();
        Intent intent = new Intent();
        intent.putExtra("username", username);
        intent.putExtra("enterYear", enterYear);
        intent.putExtra("dept", dept);
        intent.putExtra("jobname", jobname);
        intent.putExtra("jobteam", jobteam);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    private boolean checkData(String username, String univ, String dept, String enterYear){

        if(username == null || username.equals("")){
            Toast.makeText(getActivity(), "이름을 확인해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(univ == null || univ.equals("")){
            Toast.makeText(getActivity(), "학교명을 확인해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dept == null || dept.equals("")){
            Toast.makeText(getActivity(), "학과명을 확인해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(enterYear == null || enterYear.equals("")){
            return false;
        }
        try {
            int num = Integer.parseInt(enterYear);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "학번을 확인해주세요.", Toast.LENGTH_SHORT).show();
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




}
