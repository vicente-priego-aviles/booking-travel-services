# booking-travel-services
Booking travel services project. A Java based 3 microservices for 3 different elements bookable: flights, hotel and rental cars.

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
