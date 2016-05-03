package kr.me.ansr.login;

import java.io.Serializable;

public class LoginResult {
	public int lastLogin;

    @Override
    public String toString() {
        return "LoginResult{" +
                "lastLogin=" + lastLogin +
                '}';
    }
}
