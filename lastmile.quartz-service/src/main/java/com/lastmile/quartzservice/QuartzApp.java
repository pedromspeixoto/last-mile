package com.lastmile.quartzservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan({"com.lastmile.utils.logs", "com.lastmile.quartzservice"})
@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class QuartzApp implements WebMvcConfigurer {
	public static void main(String[] args) {
		SpringApplication.run(QuartzApp.class, args);
	}
}