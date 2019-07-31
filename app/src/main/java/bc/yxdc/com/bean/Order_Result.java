package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/10/10.
 */

public class Order_Result {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-10-10 11:40:11
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private int order_id;
        private String order_sn;
        private int user_id;
        private int order_status;
        private int shipping_status;
        private int pay_status;
        private String consignee;
        private int country;
        private int province;
        private int city;
        private int district;
        private int twon;
        private String address;
        private String zipcode;
        private String mobile;
        private String email;
        private String shipping_code;
        private String shipping_name;
        private String pay_code;
        private String pay_name;
        private String invoice_title;
        private String taxpayer;
        private String invoice_desc;
        String invoice_no;
        private String goods_price;
        private String shipping_price;
        private String user_money;
        private String coupon_price;
        private int integral;
        private String integral_money;
        private String order_amount;
        private String total_amount;
        private long add_time;
        private int shipping_time;
        private int confirm_time;
        private int pay_time;
        private String transaction_id;
        private int prom_id;
        private int prom_type;
        private int order_prom_id;
        private String order_prom_amount;
        private String discount;
        private String user_note;
        private String admin_note;
        private String parent_sn;
        private int is_distribut;
        private String paid_money;
        private int shop_id;
        private int deleted;
        private String order_status_detail;
        private Order_button order_button;
        private List<Order_goods> order_goods;

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }
        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_sn(String order_sn) {
            this.order_sn = order_sn;
        }
        public String getOrder_sn() {
            return order_sn;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
        public int getUser_id() {
            return user_id;
        }

        public void setOrder_status(int order_status) {
            this.order_status = order_status;
        }
        public int getOrder_status() {
            return order_status;
        }

        public void setShipping_status(int shipping_status) {
            this.shipping_status = shipping_status;
        }
        public int getShipping_status() {
            return shipping_status;
        }

        public void setPay_status(int pay_status) {
            this.pay_status = pay_status;
        }
        public int getPay_status() {
            return pay_status;
        }

        public void setConsignee(String consignee) {
            this.consignee = consignee;
        }
        public String getConsignee() {
            return consignee;
        }

        public void setCountry(int country) {
            this.country = country;
        }
        public int getCountry() {
            return country;
        }

        public void setProvince(int province) {
            this.province = province;
        }
        public int getProvince() {
            return province;
        }

        public void setCity(int city) {
            this.city = city;
        }
        public int getCity() {
            return city;
        }

        public void setDistrict(int district) {
            this.district = district;
        }
        public int getDistrict() {
            return district;
        }

        public void setTwon(int twon) {
            this.twon = twon;
        }
        public int getTwon() {
            return twon;
        }

        public void setAddress(String address) {
            this.address = address;
        }
        public String getAddress() {
            return address;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }
        public String getZipcode() {
            return zipcode;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
        public String getMobile() {
            return mobile;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public String getEmail() {
            return email;
        }

        public void setShipping_code(String shipping_code) {
            this.shipping_code = shipping_code;
        }
        public String getShipping_code() {
            return shipping_code;
        }

        public void setShipping_name(String shipping_name) {
            this.shipping_name = shipping_name;
        }
        public String getShipping_name() {
            return shipping_name;
        }

        public void setPay_code(String pay_code) {
            this.pay_code = pay_code;
        }
        public String getPay_code() {
            return pay_code;
        }

        public void setPay_name(String pay_name) {
            this.pay_name = pay_name;
        }
        public String getPay_name() {
            return pay_name;
        }

        public void setInvoice_title(String invoice_title) {
            this.invoice_title = invoice_title;
        }
        public String getInvoice_title() {
            return invoice_title;
        }

        public void setTaxpayer(String taxpayer) {
            this.taxpayer = taxpayer;
        }
        public String getTaxpayer() {
            return taxpayer;
        }

        public void setInvoice_desc(String invoice_desc) {
            this.invoice_desc = invoice_desc;
        }
        public String getInvoice_desc() {
            return invoice_desc;
        }

        public void setGoods_price(String goods_price) {
            this.goods_price = goods_price;
        }
        public String getGoods_price() {
            return goods_price;
        }

        public void setShipping_price(String shipping_price) {
            this.shipping_price = shipping_price;
        }
        public String getShipping_price() {
            return shipping_price;
        }

        public void setUser_money(String user_money) {
            this.user_money = user_money;
        }
        public String getUser_money() {
            return user_money;
        }

        public void setCoupon_price(String coupon_price) {
            this.coupon_price = coupon_price;
        }
        public String getCoupon_price() {
            return coupon_price;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }
        public int getIntegral() {
            return integral;
        }

        public void setIntegral_money(String integral_money) {
            this.integral_money = integral_money;
        }
        public String getIntegral_money() {
            return integral_money;
        }

        public void setOrder_amount(String order_amount) {
            this.order_amount = order_amount;
        }
        public String getOrder_amount() {
            return order_amount;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }
        public String getTotal_amount() {
            return total_amount;
        }

        public void setAdd_time(long add_time) {
            this.add_time = add_time;
        }
        public long getAdd_time() {
            return add_time;
        }

        public void setShipping_time(int shipping_time) {
            this.shipping_time = shipping_time;
        }
        public int getShipping_time() {
            return shipping_time;
        }

        public void setConfirm_time(int confirm_time) {
            this.confirm_time = confirm_time;
        }
        public int getConfirm_time() {
            return confirm_time;
        }

        public void setPay_time(int pay_time) {
            this.pay_time = pay_time;
        }
        public int getPay_time() {
            return pay_time;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }
        public String getTransaction_id() {
            return transaction_id;
        }

        public void setProm_id(int prom_id) {
            this.prom_id = prom_id;
        }
        public int getProm_id() {
            return prom_id;
        }

        public void setProm_type(int prom_type) {
            this.prom_type = prom_type;
        }
        public int getProm_type() {
            return prom_type;
        }

        public void setOrder_prom_id(int order_prom_id) {
            this.order_prom_id = order_prom_id;
        }
        public int getOrder_prom_id() {
            return order_prom_id;
        }

        public void setOrder_prom_amount(String order_prom_amount) {
            this.order_prom_amount = order_prom_amount;
        }
        public String getOrder_prom_amount() {
            return order_prom_amount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }
        public String getDiscount() {
            return discount;
        }

        public void setUser_note(String user_note) {
            this.user_note = user_note;
        }
        public String getUser_note() {
            return user_note;
        }

        public void setAdmin_note(String admin_note) {
            this.admin_note = admin_note;
        }
        public String getAdmin_note() {
            return admin_note;
        }

        public void setParent_sn(String parent_sn) {
            this.parent_sn = parent_sn;
        }
        public String getParent_sn() {
            return parent_sn;
        }

        public void setIs_distribut(int is_distribut) {
            this.is_distribut = is_distribut;
        }
        public int getIs_distribut() {
            return is_distribut;
        }

        public void setPaid_money(String paid_money) {
            this.paid_money = paid_money;
        }
        public String getPaid_money() {
            return paid_money;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
        }
        public int getShop_id() {
            return shop_id;
        }

        public void setDeleted(int deleted) {
            this.deleted = deleted;
        }
        public int getDeleted() {
            return deleted;
        }

        public void setOrder_status_detail(String order_status_detail) {
            this.order_status_detail = order_status_detail;
        }
        public String getOrder_status_detail() {
            return order_status_detail;
        }

        public void setOrder_button(Order_button order_button) {
            this.order_button = order_button;
        }
        public Order_button getOrder_button() {
            return order_button;
        }

        public void setOrder_goods(List<Order_goods> order_goods) {
            this.order_goods = order_goods;
        }
        public List<Order_goods> getOrder_goods() {
            return order_goods;
        }

}
