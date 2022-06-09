#include "main.h"


/////////////////// FUNCIÓN PARA CONEXIÓN WIFI ///////////////////
void setup_wifi()
{
  Serial.print("Connecting to ");
  Serial.println(STASSID);
  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println("Setup!");
}

///////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////// INICIO FUNCIONES MQTT ////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

void callback(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("]: ");
  String data;
  for (int i = 0; i < length; i++)
  {
    data = data + (char)payload[i];
  }

  if (strcmp(topic, "buzzer") == 0)
  {
    deserializeBodyBuzzer(data);
  }

  if (strcmp(topic, "temperatura") == 0)
  {
    if (data == "1")
      POST_temp();
    else
      Serial.println("No se insertó temperatura");
  }

  if (strcmp(topic, "gps") == 0)
  {
    if (data == "1")
      POST_GPS();
    else
      Serial.println("No se insertó GPS");
  }
}

void reconnect()
{
  while (!mqttclient.connected())
  {
    Serial.print("Attempting MQTT connection...");
    if (mqttclient.connect("ESP8266Client"))
    {
      Serial.println("connected");
      mqttclient.subscribe("buzzer");
      mqttclient.subscribe("temperatura");
      mqttclient.subscribe("gps");
    }
    else
    {
      Serial.print("failed, rc=");
      Serial.print(mqttclient.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

///////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////// FINAL FUNCIONES MQTT ////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////
////////////////////////////// INICIO FUNCIONES SENSORES //////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

////////////////////// LECTURA SENSOR GPS //////////////////////
void leer_gps_DISP()
{

  Serial.println(ss.read());
  while (ss.available() > 0)
    if (gps.encode(ss.read()))
      displayInfo();

  if (millis() > 5000 && gps.charsProcessed() < 10)
  {
    Serial.println(F("No GPS detected: check wiring."));
  }
}

////////////////////// LECTURA SENSOR DE TEMPERATURA //////////////////////
String leer_temp()
{
  int valor = 0;
  StaticJsonDocument<200> doc;
  sensors_event_t event;
  dht.temperature().getEvent(&event);
  if (isnan(event.temperature))
  {
    Serial.println("Error reading temperature!");
  }
  else
  {
    valor = event.temperature;
    if (valor >= 32)
    {
      tone(D6, 4000, 1000); // ACTIVAMOS EL BUZZER SI LA TEMPERATURA ES SUPERIOR A 32ºC, se cambiaria para producto final
    }
  }
  doc["valor"] = valor;
  doc["IDVeh"] = ID_PLACA;
  String output;
  serializeJson(doc, output);
  return output;
}

////////////////////// LECTURA SENSOR DE MOVIMIENTO //////////////////////
void leedistancia()
{
  digitalWrite(trigPin, HIGH);
  delay(1);
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH);
  distance = duration / 58.2;

  if (distance <= 30)
  {
    tone(PIN_TONE, 4000 , 500); // ACTIVAMOS EL BUZZER SI LA DISTANCIA ES <= 30 CM (cuanto mas cerca mas agudo)
  }
}

////////////////////// FUNCIÓN PARA INSERTAR TEMPERATURA LEÍDA //////////////////////
void POST_temp()
{
  String post_body = leer_temp();
  describe("Test POST with path and body and response");
  test_status(restclient.post("/api/temp", post_body.c_str(), &response));
  test_response();
}

////////////////////// FUNCIÓN PARA INSERTAR INFO DE LOCALIZACIÓN GPS //////////////////////
void POST_GPS()
{
  StaticJsonDocument<200> doc;
  doc["Posicion"] = "37.358313,-5.987542";
  doc["IDVeh"] = ID_PLACA;
  String post_body;
  serializeJson(doc, post_body);
  describe("Test POST with path and body and response");
  test_status(restclient.post("/api/gps", post_body.c_str(), &response));
  test_response();
}

///////////////////////////////////////////////////////////////////////////////////////
////////////////////////////// FINAL FUNCIONES SENSORES ///////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////  FUNCIONES AUXILIARES ////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

/////////////////// DESERIALIZAR PARA ACTIVAR BUZZER ///////////////////
void deserializeBodyBuzzer(String responseJson)
{
  if (responseJson != "")
  {
    StaticJsonDocument<200> doc;
    DeserializationError error = deserializeJson(doc, responseJson);
    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }
    int frecuencia = doc["frecuencia"];
    int duracion = doc["duracion"];
    Serial.println(frecuencia);
    // Pita buzzer
    tone(PIN_TONE, frecuencia, duracion);
  }
}

/////////////////// COMPROBAR ESTADO ///////////////////
void test_status(int statusCode)
{
  delay(test_delay);
  if (statusCode == 200 || statusCode == 201)
  {
    Serial.print("TEST RESULT: ok (");
    Serial.print(statusCode);
    Serial.println(")");
  }
  else
  {
    Serial.print("TEST RESULT: fail (");
    Serial.print(statusCode);
    Serial.println(")");
  }
}

void test_response()
{
  Serial.println("TEST RESULT: (response body = " + response + ")");
  response = "";
}

void describe(char *description)
{
  if (describe_tests)
    Serial.println(description);
}

void displayInfo()
{

  Serial.print("Location: ");
  if (gps.location.isValid())
  {
    Serial.print(gps.location.lat(), 6);
    Serial.print(F(","));
    Serial.println(gps.location.lng(), 6);
  }
  else
  {
    Serial.println("INVALID");
  }
}

///////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////  FIN FUNCIONES AUXILIARES //////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

///////////// FUNCIÓN SETUP /////////////
void setup()
{
  Serial.begin(9600);
  ///////////////////// CONEXION WIFI /////////////////////
  setup_wifi();
  ///////////////////// INICIALIZACION CLIENTE MQTT /////////////////////
  mqttclient.setServer(Mqttserver, 1883);
  mqttclient.setCallback(callback);
  ///////////////// SERIAL SENSOR GPS /////////////////
  //ss.begin(9600);
  //////////////// SENSOR TEMPERATURA ////////////////
  dht.begin();
  //////////////// PINES SENSOR MOVIMIENTO ////////////////
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
  pinMode(echoPin, INPUT);  // Sets the echoPin as an Input

  ////////////////////////// TIMERS //////////////////////////
  ticker1.start(); // TIMER GPS
  ticker2.start(); // TIMER TEMPERATURA

//Active Buzzer
#ifdef ESP_PLATFORM
#define CHANNEL 5
  ledcSetup(CHANNEL, 5000, 8);
  ledcAttachPin(PIN_TONE, CHANNEL);
  ledcWrite(CHANNEL, 0); //duty Cycle de 0
#endif
}

//////////// RUN THE TESTS! ////////////
void loop()
{
  if (!mqttclient.connected())
  {
    reconnect(); // CONECTAMOS EL CLIENTE EN CASO DE NO ESTARLO
  }
  mqttclient.loop(); //CONSTANTEMENTE COMPROBAMOS SI HAY MENSAJES NUEVOS

  leer_temp();      //MIRAMOS SI SOBREPASA LA TEMPERATURA MINIMA
  leedistancia();   //MIRAMOS SI SE ACERCA A UNA PARED

  ////// ACTUALIZACIÓN DE TIMERS //////
  ticker1.update();
  ticker2.update();
}
