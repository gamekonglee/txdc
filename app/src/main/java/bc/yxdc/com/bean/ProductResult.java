package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/9/14.
 */

public class ProductResult {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-09-14 15:20:37
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private Activity activity;
        private Goods goods;
        private List<Goods_spec_list> goods_spec_list;
        private List<Spec_goods_price> spec_goods_price;
        private List<Gallery> gallery;
        private List<Object> comment;
        private List<Recommend_goods> recommend_goods;
        private Consignee consignee;
        public void setActivity(Activity activity) {
            this.activity = activity;
        }
        public Activity getActivity() {
            return activity;
        }

        public void setGoods(Goods goods) {
            this.goods = goods;
        }
        public Goods getGoods() {
            return goods;
        }

        public void setGoods_spec_list(List<Goods_spec_list> goods_spec_list) {
            this.goods_spec_list = goods_spec_list;
        }
        public List<Goods_spec_list> getGoods_spec_list() {
            return goods_spec_list;
        }

        public void setSpec_goods_price(List<Spec_goods_price> spec_goods_price) {
            this.spec_goods_price = spec_goods_price;
        }
        public List<Spec_goods_price> getSpec_goods_price() {
            return spec_goods_price;
        }

        public void setGallery(List<Gallery> gallery) {
            this.gallery = gallery;
        }
        public List<Gallery> getGallery() {
            return gallery;
        }

        public void setComment(List<Object> comment) {
            this.comment = comment;
        }
        public List<Object> getComment() {
            return comment;
        }

        public void setRecommend_goods(List<Recommend_goods> recommend_goods) {
            this.recommend_goods = recommend_goods;
        }
        public List<Recommend_goods> getRecommend_goods() {
            return recommend_goods;
        }

        public void setConsignee(Consignee consignee) {
            this.consignee = consignee;
        }
        public Consignee getConsignee() {
            return consignee;
        }

}
