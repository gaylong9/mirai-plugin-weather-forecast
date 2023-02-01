package pers.gaylong9.command;


import net.mamoe.mirai.console.command.*;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pers.gaylong9.MyPluginConfig;
import pers.gaylong9.MyPluginData;
import pers.gaylong9.WeatherForecastPlugin;
import pers.gaylong9.forecast.ForecastInfo;
import pers.gaylong9.util.RequestParamType;
import pers.gaylong9.util.Util;

import java.io.IOException;
import java.util.*;

import static pers.gaylong9.util.RequestParamType.CITY;
import static pers.gaylong9.util.RequestParamType.CITYID;
import static pers.gaylong9.util.Util.*;
import static pers.gaylong9.util.Util.appsecret;


public class SubscribeCommand extends JSimpleCommand {

    private static final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(SubscribeCommand.class);

    public static final SubscribeCommand INSTANCE = new SubscribeCommand();

    private static final MyPluginData MY_PLUGIN_DATA = MyPluginData.INSTANCE;

    private SubscribeCommand() {
        super(WeatherForecastPlugin.INSTANCE, "weather-forecast-subscribe", "预订天气预报");
        super.setDescription("聊天窗口订阅天气预报");
    }

    @Handler
    public void onCommand(UserCommandSender sender, @Name("城市名/城市编号") String city) {

        // 仅支持好友和群组内使用此命令
        if (!(sender instanceof FriendCommandSender) && !(sender instanceof MemberCommandSender)) {
            sender.sendMessage("仅支持好友和群组内使用此命令");
            return;
        }

        // 检查配置文件中是否设置了API接口信息
        if (appid == null || appid.isEmpty() || appsecret == null || appsecret.isEmpty()) {
            sender.sendMessage("配置文件中尚未设置API接口信息");
            return;
        }

        // 如果是重名地区，应改用城市id订阅
        Value<Set<String>> duplicateRegionsValue = MY_PLUGIN_DATA.duplicateRegions;
        Set<String> duplicateRegions = duplicateRegionsValue.get();
        if (duplicateRegions.contains(city)) {
            sender.sendMessage(city + " 存在重名地区，请改用城市编号订阅\n" +
                    "城市编号表：https://github.com/gaylong9/mirai-plugin-weather-forecast/blob/main/city.txt");
            return;
        }

        // 参数校验，获得城市id
        ForecastInfo info;
        if (city.matches("\\d{9}")) {
            // 若参数是9位纯数字，则符合编号格式
            info = request(CITYID, city);
        } else {
            // 否则当做城市名
            info = request(CITY, city);
        }
        if (info == null) {
            sender.sendMessage("请核对城市名是否合法，并稍后再试\n" +
                    "城市编号表：https://github.com/gaylong9/mirai-plugin-weather-forecast/blob/main/city.txt");
            return;
        }

        if (sender instanceof FriendCommandSender) {
            // 好友订阅
            long id = sender.getUser().getId();
            Value<Map<Long, Map<String, String>>> friendSubscribes = MY_PLUGIN_DATA.friendSubscribes;
            Map<Long, Map<String, String>> subscribes = friendSubscribes.get();
            subscribe(sender, id, info, subscribes);
        } else if (sender instanceof MemberCommandSender) {
            // 群组订阅
            long id = ((MemberCommandSender) sender).getGroup().getId();
            Value<Map<Long, Map<String, String>>> groupSubscribes = MY_PLUGIN_DATA.groupSubscribes;
            Map<Long, Map<String, String>> subscribes = groupSubscribes.get();
            subscribe(sender, id, info, subscribes);
        }
    }

    private boolean subscribe(CommandSender sender, Long userId, ForecastInfo info, Map<Long, Map<String, String>> subscribes) {
        // 若该用户/群聊从未订阅过，则增加其信息，统一后续操作
        if (!subscribes.containsKey(userId)) {
            subscribes.put(userId, new HashMap<>(8));
        }

        // 若该用户已订阅过指定城市
        Map<String, String> subscribedCities = subscribes.get(userId);
        if (subscribedCities.containsKey(info.cityid)) {
            sender.sendMessage("已订阅过该城市");
            return false;
        }

        // 若未订阅过该城市，加入数据
        subscribedCities.put(info.cityid, info.city);
        sender.sendMessage("成功订阅" + info.city + "(" + info.cityid + ")天气");
        return true;
    }

}
