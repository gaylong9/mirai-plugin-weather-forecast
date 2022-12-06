package pers.gaylong9.command;

import net.mamoe.mirai.console.command.FriendCommandSender;
import net.mamoe.mirai.console.command.MemberCommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.utils.MiraiLogger;
import pers.gaylong9.MyPluginConfig;
import pers.gaylong9.MyPluginData;
import pers.gaylong9.WeatherForecastPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnsubscribeCommand extends JSimpleCommand {

    private static final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(UnsubscribeCommand.class);

    public static final UnsubscribeCommand INSTANCE = new UnsubscribeCommand();

    private static final MyPluginData myPluginData = MyPluginData.INSTANCE;
    private static final MyPluginConfig myPluginConfig = MyPluginConfig.INSTANCE;

    private UnsubscribeCommand() {
        super(WeatherForecastPlugin.INSTANCE, "weather-forecast-unsubscribe", "退订天气预报");
        super.setDescription("聊天窗口退订天气预报");
    }

    @Handler
    public void onCommand(UserCommandSender sender, @Name("城市名") String city) {
        if (sender instanceof FriendCommandSender) {
            // 好友订阅
            friendUnsubscribe((FriendCommandSender) sender, city);
        } else if (sender instanceof MemberCommandSender) {
            // 群组订阅
            groupUnsubscribe((MemberCommandSender) sender, city);
        } else {
            // 其余订阅
            sender.sendMessage("仅支持好友和群组内使用此命令");
        }
    }

    private void groupUnsubscribe(MemberCommandSender sender, String city) {
        long id = sender.getGroup().getId();
        Value<Map<Long, List<String>>> groupSubscribes = myPluginData.groupSubscribes;
        Map<Long, List<String>> subscribes = groupSubscribes.get();
        if (subscribes.containsKey(id)) {
            // 如果该群聊之前已经已经订阅过
            if (subscribes.get(id).contains(city)) {
                // 订阅过指定城市，可以退订
                subscribes.get(id).remove(city);
                sender.sendMessage("成功退订" + city + "天气");
                return;
            }
        }

        // 若该群聊之前尚未订阅过指定城市
        sender.sendMessage("尚未订阅过" + city + "天气");
    }

    private void friendUnsubscribe(FriendCommandSender sender, String city) {
        long id = sender.getUser().getId();
        Value<Map<Long, List<String>>> friendSubscribes = myPluginData.friendSubscribes;
        Map<Long, List<String>> subscribes = friendSubscribes.get();
        if (subscribes.containsKey(id)) {
            // 如果该用户之前已经已经订阅过
            if (subscribes.get(id).contains(city)) {
                // 订阅过指定城市，可以退订
                subscribes.get(id).remove(city);
                sender.sendMessage("成功退订" + city + "天气");
                return;
            }
        }

        // 若该用户之前尚未订阅过指定城市
        sender.sendMessage("尚未订阅过" + city + "天气");
    }


}
