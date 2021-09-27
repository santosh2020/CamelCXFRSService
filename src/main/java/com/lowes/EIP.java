package com.lowes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class EIP extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:EIP")
		//split and aggregate response
		.log(LoggingLevel.INFO, "{{R1.log4j.logger}}","Start of EIP Route")
		 
		//EIP Code here 
		
		.log(LoggingLevel.INFO, "{{R1.log4j.logger}}","End of EIP Route");

	}

}
