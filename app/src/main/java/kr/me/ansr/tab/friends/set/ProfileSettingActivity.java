package kr.me.ansr.tab.friends.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kr.me.ansr.R;
import kr.me.ansr.common.PhotoChangeDialogFragment;
import kr.me.ansr.tab.friends.model.FriendsResult;

public class ProfileSettingActivity extends AppCompatActivity {
    TextView toolbarTitle; ImageView iconPhoto, toolbarMenu;

    FriendsResult mItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.c_artboard_final_title_cancel);
        toolbar.setBackgroundResource(R.drawable.c_artboard_final_setting_title1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarMenu = (ImageView)toolbar.findViewById(R.id.toolbar_menu1);
        toolbarMenu.setImageResource(R.drawable.d_title_edit_selector);
        Intent intent = getIntent();
        if (intent != null) {
            mItem = (FriendsResult)intent.getSerializableExtra("mItem");
            if(mItem == null) forcedFinish();
        } else {
            forcedFinish();
        }

        iconPhoto = (ImageView)findViewById(R.id.image_prof_set_change);
        iconPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PhotoChangeDialogFragment mDialogFragment = PhotoChangeDialogFragment.newInstance();
                Bundle b = new Bundle();
                b.putSerializable("userInfo", mItem);
                mDialogFragment.setArguments(b);
                mDialogFragment.show(getSupportFragmentManager(), "photoChangeDialog");
            }
        });
        toolbarMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileSettingActivity.this, "toolbar_menu", Toast.LENGTH_SHORT).show();
            }
        });
        init();
    }

    private void init(){
        toolbarTitle.setText("");
    }
    private void forcedFinish(){
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
