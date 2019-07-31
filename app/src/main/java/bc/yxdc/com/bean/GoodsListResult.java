package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/9/14.
 */

public class GoodsListResult {
    List<GoodsBean> goods_list;
    List<FilterPrice> filter_price;
    String orderby_default;
    String orderby_sales_sum;
    String orderby_price;
    String orderby_comment_count;
    String orderby_is_new;
    String sort_asc;
    String sort;
    List<FilterAttr> filter_attr;
    List<FilterAttr> filter_spec;

    public String getSort_asc() {
        return sort_asc;
    }

    public void setSort_asc(String sort_asc) {
        this.sort_asc = sort_asc;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<FilterAttr> getFilter_attr() {
        return filter_attr;
    }

    public void setFilter_attr(List<FilterAttr> filter_attr) {
        this.filter_attr = filter_attr;
    }

    public List<FilterAttr> getFilter_spec() {
        return filter_spec;
    }

    public void setFilter_spec(List<FilterAttr> filter_spec) {
        this.filter_spec = filter_spec;
    }

    public List<GoodsBean> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(List<GoodsBean> goods_list) {
        this.goods_list = goods_list;
    }

    public List<FilterPrice> getFilter_price() {
        return filter_price;
    }

    public void setFilter_price(List<FilterPrice> filter_price) {
        this.filter_price = filter_price;
    }

    public String getOrderby_default() {
        return orderby_default;
    }

    public void setOrderby_default(String orderby_default) {
        this.orderby_default = orderby_default;
    }

    public String getOrderby_sales_sum() {
        return orderby_sales_sum;
    }

    public void setOrderby_sales_sum(String orderby_sales_sum) {
        this.orderby_sales_sum = orderby_sales_sum;
    }

    public String getOrderby_price() {
        return orderby_price;
    }

    public void setOrderby_price(String orderby_price) {
        this.orderby_price = orderby_price;
    }

    public String getOrderby_comment_count() {
        return orderby_comment_count;
    }

    public void setOrderby_comment_count(String orderby_comment_count) {
        this.orderby_comment_count = orderby_comment_count;
    }

    public String getOrderby_is_new() {
        return orderby_is_new;
    }

    public void setOrderby_is_new(String orderby_is_new) {
        this.orderby_is_new = orderby_is_new;
    }
}
