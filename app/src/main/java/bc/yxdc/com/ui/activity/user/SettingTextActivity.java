package bc.yxdc.com.ui.activity.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liuwan.demo.datepicker.CustomDatePicker;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.User;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.utils.DateUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/18.
 */

public class SettingTextActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_save)
    TextView tv_save;
    @BindView(R.id.et_text)
    EditText et_text;
    private String text;
    private int type;
    @BindView(R.id.ll_sex)
    LinearLayout ll_sex;
    @BindView(R.id.rg_sex)
    RadioGroup rg_sex;
    @BindView(R.id.rb_male)RadioButton rb_male;
    @BindView(R.id.rb_female)RadioButton rb_female;
    @BindView(R.id.rb_invisible)RadioButton rb_invisible;
    private String sex;
    private String content;
    private String sexType;
    private String mText;
    private User user;
    private CustomDatePicker customDatePicker;

    @Override
    protected void initData() {
        text = getIntent().getStringExtra(Constance.text);
        type = getIntent().getIntExtra(Constance.type,1);
        sex = getIntent().getStringExtra(Constance.sex);
        content = getIntent().getStringExtra(Constance.content);
        user = new Gson().fromJson(IssApplication.mUserBean,User.class);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_setting_text);
        ButterKnife.bind(this);
        tv_title.setText(text+"");
        switch (type){
            case 2:
                ll_sex.setVisibility(View.VISIBLE);
                et_text.setVisibility(View.GONE);
                if(TextUtils.isEmpty(sex)||sex.equals("0")){
                    rb_invisible.setChecked(true);
                    sexType="0";
                }else {
                    if(sex.equals("1")){
                        rb_male.setChecked(true);
                        sexType="1";
                    }else {
                        sexType="2";
                        rb_female.setChecked(true);
                    }
                }
                break;
            case 3:
                ll_sex.setVisibility(View.GONE);
                et_text.setVisibility(View.VISIBLE);
                et_text.setText(content);
                et_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            initDatePicker(simpleDateFormat.parse(content));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                        showDatePickerDialog(SettingTextActivity.this,R.style.time_dialog,et_text,Calendar.getInstance());
                    }
                });
                break;
            case 1:
            case 4:
                ll_sex.setVisibility(View.GONE);
                et_text.setVisibility(View.VISIBLE);
                et_text.setText(content);
                break;
        }
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type!=2) {
                    mText = et_text.getText().toString();
                    if (TextUtils.isEmpty(mText)) {
                        MyToast.show(SettingTextActivity.this, "请正确填写信息");
                        return;
                    }
                }

                switch (type){
                    case 1:
                        user.setNickname(mText);
                        break;
                    case 2:
                        user.setSex(sexType);
                        break;
                    case 3:
                        user.setBirthday(mText);
                        break;
                    case 4:
                        break;
                }
                misson(0, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final JSONObject jsonObject=new JSONObject(response.body().string());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.show(SettingTextActivity.this,jsonObject.getString(Constance.msg));
                                if(jsonObject.getInt(Constance.status)==1){
                                    SettingTextActivity.this.finish();
                                }
                            }
                        });

                    }
                });

            }
        });
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_male:
                        sexType = "1";
                        break;
                    case R.id.rb_female:
                        sexType="2";
                        break;
                    case R.id.rb_invisible:
                        sexType="0";
                        break;

                }
            }
        });

    }
//    public static void showDatePickerDialog(Activity activity, int themeResId, final EditText tv, Calendar calendar) {
//        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
//        new DatePickerDialog(activity , themeResId,new DatePickerDialog.OnDateSetListener() {
//            // 绑定监听器(How the parent is notified that the date is set.)
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                // 此处得到选择的时间，可以进行你想要的操作
//                tv.setText(year + "-" + (monthOfYear+1)+ "-" + dayOfMonth );
//
//            }
//        }
//                // 设置初始日期
//                , calendar.get(Calendar.YEAR)
//                ,calendar.get(Calendar.MONTH)
//                ,calendar.get(Calendar.DAY_OF_MONTH)).show();
//    }
    private void initDatePicker(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00", Locale.CHINA);
        String now = sdf.format(date);

        // 回调接口，获得选中的时间
// 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                et_text.setText(""+time.split(" ")[0]);
            }
        }, "1900-01-01 00:00", now);
        customDatePicker.showSpecificTime(false); // 显示时和分
        customDatePicker.show(now);
    }
    @Override
    public void getData(int type, Callback callback) {
        String token= MyShare.get(this).getString(Constance.token);
        OkHttpUtils.updateUserInfo(token,user.getUser_id()+"",user.getNickname(),user.getSex(),user.getBirthday(),callback);
    }
}
