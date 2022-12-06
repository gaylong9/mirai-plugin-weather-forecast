package pers.gaylong9.command;


import net.mamoe.mirai.console.command.*;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.permission.Permission;
import net.mamoe.mirai.console.permission.PermissionImplementation;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pers.gaylong9.MyPluginConfig;
import pers.gaylong9.MyPluginData;
import pers.gaylong9.WeatherForecastPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SubscribeCommand extends JSimpleCommand {

    private static final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(SubscribeCommand.class);

    public static final SubscribeCommand INSTANCE = new SubscribeCommand();

    private static final MyPluginData myPluginData = MyPluginData.INSTANCE;
    private static final MyPluginConfig myPluginConfig = MyPluginConfig.INSTANCE;

    private SubscribeCommand() {
        super(WeatherForecastPlugin.INSTANCE, "weather-forecast-subscribe", "预订天气预报");
        super.setDescription("聊天窗口订阅天气预报");
    }

    @Handler
    public void onCommand(UserCommandSender sender, @Name("城市名") String city) {
        if (sender instanceof FriendCommandSender) {
            // 好友订阅
            long id = sender.getUser().getId();
            Value<Map<Long, List<String>>> friendSubscribes = myPluginData.friendSubscribes;
            Map<Long, List<String>> subscribes = friendSubscribes.get();
            boolean subscribeSucc = subscribe(sender, id, city, subscribes);
        } else if (sender instanceof MemberCommandSender) {
            // 群组订阅
            long id = ((MemberCommandSender) sender).getGroup().getId();
            Value<Map<Long, List<String>>> groupSubscribes = myPluginData.groupSubscribes;
            Map<Long, List<String>> subscribes = groupSubscribes.get();
            boolean subscribeSucc = subscribe(sender, id, city, subscribes);
        } else {
            // 其余订阅
            sender.sendMessage("仅支持好友和群组内使用此命令");
        }
    }

    private boolean subscribe(CommandSender sender, Long id, String city, Map<Long, List<String>> subscribes) {
        // 若该用户/群聊从未订阅过，则增加其信息，统一后续操作
        if (!subscribes.containsKey(id)) {
            subscribes.put(id, new ArrayList<>());
        }

        // 若该用户已订阅过指定城市
        if (subscribes.get(id).contains(city)) {
            sender.sendMessage("已订阅过该城市");
            return false;
        }

        // 若未订阅过该城市，先判断城市是否合法，再加入数据
        boolean isLegal = verify(city);
        if (!isLegal) {
            sender.sendMessage("请核对城市名是否合法，并稍后再试");
            return false;
        }
        subscribes.get(id).add(city);
        sender.sendMessage("成功订阅" + city + "天气");
        return true;
    }

    /**用指定城市发送一条请求，验证城市名是否合法*/
    private boolean verify(String city) {
        Value<String> urlValue =  MyPluginConfig.INSTANCE.url;
        String url = urlValue.get();
        Value<String> appidValue = MyPluginConfig.INSTANCE.appid;
        String appid = appidValue.get();
        Value<String> appsecretValue = MyPluginConfig.INSTANCE.appsecret;
        String appsecret = appsecretValue.get();
        try {
            Document document = Jsoup.connect(url
                            + "?unescape=1&appid=" + appid
                            + "&appsecret=" + appsecret
                            + "&city=" + city)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .ignoreContentType(true)
                    .timeout(3000)
                    .get();
            String json = document.body().text();
            return !json.contains("errcode") && (!json.contains("北京") || city.equals("北京"));
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
