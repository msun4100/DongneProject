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
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import kr.me.ansr.common.CommonInfo;
import kr.me.ansr.login.LoginInfo;
import kr.me.ansr.login.autocomplete.dept.DeptInfo;
import kr.me.ansr.login.autocomplete.univ.UnivInfo;
import kr.me.ansr.tab.board.CommentInfo;
import kr.me.ansr.tab.board.WriteInfo;
import kr.me.ansr.tab.board.like.LikeInfo;
import kr.me.ansr.tab.board.one.BoardInfo;
import kr.me.ansr.tab.friends.model.FriendsInfo;
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
            json.addProperty("univId", PropertyManager.getInstance().getUnivId());
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
            location.addProperty("lat", PropertyManager.getInstance().getLatitude());
            location.addProperty("lon", PropertyManager.getInstance().getLongitude());

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

//    private static final String URL_FRIEND_UNIV_MY = SERVER_URL + "/friends/test/my";
    public Request postDongneUnivUsers(Context context, int mode, String univId, String start, String display, String reqDate, final OnResultListener<FriendsInfo> listener) {
        try {
            String url = URL_FRIEND_UNIV_USERS.replace(":univId", ""+univId);
            if(mode == 1){
                url += "/my";
            }

            final CallbackObject<FriendsInfo> callbackObject = new CallbackObject<FriendsInfo>();

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
    private static final String URL_BOARD_LIKE = SERVER_URL + "/board/like";
    public Request postDongneBoardLike(Context context, int like, String boardId, String userId, final OnResultListener<LikeInfo> listener) {
        try {
            String url = "";
            if(like < 2 && like == LikeInfo.DISLIKE){
                url = URL_BOARD_LIKE.replace("like", "dislike");
            } else if(like < 2 && like == LikeInfo.LIKE){
                url = URL_BOARD_LIKE;
            }
            final CallbackObject<LikeInfo> callbackObject = new CallbackObject<LikeInfo>();
            JsonObject json = new JsonObject();
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
    public Request postDongneCommentAdd(Context context, int boardId, String parentCommentId, String content, final OnResultListener<CommentInfo> listener) {
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

}
