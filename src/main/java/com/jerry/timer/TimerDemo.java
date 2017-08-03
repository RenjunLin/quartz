package com.jerry.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class TimerDemo {

    public static void main(String[] args) throws ParseException {
        Timer t = new Timer();
        t.schedule(new MyTask(), 3000);  // 3秒后开始执行

        // 指定时间执行
        String time="2017-08-02 11:26:40";
        Date d= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        t.schedule(new MyTask(), d);

        // 指定之间开始，重复地执行，时间间隔为100ms
        t.schedule(new MyTask(), d, 100);

        // 延迟200ms之后，重复地执行，时间间隔为100ms
        t.schedule(new MyTask(), 200, 100);

        /**
         *（1）schedule方法：“fixed-delay”；如果第一次执行时间被delay了，
         * 随后的执行时间按 照 上一次 实际执行完成的时间点 进行计算
         *（2）scheduleAtFixedRate方法：“fixed-rate”；如果第一次执行时间被delay了，
         * 随后的执行时间按照 上一次开始的 时间点 进行计算，并且为了”catch up”会多次执行任务,
         * TimerTask中的执行体需要考虑同步
         */
        t.scheduleAtFixedRate(new MyTask(), d, 100);
        t.scheduleAtFixedRate(new MyTask(), 200, 100);
    }
}
