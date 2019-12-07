#include <ESP8266WiFi.h>
const char *ssid = "ssid";
const char *password = "password";
const char *host = "ip number";

int Vo = A0;
int V_LED = 2;

float Vo_value = 0;
float Voltage = 0;
float dustDensity = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  delay(1000);
  WiFi.begin(ssid, password);
  pinMode(V_LED, OUTPUT);
  pinMode(Vo, INPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(V_LED, LOW);
  delayMicroseconds(280);
  Vo_value = analogRead(Vo);
  delayMicroseconds(40);
  digitalWrite(V_LED, HIGH);
  delayMicroseconds(9680);

  Voltage =Vo_value * 5.0 / 1024.0;
  dustDensity = (Voltage - 0.03)/0.005;
  if(dustDensity < 0) dustDensity = 0;

  Serial.print("Voltage : ");
  Serial.print(Voltage);
  Serial.print(" Dust Density : ");
  Serial.println(dustDensity);

  delay(1000);
  WiFiClient client;
  const int Port = 9000;
  if (!client.connect(host, Port)) {
    //Serial.println("dd");
    return;
  }
  String sender =String(dustDensity);
  client.print(sender);
   if (!client.connected()) {
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();

    while (true);
  }
  delay(1000);
  String request = "c";
  while (request == "c") {
    if (client) {
//      Serial.println("연결 성공1");
      if (client.connected()) {
//        Serial.println("연결 성공");
        request = client.readStringUntil('\r');
//        Serial.println(request);
        if (request == "d") {
          client.flush();
          request = "c";
          break;
        }
      }
      else {
//        Serial.println("실패");
      }
    }
  }
  
}
