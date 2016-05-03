package kr.me.ansr;

import android.content.Context;
import android.content.SharedPreferences;

public class PropertyManager {
	private static PropertyManager instance;
	public static PropertyManager getInstance() {
		if (instance == null) {
			instance = new PropertyManager();
		}
		return instance;
	}
	
	private static final String PREF_NAME = "mypref";
	SharedPreferences mPrefs;
	SharedPreferences.Editor mEditor;
	
	private PropertyManager() {
		mPrefs = MyApplication.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		mEditor = mPrefs.edit();
	}
	
	private static final String FIELD_USER_NAME = "username";
	private String mUserName;
	
	public String getUserName() {
		if (mUserName == null) {
			mUserName = mPrefs.getString(FIELD_USER_NAME, "");
		}
		return mUserName;
	}
	
	public void setUserName(String name) {
		mUserName = name;
		mEditor.putString(FIELD_USER_NAME, name);
		mEditor.commit();
	}
	
	private static final String FIELD_USER_ID = "userId";
	private String mUserId;

	public String getUserId() {
		if (mUserId == null) {
			mUserId = mPrefs.getString(FIELD_USER_ID, "");
		}
		return mUserId;
	}

	public void setUserId(String userId) {
		mUserId = userId;
		mEditor.putString(FIELD_USER_ID, userId);
		mEditor.commit();
	}

    private static final String FIELD_EMAIL = "EMAIL";
    private String mEmail;

    public String getEmail() {
        if (mEmail == null) {
            mEmail = mPrefs.getString(FIELD_EMAIL, "");
        }
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
        mEditor.putString(FIELD_EMAIL, email);
        mEditor.commit();
    }


	private static final String FIELD_PASSWORD = "password";
	private String mPassword;
	
	public String getPassword() {
		if (mPassword == null) {
			mPassword = mPrefs.getString(FIELD_PASSWORD, "");
		}
		return mPassword;
	}
	
	public void setPassword(String password) {
		mPassword = password;
		mEditor.putString(FIELD_PASSWORD, password);
		mEditor.commit();
	}


	private static final String FIELD_REGISTRATION_ID = "regid";
	private String regid;

	public void setRegistrationId(String regid) {

		this.regid = regid;
		mEditor.putString(FIELD_REGISTRATION_ID, regid);
		mEditor.commit();
	}

	public String getRegistrationId() {
		if (regid == null) {
			regid = mPrefs.getString(FIELD_REGISTRATION_ID, "");
		}
		return regid;
	}

	
}
