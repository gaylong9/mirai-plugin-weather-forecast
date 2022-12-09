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

import static pers.gaylong9.util.RequestParamType.CITYID;
import static pers.gaylong9.util.Util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pers.gaylong9.util.Util.GSON;

/**Quartz的具体任务类，在此完成数据请求与消息发送*/
public class ForecastJob implements Job {

    private final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(ForecastJob.class);

    private static final String ICON_PATH = WeatherForecastPlugin.INSTANCE.getDataFolderPath() + "/wea_img/";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 校验配置中是否有api信息
        if (appid == null || appid.isEmpty() || appsecret == null || appsecret.isEmpty()) {
            logger.warning("尚未配置天气API的用户信息 https://www.tianqiapi.com/index/doc?version=week");
            return;
        }

        Value<Map<Long, Map<String, String>>> groupSubscribesValue = MyPluginData.INSTANCE.groupSubscribes;
        Map<Long, Map<String, String>> groupSubscribes = groupSubscribesValue.get();

        // 给订阅的群聊和好友发送天气预报消息
        for (Map.Entry<Long, Map<String, String>> entry : groupSubscribes.entrySet()) {
            Long groupId = entry.getKey();
            Map<String, String> subscribedCities = entry.getValue();
            for (String cityId : subscribedCities.keySet()) {
                sendMsg(Forecast.bot.getGroupOrFail(groupId), cityId);
            }
        }

        Value<Map<Long, Map<String, String>>> friendSubscribesValue = MyPluginData.INSTANCE.friendSubscribes;
        Map<Long, Map<String, String>> friendSubscribes = friendSubscribesValue.get();

        for (Map.Entry<Long, Map<String, String>> entry : friendSubscribes.entrySet()) {
            Long friendId = entry.getKey();
            Map<String, String> subscribedCities = entry.getValue();
            for (String cityId : subscribedCities.keySet()) {
                sendMsg(Forecast.bot.getFriendOrFail(friendId), cityId);
            }
        }

    }

    private void sendMsg(Contact contact, String city) {

        ForecastInfo info = request(CITYID, city);
        if (info == null) {
            contact.sendMessage("天气获取失败，请稍后再试");
            return;
        }

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
            chianBuilder.add("\n");
        }
        // 晴，-3 ~ 8℃，南风 <3级
        sb.delete(0, sb.length());
        sb.append(tomorrow.wea).append("，")
                .append(tomorrow.tem_night).append(" ~ ").append(tomorrow.tem_day).append("\u2103").append("，")
                .append(tomorrow.win).append(" ").append(tomorrow.win_speed);
        chianBuilder.add(sb.toString());

        contact.sendMessage(chianBuilder.build());
    }
}
