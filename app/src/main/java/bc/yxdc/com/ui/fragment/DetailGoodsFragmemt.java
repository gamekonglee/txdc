package bc.yxdc.com.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.utils.LogUtils;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/9/14.
 */

public class DetailGoodsFragmemt extends BaseFragment{

    private WebView webView;

    @Override
    public void initUI() {
        webView = getView().findViewById(R.id.webview);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        try {
            EventBus.getDefault().register(this);
        }catch (Exception e){

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String event){
        setWebview(event);
    }
    private void setWebview(String htmlValue) {
        String html = htmlValue;
        String replace = html.replace("&lt;", "<");
        String replace1 = replace.replace("&gt;", ">");
        String replace2 = replace1.replace("&amp;", "&");
        html = replace2.replace("&quot;", "\"");

        html = html.replace("/public/upload/",  NetWorkConst.API_HOST_2+"/public/upload/");
        html = "<meta name=\"viewport\" content=\"width=device-width\">" + html;
        html=html.replace("<p>","");
        html=html.replace("</p>","");
        LogUtils.logE("webData",html);
        webView.loadData(html, "text/html; charset=UTF-8", null);//这种写法可以正确解析中文
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        }catch (Exception e){

        }
    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @Override
    protected void initData() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_goods_detail,null);
    }

}
