package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/9/14.
 */
/**
 * Copyright 2018 bejson.com
 */

/**
 * Auto-generated: 2018-09-14 15:20:37
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Recommend_goods {

    private int goods_id;
    private String goods_name;
    private String shop_price;
    public String original_img;
    public String sales_sum;

    public String getSales_sum() {
        return sales_sum;
    }

    public void setSales_sum(String sales_sum) {
        this.sales_sum = sales_sum;
    }

    public String getOriginal_img() {
        return original_img;
    }

    public void setOriginal_img(String original_img) {
        this.original_img = original_img;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }
    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }
    public String getGoods_name() {
        return goods_name;
    }

    public void setShop_price(String shop_price) {
        this.shop_price = shop_price;
    }
    public String getShop_price() {
        return shop_price;
    }

}
