package bc.yxdc.com.ui.activity;

import android.graphics.Color;

import com.tencent.smtt.sdk.WebView;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/11/26.
 */

public class MyWebViewActivity extends BaseActivity {

    private String url;

    @Override
    protected void initData() {
        url = getIntent().getStringExtra(Constance.url);

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_webview);
        setStatuTextColor(this, getResources().getColor(R.color.theme_red));
        setFullScreenColor(Color.TRANSPARENT,this);
        WebView webview=findViewById(R.id.webview);
        if(!url.contains("http")){
            url= NetWorkConst.API_HOST+url;
        }
        webview.loadUrl(url);

    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
