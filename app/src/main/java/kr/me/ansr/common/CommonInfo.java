package kr.me.ansr.common;


import kr.me.ansr.login.LoginResult;

public class CommonInfo {
	public static final int RESPONSE_SUCCESS = 1;
	public static final int RESPONSE_FAILURE = 0;
	public static final String RESPONSE_WORK = "work";
	public int success;
	public String msg;

    @Override
    public String toString() {
        return "CommonInfo{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                '}';
    }
}
