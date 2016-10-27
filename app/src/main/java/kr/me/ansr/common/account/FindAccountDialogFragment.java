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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.common.IPWReturned;

/**
 * Created by KMS on 2016-10-27.
 */
public class FindAccountDialogFragment extends DialogFragment {
    public static final String TAG_FIND_ACCOUNT = "findAccount";

    public FindAccountDialogFragment() {

    }
    Button btn2, btn3;
    String tag = null;
    CustomEditText inputName, inputUniv, inputDept, inputEnterYear;
    TextView emailView;
    public static FindAccountDialogFragment newInstance() {
        FindAccountDialogFragment f = new FindAccountDialogFragment();
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

        View view = inflater.inflate(R.layout.dialog_find_account_layout, container, false);

        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        btn2 = (Button) view.findViewById(R.id.btn_report_form_mid);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    if (tag.equals(TAG_FIND_ACCOUNT)) {
                        String username = inputName.getText().toString();
                        String univ = inputUniv.getText().toString();
                        String dept = inputDept.getText().toString();
                        String enterYear = inputEnterYear.getText().toString();
                        if(checkData(username, univ, dept, enterYear)){
                            mCallback.onDataReturned(new FindAccountModel(username, univ, dept, enterYear));
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

        inputName = (CustomEditText)view.findViewById(R.id.edit_find_name);
        inputUniv = (CustomEditText)view.findViewById(R.id.edit_find_univ);
        inputDept = (CustomEditText)view.findViewById(R.id.edit_find_dept);
        inputEnterYear = (CustomEditText)view.findViewById(R.id.edit_find_enterYear);
        inputEnterYear.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    //....
                    if (tag.equals(TAG_FIND_ACCOUNT)) {
                        String username = inputName.getText().toString();
                        String univ = inputUniv.getText().toString();
                        String dept = inputDept.getText().toString();
                        String enterYear = inputEnterYear.getText().toString();
                        if(checkData(username, univ, dept, enterYear)){
                            mCallback.onDataReturned(new FindAccountModel(username, univ, dept, enterYear));
                            dismiss();
                        }
                    }
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        emailView = (TextView)view.findViewById(R.id.text_find_msg);
        emailView.setText("");

        initData();

        return view;
    }

    private void initData() {

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

    private IFindAccountReturned mCallback;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (IFindAccountReturned) activity;

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
