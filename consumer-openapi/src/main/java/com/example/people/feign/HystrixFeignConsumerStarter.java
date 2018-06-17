package com.example.people.feign;

import com.example.people.api.PeopleApi;
import com.example.people.model.Person;

import feign.Logger;
import feign.Request;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;

public class HystrixFeignConsumerStarter {
    public static void main(String[] args) {
        final PeopleApi api = HystrixFeign
            .builder()
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .logLevel(Logger.Level.HEADERS)
            .options(new Request.Options(1000, 2000))
            .target(PeopleApi.class, "http://localhost:8080/");
        
        final Person person = api.addPerson(
                new Person()
                    .email("a@b.com")
                    .firstName("John")
                    .lastName("Smith")
            );
        System.out.println(person);
    }
}
