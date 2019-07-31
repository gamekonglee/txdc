package bc.yxdc.com.ui.activity.goods;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.utils.UIUtils;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/11/20.
 */

public class ImageShowActivity extends BaseActivity {

    private String path;
    private boolean isFont;

    @Override
    protected void initData() {
        path = getIntent().getStringExtra(Constance.path);
        isFont = getIntent().getBooleanExtra(Constance.isFont,false);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_image_show);
        setStatuTextColor(this, Color.BLACK);
        setFullScreenColor(Color.TRANSPARENT,this);
        final ImageView iv_img=findViewById(R.id.iv_img);
        ImageView iv_cancel=findViewById(R.id.iv_delete);
        ImageView iv_save=findViewById(R.id.iv_save);
        ImageLoader.getInstance().loadImage("file://" + path, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if(isFont){
                    Bitmap fontBitmap= UIUtils.converBitmap(bitmap);
                    iv_img.setImageBitmap(fontBitmap);
                }else {
                iv_img.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(404);
                finish();
            }
        });
        iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra(Constance.path,path);
                setResult(200,intent);
                finish();
            }
        });

    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
