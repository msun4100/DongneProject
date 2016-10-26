package kr.me.ansr.common.account;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.friends.MySpinnerAdapter;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-10-21.
 */
public class WithdrawDialogFragment extends DialogFragment{
    public static final String TAG_WITHDRAW = "withdraw";

    public WithdrawDialogFragment() {

    }
    Button btn2, btn3;
    FriendsResult fItem;
    BoardResult bItem;
    String tag = null;
    int userId = Integer.parseInt(PropertyManager.getInstance().getUserId());

    Spinner spinner;
    MySpinnerAdapter mySpinnerAdapter;
    int spinnerPos = 0;
    CustomEditText inputPW;
    public static WithdrawDialogFragment newInstance() {
        WithdrawDialogFragment f = new WithdrawDialogFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if (b != null) {
            fItem = (FriendsResult) b.getSerializable("fItem");
            bItem = (BoardResult) b.getSerializable("bItem");
            tag = b.getString("tag", "0");
        }
    }

    //0831_@nullable 어노테이션 삭제함
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getDialog().getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

        View view = inflater.inflate(R.layout.dialog_withdraw_form_layout, container, false);


        btn2 = (Button) view.findViewById(R.id.btn_report_form_mid);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerPos == 0){
                    Toast.makeText(getActivity(), "탈퇴사유를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tag != null) {
                    if (tag.equals(TAG_WITHDRAW)) {
                        if(PropertyManager.getInstance().getPassword().equals(inputPW.getText().toString())){
                            mCallback.onDataReturned(""+spinnerPos);
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
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
//        implements common_search_bar_spinner======================================
        spinner = (Spinner) view.findViewById(R.id.spinner);
        mySpinnerAdapter = new MySpinnerAdapter();
        spinner.setAdapter(mySpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String)mySpinnerAdapter.getItem(position);
                spinnerPos = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
            }
        });
        inputPW = (CustomEditText)view.findViewById(R.id.edit_report_pw);

        initData();

        return view;
    }

    private void initData() {
        String[] arr = getResources().getStringArray(R.array.withdraw_items);
        for (int i = 0; i < arr.length; i++) {
            mySpinnerAdapter.add(arr[i]);
        }
        spinner.setSelection(0);
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
