package bc.yxdc.com.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.bean.GoodsCategoryBean;
import bocang.json.JSONArray;
import bocang.json.JSONObject;

/**
 * @author: Jun
 * @date : 2017/1/21 13:46
 * @description :
 */
public class ClassifyGoodsAdapter extends BaseAdapter {
    private ArrayList<Boolean> mColorList;
    private List<GoodsCategoryBean> mClassifyGoodsLists;
    private Context mContext;
    public int mCurrTabIndex=-1;

    public ClassifyGoodsAdapter(ArrayList<Boolean> colorList,Context context) {
        mColorList=colorList;
        mContext=context;
    }

    public void setData(List<GoodsCategoryBean> classifyGoodsLists){
        mClassifyGoodsLists=classifyGoodsLists;
        for(int i=0;i<classifyGoodsLists.size();i++){
            mColorList.add(false);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == mClassifyGoodsLists)
            return 0;
        return mClassifyGoodsLists.size();
    }

    @Override
    public GoodsCategoryBean getItem(int position) {
        if (null == mClassifyGoodsLists)
            return null;
        return mClassifyGoodsLists.get(position);
    }

    public  void setLineColor(int position){
        for(int i=0;i<mColorList.size();i++){
            mColorList.set(i,false);
        }
        mColorList.set(position,true);
        notifyDataSetChanged();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_classify_type_new, null);

            holder = new ViewHolder();
            holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
            holder.divider= (View)convertView.findViewById(R.id.divider);
            holder.parentPanel=convertView.findViewById(R.id.parentPanel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name_tv.setText(mClassifyGoodsLists.get(position).getName());
        holder.divider.setVisibility(mColorList.get(position) == true? View.VISIBLE : View.INVISIBLE);
        holder.name_tv.setTextColor(mContext.getResources().getColor(mColorList.get(position) == true? R.color.theme_red: R.color.tv_444444));
        holder.parentPanel.setBackgroundColor(mContext.getResources().getColor(mColorList.get(position)?R.color.tv_efefef:R.color.white));
        return convertView;
    }

    class ViewHolder {
        TextView name_tv;
        View divider;
        View parentPanel;
    }
}
