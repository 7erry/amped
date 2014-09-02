<h1>Amped</h1>

<img src="https://raw.githubusercontent.com/7erry/amped/master/master/docs/images/amped-logo.jpg" />
 
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

* [Hazelcast](http://www.hazelcast.com) for cachin and all things distributed
  * Cache frequently accessed data in-memory, often in front of a database
  * Store temporal data like web sessions
  * In-memory data processing/analytics
  * Cross-JVM communication/shared storage
* [Camel](http://camel.apache.org) for EIP'n
  * Good for decomposition / Component based (plugins / routes)
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
Hystrix Dashboard
-----------------
<img src="https://raw.github.com/7erry/amped/master/docs/images/amped-hystrix-dashboard.jpg">
<br/>
https://github.com/Netflix/Hystrix/tree/master/hystrix-dashboard

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
https://github.com/dropwizard/metrics


