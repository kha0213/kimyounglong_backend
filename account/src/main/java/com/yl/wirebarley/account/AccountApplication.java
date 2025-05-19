package com.yl.wirebarley.account;

import com.yl.wirebarley.common.config.AuditorAwareImpl;
import com.yl.wirebarley.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.yl.wirebarley.account.repository")
@EntityScan("com.yl.wirebarley.account")
@EnableJpaAuditing
@Import({AuditorAwareImpl.class, GlobalExceptionHandler.class})
@ComponentScan(basePackages = {"com.yl.wirebarley.account", "com.yl.wirebarley.common.exception"})
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }
}
