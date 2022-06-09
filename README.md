# ProyectoDAD
 Proyecto de Cliente-Servidor.

El cliente es un ESP8266 el cual incorpora un sensor de temperatura que  estará comprobando continuamente la temperatura, de modo que si el sensor capta que la temperatura excede de un umbral prestablecido se alertará al usuario mediante un pitido.
Además, cada cierto periodo de tiempo, el valor de la temperatura del vehículo se almacenará en la base de datos, así como el instante de tiempo en el que se haya insertado, para tener un histórico de todas las irregularidades que puedan suceder y tener una mayor facilidad de estudio de las diferentes temperaturas que alcanza el vehículo cuando queramos consultar la base de datos.

También tiene un sensor GPS y se almacenará de igual manera en la base de datos cada cierto tiempo.
Por otra parte tendremos un sensor de distancia, yi rebasamos el umbral de cercanía (unos treinta cm) el zumbador se activará, avisándonos que existe un objeto cercano 

La comunicaión cliente-servidor se hace mediante api-rest y mqtt.
