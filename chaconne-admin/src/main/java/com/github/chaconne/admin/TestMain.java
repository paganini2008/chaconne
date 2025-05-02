package com.github.chaconne.admin;

import com.github.cronsmith.CRON;
import com.github.cronsmith.cron.CronExpression;

public class TestMain {

    public static void main(String[] args) {
        CronExpression cronExpression = CRON.parse("0/3 * * * * ?");
    }

}
