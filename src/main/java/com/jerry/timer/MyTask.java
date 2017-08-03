package com.jerry.timer;

import java.util.TimerTask;

public class MyTask extends TimerTask{

    @Override
    public void run() {
        System.out.println("run my task");
    }
}
