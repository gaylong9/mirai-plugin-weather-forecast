package pers.gaylong9.forecast;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.data.Value;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import pers.gaylong9.MyPluginConfig;

import java.util.TimeZone;

public class Forecast {

    public static Bot bot;

    public static void forecast(@NotNull Bot bot) throws SchedulerException {
        Forecast.bot = bot;
        Value<String> cronValue = MyPluginConfig.INSTANCE.cron;
        String cron = cronValue.get();
        Value<String> timezoneValue = MyPluginConfig.INSTANCE.timezone;
        String timezone = timezoneValue.get();

        // 1、创建调度器Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        // 2、创建JobDetail实例，并与Job类绑定(Job执行内容)
        JobDetail jobDetail = JobBuilder.newJob(ForecastJob.class)
                .withIdentity("job", "group").build();
        // 3、构建Trigger实例
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", "triggerGroup")
                .startNow() //立即生效
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)
                        .inTimeZone(TimeZone.getTimeZone(timezone))
                ).build();

        // 4、Scheduler绑定Job和Trigger，并执行
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }
}
