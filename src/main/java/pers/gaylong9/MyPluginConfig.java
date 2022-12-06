package pers.gaylong9;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginConfig;
import org.jetbrains.annotations.NotNull;

public class MyPluginConfig extends JavaAutoSavePluginConfig {

    public static final MyPluginConfig INSTANCE = new MyPluginConfig();

    private MyPluginConfig() {
        // 文件名 xxx.yml
        super("pers.gaylong9.WeatherForecastPlugin.config");
    }

    /**天气API请求地址，不建议修改*/
    // private String url = "https://v0.yiketianqi.com/free/week";
    public final Value<String> url = value("url", "https://v0.yiketianqi.com/free/week");

    /**天气api的用户id*/
    // private String appid = "";
    public final Value<String> appid = value("appid", "");

    /**天气api的用户*/
    // private String appsecret = "";
    public final Value<String> appsecret = value("appsecret", "");

    /**cron表达式，默认每天晚上22点*/
    // private String cron = "0 0 22 ? * SUN-SAT *";
    public final Value<String> cron = value("cron", "0 0 22 ? * SUN-SAT *");

    /**时区，默认北京时间*/
    // private String timezone = "Asia/Shanghai";
    public final Value<String> timezone = value("timezone", "Asia/Shanghai");

//    public String getCron() {
//        return cron;
//    }
//
//    public void setCron(String cron) {
//        this.cron = cron;
//    }
//
//    public String getTimezone() {
//        return timezone;
//    }
//
//    public void setTimezone(String timezone) {
//        this.timezone = timezone;
//    }
//
//
//    public String getAppid() {
//        return appid;
//    }
//
//    public void setAppid(String appid) {
//        this.appid = appid;
//    }
//
//    public String getAppsecret() {
//        return appsecret;
//    }
//
//    public void setAppsecret(String appsecret) {
//        this.appsecret = appsecret;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
}
