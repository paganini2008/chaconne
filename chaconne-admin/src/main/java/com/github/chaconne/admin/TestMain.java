package com.github.chaconne.admin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TestMain {

    public static void main(String[] args) {
        System.out.println(ZonedDateTime.now(ZoneId.of("UTC")));
        System.out.println(LocalDateTime.now(ZoneId.of("UTC")));
    }

}
