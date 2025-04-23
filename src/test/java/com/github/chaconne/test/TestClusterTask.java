package com.github.chaconne.test;

import java.util.Date;
import java.util.UUID;
import com.github.chaconne.Task;
import com.github.cronsmith.cron.CronBuilder;
import com.github.cronsmith.cron.CronExpression;

public class TestClusterTask implements Task {

    @Override
    public CronExpression getCronExpression() {
        return new CronBuilder().everySecond(1);
    }

    @Override
    public Object execute(String parameter) {
        return parameter + ": " + UUID.randomUUID().toString() + "\t"
                + Thread.currentThread().getName();
    }

    @Override
    public void handleResult(Object result, Throwable reason) {
        System.out.println(
                String.format("%s, %s, %s", new Date(), result, Thread.currentThread().getName()));
        if (reason != null) {
            reason.printStackTrace();
        }
    }

}
