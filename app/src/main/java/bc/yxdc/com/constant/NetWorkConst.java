package bc.yxdc.com.constant;

/**
 * Created by gamekonglee on 2018/9/11.
 */

public class NetWorkConst {
//    public static final String API_HOST="http://192.168.1.20";
    public static final String API_HOST="https://www.tianxiadengcang.com/";
    public static final String API_HOST_2="http://www.tianxiadengcang.com/";
    public static final String IMAGE_URL = API_HOST + "/index.php?m=Api&c=Goods&a=goodsThumImages&width=200&height=200&goods_id=";
    public static final String APK_NAME = "txdc_v1";
    public static final String DOWN_APK_URL = "http://app.08138.com/txdc.apk";
    public static final String PRODUCT = "PRODUCT";
    public static final String URL_PRODUCT = API_HOST+"/index.php?m=Api&c=Goods&a=goodsList";
    public static final String URL_CLASSIFY = API_HOST+"/index.php?m=Api&c=Goods&a=goodsCategoryList";
    public static final String URL_PRODUCT_DETAIL = API_HOST + "/index.php?m=Api&c=Goods&a=goodsInfo";
    public static final String URL_HOME_PAGE = API_HOST + "/index.php?m=Api&c=Index&a=homePage";
    public static final String URL_LOGIN = API_HOST + "/index.php?m=Api&c=User&a=login";
    public static final String URL_SEND_SMS = API_HOST + "/index.php?m=Home&c=Api&a=send_validate_code&unique_id=bocang&scene=1&mobile=";
    public static final String URL_REGISTER = API_HOST + "/index.php?m=Api&c=User&a=reg";
    public static final String URL_USER = API_HOST + "/index.php?m=Api&c=User&a=userInfo";
    public static final String URL_SHOPCART_ADD = API_HOST + "/index.php?m=Api&c=Cart&a=addcart";
    public static final String URL_SHOPCART_LIST = API_HOST + "/index.php?m=Api&c=Cart&a=cartList";
    public static final String URL_PWD_FORGET_ONE = API_HOST+"/index.php?m=api&c=user&a=forgetPasswordInfo";

    public static final String URL_SEND_VERIFY_IMG = API_HOST + "/index.php?m=api&c=User&a=verify";
    public static final String URL_PWD_FORGET_TWO = API_HOST + "/index.php?m=api&c=user&a=check_sms";
    public static final String URL_PWD_FORGET_THREE = API_HOST + "/index.php?m=api&c=user&a=forgetPassword";
    public static final String URL_ORDER_CONFIRM = API_HOST + "/index.php?m=Api&c=Cart&a=cart2";
    public static final String URL_ADDRESS_ADD = API_HOST + "/index.php?m=Api&c=User&a=addAddress";
    public static final String URL_SUBMIT_ORDER = API_HOST + "/index.php?m=Api&c=Cart&a=cart3";
    public static final String URL_DELETE_CART = API_HOST + "/index.php?m=Api&c=Cart&a=delCart";
    public static final String URL_WX_PAY = API_HOST + "/index.php?m=api&c=Wxpay&a=dopay";
    public static final String URL_ALI_PAY = API_HOST + "/index.php?m=api&c=Payment&a=alipay_sign";
    public static final String URL_CART_LIST = API_HOST + "/index.php?m=Api&c=Cart&a=cartList";
    public static final String URL_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?grant_type=refresh_token";
    
    public static final String URL_THIRD_LOGIN = API_HOST + "/index.php?m=Api&c=User&a=thirdLogin";
    public static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code";
    public static final String URL_ADDRESS_LIST = API_HOST + "/index.php?m=Api&c=User&a=getAddressList";
    public static final String URL_ORDER_LIST = API_HOST + "/index.php?m=Api&c=User&a=getOrderList";
    public static final String URL_GET_REGION = API_HOST + "/index.php?m=Api&c=index&a=get_region&parent_id=";
    public static final String URL_DELETE_ADDRESS = API_HOST + "/index.php?m=Api&c=User&a=del_address";
    public static final String URL_ORDER_DETAIL = API_HOST + "/index.php?m=Api&c=order&a=order_detail";
    public static final String URL_SEARCH_GOODS = API_HOST+"/index.php?m=Api&c=Goods&a=search";
    public static final String VERSION_URL_CONTENT = "http://app.08138.com/version/versioninfo.php?bc_ver_name2=txdc&bejson=1";
    public static final String URL_BUY_NOW = API_HOST + "/index.php?m=Api&c=cart&a=cart2";
    public static final String URL_ORDER_CANCEL = API_HOST + "/index.php?m=Api&c=User&a=cancelOrder";
    public static final String URL_PROMOTE_LIST = API_HOST + "/index.php?m=Api&c=Activity&a=promote_list";
    public static final String URL_PRODUCT_THUMB = API_HOST + "/index.php?m=Api&c=Goods&a=goodsThumImages";
    public static final String URL_CHANGE_PWD = API_HOST + "/index.php?m=Api&c=User&a=password";
    public static final String URL_ORDER_RECEIVER_CONFIRM = API_HOST + "/index.php?m=Api&c=User&a=orderConfirm";
    public static final String URL_NEWGOODS = API_HOST + "/index.php/index.php?m=Api&c=Goods&a=new_goods";
    public static final String URL_TAILGOODS = API_HOST + "/index.php/index.php?m=Api&c=Goods&a=tail_goods";
    public static final String URL_PRODUCT_CONTENT = API_HOST + "/index.php?m=api&c=goods&a=goodsContent&is_json=1";
    public static final String URL_BIND_ACCOUNT = API_HOST + "/index.php?m=Api&c=User&a=bind_account";
    public static final String URL_BIND_REGIS = API_HOST + "/index.php?m=Api&c=User&a=bind_reg";
    public static final String URL_FLASH_SALE_TIME = API_HOST + "/index.php?m=api&c=activity&a=flash_sale_time";
    public static final String URL_FLASH_SALE_LIST = API_HOST + "/index.php?m=api&c=activity&a=flash_sale_list";
    public static final String URL_COUPON_GET = API_HOST + "/index.php?m=api&c=Activity&a=coupon_center&cat_id=0&p=";
    public static final String URL_COUPON_MINE_GET = API_HOST + "/index.php?m=api&c=User&a=getCouponList";
    public static final String URL_COUPON_GET_MINE = API_HOST + "/index.php?m=api&c=Activity&a=get_coupon";
    public static final String URL_GOODS_COLLECT = API_HOST + "/index.php?m=Api&c=Goods&a=collectGoodsOrNo";
    public static final String URL_COLLECT_LIST = API_HOST + "/index.php?m=Api&c=User&a=getGoodsCollect";

    public static final String FANGANUPLOAD =  API_HOST+"/index.php?m=Api&c=Plan&a=plan_create";
    public static final String SHAREFANAN = API_HOST+"";
    public static final String SHAREPRODUCT = API_HOST+"/Mobile/Goods/goodsInfo/id/";
    public static final String URL_SCENE_TYPE = API_HOST + "/index.php?m=api&c=Scene&a=type";
    public static final String UPLOADAVATAR = API_HOST + "/index.php?m=api&c=User&a=upload_headpic";
    public static final String URL_SCENE_LIST = API_HOST + "/index.php?m=api&c=Scene&a=lists";
    public static final String URL_PROGRAMME_LIST = API_HOST + "/index.php?m=Api&c=Plan&a=plan_list";
    public static final String URL_PROGRAMME_EDIT = API_HOST + "/index.php?m=Api&c=Plan&a=plan_update";
    public static final String URL_PROGRAMME_DELETE = API_HOST + "/index.php?m=Api&c=Plan&a=plan_del";
//    public static final String URL_EXPRESS = API_HOST + "/index.php?m=api&c=user&a=express&is_json=1";
    public static final String URL_EXPRESS = API_HOST +"/index.php?m=Home&c=Api&a=queryExpress";
    public static final String URL_COMMENT_GOODS = API_HOST+"/index.php?m=Api&c=User&a=add_comment";
    public static final String URL_UPDATE_USER = API_HOST + "/index.php?m=Api&c=User&a=updateUserInfo";
    public static final String URL_GET_USER = API_HOST + "/index.php?m=Api&c=User&a=userInfo";
    public static final String URL_LIKE_GOODS = API_HOST + "/index.php?m=Api&c=Index&a=favourite";
    public static final String URL_SEARCH_IMG = API_HOST + "/index.php?m=api&c=goods&a=search_similar_img";
    public static final String URL_GET_MESSAGE = API_HOST + "/index.php?m=api&c=user&a=message";
    public static final String SHAREIMAGE_LOGO = API_HOST + "logo.png";
    public static final String URL_PRODUCT_ACTIVTY = API_HOST + "/index.php?m=Api&c=Goods&a=goods_activity";
    public static final String URL_WX_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?";
    public static final String SHARE_APK_URL = "https://sj.qq.com/myapp/detail.htm?apkName=app.txdc.shop";
    public static final String URL_DISTRUBUTOR = API_HOST + "/api/user/reg_distributor";
}
