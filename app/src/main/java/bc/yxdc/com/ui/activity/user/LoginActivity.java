package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.pgyersdk.crash.PgyCrashManager;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.open.utils.HttpUtils;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.conn.ConnectTimeoutException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Map;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.AuthResult;
import bc.yxdc.com.bean.LoginResult;
import bc.yxdc.com.bean.User;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.ui.view.TimerButton;
import bc.yxdc.com.utils.CommonUtil;
import bc.yxdc.com.utils.HyUtil;
import bc.yxdc.com.utils.IntentUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.OrderInfoUtil2_0;
import bocang.json.JSONObject;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/15.
 */

public class LoginActivity extends BaseActivity {

    private static final int LOGIN_BY_PWD = 0;
    private static final int LOGIN_BY_SMS = 1;
    private static final int LOGIN_BY_WX = 2;
    private static final int LOGIN_BY_QQ = 3;
    private static final int LOGIN_BY_ALIPAY = 4;

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();


                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        MyToast.show(LoginActivity.this,"授权成功");
                        alipayOpenId = authResult.getAlipayOpenId();
                        misson(LOGIN_BY_ALIPAY, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final JSONObject reJ=new JSONObject(response.body().string());
                                LogUtils.logE("loginByAli",reJ.toString());
                                if(reJ!=null){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyToast.show(LoginActivity.this,reJ.getString(Constance.msg));
                                            if(reJ.getInt(Constance.status)==1){
                                                String phone=reJ.getJSONObject(Constance.result).getString(Constance.mobile);
                                                String pwd=reJ.getJSONObject(Constance.result).getString(Constance.password);
                                                String token = reJ.getJSONObject(Constance.result).getString(Constance.token);
                                                String user_id=reJ.getJSONObject(Constance.result).getString(Constance.user_id);
                                                if(TextUtils.isEmpty(phone)){
                                                    MainActivity.currentTab=0;
                                                    startActivityForResult(new Intent(LoginActivity.this,WxBindActivity.class),200);
                                                    finish();
                                                    return;
                                                }
                                                MyShare.get(LoginActivity.this).putString(Constance.username,phone);
                                                MyShare.get(LoginActivity.this).putString(Constance.pwd,pwd);

                                                MyShare.get(LoginActivity.this).putString(Constance.TOKEN, token);
                                                MyShare.get(LoginActivity.this).putString(Constance.user_id,user_id);
                                                EventBus.getDefault().postSticky(new User());
                                                IntentUtil.startActivity(LoginActivity.this, MainActivity.class, true);
                                            }else {
                                                MyToast.show(LoginActivity.this,reJ.getString(Constance.msg));
                                                MainActivity.currentTab=0;
                                                startActivityForResult(new Intent(LoginActivity.this,WxBindActivity.class),200);
                                                finish();
                                            }

                                        }
                                    });

                                }

                            }
                        });

                    } else {
                        // 其他状态值则为授权失败
                        MyToast.show(LoginActivity.this,"授权失败");
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2018092561538308";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088231372934550";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = SystemClock.currentThreadTimeMillis()+"";

    /**
     *  pkcs8 格式的商户私钥。
     *
     * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * 	RSA2_PRIVATE。
     *
     * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgb6mTlNQTxl5KfurvKuC4Nh6Hl/HDWel+Ahx5azPm7DgLWQ19S2Qnh0ZdokemTmQhQQ+BlVP17yjOmllDj2ENXT99gCjHhKMg40d7Tb5CJdY5QIMM3n8B8JrRtZTTuZmaawxcfHp/3syIXeldrAgY3ApzeRAag7c2pdF+Z8QNa1JeVWoohqPP55wCxMLXBY7OlwzV/C5nJlUoaDmuUlvFRnGjifV+RGtqDQ+Hjdfsh/vrtLbpz3pqCckCB3mwWzM10227IP/kgxC9YbC2eZqUbAtioE81QJYZB14KxoT5XYrXqp2vOKc6a91TEy12vIE3wUXE7TXD6OiwQYe0UyuFQIDAQAB";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private EditText et_phone;
    private EditText et_pwd;
    private TimerButton tv_forget_pwd;
    private TextView btn_login;
    private ImageView iv_login_by_wx;
    private ImageView iv_login_by_qq;
    private TextView tv_sms;
    private TextView tv_register;
    private int mode;
    private String phone;
    private String pwd="";
    private LoginResult mLoginResult;
    private String openid;
    private String unionid;
    private Tencent mTencent;
    private IUiListener baseListener;
    private String username="";
    private String alipayOpenId;
    private String head_pic="";
    private String access_token;

    @Override
    protected void initData() {
        if(getIntent().hasExtra(Constance.user_name)){
        username = getIntent().getStringExtra(Constance.user_name);
        pwd= getIntent().getStringExtra(Constance.pwd);
        }
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);
        EventBus.getDefault().register(this);
        et_phone = findViewById(R.id.et_phone);
        et_pwd = findViewById(R.id.et_pwd);
        tv_forget_pwd = getViewAndClick(R.id.tv_foget_pwd);
        btn_login = getViewAndClick(R.id.btn_login);
        iv_login_by_wx = getViewAndClick(R.id.iv_login_wx);
        iv_login_by_qq = getViewAndClick(R.id.iv_login_qq);
        tv_sms = getViewAndClick(R.id.tv_sms);
        tv_register = getViewAndClick(R.id.tv_register);
        mode = LOGIN_BY_PWD;
        et_phone.setText(""+username);
        et_pwd.setText(""+pwd);
        if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(pwd)){
            btn_login.performClick();
        }
    }

    @Override
    public void getData(int type, Callback callback) {
        if(type==LOGIN_BY_PWD){
            String regiId=MyShare.get(this).getString(Constance.RegistrationID);
        OkHttpUtils.login(phone,pwd,LOGIN_BY_PWD,regiId,callback);
        }else if(type==LOGIN_BY_WX){
            OkHttpUtils.loginByWx(openid,"weixin",unionid,head_pic,callback);
        }else if(type==LOGIN_BY_QQ){
            OkHttpUtils.loginByWx(openid,"qq","",head_pic,callback);
        }else if(type==LOGIN_BY_ALIPAY){
            OkHttpUtils.loginByWx(alipayOpenId,"alipay","",head_pic,callback);
        }
    }

    @OnClick(R.id.iv_login_ali)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_foget_pwd:
                if(mode==LOGIN_BY_PWD){
                    phone=et_phone.getText().toString();
                    startActivity(new Intent(this,PwdForgetActivity.class));
                    finish();
                }else {
                    requestYZM();
                }
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.iv_login_wx:
                IWXAPI api= WXAPIFactory.createWXAPI(this,Constance.APP_ID,true);
                final SendAuth.Req req = new SendAuth.Req();
                final SendAuth.Resp resp=new SendAuth.Resp();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                api.sendReq(req);
//                api.sendResp(resp);
                String code=resp.code;
                String url=resp.url;
                int errorCode=resp.errCode;
                LogUtils.logE("response",errorCode+","+code+","+url);
                break;
            case R.id.iv_login_qq:
                loginByQQ();
                break;
            case R.id.iv_login_ali:
                loginByAli();
                break;
            case R.id.tv_sms:
                if(mode==LOGIN_BY_PWD){
                    mode=LOGIN_BY_SMS;
                    tv_sms.setText("账号密码登录");
                    tv_forget_pwd.setText("获取验证码");
                    et_pwd.setHint("请输入验证码");
                }else {
                    mode=LOGIN_BY_PWD;
                    tv_forget_pwd.setText("忘记密码");
                    tv_sms.setText("短信验证码登录");
                    et_pwd.setHint("请输入密码");
                }
                break;
            case R.id.tv_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;

        }
    }
    public void getUserInfo()
    {

    }

    private void loginByQQ() {
        mTencent = Tencent.createInstance(Constance.QQ_APP_ID, this.getApplicationContext());
        if (!mTencent.isSessionValid())
        {
            //                            org.json.JSONObject jsonObject= (org.json.JSONObject) o;

            baseListener = new  IUiListener() {
                @Override
                public void onComplete(Object o) {
                    org.json.JSONObject jsonObject= (org.json.JSONObject) o;
                    String token = null;
                    try {
                        token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                        String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                        openid= jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                        QQToken qqToken=mTencent.getQQToken();
                        qqToken.setOpenId(openid);
                        qqToken.setAccessToken(token,SystemClock.currentThreadTimeMillis()+"");
                        qqToken.setAppId(Constance.QQ_APP_ID);
                        qqToken.setAuthSource(1);

                        UserInfo userInfo=new UserInfo(LoginActivity.this,qqToken);
                        userInfo.getUserInfo(new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                org.json.JSONObject jsonObject= (org.json.JSONObject) o;
                                LogUtils.logE("qqinfo",jsonObject.toString());
                                try {
                                    head_pic=jsonObject.getString(Constance.figureurl_qq_2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                misson(LOGIN_BY_QQ, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final JSONObject reJ=new JSONObject(response.body().string());
                                        LogUtils.logE("loginByqq",reJ.toString());
                                        if(reJ!=null){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyToast.show(LoginActivity.this,reJ.getString(Constance.msg));
                                                    if(reJ.getInt(Constance.status)==1){
                                                        String phone=reJ.getJSONObject(Constance.result).getString(Constance.mobile);
                                                        String pwd=reJ.getJSONObject(Constance.result).getString(Constance.password);
                                                        String token = reJ.getJSONObject(Constance.result).getString(Constance.token);
                                                        String user_id=reJ.getJSONObject(Constance.result).getString(Constance.user_id);
                                                        if(TextUtils.isEmpty(phone)){
                                                            MainActivity.currentTab=0;
                                                            startActivityForResult(new Intent(LoginActivity.this,WxBindActivity.class),200);
                                                            finish();
                                                            return;
                                                        }
                                                        MyShare.get(LoginActivity.this).putString(Constance.username,phone);
                                                        MyShare.get(LoginActivity.this).putString(Constance.pwd,pwd);

                                                        MyShare.get(LoginActivity.this).putString(Constance.TOKEN, token);
                                                        MyShare.get(LoginActivity.this).putString(Constance.user_id,user_id);
                                                        EventBus.getDefault().postSticky(new User());
                                                        IntentUtil.startActivity(LoginActivity.this, MainActivity.class, true);
                                                    }else {
                                                        MyToast.show(LoginActivity.this,reJ.getString(Constance.msg));
                                                        MainActivity.currentTab=0;
                                                        startActivityForResult(new Intent(LoginActivity.this,WxBindActivity.class),200);
                                                        finish();
                                                    }

                                                }
                                            });

                                        }

                                    }
                                });
                            }

                            @Override
                            public void onError(UiError uiError) {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                        mTencent.requestAsync(Constants.LOGIN_INFO, null,
                                Constants.HTTP_GET,  new BaseApiListener("get_simple_userinfo", false){
                                    @Override
                                    public void onComplete(org.json.JSONObject jsonObject) {
                                        super.onComplete(jsonObject);
//                                        LogUtils.logE("qqinfo",jsonObject.toString());

                                    }
                                }, null);

//                                BmobThirdUserAuth authInfo = new BmobThirdUserAuth(BmobThirdUserAuth.SNS_TYPE_QQ,token, expires,openId);
//                                loginWithAuth(authInfo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    LogUtils.logE("QQlogin",o.toString());
                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            };
            mTencent.login(this, "all",baseListener );
        }
    }

    private void login() {
        phone = et_phone.getText().toString().trim();
        pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            MyToast.show(this, "请填写手机号码");
            return;
        }
        if(mode==LOGIN_BY_PWD) {

            if (TextUtils.isEmpty(pwd)) {
                MyToast.show(this, "请填写密码");
                return;
            }
            misson(0, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    Log.e("result",result);
                    final JSONObject reJ=new JSONObject(result);
                    int status=reJ.getInt(Constance.status);
                    if(status==1){
//                        if (mType == 0) {
//                            MyShare.get(mView).putString(Constance.USERNAME, mCode);
//                            MyShare.get(mView).putString(Constance.USERID, ans.getJSONObject(Constance.user).getString(Constance.id));
//                        }
//                        String phone=reJ.getJSONObject(Constance.result).getString(Constance.mobile);
//                        String pwd=reJ.getJSONObject(Constance.result).getString(Constance.password);
                        String token = reJ.getJSONObject(Constance.result).getString(Constance.token);
                        String user_id=reJ.getJSONObject(Constance.result).getString(Constance.user_id);
//                        if(TextUtils.isEmpty(phone)){
//
//                            return;
//                        }
                        MyShare.get(LoginActivity.this).putString(Constance.username,phone);
                        MyShare.get(LoginActivity.this).putString(Constance.pwd,pwd);


                        MyShare.get(LoginActivity.this).putString(Constance.TOKEN, token);
                        MyShare.get(LoginActivity.this).putString(Constance.user_id,user_id);
//                        EventBus.getDefault().postSticky(new LoginResult(1));
                        IntentUtil.startActivity(LoginActivity.this, MainActivity.class, true);
                        EventBus.getDefault().postSticky(new User());
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                MyToast.show(LoginActivity.this,reJ.getString(Constance.msg));
                            }
                        });

                    }

                }
            });

        }else {
            if (TextUtils.isEmpty(pwd)) {
                MyToast.show(this, "请填写验证码");
                return;
            }

        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginResult(LoginResult loginResult){
        mLoginResult = loginResult;
//        PgyCrashManager.reportCaughtException(LoginActivity.this,new Exception("loginResult"+mLoginResult.code));

        OkHttpUtils.getAccess_token(mLoginResult.code,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject resultObj=new JSONObject(response.body().string());
                openid = resultObj.getString(Constance.openid);
                unionid = resultObj.getString(Constance.unionid);
                access_token = resultObj.getString(Constance.access_token);
//                PgyCrashManager.reportCaughtException(LoginActivity.this,new Exception("getopenid"+resultObj.toString()));
                if(!TextUtils.isEmpty(openid)&&!TextUtils.isEmpty(unionid)){
                    OkHttpUtils.getUserInfoWx(access_token,openid, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            JSONObject jsonObject=new JSONObject(response.body().string());
                            if(jsonObject!=null&&jsonObject.getString(Constance.headimgurl)!=null){
                                head_pic=jsonObject.getString(Constance.headimgurl);
                            }
                            misson(LOGIN_BY_WX, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                final String resultStr=response.body().string();
//                                        PgyCrashManager.reportCaughtException(LoginActivity.this,new Exception("loginByWx,"+resultStr));
                                                final JSONObject reJ=new JSONObject(resultStr);
//                                        MyToast.show(LoginActivity.this,""+resultStr);
                                                int status=reJ.getInt(Constance.status);
                                                if(status==1){
                                                    String phone=reJ.getJSONObject(Constance.result).getString(Constance.mobile);
                                                    String pwd=reJ.getJSONObject(Constance.result).getString(Constance.password);
                                                    String token = reJ.getJSONObject(Constance.result).getString(Constance.token);
                                                    String user_id=reJ.getJSONObject(Constance.result).getString(Constance.user_id);
                                                    if(TextUtils.isEmpty(phone)){
                                                        MainActivity.currentTab=0;
                                                        startActivityForResult(new Intent(LoginActivity.this,WxBindActivity.class),200);
                                                        finish();
                                                        return;
                                                    }
                                                    MyShare.get(LoginActivity.this).putString(Constance.username,phone);
                                                    MyShare.get(LoginActivity.this).putString(Constance.pwd,pwd);

                                                    MyShare.get(LoginActivity.this).putString(Constance.TOKEN, token);
                                                    MyShare.get(LoginActivity.this).putString(Constance.user_id,user_id);
//                        EventBus.getDefault().postSticky(new LoginResult(1));
                                                    EventBus.getDefault().postSticky(new User());
                                                    IntentUtil.startActivity(LoginActivity.this, MainActivity.class, true);
                                                }else {
                                                    MyToast.show(LoginActivity.this,reJ.getString(Constance.msg));
                                                    MainActivity.currentTab=0;
                                                    startActivityForResult(new Intent(LoginActivity.this,WxBindActivity.class),200);
                                                    finish();
                                                }

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
//                LogUtils.logE("refreshToken",response.body().string());
            }
        });
    }
    /**
     * 支付宝账户授权业务示例
     */
    public void loginByAli() {
//        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
//                || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
//                || TextUtils.isEmpty(TARGET_ID)) {
////            showAlert(this, getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id));
//            return;
//        }

		/*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
		 *
		 * authInfo 的获取必须来自服务端；
		 */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(LoginActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==200){
            finish();
        }else   if(requestCode ==11101) {
            if (resultCode == Integer.parseInt(Constants.VIA_RESULT_SUCCESS)) {
            }
                mTencent.handleLoginData(data, baseListener);
        }
    }

    @Override
    public void goBack(View v) {
        MainActivity.currentTab=0;
        super.goBack(v);
    }

    @Override
    public void onBackPressed() {
        MainActivity.currentTab=0;
        super.onBackPressed();
    }

    public void requestYZM() {
        phone= et_phone.getText().toString();
        if (HyUtil.isEmpty(phone)) {
            MyToast.show(this, "请输入手机号码");
            return;
        }
        if (!CommonUtil.isMobileNO(phone)) {
            MyToast.show(this, "请输入正确的手机号码");
            return;
        }
        tv_forget_pwd.start(60);
        OkHttpUtils.sendSms(phone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("sms",response.body().string());

            }
        });
    }
}
