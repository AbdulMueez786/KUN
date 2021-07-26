#include <SoftwareSerial.h>
#include<Servo.h>
SoftwareSerial Blue(2, 3);

Servo doorMotor;
Servo windowMotor;

String data;

int LED = 13; // Led connected
int FAN = 12; // Fan connected
int door_portNo=8;// door connected
int window_portNo=9; // window connected

void setup()
{
//Setting up the Connection

pinMode(LED, OUTPUT);
pinMode(FAN, OUTPUT);
doorMotor.attach(door_portNo);
windowMotor.attach(window_portNo);


// setting up LED ON
digitalWrite(LED, LOW);

//setting up FAN ON
digitalWrite(FAN, LOW);


Serial.begin(9600);// Serial Communication

Blue.begin(9600);  //  Bluetooth

}
void loop()
{
 while(Blue.available()==0) ;

 if(Blue.available()>0) 
{
  data = Blue.readString();

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
