package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/11/13.
 */

public class ExpressBean {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-11-13 14:37:21
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private String message;
        private String nu;
        private String ischeck;
        private String condition;
        private String com;
        private String status;
        private String state;
        private List<Data> data;
        public void setMessage(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }

        public void setNu(String nu) {
            this.nu = nu;
        }
        public String getNu() {
            return nu;
        }

        public void setIscheck(String ischeck) {
            this.ischeck = ischeck;
        }
        public String getIscheck() {
            return ischeck;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }
        public String getCondition() {
            return condition;
        }

        public void setCom(String com) {
            this.com = com;
        }
        public String getCom() {
            return com;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

        public void setState(String state) {
            this.state = state;
        }
        public String getState() {
            return state;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }
        public List<Data> getData() {
            return data;
        }
/**
 * Copyright 2018 bejson.com
 */

    /**
     * Auto-generated: 2018-11-13 14:37:21
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class Data {

        private String time;
        private String ftime;
        private String context;
        private String location;
        public void setTime(String time) {
            this.time = time;
        }
        public String getTime() {
            return time;
        }

        public void setFtime(String ftime) {
            this.ftime = ftime;
        }
        public String getFtime() {
            return ftime;
        }

        public void setContext(String context) {
            this.context = context;
        }
        public String getContext() {
            return context;
        }

        public void setLocation(String location) {
            this.location = location;
        }
        public String getLocation() {
            return location;
        }

    }
}
