package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/9/19.
 */

/**
 * Copyright 2018 bejson.com 
 */

import java.util.List;

/**
 * Auto-generated: 2018-09-19 10:48:24
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class CartList {

    private int id;
    private int user_id;
    private String session_id;
    private int goods_id;
    private String goods_sn;
    private String goods_name;
    private String market_price;
    private String goods_price;
    private String member_goods_price;
    private int goods_num;
    private int item_id;
    private String spec_key;
    private String spec_key_name;
    private String bar_code;
    private String original_img;
    private int selected;
    private long add_time;
    private int prom_type;
    private int prom_id;
    private String sku;
    private int combination_group_id;
    private double count_price;
    private double count_total_price;
    private int count_num;
    private int store_count;
    private String delivery;
    private  int warn_number;

    public int getWarn_number() {
        return warn_number;
    }

    public void setWarn_number(int warn_number) {
        this.warn_number = warn_number;
    }

    private List<String> combination_cart;
    Goods goods;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getOriginal_img() {
        return original_img;
    }

    public void setOriginal_img(String original_img) {
        this.original_img = original_img;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public int getUser_id() {
        return user_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
    public String getSession_id() {
        return session_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }
    public int getGoods_id() {
        return goods_id;
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

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }
    public String getMarket_price() {
        return market_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }
    public String getGoods_price() {
        return goods_price;
    }

    public void setMember_goods_price(String member_goods_price) {
        this.member_goods_price = member_goods_price;
    }
    public String getMember_goods_price() {
        return member_goods_price;
    }

    public void setGoods_num(int goods_num) {
        this.goods_num = goods_num;
    }
    public int getGoods_num() {
        return goods_num;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }
    public int getItem_id() {
        return item_id;
    }

    public void setSpec_key(String spec_key) {
        this.spec_key = spec_key;
    }
    public String getSpec_key() {
        return spec_key;
    }

    public void setSpec_key_name(String spec_key_name) {
        this.spec_key_name = spec_key_name;
    }
    public String getSpec_key_name() {
        return spec_key_name;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }
    public String getBar_code() {
        return bar_code;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }
    public int getSelected() {
        return selected;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }
    public long getAdd_time() {
        return add_time;
    }

    public void setProm_type(int prom_type) {
        this.prom_type = prom_type;
    }
    public int getProm_type() {
        return prom_type;
    }

    public void setProm_id(int prom_id) {
        this.prom_id = prom_id;
    }
    public int getProm_id() {
        return prom_id;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
    public String getSku() {
        return sku;
    }

    public void setCombination_group_id(int combination_group_id) {
        this.combination_group_id = combination_group_id;
    }
    public int getCombination_group_id() {
        return combination_group_id;
    }

    public void setCount_price(double count_price) {
        this.count_price = count_price;
    }
    public double getCount_price() {
        return count_price;
    }

    public void setCount_total_price(double count_total_price) {
        this.count_total_price = count_total_price;
    }
    public double getCount_total_price() {
        return count_total_price;
    }

    public void setCount_num(int count_num) {
        this.count_num = count_num;
    }
    public int getCount_num() {
        return count_num;
    }

    public void setStore_count(int store_count) {
        this.store_count = store_count;
    }
    public int getStore_count() {
        return store_count;
    }

    public void setCombination_cart(List<String> combination_cart) {
        this.combination_cart = combination_cart;
    }
    public List<String> getCombination_cart() {
        return combination_cart;
    }


    public String  toJSON(){
        return "{\"cartID\":\""+this.getId()+"\",\"goodsNum\":\""+this.getGoods_num()+"\",\"storeCount\":\""+this.getStore_count()+"\",\"selected\":\""+this.getSelected()+"\"}";
    }

}
