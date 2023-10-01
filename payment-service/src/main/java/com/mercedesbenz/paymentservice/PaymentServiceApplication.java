package com.mercedesbenz.paymentservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"com.mercedesbenz.paymentservice, com.mercedesbenz.basedomains"})
@OpenAPIDefinition(
		info = @Info(
				title = "Payment Travel API Documentation",
				description = "API Documentation for endpoints to pay your travel",
				version = "1.0",
				contact = @Contact(
						name = "Pablo Sánchez Bello",
						email = "pablo.sanchez_bello@mercedes-benz.com",
						url = "https://mbinside.app.corpintra.net/person/SANCHPA"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://mercedes-benz.com"
				)
		)
)
@EnableKafka
public class PaymentServiceApplication {

	@Bean
	public ModelMapper modelMapper() { return new ModelMapper(); }

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}