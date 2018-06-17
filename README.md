Using OpenAPI v3.0.0 for client generation
==============

- Run Jaeger

      docker run -d -e \
          COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
          -p 5775:5775/udp \
          -p 6831:6831/udp \
          -p 6832:6832/udp \
          -p 5778:5778 \
          -p 16686:16686 \
          -p 14268:14268 \
          -p 9411:9411 \
          jaegertracing/all-in-one:latest

- Build and run server

      mvn clean package
      java -jar server-openapi/target/server-openapi-0.0.1-SNAPSHOT.jar

 - Run examples from your preferred IDE:

      - `FeignConsumerStarter`: simple example of Feign client
      - `HystrixFeignConsumerStarter`: an example of Feign client integrated with Hytrix
      - `OpentracingFeignConsumerStarter`: an example of Feign client integrated with Opentracing

- Open browser to access Swagger UI at: http://localhost:8080/api/api-docs?url=/api/openapi.json

- Open browser to access Jaeger UI at: http://localhost:16686
