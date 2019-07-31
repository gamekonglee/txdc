package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.view.MovieRecorderView;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/9/12.
 */

public class  VideoShotActivity extends BaseActivity{
    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_video_shot);
//        setColor(this, Color.WHITE);
        fullScreen(this);
        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (Button) findViewById(R.id.shoot_button);

        //用户长按事件监听
        mShootBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {//用户按下拍摄按钮
                    mShootBtn.setBackgroundResource(R.mipmap.ps_icon_sel);
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {

                        @Override
                        public void onRecordFinish() {
                            if(!success&&mRecorderView.getTimeCount()<10){//判断用户按下时间是否大于10秒
                                success = true;
                                handler.sendEmptyMessage(1);
                            }
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {//用户抬起拍摄按钮
                    mShootBtn.setBackgroundResource(R.mipmap.ps_icon_nor);
                    if (mRecorderView.getTimeCount() > 1){//判断用户按下时间是否大于3秒
                        if(!success){
                            success = true;
                            handler.sendEmptyMessage(1);
                        }
                    } else {
                        success = false;
                        if (mRecorderView.getmVecordFile() != null)
                            mRecorderView.getmVecordFile().delete();//删除录制的过短视频
                        mRecorderView.stop();//停止录制
                        Toast.makeText(VideoShotActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                        setResult(100);
                        finish();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {

    }
    private MovieRecorderView mRecorderView;//视频录制控件
    private Button mShootBtn;//视频开始录制按钮
    private boolean isFinish = true;
    private boolean success = false;//防止录制完成后出现多次跳转事件


    @Override
    public void onResume() {
        super.onResume();
        isFinish = true;
        if (mRecorderView.getmVecordFile() != null)
            mRecorderView.getmVecordFile().delete();//视频使用后删除
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        success = false;
        mRecorderView.stop();//停止录制
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(success){
                finishActivity();
            }else {
                startActivity(new Intent(VideoShotActivity.this,VideoShotActivity.class));
                finish();
            }
        }
    };

    //视频录制结束后，跳转的函数
    private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            Intent intent = new Intent(this, SuccessVideoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("text", mRecorderView.getmVecordFile().toString());
            intent.putExtras(bundle);
            startActivityForResult(intent,300);
        }
        success = false;
    }

    /**
     * 录制完成回调
     */
    public interface OnShootCompletionListener {
        public void OnShootSuccess(String path, int second);
        public void OnShootFailure();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==300&&requestCode==300){
            setResult(300,data);
            finish();
        }
    }
}
