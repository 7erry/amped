<h1>Amped - UIFacade</h1>
https://slides.com/twalters/amped

<img src="https://raw.githubusercontent.com/7erry/amped/master/master/docs/images/amped-logo.jpg" height="320" width="320"/>

 
The following project layout is typical of many distributed applications: 

Amped-facade
============
* Customer-facing REST-based edge service
* Uses DropWizard
* Uses Hazelcast
* Uses Camel - see resources/HelloWorldService and routes/HelloCamel

Quick Start
===========
*  git clone https://github.com/7erry/amped.git
*  cd uifacade
*  mvn package
*  java -jar target/amped-helloworld-0.7.1.jar server amped.yml 

FAQ
==========
Default Hazelcast discovery is using multicast so be sure to use a wired connection instead of wireless
Default Swagger UI is located at http://localhost:8080/ui/index.html
Default password for the amped.keystore is example
Application version of 0.7.1 actually indicates the Dropwizard version it is based on

