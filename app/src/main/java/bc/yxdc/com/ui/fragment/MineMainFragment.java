package bc.yxdc.com.ui.fragment;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import app.txdc.shop.R;
import app.txdc.shop.databinding.FragmentMineHomeBinding;
import bc.yxdc.com.bean.User;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.ui.activity.user.AddressListActivity;
import bc.yxdc.com.ui.activity.user.CollectActivity;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.ui.activity.user.MessageHomeActivity;
import bc.yxdc.com.ui.activity.user.MyOrderActivity;
import bc.yxdc.com.ui.activity.user.SettingActivity;
import bc.yxdc.com.ui.activity.user.UserInfoActivity;
import bc.yxdc.com.ui.activity.user.ZujiActivity;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/8/15.
 */

public class MineMainFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.rl_address)
    RelativeLayout rl_address;
    @BindView(R.id.rl_order_all)
    RelativeLayout rl_order_all;
    @BindView(R.id.rl_setting)
    RelativeLayout rl_setting;
    @BindView(R.id.iv_head)
    ImageView iv_head;
    @BindView(R.id.iv_edit)
    ImageView iv_edit;
    @BindView(R.id.rl_zuji)
    RelativeLayout rl_zuji;
    @BindView(R.id.rl_collect)
    RelativeLayout rl_collect;
    @BindView(R.id.tv_wait_pay_num)
    TextView tv_wait_pay_num;
    @BindView(R.id.tv_wait_send_num)
    TextView tv_wait_send_num;
    @BindView(R.id.tv_wait_receive_num)
    TextView tv_wait_receive_num;



    private FragmentMineHomeBinding mBinding;
    private String user_id;
    private String token;
    private Unbinder unbinder;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine_home,container,false);
        unbinder = ButterKnife.bind(this,mBinding.getRoot());

        return mBinding.getRoot();
//        return inflater.inflate(R.layout.fragment_mine_home,null);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if(MainActivity.currentTab==0)
            return;
        user_id = MyShare.get(getActivity()).getString(Constance.user_id);
        token = MyShare.get(getActivity()).getString(Constance.token);
        if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
            MyToast.show(getActivity(),"登录状态失效");
            UIUtils.clearLoginInfo(getActivity());
            startActivity(new Intent(getActivity(), LoginActivity.class));
            MainActivity.currentTab=0;
            return;
        }
//        OkHttpUtils.getUser(user_id,token,new Callback());
//        mBinding.getUser();
        getUser();
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onMessageEvent(User event) {
        getUser();
        LogUtils.logE("onMsgEvent","event:"+event);
    }
    public void getUser() {
        user_id = MyShare.get(getActivity()).getString(Constance.user_id);
        token = MyShare.get(getActivity()).getString(Constance.token);
        OkHttpUtils.getUserInfo(token, user_id, new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.show(getActivity(),"登录状态失效");
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        MainActivity.currentTab=0;
                        return;
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                final JSONObject jsonObject=new JSONObject(response.body().string());
                LogUtils.logE("userinfo",jsonObject.toString()+"");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(jsonObject!=null&&jsonObject.getJSONObject(Constance.result)!=null){
                                final User user=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),User.class);

                                if(jsonObject.getInt(Constance.status)==1){
                                IssApplication.mUserBean=new Gson().toJson(user,User.class);
                                ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+user.getHead_pic(),iv_head);
                                if(user.getNickname()==null){
                                    user.setNickname(user.getMobile());
                                }else {
                                    user.setNickname(user.getNickname()+"");
                                }
                                mBinding.setUser(user);
                                int waitPay=user.getWaitPay();
                                int waitSend=user.getWaitSend();
                                int waitReceive=user.getWaitReceive();
                                    if(waitPay==0){
                                        tv_wait_pay_num.setVisibility(View.INVISIBLE);
                                    }else {
                                        tv_wait_pay_num.setVisibility(View.VISIBLE);
                                        tv_wait_pay_num.setText(""+user.getWaitPay());
                                    }
                                    if(waitSend==0){
                                        tv_wait_send_num.setVisibility(View.INVISIBLE);
                                    }else {
                                        tv_wait_send_num.setVisibility(View.VISIBLE);
                                        tv_wait_send_num.setText(""+waitSend);
                                    }
                                    if(waitReceive==0){
                                        tv_wait_receive_num.setVisibility(View.INVISIBLE);
                                    }else {
                                        tv_wait_receive_num.setVisibility(View.VISIBLE);
                                        tv_wait_receive_num.setText(""+waitReceive);
                                    }

//                            LogUtils.logE("user",user.getMobile()+"");

                            }else {
                                MyToast.show(getActivity(),jsonObject.getString(Constance.msg));
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                MainActivity.currentTab=0;
                                user.setNickname("注册/登录");
                            }
                        }else {
                                MyToast.show(getActivity(),"登录状态失效");
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                MainActivity.currentTab=0;
                                return;}
                        }
                    });
        }
        });
//        Retrofit retrofit=new Retrofit.Builder()
//                .baseUrl(NetWorkConst.API_HOST)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        NewsApi newsApi=retrofit.create(NewsApi.class);
//        Call<BaseUser> call=newsApi.getUser(user_id,token);
//        call.enqueue(new retrofit2.Callback<BaseUser>() {
//            @Override
//            public void onResponse(Call<BaseUser> call, Response<BaseUser> response) {
//                BaseUser baseBean=response.body();
//                User user=new User();
//                if(baseBean==null){
//                    MyToast.show(getActivity(),"登录状态失效");
//                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                    MainActivity.currentTab=0;
//                    return;
//                }
//                if(baseBean.getStatus()==1){
//                    user=baseBean.getResult();
//                    IssApplication.mUserBean=new Gson().toJson(user,User.class);
//                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+user.getHead_pic(),iv_head);
//                    if(user.getNickname()==null){
//                        user.setNickname(user.getMobile());
//                    }
////                            LogUtils.logE("user",user.getMobile()+"");
//
//                }else {
//                    MyToast.show(getActivity(),baseBean.getMsg());
//                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                    MainActivity.currentTab=0;
//                    user.setNickname("注册/登录");
//                }
//
//                mBinding.setUser(user);
//            }
//
//            @Override
//            public void onFailure(Call<BaseUser> call, Throwable t) {
//
//                User user=new User();
//                user.setNickname("注册/登录");
//                mBinding.setUser(user);
//                MyToast.show(getActivity(),"登录状态失效");
//                UIUtils.clearLoginInfo(getActivity());
//                MainActivity.currentTab=0;
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//            }
//        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rl_address.setOnClickListener(this);
        rl_order_all.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        rl_zuji.setOnClickListener(this);
        rl_collect.setOnClickListener(this);
    }

    @OnClick({R.id.rl_top_mine,R.id.tv_wait_pay,R.id.tv_wait_receive,R.id.tv_wait_send,R.id.tv_has_complete,R.id.rl_message})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_address:
                startActivity(new Intent(getActivity(), AddressListActivity.class));
                break;
            case R.id.rl_order_all:
                startActivity(new Intent(getActivity(), MyOrderActivity.class));
                break;
            case R.id.rl_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.iv_edit:
            case R.id.rl_top_mine:
                if(TextUtils.isEmpty(IssApplication.mUserBean)){
                    MyToast.show(getActivity(),"数据加载中，请稍等");
                    return;
                }
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.rl_zuji:
                startActivity(new Intent(getActivity(), ZujiActivity.class));
                break;
            case R.id.rl_collect:
                startActivity(new Intent(getActivity(), CollectActivity.class));
                break;
            case R.id.tv_wait_pay:
                intent = new Intent(getActivity(),MyOrderActivity.class);
                intent.putExtra(Constance.order_type,1);
                startActivity(intent);
                break;
            case R.id.tv_wait_send:
                intent=new Intent(getActivity(),MyOrderActivity.class);
                intent.putExtra(Constance.order_type,2);
                startActivity(intent);
                break;
            case R.id.tv_wait_receive:
                intent=new Intent(getActivity(),MyOrderActivity.class);
                intent.putExtra(Constance.order_type,3);
                startActivity(intent);
                break;
            case R.id.tv_has_complete:
                intent=new Intent(getActivity(),MyOrderActivity.class);
                intent.putExtra(Constance.order_type,4);
                startActivity(intent);
                break;
            case R.id.rl_message:
                intent=new Intent(getActivity(), MessageHomeActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
