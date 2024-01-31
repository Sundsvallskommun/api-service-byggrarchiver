package se.sundsvall.byggrarchiver;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.sundsvall.dept44.ServiceApplication;

import static org.springframework.boot.SpringApplication.run;

@ServiceApplication
@EnableFeignClients
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT2M")
public class Application {

    public static void main(String[] args) {
        run(Application.class, args);
    }
}
