package kr.me.ansr;

import android.content.Context;
import android.content.Intent;
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

public void clearProperties() {
//	mPrefs.edit().clear().commit();
	setEmail("");
	setPassword("");
	setLatitude("");
	setLongitude("");
	setUsingLocation(0);
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

	//for chatting push notifications
	private static final String FIELD_IS_TAB2_VISIBLE = "istab2visible";
	private String isTab2Visible;

	public void setIsTab2Visible(String isVisible) {
		this.isTab2Visible = isVisible;
		mEditor.putString(FIELD_IS_TAB2_VISIBLE, isVisible);
		mEditor.commit();
	}
	public String getIsTab2Visible() {
		if (isTab2Visible == null) {
			isTab2Visible = mPrefs.getString(FIELD_IS_TAB2_VISIBLE, "");
		}
		return isTab2Visible;
	}

	// for Location info
	private static final String FIELD_LOCATION_LATITUDE = "latitude";
	private String mLatitude;

	public String getLatitude() {
		if (mLatitude == null) {
			mLatitude = mPrefs.getString(FIELD_LOCATION_LATITUDE, null);
		}
		return mLatitude;
	}
	public void setLatitude(String latitude) {
		mLatitude = latitude;
		mEditor.putString(FIELD_LOCATION_LATITUDE, latitude);
		mEditor.commit();
	}

	private static final String FIELD_LOCATION_LONGITUDE = "longitude";
	private String mLongitude;

	public String getLongitude() {
		if (mLongitude == null) {
			mLongitude = mPrefs.getString(FIELD_LOCATION_LONGITUDE, null);
		}
		return mLongitude;
	}
	public void setLongitude(String longitude) {
		mLongitude = longitude;
		mEditor.putString(FIELD_LOCATION_LONGITUDE, longitude);
		mEditor.commit();
	}

	private static final String FIELD_USING_LOCATION = "usinglocation";
	private int mUsingLocation; // 0안씀 1사용 2다시보지않기

	public int getUsingLocation() {
		if (mUsingLocation == 0) {
			mUsingLocation = mPrefs.getInt(FIELD_USING_LOCATION, 0);
		}
		return mUsingLocation;
	}

	public void setUsingLocation(int usingLocation) {
		mUsingLocation = usingLocation;
		mEditor.putInt(FIELD_USING_LOCATION, usingLocation);
		mEditor.commit();
	}
	// for Location info

	private static final String FIELD_PROFILE = "profile";
	private String mProfile;

	public String getProfile(){
		if (mProfile == null) {
			mProfile = mPrefs.getString(FIELD_PROFILE, "");
		}
		return mProfile;
	}
	public void setProfile(String profile) {
		mProfile = profile;
		mEditor.putString(FIELD_PROFILE, profile);
		mEditor.commit();
	}


}
