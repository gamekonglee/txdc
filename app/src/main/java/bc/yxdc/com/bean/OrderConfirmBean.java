package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/9/24.
 */

public class OrderConfirmBean {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-09-24 13:47:35
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private AddressList userAddress;
        private List<ShippingList> shippingList;
        private List<CartList> cartList;
        private TotalPrice cartPriceInfo;
        public  String couponNum;
        private List<CouponMineBean> userCartCouponList;
        private UserInfo userInfo;
        public void setAddressList(AddressList addressList) {
            this.userAddress = addressList;
        }
        public AddressList  getAddressList() {
            return userAddress;
        }

        public void setShippingList(List<ShippingList> shippingList) {
            this.shippingList = shippingList;
        }
        public List<ShippingList> getShippingList() {
            return shippingList;
        }

        public void setCartList(List<CartList> cartList) {
            this.cartList = cartList;
        }
        public List<CartList> getCartList() {
            return cartList;
        }

        public void setTotalPrice(TotalPrice totalPrice) {
            this.cartPriceInfo = totalPrice;
        }
        public TotalPrice getTotalPrice() {
            return cartPriceInfo;
        }

        public void setCouponList(List<CouponMineBean> couponList) {
            this.userCartCouponList = couponList;
        }
        public List<CouponMineBean> getCouponList() {
            return userCartCouponList;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }
        public UserInfo getUserInfo() {
            return userInfo;
        }

}
