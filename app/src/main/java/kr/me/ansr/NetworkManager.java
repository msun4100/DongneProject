package kr.me.ansr;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import kr.me.ansr.common.CommonInfo;
import kr.me.ansr.common.account.FindAccountInfo;
import kr.me.ansr.common.account.FindAccountModel;
import kr.me.ansr.gcmchat.app.Config;
import kr.me.ansr.gcmchat.model.ChatInfo;
import kr.me.ansr.login.LoginInfo;
import kr.me.ansr.login.autocomplete.dept.DeptInfo;
import kr.me.ansr.login.autocomplete.univ.UnivInfo;
import kr.me.ansr.tab.board.CommentInfo;
import kr.me.ansr.tab.board.WriteInfo;
import kr.me.ansr.tab.board.like.LikeInfo;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.chat.ChatRoomInfo;
import kr.me.ansr.tab.friends.detail.StatusInfo;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.mypage.mywriting.tabtwo.MyCommentInfo;
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

    private static final MediaType CONTENT_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
//    private static final String SERVER_URL = "http://ec2-52-78-118-216.ap-northeast-2.compute.amazonaws.com:3000";
//    private static final String SERVER_URL = "http://10.0.3.2:3000";
    private static final String SERVER_URL = Config.SERVER_URL;

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
            json.addProperty("univId", PropertyManager.getInstance().getUnivId());
            String lat = PropertyManager.getInstance().getLatitude();
            String lon = PropertyManager.getInstance().getLongitude();
            if(lat == null) lat = "0"; if(lon == null) lon = "0";
            json.addProperty("lat", lat);
            json.addProperty("lon", lon);
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json") //API전용 헤더인듯?
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

    private static final String URL_LOGOUT = SERVER_URL + "/account/logout";
    public Request getDongneLogout(Context context, final OnResultListener<CommonInfo> listener) {
        try {
//            String url = String.format(URL_LOGIN, URLEncoder.encode(keyword, "utf-8")); //get method
            String url = URL_LOGOUT;
            final CallbackObject<CommonInfo> callbackObject = new CallbackObject<CommonInfo>();

            JsonObject json = new JsonObject();
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json") //API전용 헤더인듯?
//                    .post(body)
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
                    CommonInfo result = gson.fromJson(response.body().charStream(), CommonInfo.class);
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

        return null;
    }

    private static final String URL_PUT_USER = SERVER_URL + "/user/:userId";
    public Request putDongnePutUser(Context context, final String token, String userId, final OnResultListener<CommonInfo> listener) {
        try {
            String url = URL_PUT_USER.replace(":userId", userId);

            final CallbackObject<CommonInfo> callbackObject = new CallbackObject<CommonInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("gcm_registration_id", token);
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").put(body).tag(context).build();
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
                    CommonInfo result = gson.fromJson(response.body().charStream(), CommonInfo.class);
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
        return null;
    }

    private static final String URL_USER_EMAIL = SERVER_URL + "/users/email";
    public Request postDongneUserEmail(Context context, String email, final OnResultListener<CommonInfo> listener) {
        try {
            String url = URL_USER_EMAIL;
            final CallbackObject<CommonInfo> callbackObject = new CallbackObject<CommonInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("email", email);
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
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
                    CommonInfo result = gson.fromJson(response.body().charStream(), CommonInfo.class);
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
        return null;
    }

    private static final String URL_UNIV_ID = SERVER_URL + "/univ/:id";
    public Request getDongneUnivItem(Context context, String univId, final OnResultListener<UnivInfo> listener) {
        try {
            String url = URL_UNIV_ID.replace(":id", univId);
            final CallbackObject<UnivInfo> callbackObject = new CallbackObject<UnivInfo>();

            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
//                    .post(body)
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
                    UnivInfo result = gson.fromJson(response.body().charStream(), UnivInfo.class);
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

        return null;
    }

    private static final String URL_UNIV = SERVER_URL + "/univ";
    public Request getDongneUniv(Context context, final OnResultListener<UnivInfo> listener) {
        try {
            String url = URL_UNIV;
            final CallbackObject<UnivInfo> callbackObject = new CallbackObject<UnivInfo>();

            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
//                    .post(body)
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
                    UnivInfo result = gson.fromJson(response.body().charStream(), UnivInfo.class);
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

        return null;
    }
    private static final String URL_DEPT = SERVER_URL + "/dept/:univId";
    public Request getDongneDept(Context context, int univId, final OnResultListener<DeptInfo> listener) {
        try {
            String url = URL_DEPT.replace(":univId", ""+univId);
            final CallbackObject<DeptInfo> callbackObject = new CallbackObject<DeptInfo>();

            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
//                    .post(body)
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
                    DeptInfo result = gson.fromJson(response.body().charStream(), DeptInfo.class);
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
        return null;
    }


    private static final String URL_REGISTER = SERVER_URL + "/account/register";
    public Request postDongneRegister(Context context, String email, String password, String name, int univId, int deptId, String deptname,String enterYear, int isGraudate, String jobname, String jobteam, final OnResultListener<LoginInfo> listener) {
        try {
//            String url = String.format(URL_LOGIN, URLEncoder.encode(keyword, "utf-8")); //get method
            String url = URL_REGISTER;
            final CallbackObject<LoginInfo> callbackObject = new CallbackObject<LoginInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("email", email);
            json.addProperty("password", password);
            json.addProperty("pushId", PropertyManager.getInstance().getRegistrationId());
            json.addProperty("username", name);
            //univ json 객체 생성
            JsonObject univ = new JsonObject();
            univ.addProperty("univId", ""+univId);
            univ.addProperty("deptId", ""+deptId);
            univ.addProperty("deptname", ""+deptname);
            univ.addProperty("enterYear", enterYear);
            univ.addProperty("isGraduate", isGraudate);
            // json 객체 생성
            JsonObject job = new JsonObject();
            job.addProperty("name", jobname);
            job.addProperty("team", jobteam);
            // location 객체 생성
            JsonObject location = new JsonObject();
            String lat = PropertyManager.getInstance().getLatitude();
            String lon = PropertyManager.getInstance().getLongitude();
            if(lat == null) lat = "0"; if(lon == null) lon = "0";
            location.addProperty("lat", lat);
            location.addProperty("lon", lon);

            json.add("univ", univ);
            json.add("job", job);
            json.add("location", location);
//            ============================================

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
    private static final String URL_FRIEND_UNIV_USERS = SERVER_URL + "/friends/univ/:univId";
    public Request getDongneUnivUsers(Context context, String univId, final OnResultListener<FriendsInfo> listener) {
        try {
            String url = URL_FRIEND_UNIV_USERS.replace(":univId", univId);
            final CallbackObject<FriendsInfo> callbackObject = new CallbackObject<FriendsInfo>();

            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
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
                    FriendsInfo result = gson.fromJson(response.body().charStream(), FriendsInfo.class);
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
        return null;
    }

    public Request postDongneUnivUsers(Context context, int mode, String userId, int sort, String univId, String start, String display, String reqDate, final OnResultListener<FriendsInfo> listener) {
        try {
            String url = URL_FRIEND_UNIV_USERS.replace(":univId", ""+univId);
            if(mode == 1){
                url += "/my";
            }

            final CallbackObject<FriendsInfo> callbackObject = new CallbackObject<FriendsInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("userId", userId); //null로 오면 기존 친구탭의 기능이고, userId가 지정되면 친구리스트 보기
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            json.addProperty("sort", ""+sort);
//            json.addProperty("order", ""+1);
            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    FriendsInfo result = gson.fromJson(response.body().charStream(), FriendsInfo.class);
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
        return null;
    }
    private static final String URL_FRIEND_UNIV_USERS_SEARCH = SERVER_URL + "/friends/univ/:univId/search";
    public Request postDongneUnivUsersSearch(Context context, int mode, int sort, String univId, String start, String display, String reqDate, String username, String enterYear, String deptname, String job, final OnResultListener<FriendsInfo> listener) {
        try {
            String url = URL_FRIEND_UNIV_USERS_SEARCH.replace(":univId", ""+univId);
            if(mode == 1){
                url += "/my";
            }

            final CallbackObject<FriendsInfo> callbackObject = new CallbackObject<FriendsInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            json.addProperty("sort", ""+sort);

            json.addProperty("username", username);
            json.addProperty("enterYear", enterYear);
            json.addProperty("deptname", deptname);
            json.addProperty("job", job);

            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    FriendsInfo result = gson.fromJson(response.body().charStream(), FriendsInfo.class);
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
        return null;
    }

    private static final String URL_BOARD_LIST = SERVER_URL + "/board/list/:univId/:tab";
    public Request postDongneBoardList(Context context, String univId, String tab, String start, String display, String reqDate, final OnResultListener<BoardInfo> listener) {
        try {
            String url = URL_BOARD_LIST.replace(":univId", ""+univId).replace(":tab", ""+tab);
            final CallbackObject<BoardInfo> callbackObject = new CallbackObject<BoardInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    BoardInfo result = gson.fromJson(response.body().charStream(), BoardInfo.class);
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
        return null;
    }

    private static final String URL_BOARD_LIST_SEARCH = SERVER_URL + "/board/list/:univId/:tab/search";
    public Request postDongneBoardListSearch(Context context, String univId, String tab, String start, String display, String reqDate, String word, final OnResultListener<BoardInfo> listener) {
        try {
            String url = URL_BOARD_LIST_SEARCH.replace(":univId", ""+univId).replace(":tab", ""+tab);
            final CallbackObject<BoardInfo> callbackObject = new CallbackObject<BoardInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            json.addProperty("word", word);
            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    BoardInfo result = gson.fromJson(response.body().charStream(), BoardInfo.class);
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
        return null;
    }

    private static final String URL_BOARD_LIST_MY_WRITING = SERVER_URL + "/board/mywriting";
    public Request postDongneBoardListMyWriting(Context context, String univId, String userId, String start, String display, String reqDate, final OnResultListener<BoardInfo> listener) {
        try {
            String url = URL_BOARD_LIST_MY_WRITING;
            final CallbackObject<BoardInfo> callbackObject = new CallbackObject<BoardInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("univId", univId);
            json.addProperty("userId", userId);
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    BoardInfo result = gson.fromJson(response.body().charStream(), BoardInfo.class);
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
        return null;
    }

    private static final String URL_BOARD_LIST_MY_WRITING_SEARCH = SERVER_URL + "/board/mywriting/search";
    public Request postDongneBoardListMyWritingSearch(Context context, String univId, String userId, String start, String display, String reqDate, String word, final OnResultListener<BoardInfo> listener) {
        try {
            String url = URL_BOARD_LIST_MY_WRITING_SEARCH;
            final CallbackObject<BoardInfo> callbackObject = new CallbackObject<BoardInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("univId", univId);
            json.addProperty("userId", userId);
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            json.addProperty("word", word);
            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    BoardInfo result = gson.fromJson(response.body().charStream(), BoardInfo.class);
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
        return null;
    }

    private static final String URL_BOARD_LIST_MY_INTEREST = SERVER_URL + "/board/myinterest";
    public Request postDongneBoardListMyInterest(Context context, String univId, String userId, String start, String display, String reqDate, final OnResultListener<BoardInfo> listener) {
        try {
            String url = URL_BOARD_LIST_MY_INTEREST;
            final CallbackObject<BoardInfo> callbackObject = new CallbackObject<BoardInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("univId", univId);
            json.addProperty("userId", userId);
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    BoardInfo result = gson.fromJson(response.body().charStream(), BoardInfo.class);
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
        return null;
    }
    private static final String URL_BOARD_LIST_MY_INTEREST_SEARCH = SERVER_URL + "/board/myinterest/search";
    public Request postDongneBoardListMyInterestSearch(Context context, String univId, String userId, String start, String display, String reqDate, String word, final OnResultListener<BoardInfo> listener) {
        try {
            String url = URL_BOARD_LIST_MY_INTEREST_SEARCH;
            final CallbackObject<BoardInfo> callbackObject = new CallbackObject<BoardInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("univId", univId);
            json.addProperty("userId", userId);
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            json.addProperty("word", word);
            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    BoardInfo result = gson.fromJson(response.body().charStream(), BoardInfo.class);
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
        return null;
    }

    private static final String URL_BOARD_REMOVE = SERVER_URL + "/board/remove";
    public Request postDongneBoardRemove(Context context, String boardId, String writer, final OnResultListener<BoardInfo> listener) {
        try {
            String url = URL_BOARD_REMOVE;
            final CallbackObject<BoardInfo> callbackObject = new CallbackObject<BoardInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("boardId", boardId);
            json.addProperty("writer", writer);
            String jsonString = json.toString();

            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    BoardInfo result = gson.fromJson(response.body().charStream(), BoardInfo.class);
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
        return null;
    }


    private static final String URL_BOARD_LIKE = SERVER_URL + "/board/like";
    public Request postDongneBoardLike(Context context, int like, String boardId, String userId, String to, final OnResultListener<LikeInfo> listener) {
        try {
            String url = "";
            if(like < 2 && like == LikeInfo.DISLIKE){
                url = URL_BOARD_LIKE.replace("like", "dislike");
            } else if(like < 2 && like == LikeInfo.LIKE){
                url = URL_BOARD_LIKE;
            }
            final CallbackObject<LikeInfo> callbackObject = new CallbackObject<LikeInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            json.addProperty("to", to);
            json.addProperty("boardId", boardId);
            json.addProperty("userId", userId);
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    LikeInfo result = gson.fromJson(response.body().charStream(), LikeInfo.class);
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
        return null;
    }

    private static final String URL_BOARD_DETAIL = SERVER_URL + "/board/:boardId";
    public Request getDongneBoardDetail(Context context, int boardId, final OnResultListener<BoardInfo> listener) {
        try {
            String url = URL_BOARD_DETAIL.replace(":boardId", ""+boardId);
            final CallbackObject<BoardInfo> callbackObject = new CallbackObject<BoardInfo>();
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").tag(context).build();
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
                    BoardInfo result = gson.fromJson(response.body().charStream(), BoardInfo.class);
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
        return null;
    }

    private static final String URL_COMMENTS_ADD = SERVER_URL + "/comments/add";
    public Request postDongneCommentAdd(Context context, int boardId, String parentCommentId, String content, String type, String to, final OnResultListener<CommentInfo> listener) {
        try {
            String url = URL_COMMENTS_ADD;

            final CallbackObject<CommentInfo> callbackObject = new CallbackObject<CommentInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            json.addProperty("boardId", ""+boardId);
            json.addProperty("userId", ""+PropertyManager.getInstance().getUserId());
            json.addProperty("username", ""+PropertyManager.getInstance().getUserName());
            json.addProperty("parentCommentId", ""+parentCommentId);
            json.addProperty("body", ""+content);
            json.addProperty("type", type);
            json.addProperty("to", to);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    CommentInfo result = gson.fromJson(response.body().charStream(), CommentInfo.class);
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
        return null;
    }

    private static final String URL_BOARD_WRITE = SERVER_URL + "/board/write";
    public Request postDongneBoardWrite(Context context, int pageId, String type, String title, String content, final OnResultListener<WriteInfo> listener) {
        try {
            String url = URL_BOARD_WRITE;

            final CallbackObject<WriteInfo> callbackObject = new CallbackObject<WriteInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            json.addProperty("pageId", ""+pageId);
            json.addProperty("univId", ""+PropertyManager.getInstance().getUnivId());
            json.addProperty("writer", ""+PropertyManager.getInstance().getUserId());
            json.addProperty("type", type);
            json.addProperty("title", title);
            json.addProperty("body", content);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    WriteInfo result = gson.fromJson(response.body().charStream(), WriteInfo.class);
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
        return null;
    }

    public Request putDongneBoardWrite(Context context, int pageId, int boardId, String type, String title, String content, String pic, final OnResultListener<WriteInfo> listener) {
        try {
            String url = URL_BOARD_WRITE;

            final CallbackObject<WriteInfo> callbackObject = new CallbackObject<WriteInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            json.addProperty("pageId", ""+pageId);
            json.addProperty("boardId", ""+boardId);
            json.addProperty("univId", ""+PropertyManager.getInstance().getUnivId());
            json.addProperty("writer", ""+PropertyManager.getInstance().getUserId());
            json.addProperty("type", type);
            json.addProperty("title", title);
            json.addProperty("body", content);
            json.addProperty("pic", pic);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").put(body).tag(context).build();
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
                    WriteInfo result = gson.fromJson(response.body().charStream(), WriteInfo.class);
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
        return null;
    }

    private static final String URL_FRIENDS_UPDATE = SERVER_URL + "/friends/:status";
    public Request postDongneFriendsUpdate(Context context, int status, int to, String msg, final OnResultListener<StatusInfo> listener) {
        try {
            String url = URL_FRIENDS_UPDATE.replace(":status", ""+status);

            final CallbackObject<StatusInfo> callbackObject = new CallbackObject<StatusInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            json.addProperty("to", ""+to);
            json.addProperty("msg", ""+msg);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    StatusInfo result = gson.fromJson(response.body().charStream(), StatusInfo.class);
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
        return null;
    }

    private static final String URL_FRIENDS_REMOVE = SERVER_URL + "/friends/:userId";
    public Request postDongneFriendsRemove(Context context, int userId, final OnResultListener<StatusInfo> listener) {
        try {
            String url = URL_FRIENDS_REMOVE.replace(":userId", ""+userId);

            final CallbackObject<StatusInfo> callbackObject = new CallbackObject<StatusInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
//            json.addProperty("to", ""+to);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").delete(body).tag(context).build();
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
                    StatusInfo result = gson.fromJson(response.body().charStream(), StatusInfo.class);
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
        return null;
    }


    private static final String URL_FRIENDS_STATUS = SERVER_URL + "/friends/:status";
    public Request getDongneFriendsStatus(Context context, String status, final OnResultListener<FriendsInfo> listener) {
        try {
            String url = URL_FRIENDS_STATUS.replace(":status", ""+status);
            final CallbackObject<FriendsInfo> callbackObject = new CallbackObject<FriendsInfo>();

            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
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
                    FriendsInfo result = gson.fromJson(response.body().charStream(), FriendsInfo.class);
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
        return null;
    }

    private static final String URL_FRIENDS_STATUS_USER_ID = SERVER_URL + "/friends/status/:userId";
    public Request getDongneFriendsStatusUserId(Context context, int userId, final OnResultListener<StatusInfo> listener) {
        try {
            String url = URL_FRIENDS_STATUS_USER_ID.replace(":userId", ""+userId);
            final CallbackObject<StatusInfo> callbackObject = new CallbackObject<StatusInfo>();

            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
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
                    StatusInfo result = gson.fromJson(response.body().charStream(), StatusInfo.class);
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
        return null;
    }

    private static final String URL_ADD_CHAT_ROOM = SERVER_URL + "/test/addChatRoom";
    public Request postDongneAddChatRoom(Context context, String chatRoomId, String roomName, ArrayList<FriendsResult> mList, String msg, final OnResultListener<ChatInfo> listener) {
        try {
            String url = URL_ADD_CHAT_ROOM;
            final CallbackObject<ChatInfo> callbackObject = new CallbackObject<ChatInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            json.addProperty("room_name", roomName);
            json.addProperty("user_id", ""+PropertyManager.getInstance().getUserId());
            json.addProperty("message", msg);
            json.addProperty("chat_room_id", chatRoomId);
            for(int i=0; i<mList.size(); i++){
                json.addProperty("to["+i+"]", ""+mList.get(i).userId);
            }

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    ChatInfo result = gson.fromJson(response.body().charStream(), ChatInfo.class);
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
        return null;
    }

    private static final String URL_CHAT_THREAD = SERVER_URL + "/chat_rooms/:id";
    public Request getDongneFetchChatThread(Context context, int chatRoomId, final OnResultListener<ChatInfo> listener) {
        try {
            String url = URL_CHAT_THREAD.replace(":id", ""+chatRoomId);
            final CallbackObject<ChatInfo> callbackObject = new CallbackObject<ChatInfo>();

            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
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
                    ChatInfo result = gson.fromJson(response.body().charStream(), ChatInfo.class);
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
        return null;
    }

//    private static final String URL_CHAT_THREAD = SERVER_URL + "/chat_rooms/:id";
    public Request postDongneFetchChatThread(Context context, int chatRoomId, String joined_at, int start, int display, final OnResultListener<ChatInfo> listener) {
        try {
            String url = URL_CHAT_THREAD.replace(":id", ""+chatRoomId);
            final CallbackObject<ChatInfo> callbackObject = new CallbackObject<ChatInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("joined_at", joined_at);
            json.addProperty("start", ""+start);
            json.addProperty("display", ""+display);
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();

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
                    ChatInfo result = gson.fromJson(response.body().charStream(), ChatInfo.class);
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
        return null;
    }

    private static final String URL_SEND_MESSAGE = SERVER_URL + "/chat_rooms/:id/message";
    public Request postDongneSendMessage(Context context, int chatRoomId, String userId, String msg, final OnResultListener<ChatInfo> listener) {
        try {
            String url = URL_SEND_MESSAGE.replace(":id", ""+chatRoomId);
            final CallbackObject<ChatInfo> callbackObject = new CallbackObject<ChatInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            json.addProperty("user_id", userId);
            json.addProperty("message", msg);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    ChatInfo result = gson.fromJson(response.body().charStream(), ChatInfo.class);
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
        return null;
    }

    private static final String URL_PUT_USER_EDIT = SERVER_URL + "/users";
    public Request putDongnePutEditUser(Context context, String reqDate, String deptname, String desc1, String desc2, String jobname, String jobteam, String fb, String insta, final OnResultListener<CommonInfo> listener) {
        try {
            String url = URL_PUT_USER_EDIT;

            final CallbackObject<CommonInfo> callbackObject = new CallbackObject<CommonInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", reqDate);
            json.addProperty("email", PropertyManager.getInstance().getEmail());
            json.addProperty("deptname", deptname);
            json.addProperty("desc1", desc1);
            json.addProperty("desc2", desc2);
            json.addProperty("jobname", jobname);
            json.addProperty("jobteam", jobteam);
            json.addProperty("fb", fb);
            json.addProperty("insta", insta);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").put(body).tag(context).build();
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
                    CommonInfo result = gson.fromJson(response.body().charStream(), CommonInfo.class);
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
        return null;
    }

    private static final String URL_REPORT_UPDATE = SERVER_URL + "/report/:userId";
    public Request postDongneReportUpdate(Context context, int type, int to, String msg, final OnResultListener<StatusInfo> listener) {
        try {
            String url = URL_REPORT_UPDATE.replace(":userId", ""+to);

            final CallbackObject<StatusInfo> callbackObject = new CallbackObject<StatusInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            json.addProperty("from", ""+PropertyManager.getInstance().getUnivId());
            json.addProperty("type", ""+type);
            json.addProperty("msg", ""+msg);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    StatusInfo result = gson.fromJson(response.body().charStream(), StatusInfo.class);
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
        return null;
    }

    private static final String URL_COMMENTS = SERVER_URL + "/comments";
    public Request postDongneCommentsMyWriting(Context context, String userId, String start, String display, String reqDate, final OnResultListener<MyCommentInfo> listener) {
        try {
            String url = URL_COMMENTS;
            final CallbackObject<MyCommentInfo> callbackObject = new CallbackObject<MyCommentInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("userId", userId);
            json.addProperty("start", start);
            json.addProperty("display", display);
            json.addProperty("reqDate", reqDate);
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    MyCommentInfo result = gson.fromJson(response.body().charStream(), MyCommentInfo.class);
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
        return null;
    }

    private static final String URL_USERS_PW = SERVER_URL + "/users/pw";
    public Request putDongneChangePW(Context context, String email, String password, String value, final OnResultListener<CommonInfo> listener) {
        try {
            String url = URL_USERS_PW;
            final CallbackObject<CommonInfo> callbackObject = new CallbackObject<CommonInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("email", email);
            json.addProperty("password", password);
            json.addProperty("value", value);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").put(body).tag(context).build();
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
                    CommonInfo result = gson.fromJson(response.body().charStream(), CommonInfo.class);
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
        return null;
    }

    private static final String URL_FIND_ID = SERVER_URL + "/findID";
    public Request postDongneFindID(Context context, FindAccountModel info, final OnResultListener<FindAccountInfo> listener) {
        try {
            String url = URL_FIND_ID;
            final CallbackObject<FindAccountInfo> callbackObject = new CallbackObject<FindAccountInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("username", info.username);
            json.addProperty("univ", info.univname);
            json.addProperty("dept", info.deptname);
            json.addProperty("enterYear", info.enterYear);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    FindAccountInfo result = gson.fromJson(response.body().charStream(), FindAccountInfo.class);
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
        return null;
    }

    private static final String URL_FIND_PW = SERVER_URL + "/findPW";
    public Request postDongneFindPW(Context context, String email, final OnResultListener<CommonInfo> listener) {
        try {
            String url = URL_FIND_PW;
            final CallbackObject<CommonInfo> callbackObject = new CallbackObject<CommonInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("email", email);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    CommonInfo result = gson.fromJson(response.body().charStream(), CommonInfo.class);
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
        return null;
    }


    private static final String URL_CHAT_ROOMS = SERVER_URL + "/chat_rooms";
    public Request postDongneChatRooms(Context context, String userId, final OnResultListener<ChatRoomInfo> listener) {
        try {
            String url = URL_CHAT_ROOMS;
            final CallbackObject<ChatRoomInfo> callbackObject = new CallbackObject<ChatRoomInfo>();

            JsonObject json = new JsonObject();
            json.addProperty("userId", userId);

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    ChatRoomInfo result = gson.fromJson(response.body().charStream(), ChatRoomInfo.class);
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
        return null;
    }

    private static final String URL_CHAT_ROOMS_CHK = SERVER_URL + "/chat_rooms/chk";
    public Request postDongneCheckChatRooms(Context context, ArrayList<String> users, final OnResultListener<ChatRoomInfo> listener) {
        try {
            String url = URL_CHAT_ROOMS_CHK;
            final CallbackObject<ChatRoomInfo> callbackObject = new CallbackObject<ChatRoomInfo>();

            JsonObject json = new JsonObject();

            for (int j=0; j< users.size(); j++){
                json.addProperty("users[" + j + "]", ""+users.get(j));
            }
            json.addProperty("reqDate", MyApplication.getInstance().getCurrentTimeStampString());
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").post(body).tag(context).build();
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
                    ChatRoomInfo result = gson.fromJson(response.body().charStream(), ChatRoomInfo.class);
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
        return null;
    }


    private static final String URL_DELETE_PIC_USER = SERVER_URL + "/deletePic/user/:userId";
    public Request deleteDongnePicUser(Context context, String reqDate, int userId, final OnResultListener<CommonInfo> listener) {
        try {
            String url = URL_DELETE_PIC_USER.replace(":userId", ""+userId);

            final CallbackObject<CommonInfo> callbackObject = new CallbackObject<CommonInfo>();
            JsonObject json = new JsonObject();
            json.addProperty("reqDate", reqDate);
//            json.addProperty("to", ""+to);
            String jsonString = json.toString();
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json").delete(body).tag(context).build();
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
                    CommonInfo result = gson.fromJson(response.body().charStream(), CommonInfo.class);
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
        return null;
    }

    private static final String URL_GET_USER = SERVER_URL + "/users/:userId";
    public Request getDongneUserInfo(Context context, int userId, final OnResultListener<FriendsInfo> listener) {
        try {
            String url = URL_GET_USER.replace(":userId", ""+userId);
            final CallbackObject<FriendsInfo> callbackObject = new CallbackObject<FriendsInfo>();

            Request request = new Request.Builder().url(url)
                    .header("Accept", "application/json")
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
                    FriendsInfo result = gson.fromJson(response.body().charStream(), FriendsInfo.class);
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
        return null;
    }

}

