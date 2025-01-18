package com.ntrs.pan.access;

import com.ntrs.pan.access.service.AccessDpSecondaryProtectionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AccessDpSecondaryProtectionApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AccessDpSecondaryProtectionApplication.class);
		// Disable the web environment (no embedded Tomcat server)
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@Bean
	public CommandLineRunner run(AccessDpSecondaryProtectionService service) {
		return args -> {
			try {
				service.accessProtectionAzureFunction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}


	@Bean
	public RestTemplate restTemplate() {

		return new RestTemplate(); // Use default HTTP client (HttpURLConnection)
	}

}
