package pers.gaylong9.forecast;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.utils.MiraiLogger;
import pers.gaylong9.MyPluginData;

import java.util.Map;

import static pers.gaylong9.util.Util.appid;
import static pers.gaylong9.util.Util.appsecret;

/**
 * 预报接口
 * 定义并实现了了预报流程forecast
 * 定义了发送信息方法sendMsg
 */
public abstract class ForecastJob {

    MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(ForecastJob.class);

    /**
     * 预报发送流程
     * 先校验配置，后向所有已订阅的用户发送预报消息
     */
    void forecast() {
        // 校验配置中是否有api信息
        if (appid == null || appid.isEmpty() || appsecret == null || appsecret.isEmpty()) {
            logger.warning("尚未配置天气API的用户信息 https://www.tianqiapi.com/index/doc?version=week");
            return;
        }

        // 给订阅的群聊和好友发送天气预报消息
        Value<Map<Long, Map<String, String>>> groupSubscribesValue = MyPluginData.INSTANCE.groupSubscribes;
        Map<Long, Map<String, String>> groupSubscribes = groupSubscribesValue.get();

        for (Map.Entry<Long, Map<String, String>> entry : groupSubscribes.entrySet()) {
            Long groupId = entry.getKey();
            Map<String, String> subscribedCities = entry.getValue();
            for (String cityId : subscribedCities.keySet()) {
                sendMsg(ForecastConfig.bot.getGroupOrFail(groupId), cityId);
            }
        }

        Value<Map<Long, Map<String, String>>> friendSubscribesValue = MyPluginData.INSTANCE.friendSubscribes;
        Map<Long, Map<String, String>> friendSubscribes = friendSubscribesValue.get();

        for (Map.Entry<Long, Map<String, String>> entry : friendSubscribes.entrySet()) {
            Long friendId = entry.getKey();
            Map<String, String> subscribedCities = entry.getValue();
            for (String cityId : subscribedCities.keySet()) {
                sendMsg(ForecastConfig.bot.getFriendOrFail(friendId), cityId);
            }
        }
    }

    /**
     *  发送消息由sendMsg方法实现，具体类的发送方式不同，有的发送今日的，有的发送明日的
     */
    abstract void sendMsg(Contact contact, String city);

}
