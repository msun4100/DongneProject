package kr.me.ansr.login.autocomplete.univ;

import java.io.Serializable;

/**
 * Created by KMS on 2016-07-14.
 */
public class UnivResult implements Serializable {
    public int univId;
    public String univname;
    public int total;

    @Override
    public String toString() {
        return univname ;
    }

    public int getUnivId(){
        return univId;
    }

}
