package esp.iomanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class EspIomanageApplication {

	public static void main(String[] args) {
		SpringApplication.run(EspIomanageApplication.class, args);
	}

}
