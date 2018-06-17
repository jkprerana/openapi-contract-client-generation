package com.example.config;

import java.util.Arrays;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.tracing.opentracing.jaxrs.OpenTracingFeature;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.resource.JaxRsApiApplication;
import com.example.resource.PeopleRestService;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.uber.jaeger.Configuration.ReporterConfiguration;
import com.uber.jaeger.Configuration.SamplerConfiguration;
import com.uber.jaeger.samplers.ConstSampler;
import com.uber.jaeger.senders.HttpSender;

import io.opentracing.Tracer;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = PeopleRestService.class)
public class AppConfig {
	@Autowired private PeopleRestService peopleRestService;
	
	@Bean
	public Tracer tracer() {
		return new com.uber.jaeger.Configuration(
				"server-openapi", 
				new SamplerConfiguration(ConstSampler.TYPE, 1),
				new ReporterConfiguration(new HttpSender("http://localhost:14268/api/traces"))
			).getTracer();
	}
	
	@Bean(destroyMethod = "destroy")
	public Server jaxRsServer(Bus bus) {
		final JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();

		factory.setApplication(new JaxRsApiApplication());
		factory.setServiceBean(peopleRestService);
		factory.setProvider(new JacksonJsonProvider());
		factory.setProvider(new OpenTracingFeature(tracer()));
		factory.setFeatures(Arrays.asList(new OpenApiFeature()));
		factory.setBus(bus);
		factory.setAddress("/");

		return factory.create();
	}

	@Bean
	public ServletRegistrationBean cxfServlet() {
		final ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CXFServlet(), "/api/*");
		servletRegistrationBean.setLoadOnStartup(1);
		return servletRegistrationBean;
	}
}
