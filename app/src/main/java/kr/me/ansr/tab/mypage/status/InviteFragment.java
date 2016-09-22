package kr.me.ansr.tab.mypage.status;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import kr.me.ansr.R;
import kr.me.ansr.tab.friends.recycler.FriendsSectionFragment;

/**
 * Created by KMS on 2016-08-31.
 */


public class InviteFragment extends Fragment {
    private String TAG = InviteFragment.class.getSimpleName();

    public static InviteFragment newInstance() {
        InviteFragment f = new InviteFragment();
        return f;
    }

    TextView kakao, fb, sms;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_invite, container, false);

        kakao = (TextView)v.findViewById(R.id.text_invite_kakao);
        fb = (TextView)v.findViewById(R.id.text_invite_fb);
        sms = (TextView)v.findViewById(R.id.text_invite_sms);
        kakao.setOnClickListener(mListener);
        fb.setOnClickListener(mListener);
        sms.setOnClickListener(mListener);
        return v;
    }

    public View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.text_invite_kakao:
//                    break;
                case R.id.text_invite_fb:
//                    break;
                case R.id.text_invite_sms:
//                    break;
                default:
                    Toast.makeText(getActivity(), "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

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

