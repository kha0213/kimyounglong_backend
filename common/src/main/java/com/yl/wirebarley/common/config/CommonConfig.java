package com.yl.wirebarley.common.config;

import com.yl.wirebarley.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@Import({AuditorAwareImpl.class,
GlobalExceptionHandler.class})
public class CommonConfig {
}
