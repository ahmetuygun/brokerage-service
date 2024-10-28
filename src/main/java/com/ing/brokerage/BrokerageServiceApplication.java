package com.ing.brokerage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.util.HashMap;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class BrokerageServiceApplication {

	public static void main(String[] args)
	{
		SpringApplication.run(BrokerageServiceApplication.class, args);
	}

}
