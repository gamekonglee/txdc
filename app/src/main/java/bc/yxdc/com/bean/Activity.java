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
public class Activity {

    private int prom_type;
    private List<Data> data;
    private long server_current_time;
    long prom_start_time;
    long prom_end_time;
    String prom_price;
    int prom_store_count;
    int virtual_num;

    public String getProm_price() {
        return prom_price;
    }

    public void setProm_price(String prom_price) {
        this.prom_price = prom_price;
    }

    public int getProm_store_count() {
        return prom_store_count;
    }

    public void setProm_store_count(int prom_store_count) {
        this.prom_store_count = prom_store_count;
    }

    public int getVirtual_num() {
        return virtual_num;
    }

    public void setVirtual_num(int virtual_num) {
        this.virtual_num = virtual_num;
    }

    public void setProm_type(int prom_type) {
        this.prom_type = prom_type;
    }
    public int getProm_type() {
        return prom_type;
    }

    public long getProm_start_time() {
        return prom_start_time;
    }

    public void setProm_start_time(long prom_start_time) {
        this.prom_start_time = prom_start_time;
    }

    public long getProm_end_time() {
        return prom_end_time;
    }

    public void setProm_end_time(long prom_end_time) {
        this.prom_end_time = prom_end_time;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }

    public void setServer_current_time(long server_current_time) {
        this.server_current_time = server_current_time;
    }
    public long getServer_current_time() {
        return server_current_time;
    }

}
