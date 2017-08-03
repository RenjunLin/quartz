## Quartz

> 官网：www.quartz-scheduler.org
>
> - 强大的任务调度功能，可以和Spring高效集成
> - 执行失败之后，任务不会丢失
> - 灵活的应用方式，支持各种插件
> - 具有分布式和集群能力

### 几点注意

- 每次调度器执行job时，他在调用execute方法前都会创建一个Job实例
- 会自动读取工程目录下的quartz.properties文件，如果不存在，则会读取quartz自己jar包里面的文件，默认如下：

```java
org.quartz.scheduler.instanceName: DefaultQuartzScheduler
org.quartz.scheduler.rmi.export: false
org.quartz.scheduler.rmi.proxy: false
org.quartz.scheduler.wrapJobExecutionInUserTransaction: false

org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount: 10
org.quartz.threadPool.threadPriority: 5
org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true

org.quartz.jobStore.misfireThreshold: 60000

org.quartz.jobStore.class: org.quartz.simpl.RAMJobStore
```



### JobDetail

JobDetail为Job实例提供了许多设置属性，以及JobDataMap成员变量属性，它用来存储特定的Job实例的状态信息，调度器需要借助JobDetail对象来添加Job实例。

JobDetail包含几个重要属性：

- name
- group
- jobClass
- jobDataMap

JobExecutionContext：Job执行的参数，Job可以访问到执行的环境信息

JobDataMap: 存在JobExecutionContext中，用于自定义参数传递

定义Job如下：

```java
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

```

使用SimpleTrigger执行任务：

```java
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

```



### Trigger

Trigger是Quartz中的触发器，用来告诉调度作业什么时候触发，即Trigger对象是用来触发执行Job的。

- JobKey是表示job实例的标识，触发器被触发时，该指定的job实例会被执行
- StartTime：首次触发时间，Java.util.Date，startAt函数指定
- EndTime：指定不再触发的时间，Java.util.Date， endAt函数指定

### CronTrigger

基于日历的作业调度器，而不是像SimpleTrigger那样精确指定时间间隔，比SimpleTrigger更为常用。其实SimpleTrigger能实现的，Timer都能实现。但是CronTrigger不能由Timer实现。

#### Cron表达式

由7个表达式组成：[秒]、[分]、[小时]、[日]、[月]、[周]、[年] 组成，下面表格展示了其允许的取值范围和允许的特殊字符。

![](http://oepmbh7b1.bkt.clouddn.com/2017-08-02-085913.jpg)

其中特殊字符：

- `,` 表示或
- `-`表示between
- `*`表示every
- `/`表示"每隔"
- `?`表示every
- `#`表示"第"，例如 6#3，表示第三周的星期五
- `L`表示"最后" 例如6L，表示最后一周的星期五
- `W`表示指定时间最近的工作日

举几个例子：

![](http://oepmbh7b1.bkt.clouddn.com/2017-08-02-090644.jpg)

可以通过在线工具生成Cron表达式：http://cron.qqe2.com/

代码方面，我们定义一个CronJob

```java
package com.jerry.cron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CronJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Hello");
    }
}

```

使用CronTrigger：

```java
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

```



### Scheduler

调度器，负责任务的实际调度。几个主要函数为：

- Date scheduleJob(JobDetail jobDetail, Trigger trigger)，返回值为Date，表示近期会执行的时间
- void start()， 启动schedule
- void standby()，暂时挂起，可以通过start重新开始
- void shutdown()，结束，参数传入true，表示等待job执行完毕之后再关闭，false表示直接关闭，默认是false




