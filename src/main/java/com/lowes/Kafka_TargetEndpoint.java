package com.lowes;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;

import beans.LoggerUtil;

public class Kafka_TargetEndpoint extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		//subroute- kafka client call

		from("direct:KafkaProducer")
		.routeId("KafkaProducer")
		.setHeader("breadCrumbID").simple("${exchangeProperty.breadCrumbID}")
		.setProperty("system").simple("${properties:targetSystem.T1}")
		.bean(LoggerUtil.class,"writeLog('info',${exchangeProperty.RouteLoggerName},'Start of publishing message to kafka topic : ${properties:kafkaTopic}')")
		.bean(LoggerUtil.class,"writeLog('debug',${exchangeProperty.RouteLoggerName},'Request message to be sent: ${body}')")
		.bean(LoggerUtil.class,"writeLog('debug',${exchangeProperty.RouteLoggerName},'Headers := ${headers}')")
		.bean(LoggerUtil.class,"writeLog('debug',${exchangeProperty.RouteLoggerName},'exchangeProperties := ${exchange.getProperties}')")
		.setExchangePattern(ExchangePattern.InOnly).id("restToKafka_cExchangePattern_1")
		.to("kafka:{{T1.Kafka.C1.topic}}"
				+ "?brokers={{T1.Kafka.C1.brokerlist}}"
				+ "&clientId={{T1.Kafka.C1.clientId}}"
				+ "&connectionMaxIdleMs=540000"
				+ "&receiveBufferBytes=1048576"
				+ "&metadataMaxAgeMs=300000"
				+ "&reconnectBackoffMs=50"
				+ "&sslTruststorePassword={{kafkaSslTruststorePassword}}"
				+ "&sslTruststoreLocation={{kafkaSslTruststoreLocation}}"
				+ "&securityProtocol=SSL"
				+ "&partitioner=org.apache.kafka.clients.producer.internals.DefaultPartitioner"
				+ "&compressionCodec=none"
				+ "&serializerClass=org.apache.kafka.common.serialization.StringSerializer"
				+ "&requestRequiredAcks=1"
				+ "&requestTimeoutMs={{T1.Kafka.C1.timeout}}"
				+ "&retryBackoffMs={{T1.Kafka.C1.retryBackOff}}"
				+ "&sendBufferBytes=131072"
				+ "&bufferMemorySize=33554432"
				+ "&retries={{T1.Kafka.C1.retryCount}}"
				+ "&producerBatchSize=16384"
				+ "&lingerMs=0"
				+ "&maxBlockMs=60000"
				+ "&maxRequestSize=1048576"
				+ "&maxInFlightRequest=5"
				+ "&sslProtocol={{kafkaSecurityProtocol}}")
		.id("restToKafka_cKafka_1")
		.to("log:{{R1.log4j.logger}}"+"?level=DEBUG"+"&showHeaders="+true+"&showProperties="+true+"&showBody="+false)
		.id("restToKafka_cLog_1")
		.bean(LoggerUtil.class,"writeLog('debug',${exchangeProperty.RouteLoggerName},'Headers := ${headers}')")
		.bean(LoggerUtil.class,"writeLog('debug',${exchangeProperty.RouteLoggerName},'exchangeProperties := ${exchange.getProperties}')")
		.bean(LoggerUtil.class,"writeLog('info',${exchangeProperty.RouteLoggerName},'Successfully placed messaged in kafka topic - {{kafkaTopic}}')");
	}

}
