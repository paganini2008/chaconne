package com.github.chaconne.admin;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.github.chaconne.cluster.EnableChaconne;

/**
 * 
 * @Description: ChaconneServerAdmin
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
@EnableChaconne
@SpringBootApplication
public class ChaconneServerAdmin {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(ChaconneServerAdmin.class, args);
    }

}
