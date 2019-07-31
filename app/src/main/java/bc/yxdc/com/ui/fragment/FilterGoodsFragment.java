package bc.yxdc.com.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.AttrList;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.ui.adapter.FilterGoodsAdapter;
import bc.yxdc.com.utils.AppUtils;
import bocang.json.JSONArray;
import okhttp3.Callback;


/**
 * @author: Jun
 * @date : 2017/1/20 9:21
 * @description :分类页面-筛选页面
 */
public class FilterGoodsFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static AttrList mAttrList;
    private LinearLayout clear_ll;
    private Button btn_ok;
    private ListView listView;
    private FilterGoodsAdapter mAdapter;
    JSONArray mFilterList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_screen, null);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                selectGoods();
                break;
            case R.id.clear_ll:
                clearData();
                break;
        }
    }


    @Override
    public void initUI() {
        btn_ok = (Button) getActivity().findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        clear_ll = (LinearLayout) getActivity().findViewById(R.id.clear_ll);
        clear_ll.setOnClickListener(this);

        listView = (ListView) getActivity().findViewById(R.id.listView);
        mAdapter =new FilterGoodsAdapter(getActivity());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        mFilterList=new JSONArray();

    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onStart() {
        super.onStart();
        if(!AppUtils.isEmpty(mFilterList)){
            mAdapter.setData(mFilterList);
        }else{
            this.showLoadingPage("", R.mipmap.ic_loading);
            sendAttrList();
        }
    }

    private void sendAttrList() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(AppUtils.isEmpty(mAttrList)) return;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void clearData() {
        mAdapter.setClearAttrList();
        mAdapter.notifyDataSetChanged();
    }

    public void selectGoods() {
        Intent mIntent = new Intent(getActivity(), SelectGoodsActivity.class);
        String filter="";
        for(int i=0;i<mFilterList.length();i++){
            if(i==mFilterList.length()-1){
                filter+=mAdapter.mAttrList.get(i).getId();
            }else{
                filter+=mAdapter.mAttrList.get(i).getId()+".";
            }
        }
        mIntent.putExtra(Constance.filter_attr, filter);
        getActivity().startActivity(mIntent);
    }
}
