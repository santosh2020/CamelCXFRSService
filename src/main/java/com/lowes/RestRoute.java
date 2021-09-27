package com.lowes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.jaxrs.CxfRsEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.support.DefaultRegistry;
import org.springframework.stereotype.Component;

import com.lowes.exceptionHandler.ExceptionHandlerProcessor;


@Component
public class RestRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
	
		final  ModelCamelContext camelContext = (ModelCamelContext) getContext();
		final DefaultRegistry registry = new DefaultRegistry();
		((DefaultCamelContext) camelContext).setRegistry(registry);

		camelContext.setUseMDCLogging(true);
		camelContext.setUseBreadcrumb(true);
		camelContext.setStreamCaching(true);

		registry.bind("propertyBean", new beans.PropertyUtil(camelContext));
		registry.bind("errorHandler", new ExceptionHandlerProcessor());
		
		RegistryConfig.setCXFRSEndpointProperties(registry);
		
		CxfRsEndpoint restEP = (CxfRsEndpoint)endpoint("cxfrs://" + "{{LB_Context}}"
				+ "?resourceClasses=com.lowes.RegistryConfig$Service_CXFRS_1"
				+ "&features=#features_CXFRS_1"
				+ "&inInterceptors=#inInterceptors_CXFRS_1"
				+ "&outInterceptors=#outInterceptors_CXFRS_1"
				+ "&properties=#properties_CXFRS_1"
				+ "&cxfRsEndpointConfigurer=#endpointConfigurer_CXFRS_1"
				+ "&providers=#providers"
				+ "&loggingFeatureEnabled=true");
		
		// Common Error handler		
		onException(Exception.class)
		.handled(true)
		.to("direct:commonException");
				
		//Route 1 -start of rest call
		from(restEP)
		.to("direct:RestAPI_RecepientList");

		
	}

}
