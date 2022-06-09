#include "RestClient.h"
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include "Melody.h"
#include <DHT.h>
#include <DHT_U.h>
#include <string.h>
#include <PubSubClient.h>
#include "SoftwareSerial.h"
#include "WiFiClient.h"
#include "TinyGPS++.h"
#include <Ticker.h>
#include "Arduino.h"

#define PIN_TONE D6
#define trigPin D7
#define echoPin D8
#define STASSID "MarcheBaite"
#define STAPSK "657213861"
#define Restserver "192.168.43.58"
const char *Mqttserver = "192.168.43.58";

int ID_PLACA = 2;

//INICIALIZACION GPS
TinyGPSPlus gps;
SoftwareSerial ss(D10, D9);
float coor[2];


//INICIALIZACION SENSOR TEMPERATURA
DHT_Unified dht(D1, DHT11);
long duration;
int distance;

int test_delay = 1000;
boolean describe_tests = true;
String response;

//REST Y MQTT
RestClient restclient = RestClient(Restserver, 8080);
WiFiClient espClient;
PubSubClient mqttclient(espClient);



//CABECERAS DE LAS FUNCIONES

//Inicializaciones
void setup_wifi();
void reconnect();
void describe(char *description);
void callback(char *topic, byte *payload, unsigned int length);

//Funcines auxiliares
void deserializeBodyBuzzer(String responseJson);
void test_status(int statusCode);
void test_response();
void displayInfo(); // <-- Se va a eliminar

//Lectura e inserccion sensores
void leer_gps_DISP();
//String leerGPSsintetico();
String leer_temp();
void leedistancia();
void POST_temp();
void POST_GPS();

//INICIALIZACIÓN DE LOS TIMERS
Ticker ticker1(POST_GPS, 30000, 0, MILLIS); //cada 30s
Ticker ticker2(POST_temp, 10000, 0, MILLIS); // cada 10sec