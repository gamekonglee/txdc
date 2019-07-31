package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/9/28.
 */

public class WxPayBean  {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-09-28 14:29:12
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private String appid;
        private String noncestr;
        private String packages;
        private String partnerid;
        private String prepayid;
        private long timestamp;
        private String sign;
        public void setAppid(String appid) {
            this.appid = appid;
        }
        public String getAppid() {
            return appid;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }
        public String getNoncestr() {
            return noncestr;
        }

        public void setPackage(String packages) {
            this.packages = packages;
        }
        public String getPackage() {
            return packages;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }
        public String getPartnerid() {
            return partnerid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }
        public String getPrepayid() {
            return prepayid;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
        public long getTimestamp() {
            return timestamp;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
        public String getSign() {
            return sign;
        }

}
