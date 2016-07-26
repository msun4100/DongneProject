package kr.me.ansr;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String currentDateandTime = sdf.format(new Date());
		return currentDateandTime;
	}
}
