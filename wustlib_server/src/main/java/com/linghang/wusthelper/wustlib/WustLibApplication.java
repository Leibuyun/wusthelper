package com.linghang.wusthelper.wustlib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.linghang.wusthelper"})
public class WustLibApplication {

    public static void main(String[] args) {
        SpringApplication.run(WustLibApplication.class, args);
    }

}
