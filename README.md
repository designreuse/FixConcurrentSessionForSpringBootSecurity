# Spring Boot Security Fix ConcurrentSessionFilter

## Prerequisites

You will need the following things properly installed on your computer.

* JDK 8


## Installation

* `git clone https://massynet@bitbucket.org/massynet/spring-boot-demo.git` this repository
* `cd spring-boot-demo`
* `mvn clean`
* `mvn install`

## Running / Development

* `java -jar -Dspring.profiles.active=dev -Dspring.config.location=file:<PROJECT_PATH>/config/application-dev.yml target\spring-boot-demo-1.0-SNAPSHOT.jar`
* `mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev -Dspring.config.location=file:<PROJECT_PATH>/config/application-dev.yml"`
* Use main class 'com.spring.Application' with VM Options '-Dspring.profiles.active=dev -Dspring.config.location=file:<PROJECT_PATH>/config/application-dev.yml'
* Visit your app at [http://localhost:8080](http://localhost:8080).