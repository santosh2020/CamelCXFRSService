package com.lowes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PostFlow extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		//POST flow
		from("direct:MainRoute_Post")
		.routeId("PostRouteName") // add route name
		.setProperty("RouteLoggerName").simple("${properties:R3.log4j.logger}")
		.setProperty("breadCrumbID").simple("${headerAs(breadCrumbID,String)}")
		.setProperty("parentRouteID").simple("${routeId}")
		.setProperty("system").simple("${properties:sourceSystem.S1}")
		.setProperty("errorFlowRoute").constant("ProcessRouteNameErrorFlow") // update as per route name
		.setProperty("OriginalRequestMessage").simple("${body}")
		.log(LoggingLevel.INFO, "{{R3.log4j.logger}}",
				"Start of the main route- ${routeId}|Source Application- ${exchangeProperty.sourceName}|Target Application- ${properties:targetSystem.T1}")
		.log(LoggingLevel.DEBUG, "{{R3.log4j.logger}}", "Request Received : ${body}")
		.setProperty("MessageID").xpath("PropertyValue").id("restToRest_OR_Http_cSetProperty_2")
		.log(LoggingLevel.INFO, "{{R1.log4j.logger}}",
				"Processing started for request with  (ID Name) - ${exchangeProperty.MessageID}");

	}
	
}
