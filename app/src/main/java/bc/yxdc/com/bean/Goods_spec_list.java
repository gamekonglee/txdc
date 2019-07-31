package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/9/14.
 */
/**
 * Copyright 2018 bejson.com
 */

import java.util.List;

/**
 * Auto-generated: 2018-09-14 15:20:37
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Goods_spec_list {

    private String spec_name;
    private List<Spec_list> spec_list;
    public int currentP;
    public void setSpec_name(String spec_name) {
        this.spec_name = spec_name;
    }
    public String getSpec_name() {
        return spec_name;
    }

    public void setSpec_list(List<Spec_list> spec_list) {
        this.spec_list = spec_list;
    }
    public List<Spec_list> getSpec_list() {
        return spec_list;
    }

}