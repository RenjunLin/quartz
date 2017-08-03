package com.jerry.quartz;

import org.quartz.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloJob implements Job {

    //成员变量的名字必须和自定义参数的Key值保持一致
    private String message;
    private Float floatJobValue;
    private Double doubleTriggerValue;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Float getFloatJobValue() {
        return floatJobValue;
    }

    public void setFloatJobValue(Float floatJobValue) {
        this.floatJobValue = floatJobValue;
    }

    public Double getDoubleTriggerValue() {
        return doubleTriggerValue;
    }

    public void setDoubleTriggerValue(Double doubleTriggerValue) {
        this.doubleTriggerValue = doubleTriggerValue;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //打印当前的执行时间
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("HelloJob Current Exec Time Is: " + sf.format(date));

        //打印JobDetail信息
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        System.out.println("JobDetail name: " + jobKey.getName());
        System.out.println("JobDetail group: " + jobKey.getGroup());
        System.out.println("JobDetail class: " + jobKey.getClass());

        //打印trigger信息
        TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();
        System.out.println("My Trigger name: " + triggerKey.getName());
        System.out.println("My Trigger group: " + triggerKey.getGroup());

        System.out.println("=========");

        //获取自定义参数信息, 方法一：显示获取
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String jobMessage = dataMap.getString("message");
        Float floatValue = dataMap.getFloat("floatJobValue");
        System.out.println("Job Message: " + jobMessage + " float value: " + floatValue.toString());
        JobDataMap triggerDataMap = jobExecutionContext.getTrigger().getJobDataMap();
        String triggerMessage = triggerDataMap.getString("message");
        Double doubleValue = triggerDataMap.getDouble("doubleTriggerValue");
        System.out.println("Trigger Message: " + triggerMessage + " trigger double value: " + doubleValue.toString());

        //获取JobDetail和Trigger中自定义参数的合并集合，其中Trigger中的value优先级更高
        JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
        System.out.println("merged message: " + mergedJobDataMap.getString("message"));

        System.out.println("=========");
        //获取自定义参数信息, 方法二：隐式传递参数，通过定义相同key值的成员变量和对应的set方法
        System.out.println("merged message: " + message);

        System.out.println("=========");
        //获取trigger的时间
        Trigger currentTrigger = jobExecutionContext.getTrigger();
        System.out.println("Start Time: " + currentTrigger.getStartTime());
    }
}
