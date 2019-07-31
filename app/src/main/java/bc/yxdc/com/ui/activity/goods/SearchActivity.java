package bc.yxdc.com.ui.activity.goods;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/9/13.
 */

public class SearchActivity extends BaseActivity{
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.gv_search_hot)
    GridView gv_hot;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        QuickAdapter<String> adapter=new QuickAdapter<String>(this,R.layout.item_search) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
            helper.setText(R.id.tv_name,item);
            }
        };
        gv_hot.setAdapter(adapter);
        List<String > search_hots=new ArrayList<>();
        search_hots.add("吸顶灯");
        search_hots.add("风扇灯");
        search_hots.add("吊灯");
        adapter.replaceAll(search_hots);
        gv_hot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(SearchActivity.this,SelectGoodsActivity.class);
                startActivity(intent);
                finish();

            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){//搜索按键action
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    String content = et_search.getText().toString();
                    if (!TextUtils.isEmpty(content)){
                        Intent intent=new Intent(SearchActivity.this,SelectGoodsActivity.class);
                        if(et_search.getText()==null)
                        {
                            return false;
                        }
                        intent.putExtra("key",et_search.getText().toString().trim());
                        setResult(120,intent);
                        finish();
                    return true;
                        }
                        return true;
                        }
                        return false;
            }
        });
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                // 修改回车键功能
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                }
                Intent intent=new Intent(SearchActivity.this,SelectGoodsActivity.class);
                if(et_search.getText()==null)
                {
                    return false;
                }
                intent.putExtra("key",et_search.getText().toString().trim());
                setResult(120,intent);
                finish();
                return false;
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
