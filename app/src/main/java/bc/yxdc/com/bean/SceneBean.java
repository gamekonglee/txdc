package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/11/8.
 */

public class SceneBean {
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-11-08 10:51:31
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private int scene_id;
        private Scene scene;
        public void setScene_id(int scene_id) {
            this.scene_id = scene_id;
        }
        public int getScene_id() {
            return scene_id;
        }

        public void setScene(Scene scene) {
            this.scene = scene;
        }
        public Scene getScene() {
            return scene;
        }
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-11-08 10:51:31
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class Scene {

        private String name;
        private String path;
        private int id;
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setPath(String path) {
            this.path = path;
        }
        public String getPath() {
            return path;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

    }

}
