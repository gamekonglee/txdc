package app.txdc.shop.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import app.txdc.shop.R;
import bc.yxdc.com.bean.LoginResult;
import bc.yxdc.com.bean.PayResult;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyToast;

/**
 * Created by gamekonglee on 2018/10/8.
 */


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_result);
        api = WXAPIFactory.createWXAPI(this, Constance.APP_ID);
        api.handleIntent(getIntent(), this);
    }
    public void goBack(View v){
        onBackPressed();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
//        Toast.makeText(this,"code"+resp.errCode,Toast.LENGTH_LONG).show();

//            LogUtils.logE("loginCode",resp.getType()+"");
//            MyToast.show(this,"code"+resp.getType());
            if(resp.getType()==1&&resp.errCode==0){
                MyToast.show(this,"登录成功");
                SendAuth.Resp temp= (SendAuth.Resp) resp;
                EventBus.getDefault().post(new LoginResult(temp.code,temp.state,temp.lang,temp.country));
            }
            finish();
    }
}
