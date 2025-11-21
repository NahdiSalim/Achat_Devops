package tn.esprit.rh.achat.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI Configuration for Swagger UI
 * Accessible at: /SpringMVC/swagger-ui.html
 */
@Configuration
public class SpringFoxSwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Achat DevOps - Stock Management API")
						.description("Purchase Management System - REST API Documentation")
						.version("1.0.0")
						.contact(new Contact()
								.name("DevOps Team")
								.email("devops@achat.tn"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0.html")));
	}
}