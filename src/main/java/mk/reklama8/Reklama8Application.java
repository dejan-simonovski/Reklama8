package mk.reklama8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Reklama8Application {

    public static void main(String[] args) {
        SpringApplication.run(Reklama8Application.class, args);
    }
}
