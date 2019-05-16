package co.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@SpringBootApplication
@EnableEurekaClient
public class ApplicationAuth {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationAuth.class, args);
	}
}
