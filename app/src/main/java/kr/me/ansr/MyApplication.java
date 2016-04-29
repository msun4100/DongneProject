package kr.me.ansr;

import android.app.Application;
import android.content.Context;
import android.view.View;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import kr.me.ansr.tab.chat.socket.Constants;

public class MyApplication extends Application {
	private static Context mContext;
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		try {
			mSocket = IO.socket(Constants.CHAT_SERVER_URL);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

	}
	public static Context getContext() {return mContext;}

	private Socket mSocket;
//	{
//		try {
//			mSocket = IO.socket(Constants.CHAT_SERVER_URL);
//		} catch (URISyntaxException e) {
//			throw new RuntimeException(e);
//		}
//	}
	public Socket getSocket() {
		return mSocket;
	}

}
