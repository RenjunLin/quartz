package com.jerry.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloScheduler {

    public static void main(String[] args) throws SchedulerException {
        // 创建一个JobDetail实例，将该实例与HelloJob Class绑定
        JobDetail jobDetail = JobBuilder
                .newJob(HelloJob.class)
                .withIdentity("myJob", "group1")
                .usingJobData("message", "hello myjob message")
                .usingJobData("floatJobValue", 3.14F)            //传入自定义参数
                .build();


        // 创建一个Trigger实，触发Job执行
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("myTrigger", "group1")
                .usingJobData("message", "hello my trigger message")
                .usingJobData("doubleTriggerValue", 2.0D)       //传入自定义参数
                .startNow()
                .withSchedule(simpleScheduleBuilder)
                .build();


        // 创建Schedule实例
        SchedulerFactory sFact = new StdSchedulerFactory();
        Scheduler scheduler = sFact.getScheduler();
        scheduler.start();

        scheduler.scheduleJob(jobDetail, trigger);
    }

}
