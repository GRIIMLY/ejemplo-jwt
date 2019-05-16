package co.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@SpringBootApplication
@EnableEurekaClient
public class ApplicationCommon {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationCommon.class, args);
	}
}
