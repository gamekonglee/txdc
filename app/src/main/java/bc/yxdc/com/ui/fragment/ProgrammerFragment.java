package bc.yxdc.com.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.Good;
import bc.yxdc.com.bean.Programme;
import bc.yxdc.com.bean.UserPro;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.diy.DiyActivity;
import bc.yxdc.com.ui.view.EndOfListView;
import bc.yxdc.com.ui.view.PMSwipeRefreshLayout;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.ScannerUtils;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/11/9.
 */

public class ProgrammerFragment extends BaseFragment implements View.OnClickListener, EndOfListView.OnEndOfListListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int TPYE_LIST = 0;
    private static final int TYPE_EDIT = 1;
    private static final int TYPE_DELETE = 2;
    private Unbinder unbinder;
    @BindView(R.id.tv_add_programme)
    TextView tv_add_programme;
//    @BindView(R.id.tv_all)TextView tv_all;
//    @BindView(R.id.tv_mine)TextView tv_mine;
//    @BindView(R.id.tv_filter_item)TextView tv_filter;
    @BindView(R.id.lv_programme)EndOfListView lv_programme;
    @BindView(R.id.pulltorefresh)PMSwipeRefreshLayout pulltorefresh;
    @BindView(R.id.rl_back)RelativeLayout rl_back;
//    @BindView(R.id.sc_programme)ScrollView sc_programme;
//    @BindView(R.id.ll_mine)LinearLayout ll_mine;
    TextView tv_add_mine;
//    @BindView(R.id.iv_programme_bg)ImageView iv_programme_bg;
    private int type;
    private QuickAdapter<Programme> adapter;
    private List<Programme> programmeList;
    private boolean isBottom;
    private int current;
    private boolean isfirst=true;
    private int firstVisible;
    private ImageView iv_programme_bg;
    private TextView tv_all;
    private TextView tv_mine;
    private TextView tv_filter;
    private View view_bottom;
    private View ll_mine;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_programme_home,null);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void initUI() {
        if(getArguments()!=null){
            boolean isActivity=getArguments().getBoolean(Constance.isActivity);
            if(isActivity){
                rl_back.setVisibility(View.VISIBLE);
            }else {
                rl_back.setVisibility(View.GONE);
            }
        }else {
            rl_back.setVisibility(View.GONE);
        }

        type = 0;
        View view=View.inflate(getActivity(),R.layout.layout_programme_top,null);
        view_bottom = View.inflate(getActivity(), R.layout.layout_programme_bottom,null);
        ll_mine = view_bottom.findViewById(R.id.ll_mine);
        tv_add_mine=view_bottom.findViewById(R.id.tv_add_programme_mine);
        lv_programme.addHeaderView(view);
        lv_programme.addFooterView(view_bottom);
        iv_programme_bg = view.findViewById(R.id.iv_programme_bg);
        tv_all = view.findViewById(R.id.tv_all);
        tv_mine = view.findViewById(R.id.tv_mine);
        tv_filter = view.findViewById(R.id.tv_filter_item);
        tv_add_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(MyShare.get(getActivity()).getString(Constance.token))){
                    UIUtils.showLoginDialog(getActivity());
                    return;
                }
                Intent intent=new Intent(getActivity(), DiyActivity.class);
                startActivity(intent);
            }
        });
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==0){
                    return;
                }
                type=0;
                refreshUI();
            }
        });
        tv_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==1){
                    return;
                }
                type=1;
                refreshUI();
            }
        });

        adapter = new QuickAdapter<Programme>(getActivity(), R.layout.item_programme) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final Programme item) {

                ImageView iv_img=helper.getView(R.id.iv_img);
                UserPro userPro=item.getUser();
                if(userPro!=null){
                    helper.setText(R.id.tv_author,item.getUser().getNickname());
                }

                helper.setText(R.id.tv_name,"作者："+item.getTitle()+"");
                helper.setText(R.id.tv_time,item.getCreate_at()+"");
                ImageView iv_path=helper.getView(R.id.iv_path);
                ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+item.getPath(),iv_path, IssApplication.getImageLoaderOption());
                List<Good> goods=item.getGood();
                if(goods!=null&&goods.size()>0){
                    ImageView iv_goods=helper.getView(R.id.iv_goods);
                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+goods.get(0).getOriginal_img(),iv_goods,IssApplication.getImageLoaderOption());
                    helper.setText(R.id.tv_goods_name,goods.get(0).getGoods_name()+"");
                    helper.setText(R.id.tv_price,"¥"+goods.get(0).getShop_price());
                }
                    helper.setText(R.id.tv_goods_style,"风格："+item.getStyle());
                helper.setVisible(R.id.rl_edit,type==1?true:false);
                helper.setOnClickListener(R.id.tv_share, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         ImageLoader.getInstance().loadImage(NetWorkConst.API_HOST + item.getPath(), new ImageLoadingListener() {
                             @Override
                             public void onLoadingStarted(String s, View view) {

                             }

                             @Override
                             public void onLoadingFailed(String s, View view, FailReason failReason) {

                             }

                             @Override
                             public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                 String mLocalPath = ScannerUtils.saveImageToGallery(getActivity(), bitmap, ScannerUtils.ScannerType.RECEIVER);
                                 UIUtils.showShareDialog(getActivity(),bitmap,NetWorkConst.API_HOST+item.getPath(),mLocalPath);
                             }

                             @Override
                             public void onLoadingCancelled(String s, View view) {

                             }
                         });

                    }
                });
                helper.setOnClickListener(R.id.rl_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog=new Dialog(getActivity(),R.style.customDialog);
                        dialog.setContentView(R.layout.dialog_programme_edit);
                        dialog.show();
                        TextView tv_private=dialog.findViewById(R.id.tv_private);
                        TextView tv_public=dialog.findViewById(R.id.tv_public);
                        TextView tv_delete=dialog.findViewById(R.id.tv_delete);
                        current = helper.getPosition();
                        tv_private.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                is_private="1";
                                misson(TYPE_EDIT, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final JSONObject jsonObject=new JSONObject(response.body().string());
                                        LogUtils.logE("response",jsonObject.toString());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                MyToast.show(getActivity(),""+jsonObject.getString(Constance.msg));
                                                programmeList=new ArrayList<>();
                                                load();
                                            }
                                        });

                                    }
                                });

                            }
                        });
                        tv_public.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                is_private="0";
                                misson(TYPE_EDIT, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final JSONObject jsonObject=new JSONObject(response.body().string());
                                        LogUtils.logE("response",jsonObject.toString());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                MyToast.show(getActivity(),""+jsonObject.getString(Constance.msg));
                                                programmeList=new ArrayList<>();
                                                load();
                                            }
                                        });
                                    }
                                });

                            }
                        });
                        tv_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                misson(TYPE_DELETE, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final JSONObject jsonObject=new JSONObject(response.body().string());
                                        LogUtils.logE("response",jsonObject.toString());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                MyToast.show(getActivity(),""+jsonObject.getString(Constance.msg));
                                                programmeList=new ArrayList<>();
                                                load();
                                            }
                                        });
                                    }
                                });
                            }
                        });


                    }
                });


            }
        };
        lv_programme.setAdapter(adapter);
        programmeList = new ArrayList<>();
        lv_programme.setOnEndOfListListener(this);
//        lv_programme.setOnRefLisner(new LoadMoreListview.OnRefLisner() {
//            @Override
//            public void setPullRfe() {
//                if(p==1&&(programmeList==null||programmeList.size()==0)||isBottom){
//                    return;
//                }
//                pulltorefresh.setRefreshing(true);
//                p++;
//                LogUtils.logE("page++",p+"");
//                load();
//            }
//
//            @Override
//            public void setDownRfe() {
//
//            }
//        });
        pulltorefresh.setOnRefreshListener(this);
        load();
//        pulltorefresh.setScrollUpChild(sc_programme);
//        if (sc_programme != null) {
//            sc_programme.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//                @Override
//                public void onScrollChanged() {
//                    if (pulltorefresh != null) {
//                        pulltorefresh.setEnabled(sc_programme.getScrollY() == 0);
//                    }
//                }
//            });
//        }
//        lv_programme.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if(isfirst){
//                    setImageAnimationClose();
//                    isfirst=false;
//                }else {
//                    if(firstVisible==0){
//                        if(iv_programme_bg.getVisibility()!=View.VISIBLE){
//                        setImageAnimationOpen();
//                        }
//                    }else {
//                        if(iv_programme_bg.getVisibility()!=View.GONE) {
//                            setImageAnimationClose();
//                        }
//                    }
//                }
//                LogUtils.logE("scrollState",""+scrollState);
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                        firstVisible= firstVisibleItem;
////                        int[] location=new int[2];
////                        view.getLocationInWindow(location);
////                        LogUtils.logE("first",""+firstVisibleItem);
////                        LogUtils.logE("lx,ly",location[0]+","+location[1]);
//
//            }
//        });
    }

    private void setImageAnimationClose() {
        Animation animation=new ScaleAnimation(1f,1f,1f,0f);
        animation.setFillAfter(true);
        animation.setDuration(500);
        iv_programme_bg.setAnimation(animation);
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_programme_bg.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void setImageAnimationOpen() {
        Animation animation=new ScaleAnimation(1f,1f,0f,1f);
        animation.setFillAfter(true);
        animation.setDuration(500);
        iv_programme_bg.setAnimation(animation);
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_programme_bg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void load() {
        misson(TPYE_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(pulltorefresh!=null)pulltorefresh.setRefreshing(false);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final List<Programme> temp=new Gson().fromJson(response.body().string(),new TypeToken<List<Programme>>(){}.getType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pulltorefresh!=null)pulltorefresh.setRefreshing(false);
                        if(temp==null||temp.size()==0){
                            isBottom = true;
                            LogUtils.logE("isbottom",temp.toString());
                        }else {
                            isBottom=false;
                        }

                        programmeList.addAll(temp);
                        adapter.replaceAll(programmeList);
                        int height=UIUtils.dip2PX(195)+UIUtils.initListViewHeight(lv_programme);
                        LogUtils.logE("height",height+"");
//                        pulltorefresh.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));

//                        pulltorefresh.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.initListViewHeight(lv_programme)+UIUtils.dip2PX(50)));
                        if(type==1&&programmeList.size()==0){
                            ll_mine.setVisibility(View.VISIBLE);
//                            pulltorefresh.setVisibility(View.GONE);
//                            lv_programme.setVisibility(View.GONE);
                        }else {
                            ll_mine.setVisibility(View.GONE);
//                            pulltorefresh.setVisibility(View.VISIBLE);
//                            lv_programme.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
    int p=1;
    String token="";
    String is_private="0";
    @Override
    public void getData(int type, Callback callback) {
        switch (type){
            case TPYE_LIST:
                OkHttpUtils.getProgramme(p,token,callback);
                break;
            case TYPE_EDIT:
                token=MyShare.get(getActivity()).getString(Constance.token);
                if(TextUtils.isEmpty(token)){
                    UIUtils.showLoginDialog(getActivity());
                    return;
                }
                LogUtils.logE("proId",programmeList.get(current).getId()+"");
                OkHttpUtils.editProgramme(token,programmeList.get(current).getId(),is_private,callback);
                break;
            case TYPE_DELETE:
                token=MyShare.get(getActivity()).getString(Constance.token);
                if(TextUtils.isEmpty(token)){
                    UIUtils.showLoginDialog(getActivity());
                    return;
                }
                OkHttpUtils.deleteProgramme(token,programmeList.get(current).getId(),callback);
                break;
        }

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        type=0;
        refreshUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_add_programme})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_add_programme:
            case R.id.tv_add_programme_mine:
                if(TextUtils.isEmpty(MyShare.get(getActivity()).getString(Constance.token))){
                    UIUtils.showLoginDialog(getActivity());
                    return;
                }
                Intent intent=new Intent(getActivity(), DiyActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_all:
                if(type==0){
                    return;
                }
                type=0;
                refreshUI();
                break;
            case R.id.tv_mine:
                if(type==1){
                    return;
                }
                type=1;
                refreshUI();
                break;
            case R.id.tv_filter_item:
                break;

        }
    }

    private void refreshUI() {
        pulltorefresh.setRefreshing(true);
        programmeList=new ArrayList<>();
        tv_all.setTextColor(getResources().getColor(R.color.tv_333333));
        tv_mine.setTextColor(getResources().getColor(R.color.tv_333333));
        Drawable drawable=getResources().getDrawable(R.drawable.bg_line_long);
        drawable.setBounds(0,0,UIUtils.dip2PX(60),UIUtils.dip2PX(2));
        tv_all.setCompoundDrawables(null,null,null,null);
        tv_mine.setCompoundDrawables(null,null,null,null);
        if(type==0){
            token="";
            tv_all.setTextColor(getResources().getColor(R.color.theme_red));
            tv_all.setCompoundDrawables(null,null,null,drawable);
        }else {
            tv_mine.setTextColor(getResources().getColor(R.color.theme_red));
            tv_mine.setCompoundDrawables(null,null,null,drawable);
            token= MyShare.get(getActivity()).getString(Constance.token);
            if(TextUtils.isEmpty(token)){
                UIUtils.showLoginDialog(getActivity());
                return;
            }
        }
        p=1;
        isBottom=false;
        load();

    }

    @Override
    public void onEndOfList(Object lastItem) {
        if(p==1&&(programmeList==null||programmeList.size()==0)||isBottom){
            return;
        }
        pulltorefresh.setRefreshing(true);
        p++;
        LogUtils.logE("page++",p+"");
        load();
    }

    @Override
    public void onRefresh() {
        p=1;
        programmeList=new ArrayList<>();
        load();
    }
}
