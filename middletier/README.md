<h1>Amped-middletier</h1>
https://slides.com/twalters/amped

<img src="https://raw.githubusercontent.com/7erry/amped/master/master/docs/images/amped-logo.jpg" height="320" width="320"/>

 
Amped-middletier
---------------
* Internal REST-based middletier service called by the edge service  
* Optionally uses DropWizard
* Optionally uses Spring Boot
* Uses Hazelcast
* Uses Camel 


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


