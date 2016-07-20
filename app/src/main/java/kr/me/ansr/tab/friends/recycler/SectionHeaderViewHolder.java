package kr.me.ansr.tab.friends.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-07-20.
 */
public class SectionHeaderViewHolder extends RecyclerView.ViewHolder{
    TextView titleView;
    public SectionHeaderViewHolder(View itemView) {
        super(itemView);
        titleView = (TextView)itemView.findViewById(R.id.text_section_header_title);
    }

    public void setGroupItem(GroupItem group) {
        titleView.setText(group.groupName);
    }
}
