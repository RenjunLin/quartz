package com.jerry.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("all")
public class DelayScheduler {

    public static void main(String[] args) throws SchedulerException {

        JobDetail jobDetail = JobBuilder
                .newJob(HelloJob.class)
                .withIdentity("myJob", "group1")
                .usingJobData("message", "hello myjob message")
                .usingJobData("floatJobValue", 3.14F)            //传入自定义参数
                .build();

        Date startDate = new Date();
        startDate.setTime(startDate.getTime() + 3000);
        Date endDate = new Date();
        endDate.setTime(endDate.getTime() + 6000);
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("myTrigger", "group1")
                .usingJobData("message", "hello my trigger message")
                .usingJobData("doubleTriggerValue", 2.0D)       //传入自定义参数
                .startAt(startDate)
                .endAt(endDate)
                .withSchedule(simpleScheduleBuilder)
                .build();

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Start Time Is: " + sf.format(startDate));
        System.out.println("End Time Is: " + sf.format(endDate));

        // 创建Schedule实例
        SchedulerFactory sFact = new StdSchedulerFactory();
        Scheduler scheduler = sFact.getScheduler();
        scheduler.start();

        scheduler.scheduleJob(jobDetail, trigger);

    }

}
