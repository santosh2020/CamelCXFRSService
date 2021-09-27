package com.lowes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.lowes.exceptionHandler.ErrorActionProcessor;
import com.lowes.exceptionHandler.ExceptionHandlerProcessor;
import com.lowes.exceptionHandler.GenericErrorResponseBean;

@Component
public class ExceptionProcessor extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:commonException")
		.routeId("CommonErrorHandler").setHeader("breadCrumbID")
		.simple("${exchangeProperty.breadCrumbID}")
		.log(LoggingLevel.DEBUG, "{{R1.log4j.logger}}", "${exception}")
		.process(this::processException).wireTap("seda:errorAction")
		.copy(true).id("mqToMQ_WireTap_2").end()
		.recipientList().simple("direct:${exchangeProperty.errorFlowRoute}")
		.stopOnException().ignoreInvalidEndpoints()
		.id("mqToMQ_RecipientList_1");
		
		//Error flow mapping route
		
		//get error flow
		from("direct:ProcessGetErrorFlowRouteName")
		.routeId("ProcessRouteNameErrorFlow")// update as per errorFlowRoute name.
		.log(LoggingLevel.ERROR, "{{R1.log4j.logger}}",
				"Error Details:${exchangeProperty.parentRouteID}|(ID Name)-${exchangeProperty.MessageID}:- ${body}")
		.log(LoggingLevel.ERROR, "{{R1.log4j.logger}}",
				"Original Message Recieved is: ${exchangeProperty.OriginalRequestMessage}")
		.to("direct:GetErrorMapping")
		.log(LoggingLevel.INFO, "{{R1.log4j.logger}}",
				"End of the main route - ${exchangeProperty.parentRouteID}|"
				+ "Source Application- ${properties:sourceSystem.S1}|"
				+ "Target Application- ${properties:targetSystem.T1}");
		
	
	
		//Error Action - send email route
		from("seda:errorAction"+"?multipleConsumers="+true+"&limitConcurrentConsumers="+false)
		.routeId("restToKafka_SEDA_1")
		.process((exchange) -> {
			try {
				GenericErrorResponseBean errorDetails=exchange.getProperty("errorDetails", GenericErrorResponseBean.class);
				String env=System.getProperty("lowes.env");
				String originalMessage=exchange.getProperty("OriginalRequestMessage",String.class);
				if(originalMessage==null){originalMessage="";}
				ErrorActionProcessor.sendEmail(errorDetails, env, originalMessage);
			} catch (Exception e) {
				//do nothing
			};
		});	
		
	}
	
	private Processor processException(Exchange ex) {
		return (exchange) -> {
			GenericErrorResponseBean lastError = ExceptionHandlerProcessor.handleException(exchange, false);
			Gson obj = new Gson();
			String json = obj.toJson(lastError);
			exchange.getIn().setBody(json);
			exchange.setProperty("errorDetails", lastError); // used in error action
		};
	}

}
