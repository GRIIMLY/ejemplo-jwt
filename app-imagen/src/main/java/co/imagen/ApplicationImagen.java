package co.imagen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@SpringBootApplication
@EnableEurekaClient
public class ApplicationImagen {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationImagen.class, args);
	}
}
