#include <SoftwareSerial.h>

SoftwareSerial Blue(2, 3);
#include<Servo.h>
Servo doorMotor;
Servo windowMotor;

String data;

int LED = 13; // Led connected
int FAN = 12; // Fan connected
int door_portNo=8;// door connected
int window_portNo=9; // window connected

void setup()
{  
pinMode(LED, OUTPUT); 

pinMode(FAN, OUTPUT);

digitalWrite(LED, LOW);

digitalWrite(FAN, LOW);

doorMotor.attach(door_portNo);

windowMotor.attach(window_portNo);

Serial.begin(9600);
Blue.begin(9600); 
}
void loop()
{
 while(Blue.available()==0) ;

 if(Blue.available()>0) 
{
  data = Blue.readString();
 Serial.println(data);
if(data=="<Light_ON>"){
  
   digitalWrite(LED,HIGH); 

}
else if (data=="<Light_OFF>"){

   digitalWrite(LED,LOW);

}
else if (data=="<Fan_ON>"){

   digitalWrite(FAN,HIGH);

}
else if (data=="<Fan_OFF>"){

   digitalWrite(FAN,LOW);  

}
else if (data =="<Door_Open>"){
  
  doorMotor.write(0);
  
}
else if (data =="<Door_Close>"){

  doorMotor.write(90);  

}
else if (data =="<Window_Open>"){

  windowMotor.write(0);

}
else if (data =="<Window_Close>"){

  windowMotor.write(90);

}

}

}
