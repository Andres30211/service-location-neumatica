package neumatica.location.service_location_neumatica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceLocationNeumaticaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceLocationNeumaticaApplication.class, args);
	}

}
