package com.lowes;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.springframework.stereotype.Component;

@Component
public class GetFlow extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:GetFlow")
		.routeId("GetRouteName") // add route name
		.setProperty("RouteLoggerName").simple("${properties:R1.log4j.logger}")
		.setProperty("breadCrumbID").simple("${headerAs(breadCrumbID,String)}")
		.setProperty("parentRouteID").simple("${routeId}")
		.setProperty("system").simple("${properties:sourceSystem.S1}")
		.setProperty("errorFlowRoute").constant("ProcessRouteNameErrorFlow") // update as per route name
		.setProperty("relativePath").simple("${headerAs(CamelHttpPath,String)}")
		.log(LoggingLevel.INFO, "{{R1.log4j.logger}}",
				"Start of the main route- ${routeId}|Source Application- ${exchangeProperty.sourceName}|Target Application- ${properties:targetSystem.T1}")
		.process(this::processRequest)
		.setBody().constant("hello")
		.log(LoggingLevel.INFO, "{{R1.log4j.logger}}",
				"Processing started for request parameters: ${exchangeProperty.OriginalRequestMessage} and relativePath=${exchangeProperty.relativePath}");
	}
	
	private Processor processRequest(Exchange ex) {
		return (exchange) -> {
			String Request = URLDecoder.decode(exchange.getIn().getHeader("http_query", String.class),"UTF-8");
			exchange.setProperty("OriginalRequestMessage", Request);
			MultivaluedMap<String, String> queryMap = JAXRSUtils.getStructuredParams(
					exchange.getIn().getHeader("CamelHttpQuery", String.class), "&", true, false);
			
			for (Map.Entry<String, List<String>> eachQueryParam : queryMap.entrySet()) {
				String value = null;
				for (int i = 0; i < eachQueryParam.getValue().size(); i++) {
					value = eachQueryParam.getValue().get(i);
					exchange.setProperty(eachQueryParam.getKey(), value);
					System.out.println(eachQueryParam.getKey()+"="+ value);
				}
			};
		};
	}

}
