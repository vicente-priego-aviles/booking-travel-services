# booking-travel-services
Booking travel services project. A Java based 3 microservices for 3 different elements bookable: flights, hotel and rental cars. A mayores, será necesario un cuarto servicio Payments.

## Installation

### Building the project
By default, the project is configured to use RabbitMQ as message broker. In case you would like to use Kafka, go to section "Using Kafka".
First of all, lets compile the maven project to create the artifacts.
<pre>$ mvn clean package</pre>
Now, we build and create all docker images.
<pre>$ docker compose create</pre>
After this, we can start running the containers. To see the different logs, the recommended approach is to open a PowerShell window for each docker to start. We will use the "-a" parameter on the docker start to attach the terminal to the logs, so we can track the logs visually.
<br><br>

### Starting the project
We first start RabbitMQ docker (for Kafka, just replace 'rabbitmq' by 'kafka'):
<pre>$ docker start rabbitmq</pre>
Now, we start the Service Registry microservice, so that the following microservices can register in the Eureka Server hold in that docker:
<pre>$ docker start service-registry -a</pre>
<b>ONLY</b> when we see in the logs the project ended, we start the API Gateway that will be the front door for all the client HTTP requests:
<pre>$ docker start api-gateway -a</pre>
<b>ONLY</b> when API Gateway has finished starting up we can continue. Now we are ready to start cars, flights, hotels and payments microservices. They can be started in any way between them.
<pre>$ docker start flight-service -a</pre>
<pre>$ docker start car-service -a</pre>
<pre>$ docker start hotel-service -a</pre>
<pre>$ docker start payment-service -a</pre>
With this, installation is finished. To see how to use IntelliJ HTTP Client plugin and testing instructions, go to "Testing the project" section.

### Using Kafka
With the project it is provided a .env file. This file contains one variable setted by default to 'rabbit' as following:
<pre>MESSAGE_BROKER=rabbit</pre>
In case of wanting to use Kafka, you should change it to value 'kafka' to leave it as following:
<pre>MESSAGE_BROKER=kafka</pre>
After performing this change, you must run again the command:
<pre>$ docker compose create</pre>
Now, go back to section "Starting the project".

## Testing the project

### Access Databases
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

## Project requirements
### REQ1: Microservicios a construir
Uno será el microservicio Fly, para realizar una reserva de un vuelo, otro será el microservicio Hotel, para hacer una reserva del hotel donde me hospedaré durante las vacaciones, y el tercero se llamará Car, para hacer alquilar un coche por el que moverme por Torrevieja. Todos tendrán un objeto de dominio con el mismo nombre, con al menos un id (si quieres puedes meter alguna propiedad más, por el tema de Lombok, pero no te compliques, tienen que ser sencillos), y el id te sugeriría que utilices UUIDs, que son únicos, y así te olvidas del tema de las secuencias de BBDD. 

### REQ2: BBDD
Utiliza BBDD en memoria de tipo H2

### REQ3.A: Proceso reserva
La idea es que un usuario que quiera contratar un viaje, pueda realizar previamente una reserva de un Vuelo, un Hotel y un Coche, en ese orden, por lo que como pista, usaría el mismo id generado durante la reserva del Vuelo para usarlo como mismo identificador para la reserva del Hotel y del Coche. Tienes que encontrar el mecanismo para que unos micros se comuniquen con los otros y terminar el proceso, esta es la parte fácil.

### REQ3.B: Error management
El siguiente paso, sería ver qué pasaría si algunos de tus micros ha tenido algún problema, por base de datos o por lo que sea, que lo simularías no levantando el microservicio, qué solución darías, usarías el circuit breaker, etc...

### REQ4: Saga management
La idea es montar un cuarto microservicio, Payment, que gestione el pago de los tres servicios anteriores, de tal forma que si se ha conseguido reserva de Vuelo, Hotel y Coche, se puede proceder con el pago, y el proceso ha terminado correctamente, si le falta alguna reserva de los anteriores, se le comunica en el endpoint el problema (con la estructura de error que me has comentado antes), pero no solo eso, hay que echar para atrás las reservas anteriores, por lo que otro campo que necesitarás en los objetos de dominio de los tres microservicios Vuelo, Hotel y Coche será el estado (hazlo con una Enum de Java), que tenga algo así como los valores: IN_PROGRESS, RESERVED, PAID, CANCELLED (más o menos, lo que veas que vayas a necesitar), para que luego podamos ver que tu Saga se comporta correctamente.
Por ejemplo, si se realiza la reserva del vuelo y del hotel, pero falla la del coche, no se podrá realizar el pago, y se han de cancelar luego las reservas del vuelo y el hotel.
Otro caso sería que se reserve todo correctamente, pero el propio servicio de Payment detecte que el cliente no tiene saldo en la tarjeta y no pueda pagar, por lo que se tendrían que cambiar el estado de las reserva a CANCELLED, pero estas operaciones no las hace el micro de pagos, las tienen que hacer cada uno de los otros microservicios, así que tendrás que usar llamadas desde el de pagos a los otros para que cada uno cancele su reserva. El caso de que falle el pago hazlo con alguna función aleatoria, por ejemplo, que el 50% de los pagos fallen. En los servicios para las reservas, podrás hacer lo mismo, que el 10% de las operaciones fallen, para que de vez en cuando entren en funcionamiento tus Sagas.
Hay frameworks específicos para gestionar Sagas, como Axon, pero de momento esto es algo más avanzado, así que la idea es que te lo montes a mano de la forma más sencilla que puedas. Básicamente tienes dos formas de hacerlo, por Coreografía o por Orquestación pero en tu caso, la opción más fácil será la Coreografía, y así no necesitarás microservicios extras.

### REQ5: Gateway
Estaría bien tener un API Gateway

### REQ6: Clean code
Yo le doy mucho valor al "clean code" y ha una buena elección de los nombres de las variables y los métodos, deberían ser auto-descriptivas, se tiene que entender todo muy bien sin necesidad de añadir anotaciones de tipo javadoc.

### REQ7: Swagger OpenAPI
La única documentación que tienes que proporcionar es en los endpoints, todo lo que añadas se tiene que ver en swagger, el resto no es necesario. Leyendo tu API tengo que saber como ser capaz de realizar el proceso completo.

## Project exclusions
### EXC1: No frontend
Sólo haz la parte de back, no necesitamos ningún front, las pruebas las haremos con Postman.


# Connections
## Connect to Kafka topics
To connect to Kafka topics, once Docker engine is up:
- Ensure that containers kafka1 and zoo1 are up, or make them up by: docker up kafka1 / zoo1.
- Use command: "$ docker exec -it kafka1 bash" -> To enter the container shell
- Use: "$ kafka-console-consumer --bootstrap-server localhost:9092 --topic topic_name --from-beginning" -> to read messages.

Delete topic:
- First list current available topics: "$ kafka-topics --list --bootstrap-server localhost:9092"
- Delete one of the topics: "$ kafka-topics --bootstrap-server localhost:9092 --delete --topic <topic-name>"

Manually create a topic:
- Create one new topic: "$ kafka-topics --bootstrap-server localhost:9092 --topic <topic-name> --create --partitions 3 --replication-factor 1"

# Architecture Diagram
![Architecture diagram of the solution](https://github.com/PabloSB96/booking-travel-services/blob/dev/booking-travel-services-architecture.jpg)
