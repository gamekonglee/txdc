package bc.yxdc.com.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gamekonglee on 2018/11/8.
 */

public class SceneType implements Serializable {
    private String attr_name;
    private List<String> attr_values;
    private boolean isChecked;
    int current;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    String attr_id;

    public List<String> getAttr_values() {
        return attr_values;
    }

    public void setAttr_values(List<String> attr_values) {
        this.attr_values = attr_values;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getAttr_id() {
        return attr_id;
    }

    public void setAttr_id(String attr_id) {
        this.attr_id = attr_id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getAttr_name() {
        return attr_name;
    }

    public void setAttr_name(String attr_name) {
        this.attr_name = attr_name;
    }

    public List<String> getAttrVal() {
        return attr_values;
    }

    public void setAttrVal(List<String> attrVal) {
        this.attr_values = attrVal;
    }


}
