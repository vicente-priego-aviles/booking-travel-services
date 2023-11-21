# booking-travel-services
Booking travel services project. A Java based 3 microservices for 3 different elements bookable: flights, hotel and rental cars. In addition, a fourth microservice Payments is develop to show Saga management, consuming/producing message throw Kafka/RabbitMQ in an abstract way and more:
- REST APIs
- Direct connection between microservices using Feign clients.
- Circuit Breaker and Retry patterns
- Dockers
- Message Brokers: RabbitMQ and Kafka

## Installation

### Building the project
By default, the project is configured to use RabbitMQ as message broker. In case you would like to use Kafka, go to section "Using Kafka".
First of all, lets compile the maven project to create the artifacts.
<pre>$ mvn clean package</pre>
We have to wait until the previous command has finished. After that, we build and create all docker images (again remember to wait until the command has finished).
<pre>$ docker compose create</pre>
<br>

### Starting the project
We first start RabbitMQ docker (for Kafka, just replace 'rabbitmq' by 'kafka'):
<pre>$ docker start rabbitmq</pre>
<br/>

#### Authomatic startup of dockers ⚙️
We have included the possibility of starting the dockers with only one command. The precondition is to have started the message broker (either RabbitMQ or Kafka) and then run the following command.
<pre>$ docker compose up service-registry api-gateway flight-service hotel-service car-service payment-service -d</pre>
With the defined docker-compose dependencies, all the microservice will start in order. The previous command used the option "-d" to start dettached from the logs. 
To monitor when all dockers are "healthy" refresh the following command to get the status (all our microservices would be ready whenever they are "healthy"):
<pre>$ docker ps</pre>

You can access the logs of a container using the following command, that will attach the terminal to "follow" the new logs:
<pre>$ docker logs -f docker_name</pre>
<br />

#### Manual startup of dockers 👨‍🔧
Now, we start the Service Registry microservice (this microservice contains an Eureka Server for the auto discovery of endpoints using application names instead of IPs addresses):
<pre>$ docker start service-registry -a</pre>
<b>ONLY</b> when we see in the logs the project ended, we start the API Gateway that will be the front door for all the client HTTP requests:
<pre>$ docker start api-gateway -a</pre>
<b>ONLY</b> when API Gateway has finished starting up we can continue. Now we are ready to start cars, flights, hotels and payments microservices. They can be started in any way between them.
<pre>$ docker start flight-service -a</pre>
<pre>$ docker start car-service -a</pre>
<pre>$ docker start hotel-service -a</pre>
<pre>$ docker start payment-service -a</pre>
With this, installation is finished. To see how to use IntelliJ HTTP Client plugin and testing instructions, go to "Testing the project" section.
<br /><br />

#### Using Kafka
With the project it is provided a .env file. This file contains one variable set by default to 'rabbit' as following:
<pre>MESSAGE_BROKER=rabbit</pre>
In case of wanting to use Kafka, you should change it to value 'kafka' to leave it as following:
<pre>MESSAGE_BROKER=kafka</pre>
After performing this change, you must run again the command:
<pre>$ docker compose create</pre>
Now, go back to section "Starting the project".

## Testing the project
To test the project, we have added some IntelliJ HTTP Client files. You can find them under the "api-samples" folder. Inside the "playground" folder you can find the individual HTTP calls for each of the microservices. Directly on the "api-samples" folder you will find 3 .http files that authomatically perform reservations:
* <b>00-insert-all-and-book-only-flight.http</b>: Inserts data for Flights, Hotels and Cars and runs the first reservation for a Flight.
* <b>01-insert-all-and-book-all</b>: Inserts data for Flights, Hotels and Cars and books a Flight, Hotel and Car and tries Payment (without status print)
* <b>02-insert-all-and-book-all-with-status-trace</b> 👍<i>(Recommended)</i>: Inserts data for Flights, Hotels and Cars and books a Flight, Hotel and Car and tries Payment (with status print: saving the Payment status after each reservation).

<b>❗⚠️IMPORTANT</b>: Remember that is needed to select as "Environment" the "dev" value. The environments for the HTTP Client are configured on the http-client.env.json file.

### Access Databases
To access the databases, we have enabled the h2-console web client on each of the projects. For that, we have cleared the access on each of the IPs directly to the microservice. So with the following URLs you can access the h2 client:

| Microservice           | URL                              | Database URL           | Username | Password |
|------------------------|----------------------------------|------------------------|----------|----------|
| <b>Flight Service</b>  | http://localhost:8081/h2-console | jdbc:h2:mem:flights_db | sa       | password |
| <b>Hotel Service</b>   | http://localhost:8082/h2-console | jdbc:h2:mem:hotels_db  | sa       | password |
| <b>Car Service</b>     | http://localhost:8083/h2-console | jdbc:h2:mem:cars_db    | sa       | password |
| <b>Payment Service</b> | http://localhost:8084/h2-console | jdbc:h2:mem:payment_db | sa       | password |


### Access RabbitMQ
To access the RabbitMQ UI access using the following URL.
<pre>http://localhost:15672/</pre>
| Field           | Value |
|-----------------|-------|
| <b>User</b>     | guest |
| <b>Password</b> | guest |

### Access Kafka
Unfortunately, there is no UI installed for Kafka. In this case, we have to access the kafka docker and use the Kafka CLI clients.
<pre>$ docker exec -it kafka bash</pre>
The we navigate to where we have the sh kafka cli scripts:
<pre>$ cd /opt/bitnami/kafka/bin/</pre>
Next, here you can find useful commands for Kafka:
<pre>
# List topics created on default bootstrap server
$ kafka-topics.sh --bootstrap-server localhost:9092 --list
<br>
# Create a topic on default bootstrap server (replace "topic_name" with corresponding name). Remember it would be also good practice to specify other params like partitions number and replication factor.
$ kafka-topics.sh --bootstrap-server localhost:9092 --topic topic_name --create
<br>
# Delete a topic on default bootstrap server (replace "topic_name" with corresponding name)
$ kafka-topics.sh --bootstrap-server localhost:9092 --topic topic_name --delete
<br>
# Consume from earlist all events in topic on default bootstrap server (replace "topic_name" with corresponding name)
$ kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic_name --from-beginning
</pre>

### Access Swagger
To access the Swagger files, you can use the following URLs. We recommend accessing the Flight Service Swagger files, as we have included some response Examples only on that microservice for time saving purposes.
<pre>
# Flight Service 👍(Recommended)
http://localhost:8081/swagger-ui/index.html
# Hotel Service
http://localhost:8082/swagger-ui/index.html
# Car Service
http://localhost:8083/swagger-ui/index.html
# Payment Service
http://localhost:8084/swagger-ui/index.html
</pre>

### Last minute useful commands
In case something went wrong and it is needed to recreate things, find here several useful commands:
<pre>
# List running containers
$ docker ps -a
# Stop a docker
$ docker stop docker_name
# Remove a container
$ docker rm docker_name
# List docker images
$ docker image ls
# Remove a docker image
$ docker image rm docker_image_name
</pre>

# Architecture Diagram
![Architecture diagram of the solution](https://github.com/PabloSB96/booking-travel-services/blob/dev/booking-travel-services-architecture.jpg)

# Saga flow
![Saga flow diagram](https://github.com/PabloSB96/booking-travel-services/blob/dev/booking-travel-services-saga-flow.gif)

## Project requirements

### Data Model
![Data Model diagram](https://github.com/PabloSB96/booking-travel-services/blob/dev/booking-travel-services-domain-data.jpg)

### REQ1: Microservices to build
One will be the Flight microservice, to do the reservation of a flight, another one will be the Hotel microservice, to the the reservation of a hostel to stay during the trip, and a third one will be the Car microservice to rent a car to move around the city. All of them will have a domain object with the same name with at least one ID (if you want you can have other properties, to also include Lombok, but it is not the main goal); and the ID would be suggested to use UUIDs, that are unique and then you can forget about BBDD sequences.

### REQ2: BBDD
As DB use in memory of type H2

### REQ3.A: Booking process
The idea is that a user that wants to do a trip, can previously make the reservation of a Flight, a Hotel and a Car, in that order, so as clue, it would be good to use the same generated ID during the reservation of the Flight to use it as same identifier for the reservation of the Hotel and the Car. You have to find the mechanism so that some microservices communicate with others to end the process.

### REQ3.B: Error management
The next step, would be to check what would happen if any of those micros have any problem, due to database or other, that could be simulated for example without starting up one of the microservices, what solution you would provide, if you would use Circuit Breaker, etc.

### REQ4: Saga management
The idea is to create a fourth microservice, Payment, that manages the payment of the tree previous microsrevices, so that if the reservation of the Flight, Hotel and Car was achieved, the payment can be done, and the process end successfully. If any of the reservations are missing, it is communicated to the corresponding endpoint (with the generic response data structure); but not only that, we have to roll back the reservations so another needed field on the domain objects will be the status (done with a Java Enum) that can have the values: IN_PROGRESS, PAID, CANCELLED (more or less, what it is needed) so that we can then see how the Saga behaves. For example, if a reservation is done for flight and hotel, but car's one fails, then the payment should not be completed and the reservations should be cancelled. Another case would be that all the reservations work well, but the own service of Payment dtects that the client does not have enough credit, so all the reservations should go to status CANCELLED, but this operations are not performed by the Payment microservice, each one has do change their own status. To simulate that the payment had failed, do it with a random function, for example, that the 50% of payments fail. There are specific frameworks to manage Sagas, like Axon, but for now lets do it manually in the simplest way possible. Basically there are two ways of doing them, by Coreography or Orchestration but for this exercice, lets use Coreography so no extra microservices are needed.

### REQ5: Gateway
Would be a nice to have an API Gateway

### REQ6: Clean code
A must is having a clean code, with a correct variable and method naming where they are auto-descriptive without need of extra javadocs.

### REQ7: Swagger OpenAPI
The only documentation to provide is the description of endpoints in form of Swagger following the OpenAPI standard. Reading the API documentation should be enough to know the API.

### REQ7: Message Brokers
The microservices consumer and produces must be abstract from client. For that, use Spring Cloud Stream to abstract the connection to Kafka or RabbitMQ brokers.

### REQ8: Docker and Installation documentation
In order for the project to be standardized for easy deployment, we include a docker-compose file to create all the docker needed, and we complete this README.md file with all the installation process and steps need to test the project.

## Project exclusions
### EXC1: No frontend
No front-end development is needed. The focus in this case is backend and endpoint testing is done throw IntelliJ HTTP Client.
