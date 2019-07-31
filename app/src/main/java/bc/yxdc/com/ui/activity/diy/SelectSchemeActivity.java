package bc.yxdc.com.ui.activity.diy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.SchemeTypeAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.SceneType;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.inter.ISchemeChooseListener;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/11/7.
 */

public class SelectSchemeActivity extends BaseActivity{
    @BindView(R.id.title_tv)EditText title_tv;
    @BindView(R.id.remark_tv)EditText remark_tv;

    @BindView(R.id.scheme_type_lv)
    ListView scheme_type_lv;
    private SchemeTypeAdapter mAdapter;
    private List<SceneType> mSceneTypes;
    private String mStyle="现代简约";
    private String mSplace="玄关";
    private Intent mIntent;
    private TextView iv_save;
    private List<SceneType> sceneTypeBeans;

    @Override
    protected void initData() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initUI() {
        setContentView(R.layout.activity_select_scheme);
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);

        ButterKnife.bind(this);
        scheme_type_lv.setDivider(null);//去除listview的下划线
        mAdapter=new SchemeTypeAdapter(this);
        scheme_type_lv.setAdapter(mAdapter);
        mAdapter.setListener(new ISchemeChooseListener() {
            @Override
            public void onSchemeChanged(String style, String splace) {
                if(!AppUtils.isEmpty(style)){
                    mStyle = style;
                }
                if(!AppUtils.isEmpty(splace)){
                    mSplace = splace;
                }
            }
        });

        mSceneTypes =new ArrayList<>();
        iv_save = getViewAndClick(R.id.iv_save);

        misson(0, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sceneTypeBeans = new Gson().fromJson(response.body().string(),new TypeToken<List<SceneType>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(sceneTypeBeans);
                        mStyle=sceneTypeBeans.get(1).getAttrVal().get(0);
                        mSplace=sceneTypeBeans.get(0).getAttrVal().get(0);
                        mAdapter.notifyDataSetChanged();
                        UIUtils.initListViewHeight(scheme_type_lv);
                    }
                });



            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        saveScheme();
    }

    @Override
    public void getData(int type, Callback callback) {
        OkHttpUtils.getSceneType(callback);

    }

    public void saveScheme() {
        if(AppUtils.isEmpty(title_tv.getText().toString())){
            MyToast.show(this,"请输入您的标题");
            return;
        }

        mIntent=new Intent();
        mIntent.putExtra(Constance.style, mStyle);
        mIntent.putExtra(Constance.space, mSplace);
        mIntent.putExtra(Constance.title, title_tv.getText().toString());
        mIntent.putExtra(Constance.content, remark_tv.getText().toString());
        setResult(Constance.FROMSCHEME, mIntent);//告诉原来的Activity 将数据传递给它
        finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
    }
}
