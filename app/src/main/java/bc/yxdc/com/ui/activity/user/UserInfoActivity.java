package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.User;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.utils.DateUtils;
import bc.yxdc.com.utils.FileUtil;
import bc.yxdc.com.utils.ImageUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.NetWorkUtils;
import bc.yxdc.com.utils.UIUtils;
import bc.yxdc.com.utils.photo.CameraUtil;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/18.
 */

public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.iv_head)
    ImageView iv_head;
    @BindView(R.id.rl_nickname)
    RelativeLayout rl_nickname;
    @BindView(R.id.tv_nickname)
    TextView tv_nickname;
    @BindView(R.id.rl_sex)
    RelativeLayout rl_sex;
    @BindView(R.id.tv_sex)
    TextView tv_sex;
    @BindView(R.id.rl_birthday)
    RelativeLayout rl_birthday;
    @BindView(R.id.tv_birthday)
    TextView tv_birthday;
    @BindView(R.id.rL_mobile)
    RelativeLayout rl_mobile;
    @BindView(R.id.tv_mobile)
    TextView tv_mobile;
    @BindView(R.id.rl_changepwd)
    RelativeLayout rl_changepwd;
    @BindView(R.id.rl_head)RelativeLayout rl_head;

    private User user;
    private String imageURL;
    private Map<String, String> params;
    private String bir;


    @Override
    protected void initData() {
        String userBean=IssApplication.mUserBean;
        if (TextUtils.isEmpty(userBean)) {
            MyToast.show(this,"用户信息失效，请重新登录");
            startActivity(new Intent(this,LoginActivity.class));
            finish();
            return;
        }
        user = new Gson().fromJson(IssApplication.mUserBean,User.class);
//        LogUtils.logE("user",IssApplication.mUserBean);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        params = new HashMap<>();
        params.put(Constance.token,MyShare.get(this).getString(Constance.token));
        fillData();
//        tv_sex.setText();
    }

    private void fillData() {
        ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+user.getHead_pic(),iv_head,IssApplication.getImageLoaderOption());
        if(TextUtils.isEmpty(user.getNickname())){
            String mobile=user.getMobile();
            mobile=mobile.substring(0,3)+"****"+mobile.substring(mobile.length()-4,mobile.length());
            tv_nickname.setText(mobile+"");
        }else {
            tv_nickname.setText(user.getNickname()+"");
        }
        String sexStr="";
        if(user.getSex().equals("0")){
            sexStr="保密";
        }else if(user.getSex().equals("1")){
            sexStr="男";
        }else {
            sexStr="女";
        }
        tv_sex.setText(sexStr);
        bir = DateUtils.getStrTime02(user.getBirthday())+"";
        tv_birthday.setText(bir);
        tv_mobile.setText(""+user.getMobile());
        rl_birthday.setOnClickListener(this);
        rl_changepwd.setOnClickListener(this);
        rl_mobile.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_nickname.setOnClickListener(this);
        iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHead();
            }
        });
        rl_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHead();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent=new Intent(this,SettingTextActivity.class);
        switch (v.getId()){
            case R.id.rl_sex:
                intent.putExtra(Constance.text,"性别");
                intent.putExtra(Constance.type,2);
                intent.putExtra(Constance.sex,user.getSex());
                break;
            case R.id.rl_birthday:
                intent.putExtra(Constance.text,"生日");
                intent.putExtra(Constance.type,3);
                intent.putExtra(Constance.content,bir);
                break;
            case R.id.rl_changepwd:
                intent=new Intent(this,ChangePwdActivity.class);
                break;
            case R.id.rL_mobile:
                intent.putExtra(Constance.text,"手机号");
                intent.putExtra(Constance.type,4);
                intent.putExtra(Constance.content,user.getMobile());
                break;
            case R.id.rl_nickname:
                intent.putExtra(Constance.text,"昵称");
                intent.putExtra(Constance.type,1);
                intent.putExtra(Constance.content,user.getNickname());
                break;
        }
        startActivity(intent);
    }
    /**
     * 头像
     */
    private CameraUtil camera;
    public void setHead() {

        FileUtil.openImage(this);

        if (camera == null) {
            camera = new CameraUtil(this, new CameraUtil.CameraDealListener() {
                @Override
                public void onCameraTakeSuccess(String path) {
                    camera.cropImageUri(1, 1, 256);
                }

                @Override
                public void onCameraPickSuccess(String path) {
                    Uri uri ;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        uri= FileProvider.getUriForFile(UserInfoActivity.this, "com.bocang.tianxiadengcang.fileprovider", new File(path));
                    }else {
                        uri = Uri.parse("file://" + path);
                    }
                    camera.cropImageUri(uri, 1, 1, 256);
                }

                @Override
                public void onCameraCutSuccess(final String uri) {
                    File file = new File(uri);
                    Uri uriTemp;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        uriTemp= FileProvider.getUriForFile(UserInfoActivity.this, "com.bocang.tianxiadengcang.fileprovider", new File(uri));
                    }else {
                        uriTemp = Uri.parse("file://" + uri);
                    }
                    iv_head.setImageURI(uriTemp);
                    upLoad(uri.toString());
                }
            });
        }

//        mHeadView.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (camera != null)
            camera.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case Constance.PHOTO_WITH_CAMERA: {// 拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡
                        File imageFile = new File(IssApplication.cameraPath, IssApplication.imagePath + ".jpg");
                        imageURL = "file://" + imageFile;
                        final Uri uri = Uri.parse("file://" + imageFile);
                        iv_head.setImageURI(uri);
                        upLoad(uri.toString());
                    }
                }
                break;
            case Constance.PHOTO_WITH_DATA: // 从图库中选择图片
                // 照片的原始资源地址
                imageURL = data.getData().toString();
                iv_head.setImageURI(data.getData());
                upLoad(imageURL.toString());
                break;
            case Constance.FLAG_UPLOAD_IMAGE_CUT:
                final Uri uri=data.getData();
                iv_head.setImageURI(uri);
                upLoad(uri.toString());
                break;
        }
    }else if(requestCode== Constance.FLAG_UPLOAD_IMAGE_CUT){
        final Uri uri=data.getData();
        iv_head.setImageURI(uri);
        upLoad(uri.toString());
    }
}

    private void upLoad(final String uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resultJson = NetWorkUtils.uploadFile(ImageUtil.drawable2Bitmap(iv_head.getDrawable()), NetWorkConst.UPLOADAVATAR, params, uri);
                //                            //分享的操作
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.logE("result",resultJson);
                        onRefresh();
                    }
                });
            }
        }).start();
    }

    public void onRefresh(){
        load();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        misson(0, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject=new JSONObject(response.body().string());
                user=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),User.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fillData();
                    }
                });

            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        String token= MyShare.get(this).getString(Constance.token);
        String user_id=MyShare.get(this).getString(Constance.user_id);
        if(TextUtils.isEmpty(token)||TextUtils.isEmpty(user_id)){
            MyToast.show(this,"登录状态失效");
            UIUtils.showLoginDialog(this);
            return;
        }
        OkHttpUtils.getUserInfo(token,user_id,callback);
    }
}
