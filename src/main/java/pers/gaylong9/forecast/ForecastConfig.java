package pers.gaylong9.forecast;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import pers.gaylong9.MyPluginConfig;

import java.util.TimeZone;

public class ForecastConfig {

    public static Bot bot;

    private static final MiraiLogger logger = MiraiLogger.Factory.INSTANCE.create(ForecastConfig.class);

    public static void forecast(@NotNull Bot bot) throws SchedulerException {
        ForecastConfig.bot = bot;
        Value<String> tomorrowCronValue = MyPluginConfig.INSTANCE.tomorrowCron;
        String tomorrowCron = tomorrowCronValue.get();
        Value<String> todayCronValue = MyPluginConfig.INSTANCE.todayCron;
        String todayCron = todayCronValue.get();
        Value<String> timezoneValue = MyPluginConfig.INSTANCE.timezone;
        String timezone = timezoneValue.get();

        // 1、创建调度器Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        // 2、创建JobDetail实例，并与Job类绑定(Job执行内容)
        JobDetail tomorrowJob = JobBuilder.newJob(TomorrowForecastJob.class)
                .withIdentity("tomorrowJob", "group").build();

        // 3、构建Trigger实例
        Trigger tomorrowTrigger = TriggerBuilder.newTrigger().withIdentity("tomorrowTrigger", "triggerGroup")
                .startNow() //立即生效
                .withSchedule(CronScheduleBuilder.cronSchedule(tomorrowCron)
                        .inTimeZone(TimeZone.getTimeZone(timezone))
                ).build();

        // 4、Scheduler绑定Job和Trigger，并执行
        scheduler.scheduleJob(tomorrowJob, tomorrowTrigger);
        logger.info("已创建预报明日天气的任务");

        // 若今日的cron不空，则设置今日预报
        if (!todayCron.isEmpty()) {
            JobDetail todayJob = JobBuilder.newJob(TodayForecastJob.class)
                    .withIdentity("todayJob", "group").build();
            Trigger todayTrigger = TriggerBuilder.newTrigger().withIdentity("todayTrigger", "triggerGroup")
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(todayCron)
                            .inTimeZone(TimeZone.getTimeZone(timezone))
                    ).build();
            scheduler.scheduleJob(todayJob, todayTrigger);
            logger.info("已创建预报今日天气的任务");
        }

        scheduler.start();
    }
}
