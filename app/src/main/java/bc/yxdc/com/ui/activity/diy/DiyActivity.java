package bc.yxdc.com.ui.activity.diy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.Goods;
import bc.yxdc.com.bean.GoodsShape;
import bc.yxdc.com.bean.Goods_attr_list;
import bc.yxdc.com.bean.SceneBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.inter.ISelectScreenListener;
import bc.yxdc.com.listener.IDiyProductInfoListener;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.ui.view.popwindow.DiyProductInfoPopWindow;
import bc.yxdc.com.ui.view.popwindow.SelectScreenPopWindow;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.FileUtil;
import bc.yxdc.com.utils.ImageUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.ScannerUtils;
import bc.yxdc.com.utils.UIUtils;
import bc.yxdc.com.view.SingleTouchView;
import bc.yxdc.com.view.StickerView;
import bc.yxdc.com.view.TouchView;
import bc.yxdc.com.view.TouchView02;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Created by gamekonglee on 2018/9/14.
 */

public class DiyActivity extends BaseActivity{
    private static final int TYPE_ADD_CART = 1;
    @BindView(R.id.goSaveIv)
    TextView goSaveIv;
    @BindView(R.id.goproductIv)
    TextView goproductIv;
    @BindView(R.id.goscreenctIv)
    TextView goscreenctIv;
    @BindView(R.id.jingxian_iv)
    TextView jingxian_iv;
    @BindView(R.id.goHelpIv)
    TextView goHelpIv;
    @BindView(R.id.goBackBtn01)
    TextView goBackBtn01;
    @BindView(R.id.sceneBgIv)
    ImageView sceneBgIv;
//    private LinearLayout pro_jingxiang_ll, goCart_ll, detail_ll, delete_ll,add_data_ll;
    @BindView(R.id.left_ll)View left_ll;
    @BindView(R.id.right_ll)View right_ll;
    @BindView(R.id.main_fl)FrameLayout main_fl;
    @BindView(R.id.yindao_iv) ImageView yindao_iv;
    @BindView(R.id.yindao_rl)RelativeLayout yindao_rl;
    @BindView(R.id.select_ll)LinearLayout select_ll;
    @BindView(R.id.select_product_tv)TextView select_product_tv;
    @BindView(R.id.select_diy_tv)TextView select_diy_tv;
    @BindView(R.id.add_data_tv)TextView add_data_tv;
    @BindView(R.id.pd2)ProgressBar pd2;
    @BindView(R.id.sceneFrameLayout)FrameLayout mFrameLayout;
    @BindView(R.id.diyContainerRl)RelativeLayout diyContainerRl;
    private String imageURL = "";
    private Boolean isFullScreen = false;
    private Intent mIntent;
    private int mScreenWidth;
    private int mScreenheight;
    private String mStyle = "";
    private String mSpace = "";
    private int TIME_OUT = 10 * 1000;   //超时时间
    private String CHARSET = "utf-8"; //设置编码
    private String mTitle = "";
    private String mContent = "";
    private SeekBar seekbar;
    private int mSeekNum = 50;
    private ProAdapter mAdapter;
    //    private PicsGestureLayout picsGestureLayout = null;

    //当前处于编辑状态的贴纸
    private StickerView mCurrentView;
    //存储贴纸列表


    public String mPath;
    public SparseArray<Goods> mSelectedLightSA = new SparseArray<>();
    public Goods mGoodsObject;
    public String mProperty = "";
    public String mProductId = "";
    public boolean isFromPhoto;
    public List<GoodsShape> goodsShapeList;
    public List<Goods> goodsList;
    public int mScaleType = 0;

    private List<Integer> mHelpImages;
    private int mIndexHelp = 0;
    @BindView(R.id.product_lv)ListView product_lv;
    @BindView(R.id.diy_lv)ListView diy_lv;
//    private RelativeLayout ContainerRl;
    public int mSelectType=0;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private List<View> thiss;
    List<Goods> goodses=new ArrayList<>();
    List<SceneBean> sceneBeans=new ArrayList<>();
    private int mLightNumber = -1;// 点出来的灯的编号
    private int leftMargin;
    private Bitmap mSceneBg;
    private String imgUri;
    private Bitmap imageData;
    private String id;
    private DiyProductInfoPopWindow mPopWindow;
    private View viewCpk;

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mPath = intent.getStringExtra(Constance.photo);
        mProperty = intent.getStringExtra(Constance.property);
        if(intent.hasExtra(Constance.product)){
        mGoodsObject = new Gson().fromJson((intent.getStringExtra(Constance.product)).toString(),Goods.class);
        }
        isFromPhoto = intent.getBooleanExtra(Constance.FROMPHOTO, false);

        mHelpImages = new ArrayList<>();
        mHelpImages.add(R.mipmap.design1);
        mHelpImages.add(R.mipmap.design2);
        mHelpImages.add(R.mipmap.design3);
        mHelpImages.add(R.mipmap.design4);
        mHelpImages.add(R.mipmap.design5);
        mHelpImages.add(R.mipmap.design6);

        if (isFromPhoto == true) {
            mPath = intent.getStringExtra(Constance.img_path);
            goodsShapeList = (List<GoodsShape>) intent.getSerializableExtra(Constance.productshape);
            goodsList = (List<Goods>) intent.getSerializableExtra(Constance.productlist);
            mScaleType = intent.getIntExtra(Constance.scaleType, 0);
        }

        if (AppUtils.isEmpty(mGoodsObject))
            return;
        mProductId = mGoodsObject.getGoods_id()+"";

    }

    private void switchProOrDiy() {
        select_product_tv.setBackgroundResource(R.color.deep_transparent11);
        select_diy_tv.setBackgroundResource(R.color.deep_transparent11);
        product_lv.setVisibility(View.GONE);
        diy_lv.setVisibility(View.GONE);
        if(mSelectType==0){
            select_product_tv.setBackgroundResource(R.color.deep_transparent22);
            product_lv.setVisibility(View.VISIBLE);
            add_data_tv.setText("选择产品");

        }else{
            select_diy_tv.setBackgroundResource(R.color.deep_transparent22);
            product_lv.setVisibility(View.VISIBLE);
            add_data_tv.setText("选择场景");

        }
    }

    @Override
    public void initUI() {

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //去掉Activity上面的状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_diy);
//        setStatuTextColor(this, Color.TRANSPARENT);
//        setFullScreenColor(this);
        ButterKnife.bind(this);
        //沉浸式状态栏
        goSaveIv.setOnClickListener(this);
        goHelpIv .setOnClickListener(this);
        goproductIv .setOnClickListener(this);
        goscreenctIv .setOnClickListener(this);
        mFrameLayout.setOnClickListener(this);
        main_fl .setOnClickListener(this);
        jingxian_iv.setOnClickListener(this);

        goBackBtn01.setOnClickListener(this);
        left_ll.setOnClickListener(this);
        right_ll .setOnClickListener(this);
        select_product_tv .setOnClickListener(this);
        select_diy_tv .setOnClickListener(this);
        yindao_iv = (ImageView) findViewById(R.id.yindao_iv);
        yindao_rl = (RelativeLayout) findViewById(R.id.yindao_rl);
        select_ll = (LinearLayout) findViewById(R.id.select_ll);

        boolean showHelp= MyShare.get(this).getBoolean(Constance.SHOWHELP);
        if(!showHelp){
            yindao_rl.setVisibility(View.VISIBLE);
            MyShare.get(this).putBoolean(Constance.SHOWHELP, true);
            select_ll.setVisibility(View.GONE);
        }else{
            yindao_rl.setVisibility(View.GONE);
            select_ll.setVisibility(View.VISIBLE);
        }
        left_ll.setOnClickListener(this);
        right_ll.setOnClickListener(this);
        sceneBgIv .setOnClickListener(this);

        initControll();
        mSelectType=0;
        switchProOrDiy();

    }

    private void initControll() {
        initImageLoader();
        thiss = new ArrayList<>();
        if (isFromPhoto == true) {
            for (int i = 0; i < goodsShapeList.size(); i++) {
                mGoodsObject = goodsList.get(i);
                displayCheckedGoods02(mGoodsObject, goodsShapeList.get(i));
            }
            if (!AppUtils.isEmpty(mPath)) {
                displaySceneBg(mPath, mScaleType);
            }
        } else {
            if (!AppUtils.isEmpty(mGoodsObject)) {
                displayCheckedGoods(mGoodsObject);
            }
        }

        goodses = IssApplication.mSelectProducts;
        mAdapter = new ProAdapter();
        product_lv.setAdapter(mAdapter);
    }
    @OnClick({R.id.add_data_ll,R.id.pro_jingxiang_ll,R.id.goCart_ll,R.id.detail_ll,R.id.delete_ll})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.goSaveIv:
                saveDiy();
                break;
            case R.id.goHelpIv:
                yindao_rl.setVisibility(View.VISIBLE);
                mIndexHelp=0;
                yindao_iv.setImageResource(mHelpImages.get(mIndexHelp));
                select_ll.setVisibility(View.GONE);
                break;
            case R.id.delete_ll:
                goDetele();
                break;
            case R.id.goCart_ll:
                goShoppingCart();
                break;
            case R.id.goproductIv:
                selectProduct();
                break;
            case R.id.goscreenctIv:
                selectScreen();
                break;
            case R.id.sceneFrameLayout:
                selectIsFullScreen();
                break;
            case R.id.main_fl:
                selectIsFullScreen();
                break;
            case R.id.sceneBgIv:
                selectIsFullScreen();
                break;
            case R.id.jingxian_iv:
                sendBackgroudImage();
                break;
            case R.id.detail_ll:
                getProductDetail();
                break;
            case R.id.pro_jingxiang_ll:
                sendProductJinxianImage();
                break;
            case R.id.left_ll:
                getYindaoImage(0);
                break;
            case R.id.right_ll:
                getYindaoImage(1);
                break;
            case R.id.select_product_tv://切换产品
                mSelectType=0;
                switchProOrDiy();
                selectShowData();
                break;
            case R.id.select_diy_tv://切换场景
                mSelectType=1;
                switchProOrDiy();
                selectShowData();
                break;
            case R.id.add_data_ll://添加数据
                if(mSelectType==0){
                    selectProduct();
                }else{
                    selectSceneDatas();
                }
                break;
            case R.id.goBackBtn01://退出
                getFinish();
                break;

        }
    }

    public void selectShowData() {
        if (mSelectType == 0) {
            goodses = IssApplication.mSelectProducts;
            mAdapter = new ProAdapter();
            product_lv.setAdapter(mAdapter);
        } else {
            sceneBeans = IssApplication.mSelectScreens;
            mAdapter = new ProAdapter();
            product_lv.setAdapter(mAdapter);
        }
    }

    /**
     * 保存
     */
    public void saveDiy() {
        if (!isToken()) {
            mIntent = new Intent(this, SelectSchemeActivity.class);
            startActivityForResult(mIntent, Constance.FROMSCHEME);
        }
    }

    /**
     * 保存方案
     */
    private void saveData() {
        //                产品ID的累加
        StringBuffer goodsid = new StringBuffer();
        StringBuffer spec_item_id=new StringBuffer();
        for (int i = 0; i < mSelectedLightSA.size(); i++) {
            goodsid.append(mSelectedLightSA.valueAt(i).getGoods_id()+ "");
            String itemid=mSelectedLightSA.valueAt(i).c_property;
            if(TextUtils.isEmpty(itemid)){
                itemid="0";
            }
            spec_item_id.append(itemid);
            if (i < mSelectedLightSA.size() - 1) {
                goodsid.append(",");
                spec_item_id.append(",");
            }
        }

        diyContainerRl.setVisibility(View.INVISIBLE);
        select_ll.setVisibility(View.INVISIBLE);

        if (!AppUtils.isEmpty(mCurrentView))
            mCurrentView.setInEdit(false);

        //截图
//        final Bitmap imageData = ImageUtil.compressImage(ImageUtil.takeScreenShot(this));
        imageData = ImageUtil.takeScreenShot(this);
        imgUri = ScannerUtils.saveImageToGallery(this, imageData, ScannerUtils.ScannerType.RECEIVER);
        select_ll.setVisibility(View.VISIBLE);
        diyContainerRl.setVisibility(View.VISIBLE);
        setShowDialog(true);
        setShowDialog("正在保存中...");
        showLoading();
        final String url = NetWorkConst.FANGANUPLOAD;//地址
        int id = MyShare.get(this).getInt(Constance.USERCODEID);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("title", mTitle);
        params.put("goods_id", goodsid.toString());
        params.put("spec_item_id",spec_item_id.toString());
        params.put("remark", mContent);
        params.put("style", mStyle);
        params.put("room", mSpace);
        params.put("is_private",  "0");
        params.put("token",MyShare.get(this).getString(Constance.token));
        params.put("unique_id","bocang");


        final String imageName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".png";
        new Thread(new Runnable() { //开启线程上传文件
            @Override
            public void run() {
                final String resultJson = uploadFile(imageData, url, params, imageName);
                //                final Result result = JSON.parseObject(resultJson, Result.class);
                //分享的操作
                LogUtils.logE("upload",resultJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        if (AppUtils.isEmpty(resultJson)) {
                            MyToast.show(DiyActivity.this, "保存失败!");
                            return;
                        }


                        com.alibaba.fastjson.JSONObject object = JSON.parseObject(resultJson);
                        int isResult = object.getInteger(Constance.status);
                        if (isResult != 1) {
                            MyToast.show(DiyActivity.this, "保存失败!");
                            return;
                        }
                        MyToast.show(DiyActivity.this,"保存成功!");
                        UIUtils.showShareDialog(DiyActivity.this,imageData,"",imgUri);
//                        String title = "来自 " + mTitle + " 方案的分享";
//                        final String path = NetWorkConst.SHAREFANAN + object.getJSONObject(Constance.fangan).getString(Constance.id);
////                        final String imgPath = NetWorkConst.SCENE_HOST + object.getJSONObject(Constance.fangan).getString(Constance.path);
//                        final String imgPath = imgUri;
//                        Intent intent = new Intent(DiyActivity.this, ShareProgrammeActivity.class);
//                        intent.putExtra(Constance.SHARE_PATH, path);
//                        intent.putExtra(Constance.SHARE_IMG_PATH, imgPath);
//                        intent.putExtra(Constance.TITLE, title);
//                        intent.putExtra(Constance.id,object.getJSONObject(Constance.fangan).getString(Constance.id));
//                        startActivity(intent);
                    }
                });

            }
        }).start();
    }

    /**
     * 照相
     */
    public void goPhoto() {
        FileUtil.openImage(this);
    }

    /**
     * 产品详情
     */
    public void getProductDetail() {
        mPopWindow = new DiyProductInfoPopWindow(this,this);
        final Goods jsonObject = mSelectedLightSA.get(IssApplication.mLightIndex);
        if (AppUtils.isEmpty(jsonObject)) {
            MyToast.show(this, "请选择产品!");
            return;
        }


        mPopWindow.productObject = jsonObject;
        mPopWindow.initViewData();
        mPopWindow.onShow(main_fl);
        mPopWindow.setListener(new IDiyProductInfoListener() {
            @Override
            public void onDiyProductInfo(int type, String msg) {
                getShowProductType(jsonObject, type, msg);
            }
        });

    }

    /**
     * 删除
     */
    public void goDetele() {
        mFrameLayout.removeView(mFrameLayout.findViewWithTag(IssApplication.mLightIndex));
        thiss.remove(mCurrentView);
        mSelectedLightSA.remove(IssApplication.mLightIndex);
    }


    /**
     * 产品镜像
     */
    public void sendProductJinxianImage() {
        try {
            StickerView stickerView = (StickerView) mFrameLayout.findViewWithTag(IssApplication.mLightIndex);
            stickerView.getFlipView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void displayCheckedGoods(final Goods goods) {
        if (AppUtils.isEmpty(goods))
            return;
        String path="";
        if(goods!=null)
        {
            if(TextUtils.isEmpty(goods.c_url)){
                path =NetWorkConst.API_HOST+goods.getOriginal_img();
            }else {
                path=NetWorkConst.API_HOST+goods.c_url;
            }
        }else {
            MyToast.show(this,"该产品尚无图片");
            return;
        }
        imageLoader.loadImage(path, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        pd2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        pd2.setVisibility(View.GONE);
                        MyToast.show(DiyActivity.this, failReason.getCause() + "请重试！");
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        pd2.setVisibility(View.GONE);
                        // 被点击的灯的编号加1
                        mLightNumber++;
                        // 把点击的灯放到集合里
                        mSelectedLightSA.put(mLightNumber, goods);
                        addStickerView(loadedImage);

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        pd2.setVisibility(View.GONE);
                    }
                });
    }


    //添加表情
    private void addStickerView(Bitmap bitmap) {

        final StickerView stickerView = new StickerView(this);
        stickerView.setBitmap(bitmap);
        stickerView.mLightCount = mLightNumber;
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                thiss.remove(stickerView);
                mFrameLayout.removeView(stickerView);
                mSelectedLightSA.remove(IssApplication.mLightIndex);
            }

            @Override
            public void onEdit(StickerView stickerView) {
                mCurrentView.setInEdit(false);
                mCurrentView = stickerView;
                mCurrentView.setInEdit(true);
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = thiss.indexOf(stickerView);
                if (position == thiss.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) thiss.remove(position);
                thiss.add(thiss.size(), stickerTemp);
            }
        });
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        stickerView.setTag(mLightNumber);
        mFrameLayout.addView(stickerView, lp2);
        thiss.add(stickerView);
        setCurrentEdit(stickerView);
    }

    /**
     * 设置当前处于编辑模式的贴纸
     */
    private void setCurrentEdit(StickerView stickerView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        mCurrentView = stickerView;
        stickerView.setInEdit(true);
    }


    /**
     * 添加图片
     *
     * @param bitmap
     */
    private void addImageView(Bitmap bitmap) {
        pd2.setVisibility(View.GONE);
        // 被点击的灯的编号加1
        mLightNumber++;
        IssApplication.mLightIndex = mLightNumber;
        // 设置灯图的ImageView的初始宽高和位置
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                mScreenWidth / 3 * 2 / 3,
                (mScreenWidth / 3 * 2 / 3 * bitmap.getHeight()) / bitmap.getWidth());
        // 设置灯点击出来的位置
        if (mSelectedLightSA.size() == 1) {
            leftMargin = mScreenWidth / 3 * 2 / 3;
        } else if (mSelectedLightSA.size() == 2) {
            leftMargin = mScreenWidth / 3 * 2 / 3 * 2;
        } else if (mSelectedLightSA.size() == 3) {
            leftMargin = 0;
        }
        lp.setMargins(mScreenWidth / 2 - (mScreenWidth / 3 * 2 / 6), 20, 0, 0);

        TouchView02 touchView = new TouchView02(this);
        touchView.setLayoutParams(lp);
        touchView.setImageBitmap(bitmap);// 设置被点击的灯的图片
        touchView.setmLightCount(mLightNumber);// 设置被点击的灯的编号
        touchView.setTag(mLightNumber);
        FrameLayout.LayoutParams newLp = new FrameLayout.LayoutParams(
                UIUtils.dip2PX(100),
                UIUtils.dip2PX(100));
        newLp.setMargins(150,100,0,0);
//        FrameLayout newFrameLayout = new FrameLayout(this);
//        newFrameLayout.setLayoutParams(newLp);
//        newFrameLayout.addView(touchView);
//        newFrameLayout.setTag(mLightNumber);
//        mFrameLayout.addView(newFrameLayout);
//        touchView.setContainer(mFrameLayout, newFrameLayout);
        mFrameLayout.addView(touchView,newLp);
    }




    private void displayCheckedGoods02(final Goods goods, final GoodsShape goodsShape) {
        if (AppUtils.isEmpty(goods))
            return;
        String path =NetWorkConst.API_HOST+goods.getOriginal_img();
        imageLoader.loadImage(path, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        pd2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        pd2.setVisibility(View.GONE);
                        MyToast.show(DiyActivity.this, failReason.getCause() + "请重试！");
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        pd2.setVisibility(View.GONE);
                        // 被点击的灯的编号加1
                        mLightNumber++;
                        // 把点击的灯放到集合里
                        mSelectedLightSA.put(mLightNumber, goods);

                        // 设置灯图的ImageView的初始宽高和位置
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(goodsShape.getWidth(), goodsShape.getHeight());
                        // 设置灯点击出来的位置
                        if (mSelectedLightSA.size() == 1) {
                            leftMargin = mScreenWidth / 3 * 2 / 3;
                        } else if (mSelectedLightSA.size() == 2) {
                            leftMargin = mScreenWidth / 3 * 2 / 3 * 2;
                        } else if (mSelectedLightSA.size() == 3) {
                            leftMargin = 0;
                        }
                        //                        lp.setMargins(goodsShape.getY(),0, 0,Math.abs(goodsShape.getX()));
                        //                        lp.setMargins(goodsShape.getY(), mScreenheight - goodsShape.getX() - (goodsShape.getHeight()), 0, 0);
                        lp.setMargins(goodsShape.getX(), goodsShape.getY(), 0, 0);


                        //                        lp.setMargins(goodsShape.getY(),482, 0,0);
                        //                        if(goodsShape.getX()>0){
                        //                            lp.setMargins(goodsShape.getY(),0, 0,goodsShape.getX());
                        //                        }else{
                        //                            lp.setMargins(goodsShape.getY(),mScreenheight/2-goodsShape.getX(), 0,0);
                        //                        }
                        Matrix matrix = new Matrix();
                        // 设置旋转角度
                        matrix.setRotate(goodsShape.getRotate());
                        // 重新绘制Bitmap
                        loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, true);


                        TouchView touchView = new TouchView(DiyActivity.this);
                        touchView.setLayoutParams(lp);
                        touchView.setImageBitmap(loadedImage);// 设置被点击的灯的图片
                        touchView.setmLightCount(mLightNumber);// 设置被点击的灯的编号
                        touchView.setTag(mLightNumber);
                        FrameLayout.LayoutParams newLp = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);

                        FrameLayout newFrameLayout = new FrameLayout(DiyActivity.this);
                        newFrameLayout.setLayoutParams(newLp);
                        newFrameLayout.addView(touchView);
                        newFrameLayout.setTag(mLightNumber);
                        mFrameLayout.addView(newFrameLayout);

                        touchView.setContainer(mFrameLayout, newFrameLayout);
                        touchView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MyToast.show(DiyActivity.this, mLightNumber + "");
                            }
                        });
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        pd2.setVisibility(View.GONE);
                    }
                });
    }

    private void displayCheckedGoods03(String path) {
        if (AppUtils.isEmpty(path))
            return;
        imageLoader.loadImage(path, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        pd2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        pd2.setVisibility(View.GONE);
                        MyToast.show(DiyActivity.this, failReason.getCause() + "请重试！");
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        pd2.setVisibility(View.GONE);
                        // 被点击的灯的编号加1
                        mLightNumber++;
                        // 把点击的灯放到集合里

                        //                        // 设置灯图的ImageView的初始宽高和位置
                        //                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        //                                mScreenWidth / 3 * 2 / 3,
                        //                                (mScreenWidth / 3 * 2 / 3 * loadedImage.getHeight()) / loadedImage.getWidth());
                        //                        // 设置灯点击出来的位置
                        //                        if (mSelectedLightSA.size() == 1) {
                        //                            leftMargin = mScreenWidth / 3 * 2 / 3;
                        //                        } else if (mSelectedLightSA.size() == 2) {
                        //                            leftMargin = mScreenWidth / 3 * 2 / 3 * 2;
                        //                        } else if (mSelectedLightSA.size() == 3) {
                        //                            leftMargin = 0;
                        //                        }
                        //                        lp.setMargins(leftMargin, 0, 0, 0);
                        // 设置灯图的ImageView的初始宽高和位置
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                mScreenWidth / 3 * 2 / 3,
                                (mScreenWidth / 3 * 2 / 3 * loadedImage.getHeight()) / loadedImage.getWidth());
                        // 设置灯点击出来的位置
                        if (mSelectedLightSA.size() == 1) {
                            leftMargin = mScreenWidth / 3 * 2 / 3;
                        } else if (mSelectedLightSA.size() == 2) {
                            leftMargin = mScreenWidth / 3 * 2 / 3 * 2;
                        } else if (mSelectedLightSA.size() == 3) {
                            leftMargin = 0;
                        }
                        lp.setMargins(mScreenWidth / 2 - (mScreenWidth / 3 * 2 / 6), 20, 0, 0);


                        TouchView02 touchView = new TouchView02(DiyActivity.this);
                        touchView.setLayoutParams(lp);
                        touchView.setImageBitmap(loadedImage);// 设置被点击的灯的图片
                        touchView.setmLightCount(mLightNumber);// 设置被点击的灯的编号
                        touchView.setTag(mLightNumber);
                        FrameLayout.LayoutParams newLp = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);

                        FrameLayout newFrameLayout = new FrameLayout(DiyActivity.this);
                        newFrameLayout.setLayoutParams(newLp);
                        newFrameLayout.addView(touchView);
                        newFrameLayout.setTag(mLightNumber);
                        mFrameLayout.addView(newFrameLayout);

                        touchView.setContainer(mFrameLayout, newFrameLayout);


                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        pd2.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * 加载场景背景图
     *
     * @param path 场景img_url
     */
    private void displaySceneBg(String path, int scaleType) {
        if (scaleType == 0) {
            sceneBgIv.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            sceneBgIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        imageURL = path;
        imageLoader.displayImage(path, sceneBgIv, options,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        pd2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        pd2.setVisibility(View.GONE);
                        MyToast.show(DiyActivity.this, failReason.getCause() + "请重试！");
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        pd2.setVisibility(View.GONE);


                        mSceneBg = ImageUtil.drawable2Bitmap(sceneBgIv.getDrawable());
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        pd2.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * 判断是否满屏
     */
    public void selectIsFullScreen() {
        if (!isFullScreen) {
            diyContainerRl.setVisibility(View.INVISIBLE);
            select_ll.setVisibility(View.GONE);
            isFullScreen = true;
        } else {
            diyContainerRl.setVisibility(View.VISIBLE);
            select_ll.setVisibility(View.VISIBLE);
            isFullScreen = false;
        }
        if (AppUtils.isEmpty(mCurrentView))
            return;
        mCurrentView.setInEdit(false);
    }


    /**
     * 选产品
     */
    public void selectProduct() {
        mIntent = new Intent(this, SelectGoodsActivity.class);
        mIntent.putExtra(Constance.ISSELECTGOODS, true);
        startActivityForResult(mIntent, Constance.FROMDIY);
    }

    /**
     * 选场景
     */
    public void selectScreen() {
        SelectScreenPopWindow popWindow = new SelectScreenPopWindow(this);
        popWindow.setListener(new ISelectScreenListener() {
            @Override
            public void onSelectScreenChanged(int type) {
                switch (type) {
                    case 0:
                        selectSceneDatas();
                        break;
                    case 1:
                        FileUtil.getTakePhoto(DiyActivity.this);
                        break;
                    case 2:
                        FileUtil.getPickPhoto(DiyActivity.this);
                        break;
                }
            }
        });
        popWindow.onShow(main_fl);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtils.logE("onRequestPermissionsResult",""+requestCode+","+grantResults);
        if(grantResults[0]==PERMISSION_GRANTED){
            if(requestCode==7){
                FileUtil.getTakePhoto(DiyActivity.this);
            }else if(requestCode==4){
                FileUtil.getPickPhoto(DiyActivity.this);
            }
        }
    }

    /**
     * 选择场景
     */
    public void selectSceneDatas() {
        mIntent = new Intent(this, SelectSceneActivity.class);
        mIntent.putExtra(Constance.ISSELECTSCRENES, true);
        startActivityForResult(mIntent, Constance.FROMDIY02);
    }
    private void getShowProductType(final Goods jsonObject, int type, String msg) {
        int productId = jsonObject.getGoods_id();
        switch (type) {
            case 0://二维码
                String name = jsonObject.getGoods_name();
                String path = NetWorkConst.SHAREPRODUCT + productId+".html";
                addImageView(ImageUtil.getTowCodeImage(ImageUtil.createQRImage(path, 150, 150), name));
                break;
            case 1://参数
                addImageView(ImageUtil.textAsBitmap(msg));
                break;
            case 2://Logo
//                String logoPath = NetWorkConst.SHAREIMAGE_LOGO;
//                displayCheckedGoods03("file://"+logoPath);
                mLightNumber++;
                Bitmap loadedImage=UIUtils.drawableToBitmap(UIUtils.dip2PX(100),UIUtils.dip2PX(100),getResources().getDrawable(R.mipmap.logo));
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        mScreenWidth / 3 * 2 / 3,
                        (mScreenWidth / 3 * 2 / 3 * loadedImage.getHeight()) / loadedImage.getWidth());
                // 设置灯点击出来的位置
                if (mSelectedLightSA.size() == 1) {
                    leftMargin = mScreenWidth / 3 * 2 / 3;
                } else if (mSelectedLightSA.size() == 2) {
                    leftMargin = mScreenWidth / 3 * 2 / 3 * 2;
                } else if (mSelectedLightSA.size() == 3) {
                    leftMargin = 0;
                }
                lp.setMargins(mScreenWidth / 2 - (mScreenWidth / 3 * 2 / 6), 20, 0, 0);


                TouchView02 touchView = new TouchView02(DiyActivity.this);
                touchView.setLayoutParams(lp);
                touchView.setImageBitmap(loadedImage);// 设置被点击的灯的图片
                touchView.setmLightCount(mLightNumber);// 设置被点击的灯的编号
                touchView.setTag(mLightNumber);
                FrameLayout.LayoutParams newLp = new FrameLayout.LayoutParams(
                        UIUtils.dip2PX(100),
                        UIUtils.dip2PX(100));
                mFrameLayout.addView(touchView,newLp);

                break;
            case 3://产品卡
                OkHttpUtils.getGoodsContent(productId, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONObject res=new JSONObject(response.body().string());
                        if(res.getInt(Constance.status)==1) {
                            final String path = "http://www.tianxiadengcang.com/Mobile/Goods/goodsInfo/id/"+jsonObject.getGoods_id()+".html";
                            final List<Goods_attr_list> goods_attr_lists = new Gson().fromJson(res.getJSONObject(Constance.result).getJSONArray(Constance.goods_attr_list).toString(), new TypeToken<List<Goods_attr_list>>() {}.getType());
                            viewCpk = View.inflate(DiyActivity.this, R.layout.layout_share_01_small,null);
                            viewCpk.setLayoutParams(new FrameLayout.LayoutParams(UIUtils.dip2PX(170),UIUtils.dip2PX(250)));
                            final ImageView iv_share_01= viewCpk.findViewById(R.id.iv_share_01);
                            LinearLayout ll_share_01= viewCpk.findViewById(R.id.ll_attr);
                            final ImageView iv_code_01= viewCpk.findViewById(R.id.iv_code);
                            final ListView lv_param= viewCpk.findViewById(R.id.lv_paramter);
                            ImageLoader.getInstance().loadImage(NetWorkConst.API_HOST + jsonObject.c_url, IssApplication.getImageLoaderOption(), new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String s, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String s, View view, FailReason failReason) {

                                }

                                @Override
                                public void onLoadingComplete(String s, final View view, Bitmap bitmap) {
                                    final Bitmap bitmap01= ImageUtil.createQRImage(path, 128, 128);

                                    final QuickAdapter adapter = new QuickAdapter<Goods_attr_list>(DiyActivity.this,R.layout.item_share_params_small) {
                                        @Override
                                        protected void convert(BaseAdapterHelper helper, Goods_attr_list item) {
                                            helper.setText(R.id.tv_key,item.getAttr_name()+":");
                                            helper.setText(R.id.tv_value,item.getAttr_value());
                                        }
                                    };
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            iv_code_01.setImageBitmap(bitmap01);
                                            lv_param.setAdapter(adapter);
                                            adapter.replaceAll(goods_attr_lists);
//                                    mFrameLayout.addView(view);
                                            Bitmap temp= UIUtils.view2Bitmap(viewCpk);
                                            addImageView(temp);
                                        }
                                    });
                                    iv_share_01.setImageBitmap(bitmap);

                                }

                                @Override
                                public void onLoadingCancelled(String s, View view) {

                                }
                            });


                        }
                    }
                });

//                String cardPath = NetWorkConst.WEB_PRODUCT_CARD + productId;
//                Intent intent=new Intent(this, WebViewActivity.class);
//                intent.putExtra(Constance.url,cardPath);
//                startActivityForResult(intent,300);
//                displayCheckedGoods03(cardPath);

                break;
        }
    }

    /**
     * 背景图镜像
     */
    public void sendBackgroudImage() {
        Bitmap mBitmap = ImageUtil.drawable2Bitmap(sceneBgIv.getDrawable());
        if (mBitmap != null) {

            Bitmap temp = ImageUtil.convertBmp(mBitmap);
            if (temp != null) {
                sceneBgIv.setImageBitmap(temp);
                mBitmap.recycle();
            }
        }
    }


    private void getYindaoImage(int type) {
        if (type == 0) {//左边
            if (mIndexHelp != 0) {
                mIndexHelp = mIndexHelp - 1;
                yindao_iv.setImageResource(mHelpImages.get(mIndexHelp));
            } else {
                yindao_rl.setVisibility(View.GONE);
                select_ll.setVisibility(View.VISIBLE);
            }

        } else {//右边
            if (mIndexHelp != mHelpImages.size() - 1) {
                mIndexHelp = mIndexHelp + 1;
                yindao_iv.setImageResource(mHelpImages.get(mIndexHelp));
            } else {
                yindao_rl.setVisibility(View.GONE);
                select_ll.setVisibility(View.VISIBLE);
            }

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        getFinish();
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出
     */
    private void getFinish(){
        UIUtils.showSingleWordDialog(this, "是否退出配灯界面?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                IssApplication.mSelectProducts=new ArrayList<>();
                IssApplication.mSelectScreens=new ArrayList<>();
            }
        });
    }
    /**
     * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    private String uploadFile(Bitmap file, String RequestURL, Map<String, String> param, String imageName) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        String token = MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);
        // 显示进度框
        //      showProgressDialog();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("X-bocang-Authorization", token);
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();

                String params = "";
                if (param != null && param.size() > 0) {
                    Iterator<String> it = param.keySet().iterator();
                    while (it.hasNext()) {
                        sb = null;
                        sb = new StringBuffer();
                        String key = it.next();
                        String value = param.get(key);
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                        sb.append(value).append(LINE_END);
                        params = sb.toString();
                        dos.write(params.getBytes());
                    }
                }
                sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"").append("plan_img_file").append("\"")
                        .append(";filename=\"").append(imageName).append("\"\n");
                sb.append("Content-Type: image/png");
                sb.append(LINE_END).append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = ImageUtil.Bitmap2InputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }


                is.close();
                //                dos.write(file);
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);

                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */

                int res = conn.getResponseCode();
                System.out.println("res=========" + res);
                if (res == 200) {
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                } else {
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
                            mPath=imageURL;
                            displaySceneBg(mPath, 0);
                        } else {
                            MyToast.show(DiyActivity.this,"读取图片失败！");
                        }
                    } else {
                        MyToast.show(DiyActivity.this,"没有SD卡！");
                    }
                    break;
                }
                case Constance.PHOTO_WITH_DATA: // 从图库中选择图片
                    // 照片的原始资源地址
                    imageURL = data.getData().toString();
                    displaySceneBg(imageURL, 1);
                    break;
            }
        } else if (requestCode == Constance.FROMDIY) {
            mSelectType = 0;
            switchProOrDiy();
        } else if (requestCode == Constance.FROMDIY02) {
            selectShowData();
            if (AppUtils.isEmpty(data))
                return;
            mPath = data.getStringExtra(Constance.SCENE);
            if (!AppUtils.isEmpty(mPath)) {
                displaySceneBg(mPath, 0);
            }
        } else if (requestCode == Constance.FROMSCHEME) {
            if (AppUtils.isEmpty(data))
                return;
            mStyle = data.getStringExtra(Constance.style);
            mSpace = data.getStringExtra(Constance.space);
            mContent = data.getStringExtra(Constance.content);
            mTitle = data.getStringExtra(Constance.title);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                saveData();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }if(requestCode==300){
            displayCheckedGoods03("file://"+data.getStringExtra("path"));
        }
    }




    private boolean isGoCart = false;

    /**
     * 保存购物车
     */
    public void goShoppingCart() {
        if (!isToken()) {
            if (mSelectedLightSA.size() == 0) {
                MyToast.show(this, "请选择产品");
                return;
            }
            isGoCart = false;
            for (int i = 0; i < mSelectedLightSA.size(); i++) {
                Goods object = mSelectedLightSA.valueAt(i);
                id = object.getGoods_id()+"";
                mProperty=object.c_property;
                sendGoShoppingCart(id, mProperty, 1);
                if (i == mSelectedLightSA.size() - 1) {
                    isGoCart = true;
                }
            }
        }
    }

    private void sendGoShoppingCart(String id, String c_property, int i) {
        misson(TYPE_ADD_CART, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONObject jsonObject=new JSONObject(response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.show(DiyActivity.this,jsonObject.getString(Constance.msg));
                    }
                });

            }
        });
    }


    @Override
    public void getData(int type, Callback callback) {
        if(TYPE_ADD_CART==type){
            String userid=MyShare.get(this).getString(Constance.user_id);
            String token=MyShare.get(this).getString(Constance.token);
            if(!TextUtils.isEmpty(userid)&&!TextUtils.isEmpty(token)){
                OkHttpUtils.addToShopCart(id,mProperty,1,userid,token,callback);
            }

        }
    }

    private void initImageLoader() {
        // 设置图片下载期间显示的图片
// 设置图片Uri为空或是错误的时候显示的图片
// 设置图片加载或解码过程中发生错误显示的图片
// 设置下载的图片是否缓存在内存中
//设置图片的质量
// 设置下载的图片是否缓存在SD卡中
// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
// 是否考虑JPEG图像EXIF参数（旋转，翻转）
//                .considerExifParams(true)
// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
// 图片加载好后渐入的动画时间
// .displayer(new FadeInBitmapDisplayer(100))
// 构建完成
        options = new DisplayImageOptions.Builder()
                // 设置图片下载期间显示的图片
                .showImageOnLoading(R.drawable.bg_default)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.drawable.bg_default)
                // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnFail(R.drawable.bg_default)
                // 设置下载的图片是否缓存在内存中
                .cacheInMemory(false)
                //设置图片的质量
                .bitmapConfig(Bitmap.Config.RGB_565)
                // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                //                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
                // 图片加载好后渐入的动画时间
                // .displayer(new FadeInBitmapDisplayer(100))
                .build();

        // 得到ImageLoader的实例(使用的单例模式)
        imageLoader = ImageLoader.getInstance();
    }

    private class ProAdapter extends BaseAdapter {
        public ProAdapter() {
        }

        @Override
        public int getCount() {
            if(mSelectType==0){

            if (null == goodses)
                return 0;
            return goodses.size();
            }else {
                if (null == sceneBeans)
                    return 0;
                return sceneBeans.size();
            }

        }

        @Override
        public Object getItem(int position) {
            if(mSelectType==0){
            if (null == goodses)
                return null;
            return goodses.get(position);
            }else {
                if (null == sceneBeans)
                    return null;
                return sceneBeans.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(DiyActivity.this, R.layout.item_gridview_diy, null);


                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.delete_iv = (ImageView) convertView.findViewById(R.id.delete_iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageResource(R.drawable.bg_default);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            try {
                if (mSelectType == 0) {
                    String url;
                    if(TextUtils.isEmpty(goodses.get(position).c_url)){
                        url=NetWorkConst.API_HOST+goodses.get(position).getOriginal_img();
                    }else {
                        url=NetWorkConst.API_HOST+goodses.get(position).c_url;
                    }
                    ImageLoader.getInstance().displayImage(url
                            , holder.imageView);
                    holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    holder.imageView.setImageResource(R.drawable.bg_default);
                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST +
                            sceneBeans.get(position).getScene().getPath(), holder.imageView);
                    holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            } catch (Exception e) {

            }


            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectType == 0) {
                        if (AppUtils.isEmpty(goodses.get(position)))
                            return;
                        mGoodsObject = goodses.get(position);
                        displayCheckedGoods(mGoodsObject);
                    } else {
                        if (AppUtils.isEmpty(sceneBeans.get(position)))
                            return;
                        mPath = NetWorkConst.API_HOST + sceneBeans.get(position).getScene().getPath();
                        if (!AppUtils.isEmpty(mPath)) {
                            displaySceneBg(mPath, 0);
                        }
                    }

                    try {
                        ((SingleTouchView) (mFrameLayout.findViewWithTag(IssApplication.mLightIndex))).isScale = false;
                    } catch (Exception e) {

                    }
                }
            });

            holder.delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectType == 0) {
                        IssApplication.mSelectProducts.remove(position);
                        notifyDataSetChanged();

                    } else {
                        IssApplication.mSelectScreens.remove(position);
                        notifyDataSetChanged();
                    }

                }
            });

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            ImageView delete_iv;
        }
    }
}
