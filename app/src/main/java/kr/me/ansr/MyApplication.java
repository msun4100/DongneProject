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


import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
		return currentDateandTime;
	}

	public static String getTimeStamp(String dateStr) {
//        2016-07-06T05:47:19.000Z
		Calendar calendar = Calendar.getInstance();
		String today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		String year = String.valueOf(calendar.get(Calendar.YEAR));
//		Log.e("My year: ", ""+year);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String timestamp = "";
		today = today.length() < 2 ? "0" + today : today;
		try {
			Date date = format.parse(dateStr);
//			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREAN);
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			String dateYear = yearFormat.format(date);
			if(year.equals(dateYear)){
//				Log.e("year check", "true"); Log.e("dateYear", ""+dateYear);
				SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
				String dateToday = todayFormat.format(date);
//				format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("MMM dd, hh:mm a");	//MMM이랑 LLL 이랑 같은 듯 MMM == "x월"
//				format = dateToday.equals(today) ? new SimpleDateFormat("a hh:mm", Locale.KOREAN) : new SimpleDateFormat("MM.dd E ahh:mm", Locale.KOREAN);
				format = dateToday.equals(today) ? new SimpleDateFormat("a hh:mm") : new SimpleDateFormat("MM.dd ahh:mm");
				String date1 = format.format(date);
				timestamp = date1.toString();
			} else {
//				Log.e("year check", "false");
				SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
				format = new SimpleDateFormat("yyyy.MM.dd a hh:mm");
				String date1 = format.format(date);
				timestamp = date1.toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return timestamp;
	}


}
