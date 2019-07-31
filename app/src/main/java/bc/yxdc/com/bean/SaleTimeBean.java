package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/10/26.
 */

public class SaleTimeBean {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-10-26 12:0:55
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private List<Time> time;
        private Ad ad;
        public void setTime(List<Time> time) {
            this.time = time;
        }
        public List<Time> getTime() {
            return time;
        }

        public void setAd(Ad ad) {
            this.ad = ad;
        }
        public Ad getAd() {
            return ad;
        }
    public class Ad{
        public String ad_link;
        public String ad_name;
        public String ad_code;

    }
}
