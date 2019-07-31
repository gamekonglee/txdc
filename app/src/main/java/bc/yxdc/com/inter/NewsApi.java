package bc.yxdc.com.inter;

import java.util.Observable;

import bc.yxdc.com.bean.BaseBean;
import bc.yxdc.com.bean.BaseUser;
import bc.yxdc.com.bean.User;
import bc.yxdc.com.constant.NetWorkConst;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by gamekonglee on 2018/9/18.
 */

public interface NewsApi  {
    @GET("/index.php?m=Api&c=User&a=userInfo")
    Call<BaseUser> getUser(@Query("user_id")String user_id, @Query("token")String token);

}
