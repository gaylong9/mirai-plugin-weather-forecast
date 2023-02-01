package pers.gaylong9.command;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.FriendCommandSender;
import net.mamoe.mirai.console.command.MemberCommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;
import pers.gaylong9.MyPluginConfig;
import pers.gaylong9.MyPluginData;
import pers.gaylong9.WeatherForecastPlugin;

import java.util.List;
import java.util.Map;

public class UnsubscribeCommand extends JSimpleCommand {

    private static final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(UnsubscribeCommand.class);

    public static final UnsubscribeCommand INSTANCE = new UnsubscribeCommand();

    private static final MyPluginData MY_PLUGIN_DATA = MyPluginData.INSTANCE;

    private UnsubscribeCommand() {
        super(WeatherForecastPlugin.INSTANCE, "weather-forecast-unsubscribe", "退订天气预报");
        super.setDescription("聊天窗口退订天气预报");
    }

    @Handler
    public void onCommand(UserCommandSender sender, @Name("城市名/城市编号") String city) {
        if (sender instanceof FriendCommandSender) {
            // 好友订阅
            long userId = sender.getUser().getId();
            Value<Map<Long, Map<String, String>>> subscribes = MY_PLUGIN_DATA.friendSubscribes;
            unsubscribe(sender, userId, city, subscribes.get());
        } else if (sender instanceof MemberCommandSender) {
            // 群组订阅
            Value<Map<Long, Map<String, String>>> subscribes = MY_PLUGIN_DATA.groupSubscribes;
            long groupId = ((MemberCommandSender) sender).getGroup().getId();
            unsubscribe(sender, groupId, city, subscribes.get());
        } else {
            // 其余订阅
            sender.sendMessage("仅支持好友和群组内使用此命令");
        }
    }

    private void unsubscribe(CommandSender sender, long userId, String param, Map<Long, Map<String, String>> subscribes) {
        // 若之前尚未订阅过任意城市，提示并结束
        if (!subscribes.containsKey(userId)) {
            sender.sendMessage("尚未订阅过" + param + "天气");
            return;
        }

        Map<String, String> subscribedCities = subscribes.get(userId);

        // 若参数是城市编号
        if (param.matches("\\d{9}")) {
            if (subscribedCities.containsKey(param)) {
                String cityName = subscribedCities.remove(param);
                sender.sendMessage("成功退订" + cityName + "(" + param + ")天气");
            } else {
                sender.sendMessage("尚未订阅过" + param + "天气");
            }
            return;
        }

        // 若参数是城市名
        // 统计本群订阅信息中该城市名个数
        int cnt = 0;
        String cityId = null;
        StringBuilder sb = new StringBuilder();
        sb.append("已订阅的城市中包含多个").append(param).append("：\n");
        for (Map.Entry<String, String> entry : subscribedCities.entrySet()) {
            if (entry.getValue().equals(param)) {
                cnt++;
                cityId = entry.getKey();
                sb.append(cityId).append("\n");
            }
        }
        if (cnt == 0) {
            sender.sendMessage("尚未订阅过" + param + "天气");
        } else if (cnt == 1) {
            String cityName = subscribedCities.remove(cityId);
            sender.sendMessage("成功退订" + cityName + "(" + cityId + ")天气");
        } else {
            sb.append("请改用城市编号退订\n");
            sb.append("城市编号表：https://github.com/gaylong9/mirai-plugin-weather-forecast/blob/main/city.txt");
            sender.sendMessage(sb.toString());
        }
    }
}
