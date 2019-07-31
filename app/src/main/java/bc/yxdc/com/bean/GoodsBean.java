package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/9/14.
 */

/**
 * Copyright 2018 bejson.com
 */
        import java.io.Serializable;
        import java.util.Date;

/**
 * Auto-generated: 2018-09-14 9:52:21
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class GoodsBean implements Serializable{

    private int goods_id;
    private int cat_id;
    private String goods_sn;
    private String goods_name;
    private String shop_price;
    private int comment_count;
    private int sales_sum;
    String original_img;
    public String c_url;
    public String c_property;
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

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }
    public int getCat_id() {
        return cat_id;
    }

    public void setGoods_sn(String goods_sn) {
        this.goods_sn = goods_sn;
    }
    public String getGoods_sn() {
        return goods_sn;
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

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }
    public int getComment_count() {
        return comment_count;
    }

    public void setSales_sum(int sales_sum) {
        this.sales_sum = sales_sum;
    }
    public int getSales_sum() {
        return sales_sum;
    }

}