package bc.yxdc.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.bean.SceneType;
import bc.yxdc.com.inter.ISchemeChooseListener;
import bc.yxdc.com.utils.UIUtils;

/**
 * Created by gamekonglee on 2018/11/8.
 */

public class SchemeTypeAdapter extends BaseAdapter {
    private List<SceneType> mSceneTypes;
    private Activity mContext;
    private Intent mIntent;
    private ISchemeChooseListener mListener;
    private String mStyle="";
    private String mSplace="";

    public void setListener(ISchemeChooseListener listener) {
        mListener = listener;
    }

    public SchemeTypeAdapter(Activity context) {
        mContext=context;
    }

    public void setData(List<SceneType> sceneTypes){
        mSceneTypes = sceneTypes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == mSceneTypes)
            return 0;
        return mSceneTypes.size();
    }

    @Override
    public SceneType getItem(int position) {
        if (null == mSceneTypes)
            return null;
        return mSceneTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_scheme_type, null);

            holder = new ViewHolder();
            holder.name_tv = (TextView) convertView.findViewById(R.id.type_name_tv);
            holder.type_gv = (GridView) convertView.findViewById(R.id.type_gv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name= mSceneTypes.get(position).getAttr_name();
        holder.name_tv.setText(name);
        final SchemeTypeGvAdapter schemeTypeGvAdapter=new SchemeTypeGvAdapter(mContext, mSceneTypes.get(position).getAttrVal());
        holder.type_gv.setAdapter(schemeTypeGvAdapter);
        UIUtils.initGridViewHeight(holder.type_gv,2);
        holder.type_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position2, long id) {
                schemeTypeGvAdapter.setIsClick(position2,true);
                if(position==0){
                    mStyle= mSceneTypes.get(position).getAttrVal().get(position2);
                }else{
                    mSplace= mSceneTypes.get(position).getAttrVal().get(position2);
                }
                mListener.onSchemeChanged(mStyle,mSplace);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView name_tv;
        GridView type_gv;
    }




}

