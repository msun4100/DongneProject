package kr.me.ansr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import kr.me.ansr.tab.friends.model.FriendsResult;

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
//	setUsingLocation(0);
	setUnivId("");
	setUserId("");
	setUserName("");
	setProfile("");
	setUnivName("");
//	setNewCount(0);
//	setRegistrationId("");
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

	private static final String FIELD_JOB_PUBLIC = "jobPublic";
	private int mJobPublic; // 0 null 1잡네임만공개 2잡팀만 공개 3잡네임잡팀 공개

	public int getJobPublic() {
		if (mJobPublic == 0) {
			mJobPublic = mPrefs.getInt(FIELD_JOB_PUBLIC, 0);
		}
		return mJobPublic;
	}

	public void setJobPublic(int jobPublic) {
		mJobPublic = jobPublic;
		mEditor.putInt(FIELD_JOB_PUBLIC, jobPublic);
		mEditor.commit();
	}
	// is job public


	//프로필 이미지
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
	//user's univName
	private static final String FIELD_UNIV_NAME = "univName";
	private String mUnivName;

	public String getUnivName(){
		if (mUnivName == null) {
			mUnivName = mPrefs.getString(FIELD_UNIV_NAME, "");
		}
		return mUnivName;
	}
	public void setUnivName(String univName) {
		mUnivName = univName;
		mEditor.putString(FIELD_UNIV_NAME, univName);
		mEditor.commit();
	}

	//univId
	private static final String FIELD_UNIV_ID = "univId";
	private String mUnivId;

	public String getUnivId(){
		if (mUnivId == null) {
			mUnivId = mPrefs.getString(FIELD_UNIV_ID, "");
		}
		return mUnivId;
	}
	public void setUnivId(String univId) {
		mUnivId = univId;
		mEditor.putString(FIELD_UNIV_ID, univId);
		mEditor.commit();
	}

	//deptId
	private static final String FIELD_DEPT_ID = "deptId";
	private String mDeptId;

	public String getDeptId(){
		if (mDeptId == null) {
			mDeptId = mPrefs.getString(FIELD_DEPT_ID, "");
		}
		return mDeptId;
	}
	public void setDeptId(String deptId) {
		mDeptId = deptId;
		mEditor.putString(FIELD_DEPT_ID, deptId);
		mEditor.commit();
	}

	//user's deptName
	private static final String FIELD_DEPT_NAME = "deptName";
	private String mDeptName;

	public String getDeptName(){
		if (mDeptName == null) {
			mDeptName = mPrefs.getString(FIELD_DEPT_NAME, "");
		}
		return mDeptName;
	}
	public void setDeptName(String deptName) {
		mDeptName = deptName;
		mEditor.putString(FIELD_DEPT_NAME, deptName);
		mEditor.commit();
	}
	//user's enterYear
	private static final String FIELD_ENTER_YEAR = "enterYear";
	private String mEnterYear;

	public String getEnterYear(){
		if (mEnterYear == null) {
			mEnterYear = mPrefs.getString(FIELD_ENTER_YEAR, "");
		}
		return mEnterYear;
	}
	public void setEnterYear(String enterYear) {
		mEnterYear = enterYear;
		mEditor.putString(FIELD_ENTER_YEAR, enterYear);
		mEditor.commit();
	}

	//user's isGraduate
	private static final String FIELD_IS_GRADUATE = "isGraduate";
	private String mIsGraduate;

	public String getIsGraduate(){
		if (mIsGraduate == null) {
			mIsGraduate = mPrefs.getString(FIELD_IS_GRADUATE, "");
		}
		return mIsGraduate;
	}
	public void setIsGraduate(String isGraduate) {
		mIsGraduate = isGraduate;
		mEditor.putString(FIELD_IS_GRADUATE, isGraduate);
		mEditor.commit();
	}

	private static final String FIELD_ALARM_ALL = "alarmall";
	private int alarmAll = 2; // 0안씀 1사용, 2로 초기화해서 최초 설정창에 스위치가 on이 되도록

	public int getAlarmAll() {
		if (alarmAll == 0) {
			alarmAll = mPrefs.getInt(FIELD_ALARM_ALL, 0);
		}
		return alarmAll;
	}

	public void setAlarmAll(int alarm) {
		alarmAll = alarm;
		mEditor.putInt(FIELD_ALARM_ALL, alarmAll);
		mEditor.commit();
	}

	private static final String FIELD_ALARM_FRIEND = "alarmFriend";
	private int alarmFriend = 2; // 0안씀 1사용, 2로 초기화해서 최초 설정창에 스위치가 on이 되도록

	public int getAlarmFriend() {
		if (alarmFriend == 0) {
			alarmFriend = mPrefs.getInt(FIELD_ALARM_FRIEND, 0);
		}
		return alarmFriend;
	}

	public void setAlarmFriend(int alarm) {
		alarmFriend = alarm;
		mEditor.putInt(FIELD_ALARM_FRIEND, alarmFriend);
		mEditor.commit();
	}

	private static final String FIELD_ALARM_CHAT = "alarmChat";
	private int alarmChat = 2; // 0안씀 1사용, 2로 초기화해서 최초 설정창에 스위치가 on이 되도록
	public int getAlarmChat() {
		if (alarmChat == 0) {
			alarmChat = mPrefs.getInt(FIELD_ALARM_CHAT, 0);
		}
		return alarmChat;
	}
	public void setAlarmChat(int alarm) {
		alarmChat = alarm;
		mEditor.putInt(FIELD_ALARM_CHAT, alarmChat);
		mEditor.commit();
	}

	private static final String FIELD_ALARM_REPLY = "alarmReply";
	private int alarmReply = 2; // 0안씀 1사용, 2로 초기화해서 최초 설정창에 스위치가 on이 되도록
	public int getAlarmReply() {
		if (alarmReply == 0) {
			alarmReply = mPrefs.getInt(FIELD_ALARM_REPLY, 0);
		}
		return alarmReply;
	}
	public void setAlarmReply(int alarm) {
		alarmReply = alarm;
		mEditor.putInt(FIELD_ALARM_REPLY, alarmReply);
		mEditor.commit();
	}

	private static final String FIELD_ALARM_LIKE = "alarmLike";
	private int alarmLike = 2; // 0안씀 1사용, 2로 초기화해서 최초 설정창에 스위치가 on이 되도록
	public int getAlarmLike() {
		if (alarmLike == 0) {
			alarmLike = mPrefs.getInt(FIELD_ALARM_LIKE, 0);
		}
		return alarmLike;
	}
	public void setAlarmLike(int alarm) {
		alarmLike = alarm;
		mEditor.putInt(FIELD_ALARM_LIKE, alarmLike);
		mEditor.commit();
	}

	private static final String FIELD_ALARM_RING_TONE = "alarmRingtone";
	private int alarmRingtone = 2; // 0안씀 1사용, 2로 초기화해서 최초 설정창에 스위치가 on이 되도록
	public int getAlarmRingtone() {
		if (alarmRingtone == 0) {
			alarmRingtone = mPrefs.getInt(FIELD_ALARM_RING_TONE, 0);
		}
		return alarmRingtone;
	}
	public void setAlarmRingtone(int alarm) {
		alarmRingtone = alarm;
		mEditor.putInt(FIELD_ALARM_RING_TONE, alarmRingtone);
		mEditor.commit();
	}

	//user's jobname
	private static final String FIELD_JOB_NAME = "jobName";
	private String mJobName;

	public String getJobName(){
		if (mJobName == null) {
			mJobName = mPrefs.getString(FIELD_JOB_NAME, "");
		}
		return mJobName;
	}
	public void setJobName(String jobName) {
		mJobName = jobName;
		mEditor.putString(FIELD_JOB_NAME, jobName);
		mEditor.commit();
	}

	//user's jobteam
	private static final String FIELD_JOB_TEAM = "jobTeam";
	private String mJobTeam;

	public String getJobTeam(){
		if (mJobTeam == null) {
			mJobTeam = mPrefs.getString(FIELD_JOB_TEAM, "");
		}
		return mJobTeam;
	}
	public void setJobTeam(String jobTeam) {
		mJobTeam = jobTeam;
		mEditor.putString(FIELD_JOB_NAME, jobTeam);
		mEditor.commit();
	}

	//new Count
	private static final String FIELD_NEW_COUNT = "newCount";
	private int mNewCount;

	public int getNewCount(){
		if (mNewCount == 0) {
			mNewCount = mPrefs.getInt(FIELD_NEW_COUNT, 0);
		}
		return mNewCount;
	}
	public void setNewCount(int count) {
		mNewCount = count;
		mEditor.putInt(FIELD_NEW_COUNT, count);
		mEditor.commit();
	}


	//user's last update
	private static final String FIELD_LAST_UPDATE = "lastUpdate";
	private String mLastUpdate;

	public String getLastUpdate(){
		if (mLastUpdate == null) {
			mLastUpdate = mPrefs.getString(FIELD_LAST_UPDATE, "");
		}
		return mLastUpdate;
	}
	public void setLastUpdate(String timestamp) {
		mLastUpdate = timestamp;
		mEditor.putString(FIELD_LAST_UPDATE, timestamp);
		mEditor.commit();
	}



	//==============================
	public void storeUser(FriendsResult user) {
		setProfile(user.pic.small);
		setLastUpdate(user.updatedAt);
		setJobName(user.job.name);
		setJobTeam(user.job.team);
		setDeptName(user.univ.get(0).deptname);
		Log.e("PropertyManager", "(FriendsResult)User is stored in shared preferences. " + user.job.name + ", " + user.job.team);
	}

	public FriendsResult getUser() {
		if(getUserId() != null && getEmail() != null){
			FriendsResult user = new FriendsResult();
			user.univ.get(0).univId = Integer.valueOf(getUnivId());
			user.userId = Integer.valueOf(getUserId());
			user.email = getEmail();
			user.location.lat = Double.valueOf(getLatitude());
			user.location.lon = Double.valueOf(getLatitude());
			user.pic.small = getProfile();

			user.univ.get(0).univId = Integer.valueOf(getUnivId());
			user.univ.get(0).deptname = getDeptName();
			user.univ.get(0).deptId = Integer.valueOf(getDeptId());
			user.univ.get(0).enterYear = Integer.valueOf(getEnterYear());
			return user;
		}
		return null;
	}

}
