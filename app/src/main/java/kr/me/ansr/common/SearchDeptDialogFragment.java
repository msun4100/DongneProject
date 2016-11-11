package kr.me.ansr.common;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import kr.me.ansr.R;
import kr.me.ansr.login.SignupActivity;

/**
 * Created by KMS on 2016-08-11.
 */
public class SearchDeptDialogFragment extends DialogFragment{
    public static final String TAG_SIGN_UP = "signUp";

    public SearchDeptDialogFragment(){

    }
    AutoCompleteTextView inputView;
    FrameLayout frameSearchIcon;
    //    ImageView searchIcon;
    ImageView backGroundImage, directIcon;
    LinearLayout backgroundLayout;
    String tag = null;
//    FriendsResult mItem=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        Bundle b = getArguments();
        if(b != null){
            tag = b.getString("tag", "0");
//            mItem = (FriendsResult)b.getSerializable("item");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getDialog().getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT );
        getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

        View view = inflater.inflate(R.layout.dialog_search_dept_layout, container, false);
        inputView = (AutoCompleteTextView)view.findViewById(R.id.auto_text_signup_dept);
        inputView.setAdapter(((SignupActivity)getActivity()).mDeptAdapter);
        inputView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("onItemClick", "asd");
                ((SignupActivity)getActivity()).mDeptId = ((SignupActivity)getActivity()).mDeptAdapter.getDeptId(position);
                ((SignupActivity)getActivity()).mDeptname = ((SignupActivity)getActivity()).mDeptAdapter.getDeptName(position);

                String str = ((SignupActivity)getActivity()).mDeptAdapter.getDeptName(position);
                if (str != null){
//                    ((SignupActivity)getActivity()).textViewDept.setText(str);
                } else {
//                    ((SignupActivity)getActivity()).textViewDept.setText("");
                }
                dismiss();
            }
        });
        inputView.addTextChangedListener(new MyDialogTextWatcher(inputView));
        frameSearchIcon = (FrameLayout) view.findViewById(R.id.frame_dialog_search_icon);
        frameSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        directIcon = (ImageView)view.findViewById(R.id.image_dialog_direct);
        directIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String str = inputView.getText().toString();
                ((SignupActivity)getActivity()).mDeptname = str;
                if (str != null){
//                    ((SignupActivity)getActivity()).textViewDept.setText(str);
                } else {
//                    ((SignupActivity)getActivity()).textViewDept.setText("");
                }
                dismiss();
            }
        });

        backgroundLayout = (LinearLayout)view.findViewById(R.id.layout_dialog_background);
        backgroundLayout.setVisibility(View.GONE);
        backGroundImage = (ImageView)view.findViewById(R.id.image_dialog_background);
        backGroundImage.setVisibility(View.VISIBLE);
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

    private class MyDialogTextWatcher implements TextWatcher {

        private View view;

        private MyDialogTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.auto_text_signup_dept:
//                    dialogLayout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                    String str =((AutoCompleteTextView)view).getText().toString();
                    int cnt = ((SignupActivity)getActivity()).mDeptAdapter.getCount();
                    if(str.length() == 0){
                        backGroundImage.setVisibility(View.VISIBLE);
                        backgroundLayout.setVisibility(View.GONE);
//                        dialogLayout.setBackgroundResource(R.drawable.a_popup_search_school);
                    } else if(str.length() > 0 && cnt == 0){
                        backgroundLayout.setVisibility(View.VISIBLE);
                        backGroundImage.setVisibility(View.GONE);
//                        dialogLayout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                    }
                    break;
            }
        }

        public void afterTextChanged(Editable editable) {

        }
    }
}
