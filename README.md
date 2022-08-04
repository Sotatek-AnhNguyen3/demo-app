# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.2/maven-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.7.2/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [H2 Database](https://www.h2database.com/html/tutorial.html)

### Overviews
* REST API development basics

For demonstrate we have implemented 2 API
1. Registration User following the requirement
2. A simple API health check -> for support purpose to check health of service when scale up service when we apply on infrastructure on cloud (aws, google cloud.. ) or kubernetes.

We're using H2 database all data will save on file on directory [your current working folder]/data

The project include three layer
1. Controller layer -> receive user request 
2. Service layer -> all logic will implement here
3. Repository -> all logic to interact with database will be here
* Design pattern and frameworks

Spring boot web is main framework on this project.

Dependency Injection is a fundamental aspect of the Spring framework, through which the Spring container “injects” objects into other objects or “dependencies”.
Simply put, this allows for loose coupling of components (controller, service, repository layer ...)

This helps us to easy to replace new component without affect to other code and easy for write unit test.

On service layer, we have abstraction by using interface of service class and not using implement class.
It helps we can change logic on feature ( by write another implement class ) that not affect to other code.

* Data and error handling

Data organized on model and dto

1. Model package is all entity class mapping with table columns

2. Dto package is all POJO class mapping for request and response

The response return to user have standardized with ResponseEntity object with construct

````
{
  "message": // description result
  "data": // all result object will wrapped by this
}
````

All logic if it to want return error then can throw custom run-time exception (for ex : ref to BadRequestException on UserService Class logic).

Then all exception will handler on GlobalExceptionHandler Class, we have standardized response for error with construct

````
{
   "message": // description of error,
   "errors": // list detail error occur with filed and message
     [
       "field": // field which has error,
       "message": // details error of this field
     ]
}
````

* Scalability and  Microservice
1. Microservice: Spring Boot’s embedded Apache Tomcat server is acting as a webserver. Our app can be packages to self-contained, ready to run applications.

With common microservice system we can have these parts
  + API gateway 
  + Service discovery
  + Those microservices

A sample spring cloud microservice system

![Spring cloud microservice system](https://ucarecdn.com/5081c764-f5ac-4cb7-bd08-6f902d850071/)

Spring cloud support all that features.
[Ref Spring Cloud Document](https://spring.io/microservices)

2. Scalability: 
Scalability of a system is complex, it depends not only on the business of each system but also may need to change the design of the system.

Example: 

There are systems that need to design more caching, type of database or apply message brokers systems like Kafka.

Because of the limitation of the topic from your side, we simply put to scaling the microservices in the system to serve the increased heavy load when there are many user requests.

With that condition we can apply Kubernetes to scale up microservice on our system.

Demo spring microservice system deploy on kubernetes
![Spring microservice system deploy on kubernetes](https://i0.wp.com/piotrminkowski.com/wp-content/uploads/2020/11/spring-boot-autoscaler-on-kubernetes-arch.png)

### Guides
The following guides illustrate how to use some features concretely:

1. To run project with command
````
./mvnw spring-boot:run
````
2. Then you can access swagger ui on link 
(server port is 8080, you can change that on the application.yaml file)
* [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
3. You also access H2 console via link with config as on the application.yaml file 
* [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
4. To run test with command
````
./mvnw test
````

