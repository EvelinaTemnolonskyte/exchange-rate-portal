package lt.fxportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FxportalApplication {

	public static void main(String[] args) {
		SpringApplication.run(FxportalApplication.class, args);
	}

}
