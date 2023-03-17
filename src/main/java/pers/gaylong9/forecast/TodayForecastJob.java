package pers.gaylong9.forecast;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pers.gaylong9.MyPluginData;
import pers.gaylong9.WeatherForecastPlugin;

import java.io.File;
import java.util.Map;

import static pers.gaylong9.util.RequestParamType.CITYID;
import static pers.gaylong9.util.Util.*;

/**Quartz的具体任务类，在此完成今日天气的数据请求与消息发送*/
public class TodayForecastJob extends ForecastJob implements Job {
    private final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(TodayForecastJob.class);

    private static final String ICON_PATH = WeatherForecastPlugin.INSTANCE.getDataFolderPath() + "/wea_img/";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        forecast();
    }

    /**
     * 发送今日消息
     */
    @Override
    void sendMsg(Contact contact, String city) {

        ForecastInfo info = request(CITYID, city);
        if (info == null) {
            contact.sendMessage(city + "天气获取失败，请稍后再试");
            return;
        }

        DayInfo today = info.data.get(0);

        /*
        预报消息格式如下：

        北京天气：
        今天(yyyy-mm-dd)：
        (天气图标img)
        晴，-3 ~ 8℃，南风 <3级
        */
        StringBuilder sb = new StringBuilder();
        // 北京天气：
        sb.append(info.city).append("天气：\n");
        // 今天(yyyy-mm-dd)：
        sb.append("今天(").append(today.date).append(")：\n");

        MessageChainBuilder chianBuilder = new MessageChainBuilder();
        chianBuilder.add(sb.toString());

        // 天气图标\n
        File img = new File(ICON_PATH + today.wea_img + ".png");
        if (img.exists()) {
            Image icon = Contact.uploadImage(contact, img);
            // logger.info("icon is upload: " + Image.isUploaded(icon, Forecast.bot));
            chianBuilder.add(icon);
            chianBuilder.add("\n");
        }
        // 晴，-3 ~ 8℃，南风 <3级
        sb.delete(0, sb.length());
        sb.append(today.wea).append("，")
                .append(today.tem_night).append(" ~ ").append(today.tem_day).append("\u2103").append("，")
                .append(today.win).append(" ").append(today.win_speed);
        chianBuilder.add(sb.toString());

        contact.sendMessage(chianBuilder.build());
    }
}
