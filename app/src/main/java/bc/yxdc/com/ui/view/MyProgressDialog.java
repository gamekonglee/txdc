package bc.yxdc.com.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import app.txdc.shop.R;

public class MyProgressDialog extends Dialog {
    public MyProgressDialog(Context context)
    {
        super(context,R.style.CustomProgressDialog);
    }

    public MyProgressDialog(Context context, int theme)
    {
        super(context, R.style.CustomProgressDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context)
    {
        setCancelable(false);
        Window window = getWindow();

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.load_dialog);

        //去除顶部状态栏
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag,flag);

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        //设置不可取消
        //点击其他区域不能取消
//        setCanceledOnTouchOutside(true); 这里是没有效果的，因为范围是全屏
        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show()
    {
        super.show();
    }

}

