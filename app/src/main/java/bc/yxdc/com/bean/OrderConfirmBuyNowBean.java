package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/10/13.
 */

public class OrderConfirmBuyNowBean {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-10-13 15:9:31
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
        private UserAddress userAddress;
        private List<CouponMineBean> userCartCouponList;
        private int couponNum;
        private List<CartList> cartList;
        private CartPriceInfo cartPriceInfo;
        private UserInfo userInfo;
        public void setUserAddress(UserAddress userAddress) {
            this.userAddress = userAddress;
        }
        public UserAddress getUserAddress() {
            return userAddress;
        }

        public void setUserCartCouponList(List<CouponMineBean> userCartCouponList) {
            this.userCartCouponList = userCartCouponList;
        }
        public List<CouponMineBean> getUserCartCouponList() {
            return userCartCouponList;
        }

        public void setCouponNum(int couponNum) {
            this.couponNum = couponNum;
        }
        public int getCouponNum() {
            return couponNum;
        }

        public void setCartList(List<CartList> cartList) {
            this.cartList = cartList;
        }
        public List<CartList> getCartList() {
            return cartList;
        }

        public void setCartPriceInfo(CartPriceInfo cartPriceInfo) {
            this.cartPriceInfo = cartPriceInfo;
        }
        public CartPriceInfo getCartPriceInfo() {
            return cartPriceInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }
        public UserInfo getUserInfo() {
            return userInfo;
        }

}
