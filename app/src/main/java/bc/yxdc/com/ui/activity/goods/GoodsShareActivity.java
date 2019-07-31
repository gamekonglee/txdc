package bc.yxdc.com.ui.activity.goods;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.Goods_attr_list;
import bc.yxdc.com.bean.Goods_spec_list;
import bc.yxdc.com.bean.ProductResult;
import bc.yxdc.com.bean.Spec_goods_price;
import bc.yxdc.com.bean.Spec_list;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.utils.ImageUtil;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.ScannerUtils;
import bc.yxdc.com.utils.ShareUtil;
import bc.yxdc.com.utils.UIUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/11/12.
 */

public class GoodsShareActivity  extends BaseActivity{
    private static final int TYPE_PRO_CONTENT = 0;
    @BindView(R.id.tv_share_01)TextView tv_share_01;
    @BindView(R.id.tv_share_02)TextView tv_share_02;
    @BindView(R.id.tv_share_03)TextView tv_share_03;
    @BindView(R.id.tv_share_04)TextView tv_share_04;
    @BindView(R.id.vp_share)ViewPager vp_share;
    @BindView(R.id.btn_share) Button btn_share;
    @BindView(R.id.ll_share_current)View ll_share_current;
    private int shareType;
    private List<View> viewList;
    private ProductResult goods;
    private String img_url;
    private String path;
    private QuickAdapter<Goods_attr_list> adapter;
    private List<Goods_attr_list> goods_attr_lists;
    private Bitmap mBitmap;
    private String mLocalPath;
    private String property_name;
    private QuickAdapter<Spec_list> adapter_attr;
    private int currentUrl;
    private int currentAttrPosi;

    @Override
    protected void initData() {
        goods = new Gson().fromJson(getIntent().getStringExtra(Constance.product),ProductResult.class);
        img_url = goods.getGoods().getOriginal_img();
        path = "http://www.tianxiadengcang.com/Mobile/Goods/goodsInfo/id/"+goods.getGoods().getGoods_id()+".html";
        property_name = getIntent().getStringExtra(Constance.property_name);

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_share_goods);
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);
        ButterKnife.bind(this);
        shareType = 0;

        View view_01=View.inflate(this,R.layout.layout_share_01,null);
        View view_02=View.inflate(this,R.layout.layout_share_02,null);
        View view_03=View.inflate(this,R.layout.layout_share_03,null);
        View view_04=View.inflate(this,R.layout.layout_share_04,null);
        viewList = new ArrayList<>();
        viewList.add(view_01);
        viewList.add(view_02);
        viewList.add(view_03);
        viewList.add(view_04);
        vp_share.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                LogUtils.logE("position,offset",position+","+positionOffset);

            }

            @Override
            public void onPageSelected(int position) {
                shareType=position;
                refreshUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        misson(TYPE_PRO_CONTENT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final bocang.json.JSONObject res=new bocang.json.JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bocang.json.JSONObject result=res.getJSONObject(Constance.result);
                            goods_attr_lists = new ArrayList<>();
                            bocang.json.JSONArray array=result.getJSONArray(Constance.goods_attr_list);
                            goods_attr_lists=new Gson().fromJson(array.toString(),new TypeToken <List<Goods_attr_list>>(){}.getType());
                            MyPagerAdapter myPagerAdapter=new MyPagerAdapter();
                            vp_share.setAdapter(myPagerAdapter);


                        }
                    });
                }
            }
        });

    }

    @OnClick({R.id.tv_share_01,R.id.tv_share_02,R.id.tv_share_03,R.id.tv_share_04,R.id.btn_share})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_share_01:
                shareType=0;
                break;
            case R.id.tv_share_02:
                shareType=1;
                break;
            case R.id.tv_share_03:
                shareType=2;
                break;
            case R.id.tv_share_04:
                shareType=3;
                break;
        }
        if(v.getId()!=R.id.btn_share){
            refreshUI();
        }else {
            getSaveImage();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1&&grantResults[0]==0){
            new Thread(runnable).start();
        }
    }

    /**
     * 保存图片
     */
    public void getSaveImage() {
        ActivityCompat.requestPermissions(this,
                new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                1);
        PackageManager packageManager = getPackageManager();
        int permission = packageManager.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", getPackageName());
        if (PackageManager.PERMISSION_GRANTED != permission) {
            return;
        } else {
//            setShowDialog(true);
//            mActivity.setShowDialog("正在保存中..");
//            showLoading();
        }
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBitmap = ImageUtil.createViewBitmap(ll_share_current);
                    mLocalPath = ScannerUtils.saveImageToGallery(GoodsShareActivity.this, mBitmap, ScannerUtils.ScannerType.RECEIVER);
                    getShareApp();
//                    if(mBitmap !=null&& mBitmap.isRecycled()) mBitmap.recycle();
                }
            });


        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBitmap !=null&& !mBitmap.isRecycled()) mBitmap.recycle();
    }

    /**
     * 分享给好友
     */
    public void getShareApp() {
        final String title = "来自 " + UIUtils.getString(R.string.app_name) + " App的分享";
        final Dialog dialog=UIUtils.showBottomInDialog(this, R.layout.share_dialog,UIUtils.dip2PX(205));
        TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);
        LinearLayout ll_wx=dialog.findViewById(R.id.ll_wx);
        LinearLayout ll_pyq=dialog.findViewById(R.id.ll_pyq);
        LinearLayout ll_qq=dialog.findViewById(R.id.ll_qq);
        LinearLayout ll_link=dialog.findViewById(R.id.ll_link);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ll_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareWxPic(GoodsShareActivity.this,title,mBitmap,true);
                dialog.dismiss();
            }
        });
        ll_pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareWxPic(GoodsShareActivity.this,title,mBitmap,false);
                dialog.dismiss();
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareQQLocalpic(GoodsShareActivity.this,mLocalPath ,title);
                dialog.dismiss();
            }
        });
        ll_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(path);
                MyToast.show(GoodsShareActivity.this,"链接复制成功！");
                dialog.dismiss();
            }
        });
    }
    
    private void refreshUI() {
        tv_share_01.setTextColor(getResources().getColor(R.color.tv_333333));
        tv_share_02.setTextColor(getResources().getColor(R.color.tv_333333));
        tv_share_03.setTextColor(getResources().getColor(R.color.tv_333333));
        tv_share_04.setTextColor(getResources().getColor(R.color.tv_333333));
        tv_share_01.setBackgroundResource(R.drawable.bg_efefef_corner_15);
        tv_share_02.setBackgroundResource(R.drawable.bg_efefef_corner_15);
        tv_share_03.setBackgroundResource(R.drawable.bg_efefef_corner_15);
        tv_share_04.setBackgroundResource(R.drawable.bg_efefef_corner_15);
        switch (shareType){
            case 0:
                tv_share_01.setTextColor(getResources().getColor(R.color.theme_red));
                tv_share_01.setBackgroundResource(R.drawable.bg_red_light_red_corner_15);
                break;
            case 1:
                tv_share_02.setTextColor(getResources().getColor(R.color.theme_red));
                tv_share_02.setBackgroundResource(R.drawable.bg_red_light_red_corner_15);
                break;
            case 2:
                tv_share_03.setTextColor(getResources().getColor(R.color.theme_red));
                tv_share_03.setBackgroundResource(R.drawable.bg_red_light_red_corner_15);
                break;
            case 3:
                tv_share_04.setTextColor(getResources().getColor(R.color.theme_red));
                tv_share_04.setBackgroundResource(R.drawable.bg_red_light_red_corner_15);
                break;
        }
        vp_share.setCurrentItem(shareType);

    }

    class MyPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view=viewList.get(position);
            container.addView(view);
            switch (position){
                case 0:
                    final ImageView iv_share_01=view.findViewById(R.id.iv_share_01);
                    GridView gv_attr=view.findViewById(R.id.ll_attr);
                    ImageView iv_code_01=view.findViewById(R.id.iv_code);
                    ListView lv_param=view.findViewById(R.id.lv_paramter);
                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+img_url,iv_share_01, IssApplication.getImageLoaderOption());
                    Bitmap bitmap01= ImageUtil.createQRImage(path, 128, 128);
                    iv_code_01.setImageBitmap(bitmap01);
                    if(adapter==null)adapter = new QuickAdapter<Goods_attr_list>(GoodsShareActivity.this,R.layout.item_share_params) {
                        @Override
                        protected void convert(BaseAdapterHelper helper, Goods_attr_list item) {
                            helper.setText(R.id.tv_key,item.getAttr_name()+":");
                            helper.setText(R.id.tv_value,item.getAttr_value());
                        }
                    };
                    currentUrl = 0;
                    for(int i=0;i<goods.getGoods_spec_list().size();i++){
                        String temp=goods.getGoods_spec_list().get(i).getSpec_list().get(0).getSrc();
                        if(temp!=null&&!temp.equals("null")){
                            currentUrl =i;
                            break;
                        }
                    }
                    if(goods.getGoods_spec_list()!=null) {
                        final Goods_spec_list itemList = goods.getGoods_spec_list().get(currentUrl);
                        int numColumn;
                        String item = itemList.getSpec_list().get(0).getItem();
                        if (item.length() < 4) {
                            numColumn = 6;
                        } else if (item.length() >= 4 && item.length() < 6) {
                            numColumn = 4;
                        } else if (item.length() >= 6 && item.length() < 8) {
                            numColumn = 3;
                        } else {
                            numColumn = 2;
                        }
                        gv_attr.setNumColumns(numColumn);
                        if (adapter_attr == null)//                    helper.setTextColor(R.id.tv_attr_name,R.color.theme_red);
                            adapter_attr = new QuickAdapter<Spec_list>(GoodsShareActivity.this, R.layout.item_attr) {
                                @Override
                                protected void convert(BaseAdapterHelper helper, Spec_list item) {
                                    if (helper.getPosition() == itemList.currentP) {
                                        helper.setBackgroundRes(R.id.tv_attr_name, R.drawable.bg_attr_selected_orange);
                                    } else {
                                        helper.setBackgroundRes(R.id.tv_attr_name, R.drawable.bg_attr_default);

                                    }
                                    ((TextView) helper.getView(R.id.tv_attr_name)).setTextColor(getResources().getColor(R.color.tv_333333));
                                    helper.setText(R.id.tv_attr_name, "" + item.getItem());
                                }
                            };
                        gv_attr.setAdapter(adapter_attr);
                        final List<Spec_list> spec_lists = itemList.getSpec_list();
                        adapter_attr.replaceAll(spec_lists);
                        UIUtils.initGridViewHeight(gv_attr, numColumn);

                        lv_param.setAdapter(adapter);
                        final List<Goods_attr_list> temp = new ArrayList<>();
                        Goods_attr_list goods_attr_list = new Goods_attr_list();
                        goods_attr_list.setAttr_name("型号");
                        goods_attr_list.setAttr_value("" + goods.getGoods().getGoods_name());
                        Goods_attr_list goods_attr_list2 = new Goods_attr_list();
                        goods_attr_list2.setAttr_name("规格");
                        goods_attr_list2.setAttr_value("" + property_name == null ? "" : property_name);

                        temp.add(goods_attr_list);
                        temp.add(goods_attr_list2);
                        temp.addAll(goods_attr_lists);
                        adapter.replaceAll(temp);
                        gv_attr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                itemList.currentP = position;
                                String url = "";
                                url = spec_lists.get(position).getSrc();
                                if (url != null && !TextUtils.isEmpty(url)) {
                                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST + url, iv_share_01);
                                }
                                String str = "";
                                for (int j = 0; j < goods.getGoods_spec_list().size(); j++) {
                                    List<Spec_list> temp = goods.getGoods_spec_list().get(j).getSpec_list();
                                    for (int i = 0; i < temp.size(); i++) {
                                        if (goods.getGoods_spec_list().get(j).currentP == i) {
                                            str += temp.get(i).getItem();
                                        }
                                    }
                                }

                                List<Goods_spec_list> goods_spec_lists = goods.getGoods_spec_list();
                                String[] strKeys = new String[goods_spec_lists.size()];
                                for (int i = 0; i < goods_spec_lists.size(); i++) {
                                    List<Spec_list> temp = goods_spec_lists.get(i).getSpec_list();
                                    for (int j = 0; j < temp.size(); j++) {
                                        if (j == goods_spec_lists.get(i).currentP) {
                                            strKeys[i] = temp.get(j).getItem_id() + "";
                                        }
                                    }
                                }
                                List<Spec_goods_price> spec_goods_prices = goods.getSpec_goods_price();
                                boolean isNotContain = true;
                                for (int j = 0; j < spec_goods_prices.size(); j++) {
                                    for (int i = 0; i < strKeys.length; i++) {
                                        if (spec_goods_prices.get(j).getKey().contains(strKeys[i])) {
                                            isNotContain = false;
                                        } else {
                                            isNotContain = true;
                                            break;
                                        }
                                    }
                                    if (!isNotContain) {
                                        currentAttrPosi = j;
                                        break;
                                    }
                                }
//                            mProperty=spec_goods_prices.get(currentAttrPosi).getItem_id()+"";
                                property_name = spec_goods_prices.get(currentAttrPosi).getKey_name();
                                temp.get(1).setAttr_value(property_name);
                                adapter.notifyDataSetChanged();
                                adapter_attr.replaceAll(spec_lists);
                            }
                        });
                        gv_attr.performItemClick(null, 0, 0);
                    }
                    break;
                case 1:
                    ImageView iv_share_02=view.findViewById(R.id.iv_share_02);
                    ImageView iv_code_02=view.findViewById(R.id.iv_code_02);
                    TextView tv_name02=view.findViewById(R.id.tv_name_02);
                    tv_name02.setText(goods.getGoods().getGoods_name()+"");
                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+img_url,iv_share_02, IssApplication.getImageLoaderOption());
                    Bitmap bitmap02= ImageUtil.createQRImage(path, 180, 180);
                    iv_code_02.setImageBitmap(bitmap02);
                    break;
                case 2:
                    ImageView iv_code_03=view.findViewById(R.id.iv_code_03);
                    TextView tv_name03=view.findViewById(R.id.tv_name_03);
                    tv_name03.setText(""+goods.getGoods().getGoods_name());
                    Bitmap bitmap03= ImageUtil.createQRImage(path, 250,250);
                    iv_code_03.setImageBitmap(bitmap03);
                    break;
                case 3:
                    ImageView iv_share_04=view.findViewById(R.id.iv_share_04);
                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+img_url,iv_share_04, IssApplication.getImageLoaderOption());
                    break;
            }
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_PRO_CONTENT){
            OkHttpUtils.getGoodsContent(Integer.parseInt(goods.getGoods().getGoods_id()+""),callback);
        }
    }
}
