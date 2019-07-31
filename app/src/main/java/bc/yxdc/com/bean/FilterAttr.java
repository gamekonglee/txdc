package bc.yxdc.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/10/31.
 */

public class FilterAttr {
    public String name;
    public List<FilterPrice> item;
    public int current;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterPrice> getItem() {
        return item;
    }

    public void setItem(List<FilterPrice> item) {
        this.item = item;
    }
}
