package com.company.hotelservice;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.modelmapper.ModelMapper;
import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"com.company.hotelservice, com.company.basedomains"})
@OpenAPIDefinition(
		info = @Info(
				title = "Hotel Travel Booking API Documentation",
				description = "API Documentation for endpoints to book Hotel for your travel",
				version = "1.0",
				contact = @Contact(
						name = "Pablo Sánchez Bello",
						email = "pablo.sanchez_bello@company.com",
						url = "https://mbinside.app.corpintra.net/person/SANCHPA"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://company.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Hotel Travel Booking Word.docx",
				url = "https://company.com"
		)
)
@EnableFeignClients
@EnableKafka
@EnableNeo4jRepositories("com.company.hotelservice")
@EntityScan("com.company.hotelservice.entity")
public class HotelServiceApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	Configuration cypherDslConfiguration() { return Configuration.newConfig().withDialect(Dialect.NEO4J_5).build(); }

	public static void main(String[] args) {
		SpringApplication.run(HotelServiceApplication.class, args);
	}

}
