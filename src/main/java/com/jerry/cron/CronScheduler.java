package com.jerry.cron;

import com.jerry.quartz.HelloJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.MutableTrigger;

public class CronScheduler {
    public static void main(String[] args) throws SchedulerException {

        // 创建一个JobDetail实例，将该实例与HelloJob Class绑定
        JobDetail jobDetail = JobBuilder
                .newJob(CronJob.class)
                .withIdentity("myJob", "group1")
                .build();


        // 创建一个Trigger实，触发Job执行
        // 2017年内每天10点15分触发一次
        // 0 15 10 ? * * 2017
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 15 10 ? * * 2017");
        CronTrigger trigger = (CronTrigger) TriggerBuilder
                .newTrigger()
                .withIdentity("myTrigger", "group1")
                .withSchedule(cronScheduleBuilder)
                .build();

        // 创建Schedule实例
        SchedulerFactory sFact = new StdSchedulerFactory();
        Scheduler scheduler = sFact.getScheduler();
        scheduler.start();

        scheduler.scheduleJob(jobDetail, trigger);

    }
}
