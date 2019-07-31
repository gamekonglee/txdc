package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/10/29.
 */

public class CouponMineBean  {
    public  int id;
    public  int cid;
    public int type;
    public int uid;
    public int order_id;
    public long use_time;
    public String code;
    public long send_time;
    public int store_id;
    public int status;
    public int deleted;
    public String name;
    public String money;
    public long use_start_time;
    public long use_end_time;
    public String condition;
    public String use_type_title;

    public String getUse_type_title() {
        return use_type_title;
    }

    public void setUse_type_title(String use_type_title) {
        this.use_type_title = use_type_title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public long getUse_time() {
        return use_time;
    }

    public void setUse_time(long use_time) {
        this.use_time = use_time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getSend_time() {
        return send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public long getUse_start_time() {
        return use_start_time;
    }

    public void setUse_start_time(long use_start_time) {
        this.use_start_time = use_start_time;
    }

    public long getUse_end_time() {
        return use_end_time;
    }

    public void setUse_end_time(long use_end_time) {
        this.use_end_time = use_end_time;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
