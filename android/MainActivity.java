package com.example.libraryreservation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private BluetoothSPP bt;
    int bt_flag = 0;
    int reservation_flag = 0;
    int reservation_count = 0;
    int out_count = 60;
    Vibrator vibrator;
    private Button button_1, button_2, button_3, button_4, button_5, button_6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = new BluetoothSPP(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        button_1 = findViewById(R.id.button_1);
        button_2 = findViewById(R.id.button_2);
        button_3 = findViewById(R.id.button_3);
        button_4 = findViewById(R.id.button_4);
        button_5 = findViewById(R.id.button_5);
        button_6 = findViewById(R.id.button_6);

        button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        button_2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        button_3.setBackgroundColor(Color.parseColor("#FFFFFF"));
        button_4.setBackgroundColor(Color.parseColor("#FFFFFF"));
        button_5.setBackgroundColor(Color.parseColor("#FFFFFF"));
        button_6.setBackgroundColor(Color.parseColor("#FFFFFF"));

        button_1.setOnClickListener(new View.OnClickListener() {//1번 버튼을 누르면
            public void onClick(View v) {
                vibrator.vibrate(500);//진동 0.5초

                if (bt_flag == 1) {//bt_flag값이 1이면 (블루투스 연결되어있으면)
                    if (reservation_flag == 0) {//reservation_flag값이 0이면 (예약이 안된상태 이면)
                        new AlertDialog.Builder(MainActivity.this).setMessage("\n" + "• 1번을 이용하시겠습니까?" + "\n").setCancelable(false)
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //예 버튼을 누르면
                                        reservation_flag = 1;//reservation_flag값ㅇ르 1로 변경
                                        reservation_count = out_count;//reservation_count값을 out_count값으로 변경
                                        bt.send("O", false);//블루투스로 O 전송
                                        Handler hd = new Handler();
                                        hd.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt.send("O", false);// 0.1초 뒤에 한번더 전송
                                            }
                                        }, 100);
                                    }
                                })
                                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reservation_flag = 0;
                                    }
                                }).show();
                    } else {//reservation_flag값이 1이면 (예약된상태 이면)
                        new AlertDialog.Builder(MainActivity.this).setMessage("\n" + "• 종료 하시겠습니까?" + "\n").setCancelable(false)
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //예 버튼을 누르면
                                        reservation_flag = 0;//reservation_flag값 초기화
                                        button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));//1번 버튼 배경 흰색

                                        bt.send("X", false);//블루투스로 X 전송
                                        Handler hd = new Handler();
                                        hd.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt.send("X", false);// 0.1초 뒤에 한번더 전송
                                            }
                                        }, 100);
                                    }
                                })
                                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    }
                } else {//bt_flag값이 1이 아니면 (블루투스 연결되어있지않으면)
                    Toast.makeText(MainActivity.this, "블루투스 연결이 되어있지않습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {//데이터 수신
            public void onDataReceived(byte[] data, String message) {
                //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                if (reservation_flag == 1) {//reservation_flag값이 1 이면 (예약된 상태)
                    if (message.equals("O")) {//수신데이터가 O 이면
                        button_1.setBackgroundColor(Color.parseColor("#FF0000"));//1번 버튼 빨간색으로
                        reservation_count = out_count;//reservation_count값을 out_count값으로 변경
                    } else if (message.equals("X")) {//수신데이터가 X 이면
                        button_1.setBackgroundColor(Color.parseColor("#20FF0000"));//1번 버튼 연한 빨간색으로
                        vibrator.vibrate(100);//진동 0.1초

                        Toast.makeText(MainActivity.this, "" + reservation_count / 2 + "초후 자동 종료됩니다.", Toast.LENGTH_SHORT).show();

                        reservation_count--;//reservation_count값을 1씩 뺀다
                        if (reservation_count <= 0) {//reservation_count값이 0이거나 0보다 작으면 (out_count값의 시간만큼 계속 압력감지가 안되면)

                            new AlertDialog.Builder(MainActivity.this).setMessage("\n" + "• 시간이 지나 자동 종료되었습니다." + "\n").setCancelable(false)
                                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();

                            reservation_flag = 0;//reservation_flag값 초기화
                            button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));//1번 버튼 배경 흰색

                            bt.send("X", false);//블루투스로 X 전송
                            Handler hd = new Handler();
                            hd.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bt.send("X", false);// 0.1초 뒤에 한번더 전송
                                }
                            }, 100);
                        }
                    }

                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {//연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "블루투스 연결성공\n" + name + " " + address
                        , Toast.LENGTH_SHORT).show();
                bt_flag = 1;//bt_flag값을 1로 변경
            }

            public void onDeviceDisconnected() {//연결해제
                Toast.makeText(getApplicationContext()
                        , "블루투스 연결해제", Toast.LENGTH_SHORT).show();
                bt_flag = 0;
                reservation_flag = 0;
                button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            public void onDeviceConnectionFailed() {//연결실패
                Toast.makeText(getApplicationContext()
                        , "블루투스 연결실패", Toast.LENGTH_SHORT).show();
                bt_flag = 0;
                reservation_flag = 0;
                button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        if (!bt.isBluetoothAvailable()) {//블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        button_6.setOnClickListener(new View.OnClickListener() {//6번 버튼을 누르면 블루투스 연결시도
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}