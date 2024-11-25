package se.sundsvall.byggrarchiver;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
@EnableFeignClients
@EnableScheduling

public class Application {
	public static void main(String... args) {
		run(Application.class, args);
	}
}
