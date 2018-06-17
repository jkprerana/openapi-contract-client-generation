package com.example.people.feign;

import com.example.people.api.PeopleApi;
import com.example.people.model.Person;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.opentracing.TracingClient;
import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.jaegertracing.samplers.ConstSampler;
import io.opentracing.Tracer;

public class OpentracingFeignConsumerStarter {
    public static void main(String[] args) throws Throwable {
        final Tracer tracer = new Configuration("consumer-openapi")
            .withSampler(
                new SamplerConfiguration()
                    .withType(ConstSampler.TYPE)
                    .withParam(new Float(1.0f)))
            .withReporter(
                new ReporterConfiguration()
                    .withSender(
                        new SenderConfiguration()
                            .withEndpoint("http://localhost:14268/api/traces")))
            .getTracer();
            
        final PeopleApi api = Feign
            .builder()
            .client(new TracingClient(new OkHttpClient(), tracer))
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .logLevel(Logger.Level.HEADERS)
            .options(new Request.Options(1000, 2000))
            .target(PeopleApi.class, "http://localhost:8080/");

        try {
            final Person person = api.addPerson(
                    new Person()
                        .email("a@b.com")
                        .firstName("John")
                        .lastName("Smith")
                );
            System.out.println(person);
        } finally {
            // Allow tracer to flush all span
            Thread.sleep(1000);
        }
    }
}
