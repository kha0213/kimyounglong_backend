package com.yl.wirebarley;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
    "com.yl.wirebarley.common",
    "com.yl.wirebarley.account",
    "com.yl.wirebarley.transaction",
    "com.yl.wirebarley.history"
})
public class WirebarleyApplication {

    public static void main(String[] args) {
        SpringApplication.run(WirebarleyApplication.class, args);
    }

}
