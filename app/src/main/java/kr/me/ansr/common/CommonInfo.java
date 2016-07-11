package kr.me.ansr.common;


import java.io.Serializable;

public class CommonInfo {
	boolean error;
    String message;

    @Override
    public String toString() {
        return "CommonInfo{" +
                "error=" + error +
                ", message='" + message + '\'' +
                '}';
    }
    public CommonInfo(){}
    public CommonInfo(boolean error, String message) {
        this.error = error;
        this.message = message;
    }
}
