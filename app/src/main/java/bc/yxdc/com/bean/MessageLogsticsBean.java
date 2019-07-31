package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/12/4.
 */

public class MessageLogsticsBean {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-12-04 9:50:45
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private int category;
        private int message_id;
        private int is_see;
        private long send_time;
        private int type;
        private Data data;
        public void setCategory(int category) {
            this.category = category;
        }
        public int getCategory() {
            return category;
        }

        public void setMessage_id(int message_id) {
            this.message_id = message_id;
        }
        public int getMessage_id() {
            return message_id;
        }

        public void setIs_see(int is_see) {
            this.is_see = is_see;
        }
        public int getIs_see() {
            return is_see;
        }

        public void setSend_time(long send_time) {
            this.send_time = send_time;
        }
        public long getSend_time() {
            return send_time;
        }

        public void setType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }

        public void setData(Data data) {
            this.data = data;
        }
        public Data getData() {
            return data;
        }
/**
 * Copyright 2018 bejson.com 
 */

    /**
     * Auto-generated: 2018-12-04 9:50:45
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class Data {

        private String title;
        private long post_time;
        private int order_id;
        private String discription;
        private int goods_id;
        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setPost_time(long post_time) {
            this.post_time = post_time;
        }
        public long getPost_time() {
            return post_time;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }
        public int getOrder_id() {
            return order_id;
        }

        public void setDiscription(String discription) {
            this.discription = discription;
        }
        public String getDiscription() {
            return discription;
        }

        public void setGoods_id(int goods_id) {
            this.goods_id = goods_id;
        }
        public int getGoods_id() {
            return goods_id;
        }

    }
}
