package bc.yxdc.com.ui.activity.diy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.SceneType;
import bc.yxdc.com.bean.SceneBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.view.EndOfGridView;
import bc.yxdc.com.ui.view.EndOfListView;
import bc.yxdc.com.ui.view.PMSwipeRefreshLayout;
import bc.yxdc.com.utils.FileUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/11/7.
 */

public class SelectSceneActivity extends BaseActivity implements EndOfListView.OnEndOfListListener {
    private static final int TYPE_LIST = 0;
    private static final int TYPE_TYPE = 1;
    @BindView(R.id.tv_album) TextView tv_album;
    @BindView(R.id.select_num_tv) TextView select_num_tv;
    private Intent mIntent;
    @BindView(R.id.select_rl) RelativeLayout select_rl;
    @BindView(R.id.gv_scene)EndOfGridView gv_scene;
    @BindView(R.id.tv_filter_item) TextView tv_filter;
    @BindView(R.id.pulltorefresh)PMSwipeRefreshLayout pulltorefresh;
    @BindView(R.id.lv_filter_type)
    ListView lv_filter_type;
    @BindView(R.id.drawerlayout) DrawerLayout drawerlayout;
    @BindView(R.id.fl_filter)View fl_filter;
    private String imageURL;
    private QuickAdapter<SceneBean> adapter;
    private int p=1;
    private String attr_values="";
    private List<SceneBean> sceneBeans;
    private boolean isBottom;
    private QuickAdapter<SceneType> adapterProgramme;
    private List<SceneType> sceneTypeList;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_scene_select);
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);
        ButterKnife.bind(this);
        select_num_tv.setText(IssApplication.mSelectScreens.size()+"");
        adapter = new QuickAdapter<SceneBean>(this, R.layout.item_scene) {
            @Override
            protected void convert(BaseAdapterHelper helper, SceneBean item) {
                ImageView iv_img=helper.getView(R.id.iv_img);
                if(item!=null&&item.getScene()!=null) {
                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST + item.getScene().getPath(), iv_img, IssApplication.getImageLoaderOption());

                    helper.getView().setLayoutParams(new ViewGroup.LayoutParams(UIUtils.getScreenWidth(SelectSceneActivity.this) / 2 - UIUtils.dip2PX(5), UIUtils.getScreenWidth(SelectSceneActivity.this) / 2 - UIUtils.dip2PX(5)));
//                holder.check_iv.setVisibility(View.GONE);
                    helper.setVisible(R.id.check_iv, false);
                    for (int i = 0; i < IssApplication.mSelectScreens.size(); i++) {
                        String screenPath = IssApplication.mSelectScreens.get(i).getScene().getPath();
                        if (item.getScene().getPath().equals(screenPath)) {
                            helper.setVisible(R.id.check_iv, true);
                            break;
                        }

                    }
                }
            }
        };
        adapterProgramme = new QuickAdapter<SceneType>(this, R.layout.item_filter) {
            @Override
            protected void convert(BaseAdapterHelper helper, final SceneType itemAttr) {
                helper.setText(R.id.tv_name,itemAttr.getAttr_name());
                QuickAdapter<String> filterPriceQuickAdapter=new QuickAdapter<String>(SelectSceneActivity.this,R.layout.item_filter_item) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, String item) {
                        helper.setText(R.id.tv_filter_item,item);
                        if(itemAttr.getCurrent()==helper.getPosition()){
                            helper.setBackgroundRes(R.id.tv_filter_item,R.drawable.bg_corner_red_empty);
                            helper.setTextColor(R.id.tv_filter_item,getResources().getColor(R.color.theme_red));
                        }else {
                            helper.setBackgroundRes(R.id.tv_filter_item,R.drawable.bg_efefef_corner_15);
                            helper.setTextColor(R.id.tv_filter_item,getResources().getColor(R.color.tv_333333));
                        }
                    }
                };

                GridView gv_item=helper.getView(R.id.gv_item);
                gv_item.setAdapter(filterPriceQuickAdapter);
                filterPriceQuickAdapter.replaceAll(itemAttr.getAttrVal());
                UIUtils.initGridViewHeight(gv_item);
                gv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        itemAttr.setCurrent(position);
                        adapterProgramme.notifyDataSetChanged();

                    }
                });
            }
        };
        lv_filter_type.setAdapter(adapterProgramme);
        gv_scene.setAdapter(adapter);
        gv_scene.setOnEndOfListListener(this);
        gv_scene.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < IssApplication.mSelectScreens.size(); i++) {
                    String selectName = IssApplication.mSelectScreens.get(i).getScene().getPath();
                    String name = sceneBeans.get(position).getScene().getPath();
                    if (selectName.equals(name)) {
                        IssApplication.mSelectScreens.remove(i);
                        adapter.notifyDataSetChanged();
                        select_num_tv.setText(IssApplication.mSelectScreens.size() + "");
                        return;
                    }
                }
                IssApplication.mSelectScreens.add(sceneBeans.get(position));
                adapter.notifyDataSetChanged();
                select_num_tv.setText(IssApplication.mSelectScreens.size() + "");
            }
        });
        sceneBeans = new ArrayList<>();
        loadScene();
        loadSceneType();

        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerlayout,R.string.open,R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerlayout.addDrawerListener(actionBarDrawerToggle);
    }

    private void loadSceneType() {
        misson(TYPE_TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sceneTypeList = new Gson().fromJson(response.body().string(),new TypeToken<List<SceneType>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterProgramme.replaceAll(sceneTypeList);
                        UIUtils.initListViewHeight(lv_filter_type);
                    }
                });

            }
        });
    }

    private void loadScene() {
        misson(TYPE_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final List<SceneBean> temp=new Gson().fromJson(response.body().string(),new TypeToken<List<SceneBean>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(temp.size()==0){
                            isBottom = true;
                        }else {
                            isBottom=false;
                        }
                        if(p==1){
                            sceneBeans=temp;
                        }else {
                            sceneBeans.addAll(temp);
                        }
                        adapter.replaceAll(sceneBeans);
                    }
                });
            }
        });
    }

    @OnClick({R.id.tv_album,R.id.select_rl,R.id.tv_filter_item,R.id.tv_reset,R.id.tv_ensure})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_album:
                goPhoto();
                break;
            case R.id.select_rl:
                mIntent = new Intent();
//                ArrayList<String> data=new ArrayList<>();
//                for(int i=0;i>IssueApplication.mSelectScreens.length();i++){
//                    data.add(IssueApplication.mSelectScreens.getJSONObject(i).getJSONObject(Constance.scene).getString(Constance.original_img));
//                }
//                mIntent.putStringArrayListExtra(Constance.SCENE,data);
                setResult(Constance.FROMDIY02, mIntent);//告诉原来的Activity 将数据传递给它
                finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
                break;
            case R.id.tv_filter_item:
                if(drawerlayout.isDrawerOpen(fl_filter)){
                    drawerlayout.closeDrawer(fl_filter);
                }else {
                    drawerlayout.openDrawer(fl_filter);
                }
                break;
            case R.id.tv_ensure:
                drawerlayout.closeDrawers();
                attr_values="";
                for(int i = 0; i< sceneTypeList.size(); i++){
                    List<String> attrV= sceneTypeList.get(i).getAttrVal();
                    for(int j=0;j<attrV.size();j++){
                        if(j== sceneTypeList.get(i).getCurrent()){
                            attr_values+="\"";
                            attr_values+=attrV.get(j);
                            attr_values+="\"";
                            attr_values+=",";
                            break;
                        }
                    }
                }
                if(attr_values!=null&&attr_values.length()>1){
                    attr_values=attr_values.substring(0,attr_values.length()-1);
                }
                attr_values="["+attr_values+"]";
                LogUtils.logE("attr_values",attr_values);
                p=1;
                loadScene();
                break;

        }
    }
    public void goPhoto() {
        FileUtil.openImage(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==0){
            goPhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 返回成功
            switch (requestCode) {
                case Constance.PHOTO_WITH_CAMERA: {// 拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡
                        File imageFile = new File(IssApplication.cameraPath, IssApplication.imagePath + ".jpg");
                        if (imageFile.exists()) {
                            imageURL = "file://" + imageFile.toString();
                            IssApplication.imagePath = null;
                            IssApplication.cameraPath = null;
                        } else {
                            MyToast.show(SelectSceneActivity.this,"读取图片失败！");
                        }
                    } else {
                        MyToast.show(SelectSceneActivity.this,"没有SD卡！");
                    }
                    break;
                }
                case Constance.PHOTO_WITH_DATA: // 从图库中选择图片
                    // 照片的原始资源地址
                    imageURL = data.getData().toString();
                    //                    ImageLoader.getInstance().displayImage(imageURL
                    //                            , sceneBgIv);
                    break;
            }

            String path = imageURL;
            mIntent = new Intent();
            mIntent.putExtra(Constance.SCENE, path);
            setResult(Constance.FROMDIY02, mIntent);//告诉原来的Activity 将数据传递给它
            finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
        }
    }

    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_LIST){
            OkHttpUtils.getSceneList(p,attr_values,callback);
        }else if(type==TYPE_TYPE){
            OkHttpUtils.getSceneType(callback);
        }
    }

    @Override
    public void onEndOfList(Object lastItem) {
        if(p==1&&(sceneBeans==null||sceneBeans.size()==0)||isBottom){
            return;
        }
        p++;
        loadScene();
        LogUtils.logE("loadMore","p"+p);
    }
}
