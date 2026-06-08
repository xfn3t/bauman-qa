package ru.bauman.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.bauman.scheduler.config.MissionSchedulerProperties;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableConfigurationProperties(MissionSchedulerProperties.class)
public class SchedulerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
	}
}