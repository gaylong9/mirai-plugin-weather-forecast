package pers.gaylong9;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.data.PluginDataStorage;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.permission.Permission;
import net.mamoe.mirai.console.permission.PermissionId;
import net.mamoe.mirai.console.permission.PermissionService;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import pers.gaylong9.command.SubscribeCommand;
import pers.gaylong9.command.UnsubscribeCommand;
import pers.gaylong9.forecast.Forecast;
import pers.gaylong9.util.Util;


public final class WeatherForecastPlugin extends JavaPlugin {
    public static final WeatherForecastPlugin INSTANCE = new WeatherForecastPlugin();
    private final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(WeatherForecastPlugin.class);

    private WeatherForecastPlugin() {
        super(new JvmPluginDescriptionBuilder("pers.gaylong9.weather_forecast", "1.0.0")
                .name("WeatherForecastPlugin")
                .author("gaylong9")
                .build());
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        // 设置与数据更新
        this.reloadPluginData(MyPluginData.INSTANCE);
        this.reloadPluginConfig(MyPluginConfig.INSTANCE);
        Util.reloadPluginData();
        WeatherForecastPlugin.INSTANCE.savePluginConfig(MyPluginConfig.INSTANCE);
        WeatherForecastPlugin.INSTANCE.savePluginData(MyPluginData.INSTANCE);
    }

    @Override
    public void onEnable() {

        // 注册命令
        CommandManager.INSTANCE.registerCommand(SubscribeCommand.INSTANCE, false);
        CommandManager.INSTANCE.registerCommand(UnsubscribeCommand.INSTANCE, false);

        getLogger().info("Weather Forecast Plugin loaded!");

        // Bot登录后执行
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            try {
                Forecast.forecast(event.getBot());
                logger.info("天气预报定时任务已启动");
            } catch (SchedulerException e) {
                logger.error(e.getMessage());
            }
        });
    }

    @Override
    public void onDisable() {
        WeatherForecastPlugin.INSTANCE.savePluginConfig(MyPluginConfig.INSTANCE);
        WeatherForecastPlugin.INSTANCE.savePluginData(MyPluginData.INSTANCE);
    }
}