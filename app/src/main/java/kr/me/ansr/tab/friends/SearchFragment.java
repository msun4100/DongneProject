package kr.me.ansr.tab.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import kr.me.ansr.R;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;

/**
 * Created by KMS on 2016-08-31.
 */

/*프래그먼트 호출로 안하고 GONE/VISIBLE 처리로 구현함*/

public class SearchFragment extends Fragment{
    private String TAG = SearchFragment.class.getSimpleName();

    public static SearchFragment newInstance() {
        SearchFragment f = new SearchFragment();
        return f;
    }


    EditText inputName, inputYear, inputDept, inputJob;
    TextView confirmView, cancelView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_layout, container, false);
        inputName = (EditText)v.findViewById(R.id.edit_search_name);
        inputYear = (EditText)v.findViewById(R.id.edit_search_enteryear);
        inputDept = (EditText)v.findViewById(R.id.edit_search_dept);
        inputJob = (EditText)v.findViewById(R.id.edit_search_job);
        confirmView = (TextView)v.findViewById(R.id.text_search_confirm);
        confirmView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
        cancelView = (TextView)v.findViewById(R.id.text_search_cancel);
        cancelView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //nullPointerException 발생
//                ((FriendsSectionFragment)getParentFragment()).setContainerVisibility();

            }
        });
        return v;
    }
    public void clearSearchInput(){
        inputName.setText(""); inputYear.setText(""); inputDept.setText(""); inputJob.setText("");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
    @Override
    public void onResume() {
        super.onResume();
        // destroy all menu and re-call onCreateOptionsMenu
//        getActivity().invalidateOptionsMenu();
//        Toast.makeText(getActivity(),"Slideshow f onresume", Toast.LENGTH_SHORT).show();
    }


    private void sendImageToOnActivityResult(String path){
        String filePath = path;
        Intent intent = new Intent();
        intent.putExtra("filePath", filePath);

//        getActivity().setResult(MediaStoreActivity.RC_SELECT_PROFILE_CODE, intent);
    }

}
