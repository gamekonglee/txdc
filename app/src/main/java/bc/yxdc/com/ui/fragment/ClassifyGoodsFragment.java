package bc.yxdc.com.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.GoodsCategoryBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.ClassifyGoodsActivity;
import bc.yxdc.com.ui.activity.goods.CategoryGoodsListActivity;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.ui.adapter.ClassifyGoodsAdapter;
import bc.yxdc.com.ui.adapter.ItemClassifyAdapter;
import bc.yxdc.com.utils.MyToast;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * @author: Jun
 * @date : 2017/1/20 9:21
 * @description :分类页面-分类一
 */
public class ClassifyGoodsFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout all_ll;
    private GridView itemGridView;
    private ListView recyclerview_category;
//    private JSONArray mClassifyGoodsLists;
    private ClassifyGoodsAdapter mAdapter;
    private ArrayList<Boolean> colorList=new ArrayList<>();
    private ItemClassifyAdapter mItemAdapter;
    //    private JSONArray categoriesArrays;
    private Intent mIntent;
//    private JSONObject goodsAllAttr;
    GoodsCategoryBean goodsAllAttr;
    private List<GoodsCategoryBean> categoryBeans;
    int TYPE_PARENT=0;
    int TYPE_ITEM=1;
    private String mId;
    private List<GoodsCategoryBean> goodsCategoryBeansItem;
    private int currentP;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_classify_goods, null);
    }

    @Override
    public void initUI() {
        all_ll = (LinearLayout) getActivity().findViewById(R.id.all_ll);
        all_ll.setOnClickListener(this);
        recyclerview_category = (ListView) getActivity().findViewById(R.id.recyclerview_category);
        itemGridView = (GridView) getActivity().findViewById(R.id.itemGridView02);
        colorList=new ArrayList<>();
        mAdapter=new ClassifyGoodsAdapter(colorList,getActivity());
        recyclerview_category.setAdapter(mAdapter);
        recyclerview_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setLineColor(position);
                goodsAllAttr = categoryBeans.get(position);
                currentP = position;
                mId = goodsAllAttr.getId();
                misson(TYPE_ITEM, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result=response.body().string();
                        Log.e("result",result);
                        goodsCategoryBeansItem = new ArrayList<>();
                        JSONObject jsonObject=new JSONObject(result);
                        if(jsonObject!=null){
                            JSONArray results=jsonObject.getJSONArray(Constance.result);
                            if(results!=null&&results.length()>0){
                                for(int i=0;i<results.length();i++){
                                    goodsCategoryBeansItem.add(new Gson().fromJson(results.getJSONObject(i).toString(),GoodsCategoryBean.class));
                                }
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mItemAdapter.setDatas(goodsCategoryBeansItem);
                            }
                        });
                    }
                });

            }


        });
        mItemAdapter = new ItemClassifyAdapter(getActivity());
        itemGridView.setAdapter(mItemAdapter);
        itemGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsCategoryBean categoryObject =goodsCategoryBeansItem.get(position);

                mIntent = new Intent(getActivity(), CategoryGoodsListActivity.class);
                String categoriesId = categoryObject.getId();
                Log.e("cateId",categoriesId);
                mIntent.putExtra(Constance.categories, categoriesId);
                mIntent.putExtra(Constance.name,categoryBeans.get(currentP).getName());
                JSONArray array=new JSONArray();
                for(int i=0;i<goodsCategoryBeansItem.size();i++){
                    array.add(new Gson().toJson(goodsCategoryBeansItem.get(i),GoodsCategoryBean.class));
                }
                mIntent.putExtra(Constance.categoryList,array);
                getActivity().startActivity(mIntent);
                if(IssApplication.isClassify){
                    IssApplication.isClassify=false;
                    try {
                        ClassifyGoodsActivity.mActivity.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        categoryBeans = new ArrayList<>();

        misson(TYPE_PARENT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                Log.e("result",result);
                JSONObject jsonObject=new JSONObject(result);
                if(jsonObject!=null){
                    JSONArray results=jsonObject.getJSONArray(Constance.result);
                    if(results!=null&&results.length()>0){
                        for(int i=0;i<results.length();i++){
                        categoryBeans.add(new Gson().fromJson(results.getJSONObject(i).toString(),GoodsCategoryBean.class));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setData(categoryBeans);
                                recyclerview_category.performItemClick(null,0,0);
                            }
                        });


                    }
                }
            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_PARENT){
            getGoodsCategory("0",callback);
        }else if(type==TYPE_ITEM){
            getGoodsCategory(mId,callback);
        }
    }

    public void getGoodsCategory(String pid,Callback callback){
        OkHttpUtils.getGoodsCategory(pid,callback);
    }
    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.all_ll:
//                mController.getAllData();
                Intent mIntent = new Intent(getActivity(), SelectGoodsActivity.class);
                if(goodsAllAttr==null){
                    MyToast.show(getActivity(),"数据加载中，请稍等");
                    return;
                }
                String categoriesId = goodsAllAttr.getId();
                mIntent.putExtra(Constance.categories, categoriesId);
                getActivity().startActivity(mIntent);
                if(IssApplication.isClassify==true){
                    IssApplication.isClassify=false;
                    try {
                        ClassifyGoodsActivity.mActivity.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            break;
        }
    }
}
