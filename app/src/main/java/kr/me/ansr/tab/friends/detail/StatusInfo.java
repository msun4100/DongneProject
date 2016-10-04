package kr.me.ansr.tab.friends.detail;

import kr.me.ansr.common.CommonInfo;

/**
 * Created by KMS on 2016-09-06.
 */
public class StatusInfo{
    public final static int STATUS_CANCEL = -1;
    public final static int STATUS_PENDING = 0;
    public final static int STATUS_ACCEPTED = 1;
    public final static int STATUS_DECLINED = 2;
    public final static int STATUS_BLOCKED = 3;

    public Boolean error;
    public String message;
    public StatusResult result;

}
