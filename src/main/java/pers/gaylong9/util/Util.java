package pers.gaylong9.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pers.gaylong9.MyPluginConfig;
import pers.gaylong9.command.SubscribeCommand;
import pers.gaylong9.forecast.ForecastInfo;

import java.io.IOException;

public class Util {

    private static final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(Util.class);

    public static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setLenient()
            .create();

    public static String appid, appsecret, url;

    /**重新获取PluginData中的相关数据*/
    public static void reloadPluginData() {
        Value<String> urlValue = MyPluginConfig.INSTANCE.url;
        url = urlValue.get();
        Value<String> appidValue = MyPluginConfig.INSTANCE.appid;
        appid = appidValue.get();
        Value<String> appsecretValue = MyPluginConfig.INSTANCE.appsecret;
        appsecret = appsecretValue.get();
    }

    /**发送请求获取天气，参数正确时返回ForecastInfo对象，若请求失败返回null*/
    public static ForecastInfo request(RequestParamType type, String param) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?unescape=1&appid=").append(appid).append("&appsecret=").append(appsecret);
        if (type == RequestParamType.CITY) {
            sb.append("&city=").append(param);
        } else {
            sb.append("&cityid=").append(param);
        }
        try {
            Document document = Jsoup.connect(sb.toString())
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .ignoreContentType(true)
                    .timeout(3000)
                    .get();
            String json = document.body().text();
            if (json.contains("errcode")) {
                return null;
            }
            ForecastInfo info = GSON.fromJson(json, ForecastInfo.class);
            // 该网站接口，传入错误参数时有时会返回北京天气信息，需排除
            if (info.city.equals("北京")) {
                if (!"101010100".equals(param) && !"北京".equals(param)) {
                    return null;
                }
            }
            return info;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
