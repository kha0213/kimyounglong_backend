package com.yl.wirebarley;

import com.yl.wirebarley.common.config.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.yl.wirebarley"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.yl.wirebarley.account.AccountApplication.class,
                        com.yl.wirebarley.transaction.TransactionApplication.class
                }
        )
)
@EnableJpaRepositories({
        "com.yl.wirebarley.account.repository",
        "com.yl.wirebarley.transaction.repository"
})
@EntityScan({
        "com.yl.wirebarley.account.domain",
        "com.yl.wirebarley.transaction.domain"
})
@Import(CommonConfig.class)
public class WirebarleyApplication {
    public static void main(String[] args) {
        SpringApplication.run(WirebarleyApplication.class, args);
    }

}
