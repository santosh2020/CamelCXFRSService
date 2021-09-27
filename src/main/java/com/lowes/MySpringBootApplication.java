package com.lowes;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MySpringBootApplication {

	/**
	 * A main method to start this application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(MySpringBootApplication.class, args);
	}

	
	/*
	 * @Bean public ServletRegistrationBean camelServletRegistrationBean() {
	 * ServletRegistrationBean registration = new ServletRegistrationBean(new
	 * CamelHttpTransportServlet(), "/services/*");
	 * registration.setName("CamelServlet"); return registration; }
	 */
	 

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public ServletRegistrationBean servletRegistrationBean(ApplicationContext context) {
		return new ServletRegistrationBean(new CXFServlet(), new String[] { "/services/*" });
	}

	@Bean(name = { "cxf" })
	public SpringBus springBus() {
		return new SpringBus();
	}

}
