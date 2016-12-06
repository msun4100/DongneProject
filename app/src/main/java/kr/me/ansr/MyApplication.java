package kr.me.ansr;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;


import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import kr.me.ansr.gcmchat.helper.MyPreferenceManager;

public class MyApplication extends Application {
	public static final String TAG = MyApplication.class.getSimpleName();

	private RequestQueue mRequestQueue;	//volley queue

	private static MyApplication mInstance;
    private static Context mContext;
	private MyPreferenceManager pref;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		mInstance = this;
	}

	public static Context getContext() {return mContext;}
    public static MyApplication getInstance() {
        return mInstance;
    }


	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public MyPreferenceManager getPrefManager() {
		if (pref == null) {
			pref = new MyPreferenceManager(this);
		}

		return pref;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public void logout() {
		pref.clear();
		Intent intent = new Intent(this, kr.me.ansr.gcmchat.activity.LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	public String getCurrentTimeStampString(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currentDateandTime = sdf.format(new Date());
		return currentDateandTime;	//서버 시간으로 리턴
	}

	public String getKoreanTimeStampString(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREAN);
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currentDateandTime = sdf.format(new Date());
		return currentDateandTime;	//국내 시간 && 새로운 포맷으로 리턴
	}

	public static String getTimeStamp(String dateStr) {	//dateStr == Server's timeStamp.(UTC)
//        2016-07-06T05:47:19.000Z
//		Log.d(TAG, "getTimeStamp: 0 "+dateStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());	// == TimeZone.getTimeZone("Asia/Seoul")
		String today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");	//UTC 포맷
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
//		Log.d(TAG, "getTimeStamp: 1 "+format.getTimeZone());
		String timestamp = "";
		today = today.length() < 2 ? "0" + today : today;
		try {
			Date date = format.parse(dateStr);	//UTC기준 dateStr을 parsing.
//			Log.d(TAG, "getTimeStamp: 2 "+date);
//			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREAN);
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			yearFormat.setTimeZone(TimeZone.getDefault());
			String dateYear = yearFormat.format(date);
			if(year.equals(dateYear)){
//				Log.e("year check", "true"); Log.e("dateYear", ""+dateYear);
				SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
				todayFormat.setTimeZone(TimeZone.getDefault());
				String dateToday = todayFormat.format(date);
//				format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("MMM dd, hh:mm a");	//MMM이랑 LLL 이랑 같은 듯 MMM == "x월"
//				format = dateToday.equals(today) ? new SimpleDateFormat("a hh:mm", Locale.KOREAN) : new SimpleDateFormat("MM.dd E ahh:mm", Locale.KOREAN);
				format = dateToday.equals(today) ? new SimpleDateFormat("a hh:mm") : new SimpleDateFormat("MM.dd a hh:mm");
				String date1 = format.format(date);
				timestamp = date1.toString();
			} else {
//				Log.e("year check", "false");
				SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
				todayFormat.setTimeZone(TimeZone.getDefault());
				format = new SimpleDateFormat("yyyy.MM.dd a hh:mm");
				String date1 = format.format(date);
				timestamp = date1.toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return timestamp;
	}


	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 *
	 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
	 * storing them all in Application object helps ensure that they are created only once per
	 * application instance.
	 */
	private static final String PROPERTY_ID = "UA-87692023-1";

	public enum TrackerName {
		APP_TRACKER,           // 앱 별로 트래킹
		GLOBAL_TRACKER,        // 모든 앱을 통틀어 트래킹
		ECOMMERCE_TRACKER,     // 아마 유료 결재 트래킹 개념 같음
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

	public synchronized Tracker getTracker(TrackerName trackerId){
		if(!mTrackers.containsKey(trackerId)){
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) :
					(trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker) :
							analytics.newTracker(R.xml.ecommerce_tracker);
			mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}

//	public void startGA(Context context){
//		GoogleAnalytics.getInstance(this).reportActivityStart(context);
//	}
//	public void stopGA(Context context){
//		GoogleAnalytics.getInstance(this).reportActivityStop(context);
//	}

}
