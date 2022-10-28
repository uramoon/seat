#include <SoftwareSerial.h>
SoftwareSerial BTSerial(2, 3);//블루투스 아두이노 2,3번핀 사용
char read_data;
String send_data = "X";

//압력센서 정보 http://blog.xcoda.net/70
int sensor = A0;//압력센서 아두이노 A0번핀 사용
int sensor_value = 0;
int set_value = 450;

int reservation_flag = 0;

long lastMillis = 0;
long currentMillis = 0;



void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);

  pinMode(sensor, INPUT);

  delay(1000);//1초 휴식
}



void loop() {

  currentMillis = millis();//currentMillis 변수에 millis()값 저장
  if (currentMillis - lastMillis >= 500) {//currentMillis값에서 lastMillis값을 뺀값이 500이거나 크면 (0.5초마다 실행되는 부분)
    lastMillis = currentMillis;//lastMillis 변수에 currentMillis값 저장

    if (reservation_flag == 1) {//reservation_flag값이 1이면 (예약된 상태)
      sensor_value =  analogRead(sensor);//sensor_value변수에 압력센서값 저장

      Serial.print("sensor : ");
      Serial.print(sensor_value);//센서값 시리얼출력

      if (sensor_value > set_value) {//센서값이 설정값보다 크면
        Serial.println("     압력 감지");
        send_data = "O";
      } else {//센서값이 설정값보다 크지않으면
        Serial.println("     압력 미감지");
        send_data = "X";
      }

      BTSerial.println(send_data);//블루투스로 압력상태값 전송
    }
  }

  if (BTSerial.available()) {//블루투스 데이터가 수신되면
    read_data = BTSerial.read();//read_data변수에 수신 데이터 저장
    Serial.print("read_data : ");
    Serial.println(read_data);

    if (read_data == 'O') {//수신데이터가 O 이면 (예약된 상태)
      Serial.println("시작");
      reservation_flag = 1;//reservation_flag값을 1로 변경
    } else if (read_data == 'X') {//수신데이터가 X 이면 (종료된 상태)
      Serial.println("종료");
      reservation_flag = 0;//reservation_flag값을 0으로 변경
    }
  }

}
