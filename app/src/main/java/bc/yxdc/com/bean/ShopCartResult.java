package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/9/19.
 */

public class ShopCartResult {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-09-19 10:48:24
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private Total_price total_price;
        private List<CartList> cartList;
        public void setTotal_price(Total_price total_price) {
            this.total_price = total_price;
        }
        public Total_price getTotal_price() {
            return total_price;
        }

        public void setCartList(List<CartList> cartList) {
            this.cartList = cartList;
        }
        public List<CartList> getCartList() {
            return cartList;
        }

}
