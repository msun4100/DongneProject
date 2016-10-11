package kr.me.ansr.common;

import android.app.ActionBar;
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
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.tab.board.one.BoardResult;
import kr.me.ansr.tab.friends.MySpinnerAdapter;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;

/**
 * Created by KMS on 2016-10-06.
 */
public class ReportFormDialogFragment extends DialogFragment {

    public static final String TAG_TAB_THREE = "tabThree";
    public static final String TAG_BOARD_DETAIL = "BoardDetail";

    public static final String TAG_TAB_ONE_UNIV = "tabOneUniv";
    public static final String TAG_TAB_ONE_MY = "tabOneMy";


    public ReportFormDialogFragment() {

    }

    Button btn2, btn3;
    FriendsResult fItem;
    BoardResult bItem;
    String tag = null;
    int userId = Integer.parseInt(PropertyManager.getInstance().getUserId());

    Spinner spinner;
    MySpinnerAdapter mySpinnerAdapter;
    Handler mHandler = new Handler(Looper.getMainLooper());
    int spinnerPos = 0;

    public static ReportFormDialogFragment newInstance() {
        ReportFormDialogFragment f = new ReportFormDialogFragment();
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

        View view = inflater.inflate(R.layout.dialog_report_form_layout, container, false);


        btn2 = (Button) view.findViewById(R.id.btn_report_form_mid);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerPos == 0){
                    Toast.makeText(getActivity(), "신고사유를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tag != null) {
                    if (tag.equals(TAG_TAB_ONE_UNIV)) {
                        if(true){
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                  리스트 삭제하는 프로세스 진행
                                    FriendsSectionFragment.reportUser(spinnerPos);
                                }
                            }, 500);
                        }
                        dismiss();
                    }
                    if (tag.equals(TAG_TAB_ONE_MY)) {
                        Toast.makeText(getActivity(), "tab one my report btn", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                    if (tag.equals(TAG_TAB_THREE)) {
                        Toast.makeText(getActivity(), "tab three report btn", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    if (tag.equals(TAG_BOARD_DETAIL)) {
                        Toast.makeText(getActivity(), "detail report btn", Toast.LENGTH_SHORT).show();
                        dismiss();
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
        initData();

        return view;
    }

    private void initData() {
        String[] arr = getResources().getStringArray(R.array.report_items);
        for (int i = 0; i < arr.length; i++) {
            mySpinnerAdapter.add(arr[i]);
        }
        spinner.setSelection(0);

        switch (tag) {
            case TAG_TAB_ONE_UNIV:
                break;
            case TAG_TAB_ONE_MY:
                break;
        }

    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
//		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
//		getDialog().setTitle("Custom Dialog");
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