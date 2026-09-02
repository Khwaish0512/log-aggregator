package com.khwaish.log_aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LogAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogAggregatorApplication.class, args);
	}

}
