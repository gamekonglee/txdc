package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/11/10.
 */

public class Programme {
    /**
     * Copyright 2018 bejson.com 
     */

    /**
     * Auto-generated: 2018-11-10 9:48:47
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private int id;
        private String title;
        private String path;
        private String room;
        private String style;
        private String remark;
        private String create_at;
        private UserPro user;
        private List<Good> good;
        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setPath(String path) {
            this.path = path;
        }
        public String getPath() {
            return path;
        }

        public void setRoom(String room) {
            this.room = room;
        }
        public String getRoom() {
            return room;
        }

        public void setStyle(String style) {
            this.style = style;
        }
        public String getStyle() {
            return style;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
        public String getRemark() {
            return remark;
        }

        public void setCreate_at(String create_at) {
            this.create_at = create_at;
        }
        public String getCreate_at() {
            return create_at;
        }

        public void setUser(UserPro user) {
            this.user = user;
        }
        public UserPro getUser() {
            return user;
        }

        public void setGood(List<Good> good) {
            this.good = good;
        }
        public List<Good> getGood() {
            return good;
        }

}
