package com.lowes;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;

import org.apache.camel.component.cxf.CxfPayload;
import org.apache.camel.component.cxf.jaxrs.CamelResourceProvider;
import org.apache.camel.component.cxf.jaxrs.CxfRsEndpointConfigurer;
import org.apache.camel.support.DefaultRegistry;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.AbstractJAXRSFactoryBean;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.SourceProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.staxutils.StaxUtils;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;


public class RegistryConfig {
	
	public static String getCXFRSEndpointAddress(String endpointUrl) {
		if (endpointUrl != null && !endpointUrl.trim().isEmpty() && !endpointUrl.contains("://")) {
			if (endpointUrl.startsWith("/services")) {
				endpointUrl = endpointUrl.substring("/services".length());
			}
			if (!endpointUrl.startsWith("/")) {
				endpointUrl =String.valueOf('/') + endpointUrl;
			}
		}
		return endpointUrl;
	}

	public interface Service_CXFRS_1 {
		@Path("/hello")
		@GET()
		@Produces({"application/json" })
		Object GetFlow();

		@Path("/deletecustomer")
		@POST()
		@Consumes({ "application/xml", "text/xml", "application/json" })
		@Produces({ "application/xml", "text/xml", "application/json" })
		Object PostFlow(String payload);

	}
	
	static class CxfPayloadProvider implements MessageBodyWriter<CxfPayload<Source>> {
		
		public boolean isWriteable(Class<?> cls, Type type, Annotation[] anns, MediaType mt) {
			return cls == CxfPayload.class;
		}

		public long getSize(CxfPayload<Source> obj, Class<?> cls, Type type, Annotation[] anns, MediaType mt) {
			return -1;
		}

		public void writeTo(CxfPayload<Source> obj, Class<?> cls, Type type, Annotation[] anns, MediaType mt,
				MultivaluedMap<String, Object> headers, OutputStream os) throws IOException, WebApplicationException {
			
			List<Source> bodySource = obj.getBodySources();
			
			if (bodySource == null || bodySource.size() != 1) {
				throw new InternalServerErrorException();
			}
			
			try {
				StaxUtils.copy(bodySource.get(0), os);
			} catch (XMLStreamException ex) {
				throw new InternalServerErrorException(ex);
			}

		}
	}

	public static void setCXFRSEndpointProperties(DefaultRegistry registry) {
		List<Object> providers = new ArrayList<Object>();
		providers.add(new JacksonJaxbJsonProvider());
		providers.add(new JacksonJaxbXMLProvider());
		registry.bind("providers", providers);
		
		AbstractJAXRSFactoryBean factory_CXFRS_1;
		JAXRSServerFactoryBean sf_CXFRS_1 = new JAXRSServerFactoryBean();
		factory_CXFRS_1 = sf_CXFRS_1;
		
		sf_CXFRS_1.setServiceClass(Service_CXFRS_1.class);
		sf_CXFRS_1.setResourceProvider(Service_CXFRS_1.class,
				new CamelResourceProvider(Service_CXFRS_1.class));
		sf_CXFRS_1.setProvider(new CxfPayloadProvider());
		// avoid JAXBException in runtime
		sf_CXFRS_1.setProvider(new SourceProvider());

		factory_CXFRS_1.setAddress(getCXFRSEndpointAddress("{{LB_Context}}"));
		registry.bind("CXFRS_1", factory_CXFRS_1);
		
		List<Object> providers_CXFRS_1 = (List<Object>) registry.lookupByName("providers");
		if (providers_CXFRS_1 == null) {
			providers_CXFRS_1 = new java.util.ArrayList<Object>();
		}
		providers_CXFRS_1.addAll(sf_CXFRS_1.getProviders());
		
		registry.bind("providers", providers_CXFRS_1);
		registry.bind("features_CXFRS_1", sf_CXFRS_1.getFeatures() != null ? sf_CXFRS_1.getFeatures()
				: new ArrayList<Feature>());
		registry.bind("inInterceptors_CXFRS_1", sf_CXFRS_1.getInInterceptors() != null ? sf_CXFRS_1.getInInterceptors()
				: new ArrayList<Interceptor<? extends Message>>());
		registry.bind("outInterceptors_CXFRS_1", sf_CXFRS_1.getOutInterceptors() != null
				? sf_CXFRS_1.getOutInterceptors()
						: new ArrayList<Interceptor<? extends Message>>());
		registry.bind("properties_CXFRS_1", sf_CXFRS_1.getProperties() != null ? sf_CXFRS_1.getProperties()
				: new HashMap<String, Object>());
		registry.bind("endpointConfigurer_CXFRS_1", new CxfRsEndpointConfigurer() {

		});
	}
}
