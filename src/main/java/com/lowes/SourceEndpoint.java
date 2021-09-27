package com.lowes;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.springframework.stereotype.Component;

@Component
public class SourceEndpoint extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:RestAPI_RecepientList")
		.routeId("ResourceNameRestAPI")
		.process((exchange)-> {
				Message inMessage = exchange.getIn();
				inMessage.setHeader("http_query",
						JAXRSUtils.getStructuredParams(
								(String) inMessage.getHeader(Exchange.HTTP_QUERY), "&",
								false, false));
		})
		
		/* enable this code for logging load balancer name incase of multiple source systems
			.process(new org.apache.camel.Processor() {
				public void process(org.apache.camel.Exchange exchange) throws Exception {
	
					try {
	
						org.apache.cxf.message.Message cxfMessage = exchange.getIn().getHeader(
								CxfConstants.CAMEL_CXF_MESSAGE,
								org.apache.cxf.message.Message.class);
						ServletRequest request = (ServletRequest) cxfMessage.get("HTTP.REQUEST");
						String remoteAddress = request.getRemoteAddr();
	
						InetAddress host = InetAddress.getByName(remoteAddress.replace("/", ""));
	
						exchange.setProperty("sourceName", host.getHostName()); // source system
																				// name
	
					} catch (Exception ex) {
	
						exchange.setProperty("sourceName", exchange.getContext()
								.resolvePropertyPlaceholders("{{sourceSystem.S1}}")); 
								// set source system value from exchangeProperty file.
				      }
					;
				}
	
			})*/

		.recipientList()
		.simple("direct:${headers.operationName}");
	}

}
