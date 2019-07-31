package bc.yxdc.com.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenu;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuCreator;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuItem;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuListView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.CartList;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.bean.ShopCartResult;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.ExInventoryActivity;
import bc.yxdc.com.ui.activity.buy.ConfirmOrderActivity;
import bc.yxdc.com.ui.activity.goods.ProDetailActivity;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.ui.view.NumberInputView;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * @author Jun
 * @time 2017/1/5  12:00
 * @desc 购物车
 */
public class CartFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final int TYPE_GOODS = 2;
    private TextView export_tv;
    private SwipeMenuListView listView;
    private Boolean mIsBack=false;
    private LinearLayout back_ll;
    private SwipeMenuListView mListView;
//    private List<CartList> goodses;
    private MyAdapter myAdapter;
    private CheckBox checkAll;
    private TextView money_tv,settle_tv,edit_tv,num_tv;
    private boolean isStart=false;
    private LinearLayout sum_ll;
    private Boolean isEdit=false;
//    private JSONArray goods;
    //    private ProgressBar pd;
    private JSONObject mAddressObject;
    private View null_view;
    private int normal;
    private int juhao;
    private GridView mProGridView;
    private QuickAdapter likeGoods;
//    private List<Goods> goodsBeans;
    private DecimalFormat df;
    private List<CartList> cartList;
    private ShopCartResult result;
    private int cP;
    private boolean[] selectEditRecord;
    private List<GoodsBean> goodsCnxhBeans;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        ((MainActivity)getActivity()).setColor(getActivity(), Color.WHITE);
        return inflater.inflate(R.layout.fm_cart_new, null);
    }

    @Override
    public void initUI() {
        edit_tv = (TextView)  getActivity().findViewById(R.id.edit_tv);
        settle_tv = (TextView) getActivity().findViewById(R.id.settle_tv);
        checkAll = (CheckBox) getActivity().findViewById(R.id.checkAll);
        settle_tv.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCkeckAll(isChecked);
            }
        });
        back_ll = (LinearLayout) getActivity().findViewById(R.id.back_ll);
        if(mIsBack==false){
            back_ll.setVisibility(View.GONE);
        }else {
            back_ll.setVisibility(View.VISIBLE);
        }
//        //取得设置好的drawable对象
//        Drawable drawable = this.getResources().getDrawable(R.drawable.selector_checkbox03);
//
//        //设置drawable对象的大小
//        drawable.setBounds(0, 0, 80, 80);
//
//        //设置CheckBox对象的位置，对应为左、上、右、下
//        checkAll.setCompoundDrawables(drawable,null,null,null);
        export_tv = (TextView) getActivity().findViewById(R.id.export_tv);
        export_tv.setOnClickListener(this);
        mListView = (SwipeMenuListView) getView().findViewById(R.id.cart_lv);
        mListView.setDivider(null);//去除listview的下划线
        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);

        final SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());

                deleteItem.setBackground(new ColorDrawable(Color.parseColor("#fe3c3a")));
                deleteItem.setWidth(UIUtils.dip2PX(80));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(20);
                menu.addMenuItem(deleteItem);
            }
        };
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    setShowDialog(true);
                    setShowDialog("正在删除");
                    showLoading();
                    String id = cartList.get(position).getId()+"";
                    //                    mDeleteIndex=position;
                    isLastDelete = true;
                    deleteShoppingCart(id);
                }
                return false;
            }
        });
        checkAll = (CheckBox) getActivity().findViewById(R.id.checkAll);
        money_tv = (TextView) getActivity().findViewById(R.id.money_tv);
        num_tv = (TextView) getActivity().findViewById(R.id.num_tv);
        settle_tv = (TextView) getActivity().findViewById(R.id.settle_tv);
        edit_tv = (TextView) getActivity().findViewById(R.id.edit_tv);
        sum_ll = (LinearLayout) getActivity().findViewById(R.id.sum_ll);
        null_view =  getView().findViewById(R.id.null_cart_view);
        TextView tv_guangguang=null_view.findViewById(R.id.tv_guangguang);
        tv_guangguang.setVisibility(View.VISIBLE);
        tv_guangguang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentTab=0;
                ((MainActivity)getActivity()).refreshUI();

            }
        });
        mProGridView = (GridView) getView().findViewById(R.id.priductGridView);
        mProGridView.setOnItemClickListener(this);
        df = new DecimalFormat("#####.00");
//        pd = (ProgressBar) getActivity().findViewById(R.id.pd);
//        pd.setVisibility(View.VISIBLE);
        likeGoods = new QuickAdapter<GoodsBean>(getActivity(), R.layout.item_like_goods){
            @Override
            protected void convert(BaseAdapterHelper helper, GoodsBean item) {


                helper.setText(R.id.tv_name,""+item.getGoods_name());
                helper.setText(R.id.tv_price,"¥"+item.getShop_price());
                TextView tv_old_price=helper.getView(R.id.tv_old_price);
//                helper.setText(R.id.tv_old_price,"¥"+(df.format(Double.parseDouble(item.getCurrent_price())*1.6)));
                tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                ImageView imageView=helper.getView(R.id.iv);
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),imageView,((IssApplication)getActivity().getApplicationContext()).getImageLoaderOption());

            }
        };
        mProGridView.setAdapter(likeGoods);
        mExtView= (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alertext_form, null);
        etNum = (EditText) mExtView.findViewById(R.id.etName);
        etNum.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        loadLikeGoods();
    }

    private void loadLikeGoods() {
        misson(TYPE_GOODS, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    goodsCnxhBeans = new ArrayList<>();
                    JSONObject result=res.getJSONObject(Constance.result);
                    JSONArray jsonArray=result.getJSONArray(Constance.favourite_goods);
                    for (int i=0;i<jsonArray.length();i++){
                        goodsCnxhBeans.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),GoodsBean.class));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(goodsCnxhBeans !=null&& goodsCnxhBeans.size()>0){
                                likeGoods.replaceAll(goodsCnxhBeans);
                                UIUtils.initGridViewHeight(mProGridView);
                            }
                        }
                    });
                }
            }
        });
    }

    private void deleteShoppingCart(String id) {
        String token=MyShare.get(getActivity()).getString(Constance.token);
        String user_id=MyShare.get(getActivity()).getString(Constance.user_id);
//        id="["+id+"]";
        LogUtils.logE("deleteId",id);
        LogUtils.logE("token",token);
        LogUtils.logE("userid",user_id);
        OkHttpUtils.deleteCart(token,user_id,id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                JSONObject resultobj=new JSONObject(result);
                if(resultobj.getInt(Constance.status)==1){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.show(getActivity(),"删除成功");
                        }
                    });

                }
//                updateCart();
                sendShoppingCart();
                LogUtils.logE("delete",result);
            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        isToken();
        if(type==TYPE_GOODS){
            OkHttpUtils.getLikeGoods(1,callback);
        }
    }

    @Override
    protected void initData() {
        if(getArguments()==null) return;
        if(getArguments().get(Constance.product)==null) return;
        mIsBack= (Boolean) getArguments().get(Constance.product);
    }

    @Override
    public void onStart() {
        super.onStart();
        sendShoppingCart();
        checkAll.setChecked(false);
    }

    private void sendShoppingCart() {
        String user_id=MyShare.get(getActivity()).getString(Constance.user_id);
        String token=MyShare.get(getActivity()).getString(Constance.token);
        if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
            return;
        }
        OkHttpUtils.getShoppingCart(user_id,token, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                LogUtils.logE("scList",response.body().string());
                JSONObject jsonObject=new JSONObject(response.body().string());
                if(jsonObject.getJSONObject(Constance.result)!=null){

                result = new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),ShopCartResult.class);
                cartList = result.getCartList();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cartList!=null&&cartList.size()> 0) {
                            null_view.setVisibility(View.GONE);
//                            if(isCheckList==null||isCheckList.size()!=cartList.size()){
//                                myAdapter.addIsCheckAll(false);
//                            }
                            myAdapter.notifyDataSetChanged();
                            myAdapter.getTotalMoney();
                            IssApplication.mCartCount = (int) result.getTotal_price().getNum();
                        } else {
                            cartList = null;
//                            isCheckList=new ArrayList<>();
                            myAdapter.notifyDataSetChanged();
                            myAdapter.getTotalMoney();
                            IssApplication.mCartCount=0;
                            null_view.setVisibility(View.VISIBLE);
                        }
//                        EventBus.getDefault().post(Constance.CARTCOUNT);
                        EventBus.getDefault().post(Constance.refreshCart);
                        isStart=true;

                    }
                });
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_tv:
                setEdit();
                break;
            case R.id.settle_tv:
                sendSettle();
                break;
            case R.id.export_tv:
                exportData();
                break;

        }
    }
    /**
     * 编辑
     */
    public void setEdit() {
        if(cartList==null||cartList.size()==0){
            return;
        }
        if(!isEdit){
            sum_ll.setVisibility(View.GONE);
            settle_tv.setText("删除");
            edit_tv.setText("完成");
            isEdit=true;
            selectEditRecord = new boolean[cartList.size()];
            myAdapter.notifyDataSetChanged();
        }else{
            sum_ll.setVisibility(View.VISIBLE);
            settle_tv.setText("结算");
            edit_tv.setText("编辑");
            isEdit=false;
            myAdapter.notifyDataSetChanged();
        }
    }
//    private ArrayList<Boolean> isCheckList = new ArrayList<>();

    public void setCkeckAll(Boolean isCheck) {
        myAdapter.setIsCheckAll(isCheck);
        myAdapter.getTotalMoney();
        myAdapter.notifyDataSetChanged();

    }



    /**
     * 结算/删除
     */
    public void sendSettle() {
        if(!isEdit){
            if(cartList==null||cartList.size()==0){
                return;
            }
            boolean atLeastOne=false;
            for(CartList temp:cartList){
                if(temp.getSelected()==1){
                    atLeastOne=true;
                    break;
                }
            }
            if(!atLeastOne){
                MyToast.show(getActivity(),"请选择产品");
                return;
            }
            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<cartList.size();i++){
                jsonArray.add(new JSONObject(new Gson().toJson(cartList.get(i),CartList.class)));
            }
            Intent intent=new Intent(getActivity(),ConfirmOrderActivity.class);
            intent.putExtra(Constance.goods,jsonArray);
            intent.putExtra(Constance.money,mMoney);
            intent.putExtra(Constance.address,mAddressObject);
            getActivity().startActivity(intent);
        }else{
            sendDeleteCart();
        }
    }

    private Boolean isLastDelete=false;

    /**
     * 删除购物车数据
     */
    public  void sendDeleteCart(){
        boolean isAtLeastOne=false;
        for(int i=0;i<selectEditRecord.length;i++){
            if(selectEditRecord[i]){
                isAtLeastOne=true;
                break;
            }
        }
        if(!isAtLeastOne){
            MyToast.show(getActivity(),"请选择产品");
            return;
        }
        setShowDialog(true);
        setShowDialog("正在删除");
        showLoading();
        String deleteList="";
        for(int i=0;i<cartList.size();i++){
            if(selectEditRecord[i]){
                String id = cartList.get(i).getId()+"";
                deleteList+=id+",";
            }
        }
        deleteList=deleteList.substring(0,deleteList.length()-1);

            deleteShoppingCart(deleteList);
            setEdit();
    }

    float mMoney=0;

    /**
     * 获取收货地址
     */
    public void sendAddressList() {

//        mNetWork.sendAddressList(this);
    }

    /**
     * 导出清单
     */
    public void exportData() {
        myAdapter.getCartGoodsCheck();
        if(cartList==null||cartList.size()==0){
            MyToast.show(getActivity(),"请选择产品");
            return;
        }
        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<cartList.size();i++){
            jsonArray.add(new JSONObject(new Gson().toJson(cartList.get(i),CartList.class)));
        }
        Intent intent=new Intent(getActivity(),ExInventoryActivity.class);
        intent.putExtra(Constance.goods, jsonArray);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(view==mListView){
        int productId = cartList.get(i).getGoods_id();
        Intent intent = new Intent(getActivity(), ProDetailActivity.class);
        intent.putExtra(Constance.product, productId);
        getActivity().startActivity(intent);
        } else {
            int productId = goodsCnxhBeans.get(i).getGoods_id();
            Intent intent = new Intent(getActivity(), ProDetailActivity.class);
            intent.putExtra(Constance.product, productId);
            getActivity().startActivity(intent);
        }
    }

    private class MyAdapter extends BaseAdapter {
        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        public MyAdapter() {
            options = new DisplayImageOptions.Builder()
                    // 设置图片下载期间显示的图片
                    .showImageOnLoading(R.drawable.bg_default)
                    // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageForEmptyUri(R.drawable.bg_default)
                    // 设置图片加载或解码过程中发生错误显示的图片
                    // .showImageOnFail(R.drawable.ic_error)
                    // 设置下载的图片是否缓存在内存中
                    .cacheInMemory(true)
                    // 设置下载的图片是否缓存在SD卡中
                    .cacheOnDisk(true)
                    // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                    // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
                    // .displayer(new FadeInBitmapDisplayer(100))//
                    // 图片加载好后渐入的动画时间
                    .build(); // 构建完成

            // 得到ImageLoader的实例(使用的单例模式)
            imageLoader = ImageLoader.getInstance();
        }

        public void setIsCheckAll(Boolean isCheck) {
            if(AppUtils.isEmpty(cartList)) return;
            if(isEdit){
                for(int i=0;i<selectEditRecord.length;i++){
                    selectEditRecord[i]=isCheck;
                }
                notifyDataSetChanged();
            }else {
                for (int i = 0; i < cartList.size(); i++) {
                    cartList.get(i).setSelected(isCheck?1:0);
//               ids+=cartList.get(i).getGoods_id()+",";
                }
                updateCart();
            }

        }

        public void getCartGoodsCheck(){

        }

        public void setIsCheck(int poistion, Boolean isCheck) {
//            isCheckList.set(poistion, isCheck);
//            setGoodsCheck(cartList.get(poistion).getGoods_id()+"",cartList.get(poistion).getGoods_num()+"",cartList.get(poistion).getStore_count()+"",isCheck);
            cartList.get(poistion).setSelected(isCheck?1:0);
            updateCart();
            getTotalMoney();
        }

        /**
         * 获取到总金额
         */
        public void getTotalMoney(){
            float isSumMoney = 0;
            int count=0;
            if(AppUtils.isEmpty(cartList)){
                checkAll.setChecked(false);
                money_tv.setText("￥" + 0 + "");
                num_tv.setText(0 + "件");
                return;
            }
            for (int i = 0; i < cartList.size(); i++) {
                if (!isEdit&&cartList.get(i).getSelected()==1||isEdit&&selectEditRecord[i]) {
                    double price = Double.parseDouble(cartList.get(i).getGoods_price());
                    int num=cartList.get(i).getGoods_num();
                    isSumMoney += (num * price);
                    count+=num;
                }
            }
            mMoney=isSumMoney;
            DecimalFormat df=new DecimalFormat("0.00");
            num_tv.setText(count+ "件");
            String price=df.format(isSumMoney )+ "";
            money_tv.setText("￥" +price);
        }

        @Override
        public int getCount() {
            if (null == cartList)
                return 0;
            return cartList.size();
        }


        @Override
        public CartList getItem(int position) {
            if (null == cartList)
                return null;
            return cartList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_lv_cart_new, null);
                holder = new ViewHolder();
                holder.checkBox = (TextView) convertView.findViewById(R.id.checkbox);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.leftTv = (ImageView) convertView.findViewById(R.id.leftTv);
                holder.rightTv = (ImageView) convertView.findViewById(R.id.rightTv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
                holder.contact_service_tv = (TextView) convertView.findViewById(R.id.contact_service_tv);
                holder.SpecificationsTv = (TextView) convertView.findViewById(R.id.SpecificationsTv);
                holder.numTv = (EditText) convertView.findViewById(R.id.numTv);
                holder.priceTv = (TextView) convertView.findViewById(R.id.priceTv);
                holder.old_priceTv = (TextView) convertView.findViewById(R.id.old_priceTv);
                holder.number_input_et = (NumberInputView) convertView.findViewById(R.id.number_input_et);
                holder.view_sc_item = convertView.findViewById(R.id.view_sc_item);
//                //取得设置好的drawable对象
//                Drawable drawable = getResources().getDrawable(R.drawable.selector_checkbox03);
//                //设置drawable对象的大小
//                drawable.setBounds(0, 0, 80, 80);
//                //设置CheckBox对象的位置，对应为左、上、右、下
//                holder.checkBox.setCompoundDrawables(drawable,null,null,null);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < cartList.size()) {
            final CartList goodsObject = cartList.get(position);
            int warn_number=goodsObject.getWarn_number();
//            JSONObject product=goodsObject.get(Constance.product);
            holder.nameTv.setText(goodsObject.getGoods_name());
            imageLoader.displayImage(NetWorkConst.IMAGE_URL + cartList.get(position).getGoods_id(), holder.imageView, IssApplication.getImageLoaderOption());
//            try{
//                imageLoader.displayImage(goodsObject.getJSONObject(Constance.product).getJSONObject(Constance.default_photo).getString(Constance.thumb)
//                        , holder.imageView, options);
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            holder.view_sc_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int productId = goodsObject.getGoods_id();
                    Intent intent = new Intent(getActivity(), ProDetailActivity.class);
                    intent.putExtra(Constance.product, productId);
                    getActivity().startActivity(intent);
                }
            });
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int productId = goodsObject.getGoods_id();
                    Intent intent = new Intent(getActivity(), ProDetailActivity.class);
                    intent.putExtra(Constance.product, productId);
                    getActivity().startActivity(intent);
                }
            });

            String property = goodsObject.getSpec_key_name();
            if (AppUtils.isEmpty(property)) {
                holder.SpecificationsTv.setVisibility(View.GONE);
            } else {
                holder.SpecificationsTv.setVisibility(View.VISIBLE);
            }

            holder.SpecificationsTv.setText(property);
            String price = goodsObject.getGoods_price();
            holder.priceTv.setText("¥" + price + "元");
            String oldPrice;
//            JSONObject group_buy=product.getJSONObject(Constance.group_buy);

//            if(null==group_buy||"212".equals(group_buy.toString())){
//                //非限购商品
//                oldPrice = goodsObject.getJSONObject(Constance.product).getString(Constance.price);
//            }else {
//                //限购商品
//                oldPrice = goodsObject.getJSONObject(Constance.product).getString(Constance.current_price);
//            }
//            holder.old_priceTv.setText("零售价:" + oldPrice+"元");
            holder.number_input_et.setMax(10000);//设置数量的最大值

            holder.numTv.setText(goodsObject.getGoods_num() + "");

            holder.numTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cP = position;
                    if (mAlertViewExt == null) {
                        mAlertViewExt = new AlertView("提示", "修改购买数量！", "取消", null, new String[]{"完成"},
                                getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position2) {
                                if (position2 != AlertView.CANCELPOSITION) {
                                    String num = etNum.getText().toString();
                                    if (num.equals("0")) {
                                        MyToast.show(getActivity(), "不能等于0");
                                        return;
                                    }
                                    if(Integer.parseInt(num)%warn_number!=0){
                                        MyToast.show(getActivity(), "数量必须是"+warn_number+"的整数倍");
                                        return;
                                    }
                                    setShowDialog(true);
                                    setShowDialog("正在处理中...");
                                    showLoading();
                                    LogUtils.logE("position", cP + "");
                                    cartList.get(cP).setGoods_num(Integer.parseInt(num));
//                                    sendUpdateCart(goodsObject.getGoods_id()+"", num);
                                    updateCart();
                                }
                            }
                        });

                        etNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                //输入框出来则往上移动
                                boolean isOpen = imm.isActive();
                                mAlertViewExt.setMarginBottom(isOpen && hasFocus ? 120 : 0);
                                System.out.println(isOpen);
                            }
                        });
                        mAlertViewExt.addExtView(mExtView);
                    }
                    etNum.setText(goodsObject.getGoods_num() + "");
                    mAlertViewExt.show();

                }
            });

//            holder.checkBox.setChecked(isCheckList.get(position));

            if (!isEdit && goodsObject.getSelected() == 0 || isEdit && !selectEditRecord[position]) {
                Drawable drawable = getActivity().getResources().getDrawable(R.mipmap.page_icon_round_default);
                drawable.setBounds(0, 0, UIUtils.dip2PX(15), UIUtils.dip2PX(15));
                holder.checkBox.setCompoundDrawables(null, drawable, null, null);
            } else {
                Drawable drawable = getActivity().getResources().getDrawable(R.mipmap.page_icon_round_seleted);
                drawable.setBounds(0, 0, UIUtils.dip2PX(15), UIUtils.dip2PX(15));
                holder.checkBox.setCompoundDrawables(null, drawable, null, null);
            }
            holder.rightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setShowDialog(true);
                    setShowDialog("正在处理中...");
                    showLoading();
                    cartList.get(position).setGoods_num(goodsObject.getGoods_num() + warn_number);
//                    sendUpdateCart(goodsObject.getGoods_id()+"",(goodsObject.getGoods_num()+1)+"");
                    updateCart();
                }
            });
            holder.leftTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (goodsObject.getGoods_num() == warn_number) {
                        MyToast.show(getActivity(), "亲,已经到底啦!");
                        return;
                    }
                    setShowDialog(true);
                    setShowDialog("正在处理中...");
                    showLoading();
                    cartList.get(position).setGoods_num(goodsObject.getGoods_num() - warn_number);
                    updateCart();
//                    sendUpdateCart(goodsObject.getGoods_id()+"",(goodsObject.getGoods_num()-1)+"");
                }
            });


            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEdit) {
                        setIsCheck(position, goodsObject.getSelected() == 1 ? false : true);
                    } else {
                        selectEditRecord[position] = !selectEditRecord[position];
                        getTotalMoney();
                    }
                    myAdapter.notifyDataSetChanged();

                }
            });
//            .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    setIsCheck(position, isChecked);
//                    getTotalMoney();
//
//                }
//            });
            holder.contact_service_tv.setVisibility(View.GONE);
//            if(IssApplication.mUserObject.getInt(Constance.level)==0){
//            }else {
//                holder.contact_service_tv.setVisibility(View.VISIBLE);
//            }
//            holder.contact_service_tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int level = IssApplication.mUserObject.getInt(Constance.level);
//                    if(level==0){
//                        if(!isToken()){
//                            IntentUtil.startActivity(getActivity(), ChartListActivity.class, false);
//                        }
//                        return;
//                    }
//
//                    int id= MyShare.get(getActivity()).getInt(Constance.USERCODEID);
//                    if(id==0){
//                        MyToast.show(getActivity(),"该用户没有客服信息!");
//                    }else{
//
////                        sendCall(position,"尝试连接聊天服务..请连接?");
//                    }
//
//                }
//            });
        }
            return convertView;
        }

        class ViewHolder {
            TextView checkBox;
            ImageView imageView;
            TextView nameTv;
            TextView priceTv;
            TextView SpecificationsTv;
            NumberInputView number_input_et;
            EditText numTv;
            ImageView leftTv,rightTv;
            TextView contact_service_tv;
            TextView old_priceTv;
            View view_sc_item;
        }
    }

    private void updateCart() {
        String updateStr="";
        for(int i=0;i<cartList.size();i++){
            updateStr+=(cartList.get(i).toJSON()+",");
        }
        updateStr=updateStr.substring(0,updateStr.length()-1);
        updateStr="["+updateStr+"]";
        String token=MyShare.get(getActivity()).getString(Constance.token);
        String user_id=MyShare.get(getActivity()).getString(Constance.user_id);
        LogUtils.logE("updateStr",updateStr);
        OkHttpUtils.updateCart(updateStr, user_id,token,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                final JSONObject jsonObject=new JSONObject(result);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject.getInt(Constance.status)!=1){
                            MyToast.show(getActivity(),jsonObject.getString(Constance.msg)+"");
                        }

                    }
                });
                LogUtils.logE("update",result);
                sendShoppingCart();
            }
        });
    }

    private void setGoodsCheck(String ids,String num,String store, Boolean isCheck) {
        String userid=MyShare.get(getActivity()).getString(Constance.user_id);
        String token=MyShare.get(getActivity()).getString(Constance.token);
        OkHttpUtils.editCart(ids,isCheck, userid,token,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    private void sendUpdateCart(String string, String num) {

    }

    private AlertView mAlertViewExt;//窗口拓展例子
    private EditText etNum;//拓展View内容
    private InputMethodManager imm;
    private ViewGroup mExtView;
//    public Dialog showTypeSelectDialog(boolean hasNormal, boolean hasJuhao){
//        final Dialog dialog = new Dialog(getActivity(), R.style.customDialog);
//        dialog.setContentView(R.layout.dialog_type_select);
//        dialog.setCanceledOnTouchOutside(true);
//        TextView tv_type_select_tip= (TextView) dialog.findViewById(R.id.tv_type_select_tip);
//        LinearLayout ll_taxpay= (LinearLayout) dialog.findViewById(R.id.ll_taxpay);
//        final CheckBox cb_taxpay= (CheckBox) dialog.findViewById(R.id.cb_taxpay);
//        TextView tv_taxpay_count= (TextView) dialog.findViewById(R.id.tv_taxpay_count);
//        LinearLayout ll_hellogou= (LinearLayout) dialog.findViewById(R.id.ll_hellogou);
//        final CheckBox cb_hellegou= (CheckBox) dialog.findViewById(R.id.cb_hellegou);
//        TextView tv_hellogou_count= (TextView) dialog.findViewById(R.id.tv_hellogou_count);
//        Button btn_back_to_sc= (Button) dialog.findViewById(R.id.btn_back_to_sc);
//        Button btn_balance= (Button) dialog.findViewById(R.id.btn_balance);
//        View line=dialog.findViewById(R.id.line_2);
//
//        tv_taxpay_count.setText(normal+"件");
//        tv_hellogou_count.setText(juhao+"件");
//        btn_back_to_sc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        btn_balance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!cb_taxpay.isChecked()&&!cb_hellegou.isChecked()){
//                    Toast.makeText(getActivity(), "至少勾选一种商品结算", Toast.LENGTH_SHORT).show();
//                    return;
//                }
////                String seletType="";
//                if(cb_taxpay.isChecked()){
////                    seletType =Constance.NORMAL_GOODS;
//                    for(int i=0;i<goods.length();i++){
//                        int is_jh=((JSONObject)goods.get(i)).getJSONObject(Constance.product).getInt(Constance.is_jh);
//                        if(is_jh==1){
//                            goods.delete(i);
//                            isCheckList.set(i,false);
//                            i--;
//                        }
//                    }
//                }else if(cb_hellegou.isChecked()){
////                    seletType =Constance.JUGAO_GOODS;
//                    for(int i=0;i<goods.length();i++){
//                        int is_jh=((JSONObject)goods.get(i)).getJSONObject(Constance.product).getInt(Constance.is_jh);
//                        if(is_jh==0){
//                            goods.delete(i);
//                            isCheckList.set(i,false);
//                            i--;
//                        }
//                    }
//                }
//                myAdapter.getTotalMoney();
//                Intent intent=new Intent(getActivity(),ConfirmOrderActivity.class);
//                intent.putExtra(Constance.goods,goods);
//                intent.putExtra(Constance.money,mMoney);
//                intent.putExtra(Constance.address,mAddressObject);
//
//                getActivity().startActivity(intent);
////                UIHelper.showOrderCreateHome(getActivity(), seletType);
//                dialog.dismiss();
//            }
//        });
//        ll_taxpay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(cb_taxpay.isChecked()){
//                    return;
//                }else {
//                    cb_taxpay.setChecked(true);
//                    cb_hellegou.setChecked(false);
//                }
//            }
//        });
//        ll_hellogou.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(cb_hellegou.isChecked()){
//                    return;
//                }else {
//                    cb_taxpay.setChecked(false);
//                    cb_hellegou.setChecked(true);
//                }
//            }
//        });
//
//        String[] ss=new String[3];
//        if(!hasNormal){
//            ll_taxpay.setVisibility(View.GONE);
//            ss[0]="";
//        }else {
//            ss[0]="普通商品";
//        }
//        if(!hasJuhao){
//            ll_hellogou.setVisibility(View.GONE);
//            ss[1]="";
//        }else {
//            ss[1]="超市商品";
//        }
//
//        StringBuilder sb=new StringBuilder();
//        int count=(hasNormal?1:0)+(hasJuhao?1:0);
//        if(count>1){
//            line.setVisibility(View.VISIBLE);
//        }else {
//            line.setVisibility(View.GONE);
//        }
//        for(int j=0;j<2;j++){
//            if(!TextUtils.isEmpty(ss[j])){
//                sb.append(ss[j]);
//                if(j!=count-1&&j<count){
//                    sb.append("、");
//                }
//            }
//        }
//        tv_type_select_tip.setText("您的购物车包含："+ sb.toString()+"，需要分开结算。");
//           /*
//         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
//         * 对象,这样这可以以同样的方式改变这个Activity的属性.
//         */
//        Window dialogWindow = dialog.getWindow();
//        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.show();
//        return dialog;
//    }
}
