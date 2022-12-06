package pers.gaylong9.forecast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.SchedulerException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pers.gaylong9.MyPluginConfig;
import pers.gaylong9.MyPluginData;
import pers.gaylong9.WeatherForecastPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**Quartz的具体任务类，在此完成数据请求与消息发送*/
public class ForecastJob implements Job {

    private final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(ForecastJob.class);

    // private static final String ICON_PATH = System.getProperty("user.dir") + "/data/pers.gaylong9.WeatherForecastPlugin/wea_img/";
    private static final String ICON_PATH = WeatherForecastPlugin.INSTANCE.getDataFolderPath() + "/wea_img/";

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setLenient()
            .create();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 校验配置中是否有api信息
        Value<String> appidValue = MyPluginConfig.INSTANCE.appid;
        String appid = appidValue.get();
        Value<String> appsecretValue = MyPluginConfig.INSTANCE.appsecret;
        String appsecret = appsecretValue.get();
        if (appid == null || appid.isEmpty() || appsecret == null || appsecret.isEmpty()) {
            logger.info("尚未配置天气api的用户信息 https://www.tianqiapi.com/index/doc?version=week");
            return;
        }

        Value<String> urlValue = MyPluginConfig.INSTANCE.url;
        String url = urlValue.get();

        Value<Map<Long, List<String>>> groupSubscribesValue = MyPluginData.INSTANCE.groupSubscribes;
        Map<Long, List<String>> groupSubscribes = groupSubscribesValue.get();

        // 给订阅的群聊和好友发送天气预报消息
        for (Map.Entry<Long, List<String>> entry : groupSubscribes.entrySet()) {
            Long groupId = entry.getKey();
            List<String> cities = entry.getValue();
            for (String city : cities) {
                sendMsg(Forecast.bot.getGroupOrFail(groupId), city, url, appid, appsecret);
            }
        }

        Value<Map<Long, List<String>>> friendSubscribesValue = MyPluginData.INSTANCE.friendSubscribes;
        Map<Long, List<String>> friendSubscribes = friendSubscribesValue.get();

        for (Map.Entry<Long, List<String>> entry : friendSubscribes.entrySet()) {
            Long friendId = entry.getKey();
            List<String> cities = entry.getValue();
            for (String city : cities) {
                sendMsg(Forecast.bot.getFriendOrFail(friendId), city, url, appid, appsecret);
            }
        }

    }

    private void sendMsg(Contact contact, String city, String url, String appid, String appsecret) {
        Document document = null;
        try {
            document = Jsoup.connect(url
                            + "?unescape=1&appid=" + appid
                            + "&appsecret=" + appsecret
                            + "&city=" + city)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .ignoreContentType(true)
                    .timeout(3000)
                    .get();
            String json = document.body().text();


            ForecastInfo info = GSON.fromJson(json, ForecastInfo.class);
            DayInfo tomorrow = info.data.get(1);

            StringBuilder sb = new StringBuilder();
            /*
             明天(yyyy-mm-dd)北京天气：\n
             */
            sb.append("明天(").append(tomorrow.date).append(")")
                    .append(info.city).append("天气：\n");
            MessageChainBuilder chianBuilder = new MessageChainBuilder();
            chianBuilder.add(sb.toString());
            // 天气图标\n
            File img = new File(ICON_PATH + tomorrow.wea_img + ".png");
            if (img.exists()) {
                Image icon = Contact.uploadImage(contact, img);
                logger.info("icon is upload: " + Image.isUploaded(icon, Forecast.bot));
                chianBuilder.add(icon);
            }
            chianBuilder.add("\n");
            // 晴，-3 ~ 8℃，南风 <3级
            sb.delete(0, sb.length());
            sb.append(tomorrow.wea).append("，")
                    .append(tomorrow.tem_night).append(" ~ ").append(tomorrow.tem_day).append("\u2103").append("，")
                    .append(tomorrow.win).append(" ").append(tomorrow.win_speed);
            chianBuilder.add(sb.toString());

            contact.sendMessage(chianBuilder.build());
        } catch (IOException e) {
            logger.error("天气API请求失败");
            logger.error(e.getMessage());
        }
    }
}
