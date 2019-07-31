package bc.yxdc.com;



/**
 * Created by gamekonglee on 2018/8/10.
 */

public abstract class EasyCallBack  {

//    @Override
//    public String onResponse(Object response, int id) {
//        return null;
//    }


    public abstract void onErrorResponse(Exception msg);
    public abstract void onSuccessResponse(String msg);

}
