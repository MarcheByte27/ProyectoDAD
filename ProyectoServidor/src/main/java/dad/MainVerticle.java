package dad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class MainVerticle extends AbstractVerticle {

	MySQLPool mySqlClient;
	private Gson gson;
	private MqttClient mqttClient;

	@Override
	public void start(Promise<Void> startFuture) {

		// Instantiating a Gson serialize object using specific date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		mqttClient = MqttClient.create(getVertx(),
				new MqttClientOptions().setAutoKeepAlive(true).setUsername("admin").setPassword("admin"));
		mqttClient.connect(1883, "192.168.43.58", connection -> {
			if (connection.succeeded()) {
				System.out.println("Client name: " + connection.result().code().name());

				mqttClient.subscribe("temperatura", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
					if (handler.succeeded()) {
						System.out.println("Client has been subscribed to temperatura");
					}
				});
				mqttClient.subscribe("buzzer", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
					if (handler.succeeded()) {
						System.out.println("Client has been subscribed to buzzer");
					}
				});
				
				mqttClient.subscribe("gps", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
					if (handler.succeeded()) {
						System.out.println("Client has been subscribed to gps");
					}
				});

				mqttClient.publishHandler(message -> {
					System.out.println("Message published on topic: " + message.topicName());
					System.out.println(message.payload().toString());
				});

			} else {
				System.out.println("Se ha producido un error en la conexión al broker");

			}
		});

		// Defining the router object
		Router router = Router.router(vertx);
		// Handling any server startup result
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		// URI's USUARIO
		router.route("/api/user*").handler(BodyHandler.create());
		router.get("/api/user").handler(this::getAllUsers);
		router.get("/api/user/:username").handler(this::getUserByUsername);
		router.post("/api/user").handler(this::insertUser);
		router.delete("/api/user/:username").handler(this::deleteUserbyUsername);

		// URI's TEMPERATURA
		router.route("/api/temp*").handler(BodyHandler.create());
		router.get("/api/temp/:temp1&:temp2&:matricula").handler(this::getTemperatura);
		router.post("/api/temp").handler(this::insertTemperatura);

		// URI's GPS
		router.route("/api/gps*").handler(BodyHandler.create());
		router.get("/api/gps/:temp1&:temp2&:matricula").handler(this::getGPS);
		router.post("/api/gps").handler(this::insertGPS);

		// URI's VEHICULO
		router.route("/api/veh*").handler(BodyHandler.create());
		router.get("/api/veh").handler(this::getAllVehiculos);
		router.get("/api/veh/:userID").handler(this::getVehiculoByUserID);
		router.post("/api/veh").handler(this::insertVehiculo);
		router.delete("/api/veh/:matricula").handler(this::deleteVehiculo);

		// URI's Notificación
		router.route("/api/notif*").handler(BodyHandler.create());
		router.post("/api/notif/buzzer").handler(this::notifybuzzer);
		router.post("/api/notif/temp").handler(this::notifytemp);
		router.post("/api/notif/gps").handler(this::notifyGPS);

		// CONEXIÓN CON BASE DE DATOS
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dad").setUser("root").setPassword("rootroot");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}
	
	
	
	// ================================================================
	// ======================== MENSAJES MQTT =========================
	// ================================================================


	
	private void notifybuzzer(RoutingContext routingContext) {
		JsonObject cuerpo = routingContext.getBodyAsJson();
		Buzzer b = new Buzzer(cuerpo.getInteger("frecuencia"), cuerpo.getInteger("duracion"));
		mqttClient.publish("buzzer", Buffer.buffer(gson.toJson(b)), MqttQoS.AT_LEAST_ONCE, false, false);
		routingContext.response().setStatusCode(200).end();
	}

	private void notifytemp(RoutingContext routingContext) {
		JsonObject cuerpo = routingContext.getBodyAsJson();
		int b = cuerpo.getInteger("activar");
		mqttClient.publish("temperatura", Buffer.buffer(gson.toJson(b)), MqttQoS.AT_LEAST_ONCE, false, false);
		routingContext.response().setStatusCode(200).end();
	}
	
	private void notifyGPS(RoutingContext routingContext) {
		JsonObject cuerpo = routingContext.getBodyAsJson();
		int b = cuerpo.getInteger("activar");
		mqttClient.publish("gps", Buffer.buffer(gson.toJson(b)), MqttQoS.AT_LEAST_ONCE, false, false);
		routingContext.response().setStatusCode(200).end();
	}
	
	
	
	
	
	// ================================================================
	// ======================== TABLA USUARIOS ========================
	// ================================================================

	// Método que permite listar todos los usuarios registrados en la base de datos

	private void getAllUsers(RoutingContext routingContext) {
		mySqlClient.query("SELECT * FROM dad.usuario;", res -> {
			if (res.succeeded()) {
				// Get the result set
				JsonArray result = new JsonArray();
				for (Row elem : res.result()) {
					result.add(JsonObject.mapFrom(new Usuario(elem)));
				}
				// Respuesta a la consulta
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(result));
				// Confirmación por consola
				System.out.println("Los usuarios se mandaron correctamente");
			} else {
				routingContext.response().setStatusCode(204)
						.putHeader("content-type", "application/json; charset=utf-8").end();
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	// Método que muestra información de un usuario concreto dependiendo del
	// username
	private void getUserByUsername(RoutingContext routingContext) {
		String username = routingContext.request().getParam("username");
		mySqlClient.preparedQuery("SELECT * FROM dad.usuario WHERE username = ? ", Tuple.of(username), res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				JsonObject result = new JsonObject();
				for (Row elem : resultSet) {
					result = JsonObject.mapFrom(new Usuario(elem));
				}
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(result));

				System.out.println("El usuario se mandó correctamente");
			} else {
				routingContext.response().setStatusCode(204)
						.putHeader("content-type", "application/json; charset=utf-8").end();
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});

	}

	// Método para eliminar un usuario concreto de la base de datos dependiendo del
	// username

	private void deleteUserbyUsername(RoutingContext routingContext) {
		String username = routingContext.request().getParam("username");
		mySqlClient.preparedQuery("DELETE FROM dad.usuario WHERE username = ? ", Tuple.of(username), res -> {
			if (res.succeeded()) {
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(username));
				System.out.println("El usuario con username " + username + " se eliminó correctamente");
			} else {
				routingContext.response().setStatusCode(204)
						.putHeader("content-type", "application/json; charset=utf-8").end();
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	// Método que permite insertar un usuario en la base de datos

	private void insertUser(RoutingContext routingContext) {
		JsonObject cuerpo = routingContext.getBodyAsJson();
		LocalDate nacimiento = LocalDate.parse(cuerpo.getString("Fecha_Nacimiento"),
				DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Usuario user = new Usuario(cuerpo.getString("DNI"), cuerpo.getInteger("Telefono"), cuerpo.getString("Nombre"),
				cuerpo.getString("Direccion"), nacimiento, cuerpo.getString("username"), cuerpo.getString("password"));
		mySqlClient.preparedQuery(
				"INSERT INTO dad.Usuario (DNI, Telefono, Nombre, Direccion, Fecha_Nacimiento, username, password) values ( ? , ?, ?, ?, ?, ?, ? ) ",
				Tuple.of(user.getDNI(), user.getTelefono(), user.getNombre(), user.getDireccion(),
						user.getFecha_Nacimiento().toString(), user.getUsername(), user.getPassword()),
				res -> {
					if (res.succeeded()) {
						routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(user));
						System.out.println("Se insertó correctamente el usuario");
					} else {
						routingContext.response().setStatusCode(204)
								.putHeader("content-type", "application/json; charset=utf-8").end();
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});

	}

	// ===================================================================
	// ======================== TABLA TEMPERATURA ========================
	// ===================================================================

	// Método que devuelve información sobre la temperatura de un vehículo (tenemos
	// que especificar su matrícula)
	// entre dos fechas determinadas

	private void getTemperatura(RoutingContext routingContext) {
		LocalDateTime d1 = LocalDateTime.parse(routingContext.request().getParam("temp1"),
				DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
		LocalDateTime d2 = LocalDateTime.parse(routingContext.request().getParam("temp2"),
				DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
		String matricula = routingContext.request().getParam("matricula");
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"SELECT * FROM dad.temperatura NATURAL JOIN dad.vehiculo WHERE Marca_Temporal_Temp between ? and ? and matricula = ?;",
						Tuple.of(d1, d2, matricula), res -> {
							if (res.succeeded()) {
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Temperatura(elem)));
								}
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(200).end(gson.toJson(result));
							} else {
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(204).end();
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});

	}

	// Método que inseta información de temperatura de un vehículo en la base de
	// datos cuando se produzca alguna anomalía

	private void insertTemperatura(RoutingContext routingContext) {

		LocalDateTime fecha = LocalDateTime.now();
		Float valor = routingContext.getBodyAsJson().getFloat("valor");
		Integer id = routingContext.getBodyAsJson().getInteger("IDVeh");
		Temperatura temp = new Temperatura(valor, fecha, id);

		mySqlClient.preparedQuery(
				"INSERT INTO dad.Temperatura (Valor, Marca_Temporal_Temp , IDVeh) values ( ? , ?, ? ) ",
				Tuple.of(temp.getValor(), temp.getMarca_temporal_temp(), temp.getIDVeh()), res -> {
					if (res.succeeded()) {
						routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(temp));
						System.out.println("Se insertó correctamente la temperatura");
					} else {
						routingContext.response().setStatusCode(204)
								.putHeader("content-type", "application/json; charset=utf-8").end();
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});
	}

	// ================================================================
	// ========================== TABLA GPS ===========================
	// ================================================================

	// Método que devuelve información sobre la localización de un vehículo (tenemos
	// que especificar su matrícula)
	// entre dos fechas determinadas

	private void getGPS(RoutingContext routingContext) {
		LocalDateTime d1 = LocalDateTime.parse(routingContext.request().getParam("temp1"),
				DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
		LocalDateTime d2 = LocalDateTime.parse(routingContext.request().getParam("temp2"),
				DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
		String matricula = routingContext.request().getParam("matricula");

		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"SELECT * FROM dad.gps NATURAL JOIN dad.vehiculo WHERE Marca_Temporal_GPS between ? and ? and matricula = ?;",
						Tuple.of(d1, d2, matricula), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new GPS(elem)));
								}
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(200).end(gson.toJson(result));
							} else {
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(204).end();
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}

	// Método que inseta información de localización de un vehículo en la base de
	// datos

	private void insertGPS(RoutingContext routingContext) {

		Integer id = routingContext.getBodyAsJson().getInteger("IDVeh");
		// No sabemos exactamente como devuelve la posición el gps, por eso lo hemos
		// como string
		String pos = routingContext.getBodyAsJson().getString("Posicion");
		LocalDateTime fecha = LocalDateTime.now();
		GPS g = new GPS(pos, fecha, id);

		mySqlClient.preparedQuery("INSERT INTO dad.GPS (Posicion, Marca_Temporal_GPS , IDVeh) values ( ?,?, ? ) ",
				Tuple.of(g.getPosicion(), g.getMarca_Temporal_GPS(), g.getIDVeh()), res -> {
					if (res.succeeded()) {
						routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(g));
						System.out.println("Se insertó correctamente la información de localización");
					} else {
						routingContext.response().setStatusCode(204)
								.putHeader("content-type", "application/json; charset=utf-8").end();
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});
	}

	// ================================================================
	// ======================== TABLA VEHÍCULO ========================
	// ================================================================

	// Método para listar todos los vehículos registrados en la base de datos

	private void getAllVehiculos(RoutingContext routingContext) {
		mySqlClient.query("SELECT * FROM dad.vehiculo;", res -> {
			if (res.succeeded()) {
				JsonArray result = new JsonArray();
				for (Row elem : res.result()) {
					result.add(JsonObject.mapFrom(new Vehiculo(elem)));
				}
				// respuesta a la consulta
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(result));
				// Confirmación por consola
				System.out.println("Los vehiculos se mandaron correctamente");
			} else {
				routingContext.response().setStatusCode(204)
						.putHeader("content-type", "application/json; charset=utf-8").end();
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	// Método que muestra todos los vehiculos de un usuario especificando su
	// IDUsuario

	private void getVehiculoByUserID(RoutingContext routingContext) {

		Integer userID = Integer.valueOf(routingContext.request().getParam("userID"));
		mySqlClient.preparedQuery("SELECT * FROM dad.vehiculo WHERE IDUsuario = ? ", Tuple.of(userID), res -> {
			if (res.succeeded()) {
				JsonArray result = new JsonArray();
				for (Row elem : res.result()) {
					result.add(JsonObject.mapFrom(new Vehiculo(elem)));
				}
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(result));

				System.out.println("El vehiculo se mandó correctamente");
			} else {

				routingContext.response().setStatusCode(204)
						.putHeader("content-type", "application/json; charset=utf-8").end();
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	// Método para insertar un vehículo en la base de datos

	private void insertVehiculo(RoutingContext routingContext) {

		Vehiculo veh = gson.fromJson(routingContext.getBodyAsString(), Vehiculo.class);
		mySqlClient.preparedQuery("INSERT INTO dad.Vehiculo (Matricula, Modelo, IDUsuario) values ( ? , ?, ? ) ",
				Tuple.of(veh.getMatricula(), veh.getModelo(), veh.getIDUsuario()), res -> {
					if (res.succeeded()) {
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(veh.getMatricula()));
						System.out
								.println("El vehículo con matricula " + veh.getMatricula() + " insertó correctamente");

					} else {
						routingContext.response().setStatusCode(204)
								.putHeader("content-type", "application/json; charset=utf-8").end();
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});
	}

	// Método para eliminar un vehículo especificando su matrícula

	private void deleteVehiculo(RoutingContext routingContext) {
		String matricula = routingContext.request().getParam("matricula");
		mySqlClient.preparedQuery("DELETE FROM dad.vehiculo WHERE matricula = ? ", Tuple.of(matricula), res -> {
			if (res.succeeded()) {
				// Get the result set
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(gson.toJson(matricula));
				System.out.println("El coche con matricula " + matricula + " se eliminó correctamente");
			} else {

				routingContext.response().setStatusCode(204)
						.putHeader("content-type", "application/json; charset=utf-8").end();
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

}
