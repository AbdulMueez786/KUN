#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <SoftwareSerial.h>
#include<Servo.h>

Servo doorMotor;
Servo windowMotor;
//OneWire oneWire(ONE_WIRE_BUS);

//http://arduino.esp8266.com/stable/package_esp8266com_index.json

// Set these to run example.


#define FIREBASE_HOST "kunfypproject-default-rtdb.firebaseio.com"      // the project name address from firebase id
#define FIREBASE_AUTH "ABr4fylb847GAYazMEHG51zL7UpVpbvKZkxa3AIE"      // the secret key generated from firebase
#define WIFI_SSID "Abeer"
#define WIFI_PASSWORD "14785230"

int n;
int fan,door,window;
int doorPort=D1;
int windowPort=D2;



void setup()
{
  Serial.begin(57600);  
  //n=Firebase.getInt("LED_STATUS");
   // connect to wifi.

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
   //
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  //Firebase.reconnectWiFi(true);
//  Firebase.setInt("LED_STATUS", 1);
//  Firebase.setInt("FAN_STATUS", 1);
//  Firebase.setInt("DOOR_STATUS", 1);
//  Firebase.setInt("WINDOW_STATUS", 1);
  //doorMotor.attach(D5);
  //pinMode(LED, OUTPUT);  
  //Serial.begin(9600);// Serial Communication
  //digitalWrite(LED,LOW); 
doorMotor.attach(doorPort);
windowMotor.attach(windowPort);
pinMode(D3,OUTPUT);
pinMode(D4,OUTPUT);  
}

void loop()
{ 
Serial.println();
 
    n=Firebase.getInt("LED_STATUS");
    Serial.print("\n");
    Serial.print("Getting from firebase: ");
    Serial.print(n);
    Serial.print("\n");

   fan=Firebase.getInt("FAN_STATUS");
    Serial.print("\n");
    Serial.print("Getting from firebase: ");
    Serial.print(fan);
    Serial.print("\n");

    door=Firebase.getInt("DOOR_STATUS");
    Serial.print("\n");
    Serial.print("Getting from firebase: ");
    Serial.print(door);
    Serial.print("\n");

  
    window=Firebase.getInt("WINDOW_STATUS");
    Serial.print("\n");
    Serial.print("Getting from firebase: ");
    Serial.print(window);
    Serial.print("\n");
    
    
  if (n==1) {
      Serial.print("LED ON\n");
      Serial.print("\t");
            digitalWrite(D3,1); 
  }
  if(fan==1){
      Serial.print("FAN ON\n");
      Serial.print("\t");
      digitalWrite(D4,HIGH); 
  }
  if(door==1){
      doorMotor.write(0);
      Serial.print("DOOR OPEN\n");
      Serial.print("\t");
          
  }
  if(window==1){
      Serial.print("WINDOW OPEN\n");
      Serial.print("\t");
      //digitalWrite(D1,HIGH); 
      //windowMotor.write(0);   
  }
  if(n==0) {
    Serial.print("LED OFF");
    Serial.print("\t");
          digitalWrite(D3,0); 
  }
  if(fan==0){
    Serial.print("FAN OFF");
    Serial.print("\t");
    digitalWrite(D4,LOW); 
  }
  if(door==0){
    doorMotor.write(90);
    Serial.print("DOOR CLOSED");
    Serial.print("\t");
         
  }
  
  if(window==0){
    Serial.print("WINDOW CLOSED");
    Serial.print("\t");
    //    windowMotor.write(90);
  }
    
    Serial.print("\t");
    //digitalWrite(D1,LOW); 
    //doorMotor.write(0);  
    
  
  
  
 if(Firebase.failed()) {
        Serial.print("setting /number failed:");
        Serial.print("\t");
        Serial.println(Firebase.error());  
        return;
    }
  delay(1000);
}
