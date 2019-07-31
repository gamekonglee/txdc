package bc.yxdc.com.bean;

/**
 * Created by gamekonglee on 2018/10/8.
 */

public class LoginResult {
    public String code;
    public String state;
    public String lang;
    public String country;

    public LoginResult(String code, String state, String lang, String country) {
        this.code = code;
        this.state = state;
        this.lang = lang;
        this.country = country;
    }
}
