package bc.yxdc.com.ui.activity.goods;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.utils.FileUtil;
import bc.yxdc.com.view.CameraSurfaceView;
import okhttp3.Callback;

import static bc.yxdc.com.constant.Constance.PHOTO_WITH_DATA;

/**
 * Created by gamekonglee on 2018/11/20.
 */

public class GoodsSearchByImgActivity extends BaseActivity implements TextureView.SurfaceTextureListener{

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int PHOTO_TAKE = 2;
    private CameraSurfaceView cameraSurfaceView;
    private Size mPreviewSize;
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private TextureView mPreviewView;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mPreviewSession;
    private ImageReader mImageReader;
    Handler mHandler=new Handler();
    private HandlerThread mThreadHandler;
    private static SparseIntArray ORIENTATION = new SparseIntArray();

    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    private boolean mSurfaceTextureAvailable;
    private boolean mCameraOpened;
    private TextView tv_change;
    private TextView iv_photo;
    private byte[] fontBytes;

    @Override
    protected void initData() {

    }
    public void setOrientation(){
        ORIENTATION=new SparseIntArray();
        if(mCameraId.equals("1")){
            ORIENTATION.append(Surface.ROTATION_0, 270);
            ORIENTATION.append(Surface.ROTATION_90, 180);
            ORIENTATION.append(Surface.ROTATION_180, 90);
            ORIENTATION.append(Surface.ROTATION_270, 0);
        }else {
            ORIENTATION.append(Surface.ROTATION_0, 270);
            ORIENTATION.append(Surface.ROTATION_90, 0);
            ORIENTATION.append(Surface.ROTATION_180, 270);
            ORIENTATION.append(Surface.ROTATION_270, 0);
        }
    }
    @Override
    public void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goods_search_img_2);
        requestCameraPermission();

        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
        mPreviewView = findViewById(R.id.textureView);
        tv_change = findViewById(R.id.tv_change);
        iv_photo = findViewById(R.id.iv_photo);
        tv_change.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                closeCamera();
                if(mCameraId.equals("1")){
                    mCameraId="0";
                }else {
                    mCameraId="1";
                }
//                setOrientation();
                openCamera();
            }
        });
        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.pickPhoto(GoodsSearchByImgActivity.this);
            }
        });
        mPreviewView.setSurfaceTextureListener(this);
        ImageView picture=findViewById(R.id.iv_take);
        picture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    //获取屏幕方向
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    //设置CaptureRequest输出到mImageReader
                    //CaptureRequest添加imageReaderSurface，不加的话就会导致ImageReader的onImageAvailable()方法不会回调
                    if(mImageReader==null)return;
                    mPreviewBuilder.addTarget(mImageReader.getSurface());
                    //设置拍照方向
                    mPreviewBuilder.set(CaptureRequest.JPEG_ORIENTATION, mCameraId.equals("1")?270:ORIENTATION.get(rotation));

                    //聚焦
                    mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    //停止预览
                    mPreviewSession.stopRepeating();
                    //开始拍照，然后回调上面的接口重启预览，因为mPreviewBuilder设置ImageReader作为target，所以会自动回调ImageReader的onImageAvailable()方法保存图片
                    mPreviewSession.capture(mPreviewBuilder.build(), mSessionCaptureCallback, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
//        setContentView(R.layout.activity_goods_search_img);
//        cameraSurfaceView = findViewById(R.id.camera);
//        cameraSurfaceView.setCameraParams(cameraSurfaceView.mCamera, UIUtils.getScreenWidth(this),UIUtils.getScreenHeight(this));
//        ImageView iv_take=findViewById(R.id.iv_take);
//
//        iv_take.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cameraSurfaceView.takePicture();
//
//            }
//        });

    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }else {
//                showToast("权限已申请");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_PERMISSIONS_REQUEST_CAMERA&&grantResults[0]==0){

        }
    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
//        openCamera();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
        stopBackgroundThread();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeCamera() {
        if(mPreviewSession!=null){
            mPreviewSession.close();
            mPreviewSession=null;
        }
        if (mCameraDevice!=null){
            mCameraDevice.close();
            mCameraDevice=null;

            if(mImageReader!=null){

                mImageReader.close();
                mImageReader=null;

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceTextureAvailable = true;
        setupCamera();
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurfaceTextureAvailable=false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupCamera() {
        //获取摄像头的管理者CameraManager
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //遍历所有摄像头
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                //默认打开后置摄像头
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT)
                    continue;
                //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // 对于静态图像捕获，我们使用最大的可用尺寸。
                mPreviewSize = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new Comparator<Size>() {
                            @Override
                            public int compare(Size lhs, Size rhs) {
                                return Long.signum(lhs.getWidth() * lhs.getHeight()
                                        - rhs.getHeight() * rhs.getWidth());
                            }
                        });
                mCameraId = id;
                break;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openCamera() {
        mCameraOpened = true;
        //获取摄像头的管理者CameraManager
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //检查权限
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //打开相机，第一个参数指示打开哪个摄像头，第二个参数stateCallback为相机的状态回调接口，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            manager.openCamera(mCameraId, stateCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            //开启预览
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPreview() {
        SurfaceTexture mSurfaceTexture = mPreviewView.getSurfaceTexture();

        //设置TextureView的缓冲区大小
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

        //获取Surface显示预览数据
        Surface mSurface = new Surface(mSurfaceTexture);

        setupImageReader();

        //获取ImageReader的Surface
        Surface imageReaderSurface = mImageReader.getSurface();
        try {
            //创建CaptureRequestBuilder，TEMPLATE_PREVIEW比表示预览请求
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //设置Surface作为预览数据的显示界面
            mPreviewBuilder.addTarget(mSurface);
            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraDevice.createCaptureSession(Arrays.asList(mSurface, imageReaderSurface), mSessionStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                //创建捕获请求
                mCaptureRequest = mPreviewBuilder.build();
                mPreviewSession = session;
                //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                mPreviewSession.setRepeatingRequest(mCaptureRequest, mSessionCaptureCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };
    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            //重启预览
            restartPreview();
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void restartPreview() {
        try {
            //执行setRepeatingRequest方法就行了，注意mCaptureRequest是之前开启预览设置的请求
            mPreviewSession.setRepeatingRequest(mCaptureRequest, null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupImageReader() {

        //前三个参数分别是需要的尺寸和格式，最后一个参数代表每次最多获取几帧数据，本例的2代表ImageReader中最多可以获取两帧图像流
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),
                ImageFormat.JPEG, 2);
        //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if(mHandler!=null){
                    mHandler.post(new ImageSaver(reader.acquireNextImage()));
                }else {
                    mHandler=new Handler();
                }
            }
        }, mHandler);
    }
    public class ImageSaver implements Runnable {

        private Image mImage;
        private File mFile;

        public ImageSaver(Image image) {
            this.mImage = image;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
//            if(mCameraId.equals("1")){
//                BitmapFactory.Options options=new BitmapFactory.Options();
//                options.inJustDecodeBounds=true;
//                BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
//                options.inJustDecodeBounds=false;
//            Bitmap bitmap= UIUtils.bytes2bitmap(bytes,null,GoodsSearchByImgActivity.this,mPreviewSize);
//                bytes = UIUtils.Bitmap2Bytes(bitmap);
//            }

            FileOutputStream output = null;

            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.US);

            String fname = "IMG_" +
                    sdf.format(new Date())
                    + ".jpg";
            mFile = new File(getApplication().getExternalFilesDir(null), fname);
//            if(!mFile.exists()){
//                mFile.mkdirs();
//            }
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent=new Intent(GoodsSearchByImgActivity.this,ImageShowActivity.class);
            intent.putExtra(Constance.path,mFile.getAbsolutePath());
            intent.putExtra(Constance.isFont,true);
            startActivityForResult(intent,PHOTO_TAKE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
        if(requestCode==PHOTO_WITH_DATA&&data.getData()!=null){
            String imagePath=data.getData().toString();
            Intent intent=new Intent();
            intent.putExtra(Constance.path,imagePath);
            setResult(200,intent);
            finish();
        }else if(requestCode==PHOTO_TAKE) {
            if (resultCode == 200) {
                Intent intent = new Intent();
                intent.putExtra(Constance.path, "file://" + data.getStringExtra(Constance.path));
                setResult(200, intent);
                finish();
            } else {

            }
        }
        }
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        if(mThreadHandler==null){
            return;
        }
        mThreadHandler.quitSafely();//线程安全停止
        try {
            mThreadHandler.join();
            mThreadHandler = null;
            mHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
