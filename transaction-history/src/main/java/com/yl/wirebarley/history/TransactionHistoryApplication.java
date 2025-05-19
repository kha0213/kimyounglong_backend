package com.yl.wirebarley.history;

import com.yl.wirebarley.common.config.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.yl.wirebarley.history.repository")
@EntityScan("com.yl.wirebarley.history")
@EnableJpaAuditing
@Import(CommonConfig.class)
public class TransactionHistoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionHistoryApplication.class, args);
    }
}
