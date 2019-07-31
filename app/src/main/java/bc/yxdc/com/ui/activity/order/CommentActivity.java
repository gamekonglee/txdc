package bc.yxdc.com.ui.activity.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.PostImageVideoBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.ui.activity.user.VideoShotActivity;
import bc.yxdc.com.utils.FileUtil;
import bc.yxdc.com.utils.ImageUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.NetWorkUtils;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Callback;

import static bc.yxdc.com.constant.Constance.PHOTO_WITH_CAMERA;
import static bc.yxdc.com.constant.Constance.ad;

/**
 * Created by gamekonglee on 2018/11/14.
 */

public class CommentActivity extends BaseActivity{
    @BindView(R.id.rb_goods_score)
    bc.yxdc.com.ui.view.RatingBar rb_score;
    @BindView(R.id.rb_goods_rank)bc.yxdc.com.ui.view.RatingBar rb_rank;
    @BindView(R.id.rb_express)bc.yxdc.com.ui.view.RatingBar rb_express;
    @BindView(R.id.rb_attitude)bc.yxdc.com.ui.view.RatingBar rb_attitude;
    @BindView(R.id.et_comment)EditText et_comment;
//    @BindView(R.id.ll_add_pic)LinearLayout ll_add_pic;
    @BindView(R.id.gv_img)GridView gv_img;
    @BindView(R.id.cb_unknow)CheckBox cb_unknow;
    @BindView(R.id.tv_submit)TextView tv_submit;
    @BindView(R.id.iv_goods_img)ImageView iv_goods_img;
    private String order_id;
    private String goods_id;
    public List<PostImageVideoBean> lists = new ArrayList<PostImageVideoBean>();
    public List<File> files=new ArrayList<>();
    private ArrayList<String> images=new ArrayList<>();
    //    private boolean[] isVideo;
    private String path;
    public List<Bitmap> upLoadBitmap=new ArrayList<>();
//    public List<File> uploadFile=new ArrayList<>();
    private BaseAdapter adapter;
    private String orignal_img;
    private String rec_id;

    @Override
    protected void initData() {
        order_id = getIntent().getStringExtra(Constance.order_id);
        goods_id = getIntent().getStringExtra(Constance.goods_id);
        orignal_img = getIntent().getStringExtra(Constance.img);
        rec_id = getIntent().getStringExtra(Constance.rec_id);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

//        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(CommentActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(et_comment,InputMethodManager);

//        imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0); //强制隐藏键盘

//        View view = getCurrentFocus();
//        if (view != null) {
//            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
/*        adapter = new QuickAdapter<PostImageVideoBean>(this, R.layout.item_comment) {
            @Override
            public int getCount() {
                return lists.size()+1;
            }

            @Override
            protected void convert(final BaseAdapterHelper helper, PostImageVideoBean item) {
                RelativeLayout rl_pic=helper.getView(R.id.rl_pic);
                LinearLayout ll_pic_add=helper.getView(R.id.ll_add_pic);
                if(helper.getPosition()==0){
                    rl_pic.setVisibility(View.GONE);
                    ll_pic_add.setVisibility(View.VISIBLE);
                }else {
                    rl_pic.setVisibility(View.VISIBLE);
                    ll_pic_add.setVisibility(View.GONE);
                }
                helper.setOnClickListener(R.id.ll_add_pic, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         addPic();
                    }
                });
                helper.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtils.logE("position",""+helper.getPosition());
                        dialog(helper.getPosition()-1);
                    }
                });
                final ImageView iv_img=helper.getView(R.id.iv_img);
                if(helper.getPosition()!=0){
                    iv_img.setImageBitmap(item.bitmap);
                }
//                ImageLoader.getInstance().loadImage("file://" + item.path, new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String s, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String s, View view, FailReason failReason) {
//
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
////                        iv_img.setImageBitmap(lists.get(helper.getPosition()).bitmap);
//                        iv_img.setImageBitmap(bitmap);
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String s, View view) {
//
//                    }
//                });

            }
        };*/
        adapter=new MyAdapter();
        gv_img.setAdapter(adapter);
        gv_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                dialog(position);
            }
        });
        adapter.notifyDataSetChanged();
//        adapter.replaceAll(lists);
        ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+orignal_img,iv_goods_img);
//        View ll_add_pic=View.inflate(CommentActivity.this,R.layout.head_add_pic,null);
//        ll_add_pic.setOnClickListener(this);
    }

    @OnClick({R.id.tv_submit})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.ll_add_pic:
                addPic();
                break;
            case R.id.tv_submit:
                final String goods_rank=rb_rank.getNumStars()+"";
                final String deliver_rank=rb_express.getNumStars()+"";
                final String service_rank=rb_attitude.getNumStars()+"";
                final String goods_score=rb_score.getNumStars()+"";
                final String content=et_comment.getText().toString();
                final String token= MyShare.get(this).getString(Constance.token);
                if(TextUtils.isEmpty(token)){
                    MyToast.show(this,"登录状态失效");
                    return;
                }
                if(TextUtils.isEmpty(content)){
                    MyToast.show(this,"评论不能为空");
                    return;
                }
                final int is_anonymous=cb_unknow.isChecked()?1:0;
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        for(int i=0;i<lists.size();i++){
                        upLoadBitmap.add(lists.get(i).bitmap);
                        }
                        final Map<String, String> params = new HashMap<String, String>();
                        params.put("goods_rank",goods_rank);
                        params.put("deliver_rank",deliver_rank);
                        params.put("service_rank",service_rank);
                        params.put("goods_score",goods_score);
                        params.put("content",content);
                        params.put("is_anonymous",""+is_anonymous);
                        params.put("order_id",order_id);
                        params.put("goods_id",goods_id);
                        params.put("rec_id",rec_id);
                        params.put("token",token);
                        LogUtils.logE("upload_params",params.toString());
                        final String resultJson = NetWorkUtils.uploadMoreFile(upLoadBitmap, NetWorkConst.URL_COMMENT_GOODS, params, "comment_img_file");
                        LogUtils.logE("upload,",resultJson);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.show(CommentActivity.this,"评价成功!");
                            }
                        });
                    }
                }.start();
                break;

        }
    }

    private void addPic() {
        //图片剪裁的一些设置
        UCrop.Options options = new UCrop.Options();
        //图片生成格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        //图片压缩比
        options.setCompressionQuality(80);
        FileUtil.openSunImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constance.REQUEST_CODE && data != null) {
            ArrayList<String> temps=data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            if(lists!=null&&temps.size()+lists.size()>5){
                MyToast.show(this,"上传图片最多5张");
                return;
            }
            images.addAll(temps);
            //获取选择器返回的数据
            for (int i = 0; i < temps.size(); i++) {
                PostImageVideoBean postImageVideoBean=new PostImageVideoBean();
                postImageVideoBean.isVideo=false;
                postImageVideoBean.path=temps.get(i);
                postImageVideoBean.bitmap= ImageUtil.adjustImage(this,temps.get(i));
                lists.add(postImageVideoBean);
            }
            // 更新GrideView
//            adapter.replaceAll(lists);
            adapter.notifyDataSetChanged();
        }else if(requestCode==PHOTO_WITH_CAMERA){
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡
                File imageFile = new File(IssApplication.cameraPath, IssApplication.imagePath + ".jpg");
                if (imageFile.exists()) {
//                    String imageURL = "file://" + imageFile.toString();
                    String imageURL =  imageFile.toString();
                    if(images!=null&&images.size()>=5){
                        MyToast.show(this,"上传图片最多5张");
                        return;
                    }
                    images.add(imageURL);
                    PostImageVideoBean postImageVideoBean=new PostImageVideoBean();
                    postImageVideoBean.isVideo=false;
                    postImageVideoBean.path=imageURL;
                    postImageVideoBean.bitmap= BitmapFactory.decodeFile(imageURL);
                    lists.add(postImageVideoBean);
//                    adapter.replaceAll(lists);
                    adapter.notifyDataSetChanged();
                    IssApplication.imagePath = null;
                    IssApplication.cameraPath = null;
                } else {
                    MyToast.show(CommentActivity.this,"读取图片失败！");
                }
            } else {
                MyToast.show(CommentActivity.this,"没有SD卡！");
            }
        }else if(requestCode==300&&resultCode==300){
            if(images!=null&&images.size()>=5){
                MyToast.show(this,"上传图片最多5张");
                return;
            }
            path = data.getStringExtra(Constance.path);
            files.add(new File(path));
            LogUtils.logE("path", path);
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
//            View view=View.inflate(this,R.layout.video_start,null);
//            View imageView=view.findViewById(R.id.iv_img);
//            imageView.setBackground(new BitmapDrawable(bitmap));
//            Bitmap temp=ImageUtil.loadBitmapFromView(view);
            PostImageVideoBean postImageVideoBean=new PostImageVideoBean();
            postImageVideoBean.isVideo=true;
            postImageVideoBean.bitmap=media.getFrameAtTime();
            postImageVideoBean.path=path;
            lists.add(postImageVideoBean);
            IssApplication.imagePath = null;
            IssApplication.cameraPath = null;
//            adapter.replaceAll(lists);
            adapter.notifyDataSetChanged();
        }else if(requestCode==300&&resultCode==100){
            startActivityForResult(new Intent(this, VideoShotActivity.class),300);
        }
        refreshAdd();
    }

    private void refreshAdd() {
//        FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) ll_add_pic.getLayoutParams();
        int left=lists.size()* (UIUtils.getScreenWidth(this)-UIUtils.dip2PX(20))/5;
        LogUtils.logE("left",","+left);
//        layoutParams.setMargins(left,0,0,0);
        if(lists.size()==5){
//            ll_add_pic.setVisibility(View.INVISIBLE);
        }else {
//            ll_add_pic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @Override
    public void onDestroy() {
        FileUtil.deleteDir();
        super.onDestroy();
    }

    /*
    * Dialog对话框提示用户删除操作
    * position为删除图片位置
    */
    protected void dialog(final int position) {
        UIUtils.showSingleWordDialog(this, "确认移除已添加图片吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lists.remove(position);
//                adapter.replaceAll(lists);
                adapter.notifyDataSetChanged();
                refreshAdd();
            }
        });
    }

    class MyAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return lists.size()+1;
        }

        @Override
        public PostImageVideoBean getItem(int position) {
            return lists.get(position+1);
        }

        @Override
        public long getItemId(int position) {
            return position+1;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder=new Holder();
            if(convertView==null){
                convertView=View.inflate(CommentActivity.this,R.layout.item_comment,null);
                holder.rl_pic=convertView.findViewById(R.id.rl_pic);
                holder.ll_add_pic=convertView.findViewById(R.id.ll_add_pic);
                holder.iv_delete=convertView.findViewById(R.id.iv_delete);
                holder.iv_img=convertView.findViewById(R.id.iv_img);
                convertView.setTag(holder);
            }else {
                holder= (Holder) convertView.getTag();
            }
            int screenWidth=UIUtils.getScreenWidth(CommentActivity.this);
            int width=(screenWidth-UIUtils.dip2PX(35))/4;


            if(position==0){
                holder.ll_add_pic.setVisibility(View.VISIBLE);
                holder.rl_pic.setVisibility(View.GONE);
                holder.ll_add_pic.setLayoutParams(new RelativeLayout.LayoutParams(width,width+UIUtils.dip2PX(10)));
            }else {
                holder.rl_pic.setLayoutParams(new RelativeLayout.LayoutParams(width,width+UIUtils.dip2PX(10)));
                holder.ll_add_pic.setVisibility(View.GONE);
                holder.rl_pic.setVisibility(View.VISIBLE);
                final Holder finalHolder = holder;
                holder.iv_img.setImageBitmap(lists.get(position-1).bitmap);
//                ImageLoader.getInstance().loadImage("file://" + lists.get(position - 1).path, new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String s, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String s, View view, FailReason failReason) {
//
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                        finalHolder.iv_img.setImageBitmap(bitmap);
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String s, View view) {
//
//                    }
//                });
                holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog(position-1);
                    }
                });
            }
            holder.ll_add_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPic();
                }
            });



            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            UIUtils.initGridViewHeight(gv_img,4);
        }
        //        @Override
//        protected void convert(final BaseAdapterHelper helper, PostImageVideoBean item) {
//            RelativeLayout rl_pic=helper.getView(R.id.rl_pic);
//            LinearLayout ll_pic_add=helper.getView(R.id.ll_add_pic);
//            if(helper.getPosition()==0){
//                rl_pic.setVisibility(View.GONE);
//                ll_pic_add.setVisibility(View.VISIBLE);
//            }else {
//                rl_pic.setVisibility(View.VISIBLE);
//                ll_pic_add.setVisibility(View.GONE);
//            }
//            helper.setOnClickListener(R.id.ll_add_pic, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    addPic();
//                }
//            });
//            helper.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LogUtils.logE("position",""+helper.getPosition());
//                    dialog(helper.getPosition()-1);
//                }
//            });
//            final ImageView iv_img=helper.getView(R.id.iv_img);
//            if(helper.getPosition()!=0){
//                iv_img.setImageBitmap(item.bitmap);
//            }
//        }
    }
    public class Holder{
        RelativeLayout rl_pic;
        LinearLayout ll_add_pic;
        ImageView iv_img;
        ImageView iv_delete;

    }
}
