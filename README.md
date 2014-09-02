https://raw.githubusercontent.com/7erry/amped/master/master/docs/images/amped-logo.jpg

Overview
========
Amped is a Java-based, cloud-native, reference architecture using many Open Source projects including the following:


Microservices

* Dropwizard
  Executable Jar (embedded Jetty)
  Yaml externalized configuration
  Jersey for Rest exposure
  Jackson for Json marshalling
  SPDY for improved web page performance
  Metrics for realtime monitoring
  Logback
  Hibernate Validator (bad config’s will not start)
  JDBI & Hibernate
  LiquidBase for source control over db stuff (yaml/json/schema)
* Hazelcast
  Memcached alternative with protocol compatible interface
  CAP based java.util.*
* MongoDB backed for non transient map data / i.e. Yaml
  Cache frequently accessed data in-memory, often in front of a database
  Store temporal data like web sessions
  In-memory data processing/analytics
  Cross-JVM communication/shared storage
* Camel
  EIP
  Good for decomposition / Component based (plugins / routes)
  Service Orchestration
  Cross-JVM communications
* MongoDB
  Document Database
  High Performance
  High Availability
  Easy Scalability
  Easy Maintainability 

Architecture Overview
=====================
<img src="https://raw.githubusercontent.com/7erry/amped/master/master/docs/images/architecture.png">

Real-time Metrics
=================================
Hystrix Dashboard
-----------------
<img src="https://raw.github.com/7erry/amped/master/docs/images/amped-hystrix-dashboard.jpg">
https://github.com/Netflix/Hystrix/tree/master/hystrix-dashboard

Historical Metrics
=================================
Graphite Dashboard
------------------
<img src="https://raw.github.com/7erry/amped/master/docs/images/amped-graphite-dashboard.jpg">
http://graphite.wikidot.com/

CloudWatch Dashboard
--------------------
<img src="https://raw.github.com/7erry/amped/master/docs/images/amped-cloudwatch-dashboard.jpg">
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
