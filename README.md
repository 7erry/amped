<h1>Amped</h1>
https://slides.com/twalters/amped

<img src="https://raw.githubusercontent.com/7erry/amped/master/master/docs/images/amped-logo.jpg" height="320" width="320"/>

 
Overview
========
Amped is a Java-based, cloud-native, reference architecture using many Open Source projects including the following:

* [Dropwizard](http://dropwizard.readthedocs.org/en/latest/getting-started.html) for microservin'
  * [Jetty](http://www.eclipse.org/jetty/) for HTTP servin'.
  * [Jersey](http://jersey.java.net/) for REST modelin'.
  * [Jackson](http://jackson.codehaus.org) for JSON parsin' and generatin'.
  * [Logback](http://logback.qos.ch/) for loggin'.
  * [Hibernate Validator](http://www.hibernate.org/subprojects/validator.html) for validatin'.
  * [Metrics](http://metrics.codahale.com) for figurin' out what your application is doin' in production.
  * [JDBI](http://www.jdbi.org) and [Hibernate](http://www.hibernate.org/) for databasin'.
  * [Liquibase](http://www.liquibase.org/) for migratin'.
  * [SPDY](http://www.chromium.org/spdy) for speedy web pagin'
* [Swagger](https://github.com/wordnik/swagger-core/wiki/JavaDropwizard-Quickstart) for API documentin'
* [Hazelcast](http://www.hazelcast.com) for cachin and all things distributed
  * Cache frequently accessed data in-memory, often in front of a database
  * Store temporal data like web sessions
  * In-memory data processing/analytics
  * Service registry and discovery
  * Distributed configuration
  * Cross-JVM communication/shared storage
* [Camel](http://camel.apache.org) for EIP'n
  * Good for decomposition / [Component](http://camel.apache.org/components.html) based (routes/components)
  * Service Orchestration
  * Cross-JVM communications
* [MongoDB](http://www.mongodb.org/) for persistin
  * Document Database

Architecture Overview
=====================
<img src="https://raw.githubusercontent.com/7erry/amped/master/master/docs/images/architecture.png">
<br/>
  * High Performance
  * High Availability
  * Easy Scalability
  * Easy Maintainability 

Real-time Metrics
=================================
Metrics Dropwizard Dashboard
-----------------
<img src="https://raw.github.com/7erry/amped/master/docs/images/amped-hystrix-dashboard.jpg">
<br/>
https://github.com/kimble/dropwizard-dashboard

Historical Metrics
=================================
Graphite Dashboard
------------------
<img src="https://raw.github.com/7erry/amped/master/docs/images/amped-graphite-dashboard.jpg">
<br/>
http://graphite.wikidot.com/

CloudWatch Dashboard
--------------------
<img src="https://raw.github.com/7erry/amped/master/docs/images/amped-cloudwatch-dashboard.jpg">
<br/>
http://aws.amazon.com/cloudwatch/"

Project Overview
================
* Scale up only some parts of the application
* Isolate services based depending on their security profiles (PII)
* Fault tolerance
* Cloud friendly

The following project layout is typical of many distributed applications: 

Amped-facade
---------
* Customer-facing REST-based edge service
* Uses DropWizard
* Uses Hazelcast
* Optionally uses Camel 

Amped-middletier
---------------
* Internal REST-based middletier service called by the edge service  
* Optionally uses DropWizard
* Optionally uses Spring Boot
* Uses Hazelcast
* Uses Camel 

Amped-core
---------
* Shared classes between edge and middletier

Documentation
==============
Please see [wiki] (https://github.com/7erry/amped/wiki) for detailed documentation.

https://github.com/dropwizard/dropwizard
<br/>
http://www.slideshare.net/tomaslin/dropwizard-and-groovy
<br/>
https://github.com/dropwizard/metrics
<br/>

Requirements
============
*mvn 3.x
<br/>
  $ mvn -version
  Apache Maven 3.2.2 (45f7c06d68e745d05611f7fd14efb6594181933e; 2014-06-17T09:51:42-04:00)
<br/>
  Maven home: /Users/twalters/Documents/tools/apache-maven-3.2.2
  Java version: 1.8.0_11, vendor: Oracle Corporation
<br/>
  Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_11.jdk/Contents/Home/jre
<br/>
  Default locale: en_US, platform encoding: UTF-8
<br/>
  OS name: "mac os x", version: "10.9.3", arch: "x86_64", family: "mac"

*java 8
<br/>
  $ java -version
<br/>
  java version "1.8.0_11"
<br/>
  Java(TM) SE Runtime Environment (build 1.8.0_11-b12)
<br/>
  Java HotSpot(TM) 64-Bit Server VM (build 25.11-b03, mixed mode)
<br/>

*JAVA_HOME set
<br/>
  $ echo $JAVA_HOME
<br/>
  /Library/Java/JavaVirtualMachines/jdk1.8.0_11.jdk/Contents/Home

<br/>
Tested on:
<br/>
OS X: 	10.9.3
<br/>
Ubuntu:	14.04.1 
<br/>

Execution
=========
* To package the helloworld run.

        mvn package

* To setup the h2 database run.

        java -jar target/amped-helloworld-0.8.0-SNAPSHOT.jar db migrate example.yml

* To run the server run.

        java -jar target/amped-helloworld-0.8.0-SNAPSHOT.jar server example.yml

* To hit the Hello World example (hit refresh a few times).

	http://localhost:8080/hello-world

* To post data into the application.

	curl -H "Content-Type: application/json" -X POST -d '{"fullName":"Other Person","jobTitle":"Other Title"}' http://localhost:8080/people
	
	open http://localhost:8080/people


