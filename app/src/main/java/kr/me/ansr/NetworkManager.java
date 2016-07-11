package kr.me.ansr;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import kr.me.ansr.login.LoginInfo;
import kr.me.ansr.login.LoginResult;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {
	private static NetworkManager instance;
	public static NetworkManager getInstance() {
		if (instance == null) {
			instance = new NetworkManager();
		}
		return instance;
	}
	
//	private NetworkManager() {
//
//	}

//	Handler mHandler = new Handler(Looper.getMainLooper());
	
	public interface OnLoginResultListener {
		public void onSuccess(String message);
	}
	public void login(String id, String password, final OnLoginResultListener listener) {
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				listener.onSuccess("success");
			}
		}, 1000);
	}

    OkHttpClient mClient;
    private static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;

    private NetworkManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Context context = MyApplication.getContext();
        File cachefile = new File(context.getExternalCacheDir(), "mycache");
        if (!cachefile.exists()) {
            cachefile.mkdirs();
        }
        Cache cache = new Cache(cachefile, MAX_CACHE_SIZE);
        builder.cache(cache);

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
        builder.cookieJar(new JavaNetCookieJar(cookieManager));

//        disableCertificateValidation(context, builder);

        mClient = builder.build();
    }

    static void disableCertificateValidation(Context context, OkHttpClient.Builder builder) {

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.client);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, tmf.getTrustManagers(), null);
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            sc.init(null, tmf.getTrustManagers(), null);
            builder.sslSocketFactory(sc.getSocketFactory());
            builder.hostnameVerifier(hv);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void cancelAll() {
        mClient.dispatcher().cancelAll();
    }

    public void cancelTag(Object tag) {
        Dispatcher dispatcher = mClient.dispatcher();
        List<Call> calls = dispatcher.queuedCalls();
        for (Call call : calls) {
            if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
        calls = dispatcher.runningCalls();
        for (Call call : calls) {
            if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
    }

    public interface OnResultListener<T> {
        public void onSuccess(Request request, T result);

        public void onFailure(Request request, int code, Throwable cause);
    }

    private static final int MESSAGE_SUCCESS = 0;
    private static final int MESSAGE_FAILURE = 1;

    static class NetworkHandler extends Handler {
        public NetworkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CallbackObject object = (CallbackObject) msg.obj;
            Request request = object.request;
            OnResultListener listener = object.listener;
            switch (msg.what) {
                case MESSAGE_SUCCESS:
                    listener.onSuccess(request, object.result);
                    break;
                case MESSAGE_FAILURE:
                    listener.onFailure(request, -1, object.exception);
                    break;
            }
        }
    }

    Handler mHandler = new NetworkHandler(Looper.getMainLooper());

    static class CallbackObject<T> {
        Request request;
        T result;
        IOException exception;
        OnResultListener<T> listener;
    }

    public void cancelAll(Object tag) {

    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//    private static final String SERVER_URL = "http://ec2-52-79-166-141.ap-northeast-2.compute.amazonaws.com:3000";
    private static final String SERVER_URL = "http://10.0.3.2:3000";
    private static final String URL_LOGIN = SERVER_URL + "/account/login";
    public Request postDongneLogin(Context context, String email, String password, final OnResultListener<LoginInfo> listener) {
        try {
//            String url = String.format(URL_LOGIN, URLEncoder.encode(keyword, "utf-8")); //get method
            String url = URL_LOGIN;
            final CallbackObject<LoginInfo> callbackObject = new CallbackObject<LoginInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("email", email);
            json.addProperty("password", password);
            json.addProperty("pushId", PropertyManager.getInstance().getRegistrationId());
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder().url(url)
//                    .header("Accept", "application/json") //API전용 헤더인듯?
                    .post(body)
                    .tag(context)
                    .build();

            callbackObject.request = request;
            callbackObject.listener = listener;
            mClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callbackObject.exception = e;
                    Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Gson gson = new Gson();
                    LoginInfo result = gson.fromJson(response.body().charStream(), LoginInfo.class);
                    callbackObject.result = result;
                    Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                    mHandler.sendMessage(msg);
                }
            });

            return request;

        } catch (JsonParseException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        return null;
    }



//    private static final String URL_TMAP_POI = "https://apis.skplanetx.com/tmap/pois?version=1&resCoordType=WGS84GEO&reqCoordType=WGS84GEO&searchKeyword=%s";
//
//    public Request getTmapPOI(Context context, String keyword, final OnResultListener<TMapPOIList> listener) {
//        try {
//            String url = String.format(URL_TMAP_POI, URLEncoder.encode(keyword, "utf-8"));
//            final CallbackObject<TMapPOIList> callbackObject = new CallbackObject<TMapPOIList>();
//
//            Request request = new Request.Builder().url(url)
//                    .header("Accept", "application/json")
//                    .header("appKey", "6aeffc95-8ebc-3f73-92e9-8a4d8f797c6b")
//                    .tag(context)
//                    .build();
//
//            callbackObject.request = request;
//            callbackObject.listener = listener;
//            mClient.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    callbackObject.exception = e;
//                    Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
//                    mHandler.sendMessage(msg);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    Gson gson = new Gson();
//                    TMapPOISearchResult result = gson.fromJson(response.body().charStream(), TMapPOISearchResult.class);
//                    for (POIItem item : result.searchPoiInfo.pois.poi) {
//                        item.updatePOIData();
//                    }
//                    callbackObject.result = result.searchPoiInfo;
////                XMLParser parser = new XMLParser();
////                NaverMovies movies = parser.fromXml(response.body().byteStream(), "channel", NaverMovies.class);
////                callbackObject.result = movies;
//                    Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
//                    mHandler.sendMessage(msg);
//                }
//            });
//
//            return request;
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
