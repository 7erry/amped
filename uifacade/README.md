<h1>Amped - UIFacade</h1>
https://slides.com/twalters/amped

<img src="https://raw.githubusercontent.com/7erry/amped/master/master/docs/images/amped-logo.jpg" height="320" width="320"/>

 
The following project layout is typical of many distributed applications: 

Amped-facade
============
* Customer-facing REST-based edge service
* Uses DropWizard
* Uses Hazelcast
* Optionally uses Camel 

Quick Start
===========
*  git clone https://github.com/7erry/amped.git
*  cd uifacade
*  mvn package
*  java -jar target/amped-helloworld-0.7.1.jar server amped.yml 
