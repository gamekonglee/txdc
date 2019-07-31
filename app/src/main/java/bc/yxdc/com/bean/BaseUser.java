package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/9/18.
 */

public class BaseUser {

    int status;
    String msg;
    User result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getResult() {
        return result;
    }

    public void setResult(User result) {
        this.result = result;
    }
}
