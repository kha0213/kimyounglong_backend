package com.yl.wirebarley.transaction;

import com.yl.wirebarley.common.config.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.yl.wirebarley.transaction.repository")
@EntityScan("com.yl.wirebarley.transaction")
@Import(CommonConfig.class)
// api 폴더만 접근가능하도록 변경.
@ComponentScan(basePackages = {"com.yl.wirebarley.transaction", "com.yl.wirebarley.account.api"})
public class TransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}
