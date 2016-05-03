package kr.me.ansr.login;


import kr.me.ansr.common.CommonInfo;

public class LoginInfo extends CommonInfo{
	LoginResult result;

    @Override
    public String toString() {
        return super.toString() + result;
    }
}
