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
    public final Value<String> url = value("url", "https://v0.yiketianqi.com/free/week");

    /**天气api的用户id*/
    public final Value<String> appid = value("appid", "");

    /**天气api的用户*/
    public final Value<String> appsecret = value("appsecret", "");

    /**cron表达式，默认每天晚上22点*/
    public final Value<String> cron = value("cron", "0 0 22 ? * SUN-SAT *");

    /**时区，默认北京时间*/
    public final Value<String> timezone = value("timezone", "Asia/Shanghai");

}
