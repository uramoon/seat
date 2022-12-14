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

        button_1.setOnClickListener(new View.OnClickListener() {//1??? ????????? ?????????
            public void onClick(View v) {
                vibrator.vibrate(500);//?????? 0.5???

                if (bt_flag == 1) {//bt_flag?????? 1?????? (???????????? ?????????????????????)
                    if (reservation_flag == 0) {//reservation_flag?????? 0?????? (????????? ???????????? ??????)
                        new AlertDialog.Builder(MainActivity.this).setMessage("\n" + "??? 1?????? ?????????????????????????" + "\n").setCancelable(false)
                                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //??? ????????? ?????????
                                        reservation_flag = 1;//reservation_flag????????? 1??? ??????
                                        reservation_count = out_count;//reservation_count?????? out_count????????? ??????
                                        bt.send("O", false);//??????????????? O ??????
                                        Handler hd = new Handler();
                                        hd.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt.send("O", false);// 0.1??? ?????? ????????? ??????
                                            }
                                        }, 100);
                                    }
                                })
                                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reservation_flag = 0;
                                    }
                                }).show();
                    } else {//reservation_flag?????? 1?????? (??????????????? ??????)
                        new AlertDialog.Builder(MainActivity.this).setMessage("\n" + "??? ?????? ???????????????????" + "\n").setCancelable(false)
                                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //??? ????????? ?????????
                                        reservation_flag = 0;//reservation_flag??? ?????????
                                        button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));//1??? ?????? ?????? ??????

                                        bt.send("X", false);//??????????????? X ??????
                                        Handler hd = new Handler();
                                        hd.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt.send("X", false);// 0.1??? ?????? ????????? ??????
                                            }
                                        }, 100);
                                    }
                                })
                                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    }
                } else {//bt_flag?????? 1??? ????????? (???????????? ???????????????????????????)
                    Toast.makeText(MainActivity.this, "???????????? ????????? ????????????????????????.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {//????????? ??????
            public void onDataReceived(byte[] data, String message) {
                //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                if (reservation_flag == 1) {//reservation_flag?????? 1 ?????? (????????? ??????)
                    if (message.equals("O")) {//?????????????????? O ??????
                        button_1.setBackgroundColor(Color.parseColor("#FF0000"));//1??? ?????? ???????????????
                        reservation_count = out_count;//reservation_count?????? out_count????????? ??????
                    } else if (message.equals("X")) {//?????????????????? X ??????
                        button_1.setBackgroundColor(Color.parseColor("#20FF0000"));//1??? ?????? ?????? ???????????????
                        vibrator.vibrate(100);//?????? 0.1???

                        Toast.makeText(MainActivity.this, "" + reservation_count / 2 + "?????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();

                        reservation_count--;//reservation_count?????? 1??? ??????
                        if (reservation_count <= 0) {//reservation_count?????? 0????????? 0?????? ????????? (out_count?????? ???????????? ?????? ??????????????? ?????????)

                            new AlertDialog.Builder(MainActivity.this).setMessage("\n" + "??? ????????? ?????? ?????? ?????????????????????." + "\n").setCancelable(false)
                                    .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();

                            reservation_flag = 0;//reservation_flag??? ?????????
                            button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));//1??? ?????? ?????? ??????

                            bt.send("X", false);//??????????????? X ??????
                            Handler hd = new Handler();
                            hd.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bt.send("X", false);// 0.1??? ?????? ????????? ??????
                                }
                            }, 100);
                        }
                    }

                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {//???????????? ???
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "???????????? ????????????\n" + name + " " + address
                        , Toast.LENGTH_SHORT).show();
                bt_flag = 1;//bt_flag?????? 1??? ??????
            }

            public void onDeviceDisconnected() {//????????????
                Toast.makeText(getApplicationContext()
                        , "???????????? ????????????", Toast.LENGTH_SHORT).show();
                bt_flag = 0;
                reservation_flag = 0;
                button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            public void onDeviceConnectionFailed() {//????????????
                Toast.makeText(getApplicationContext()
                        , "???????????? ????????????", Toast.LENGTH_SHORT).show();
                bt_flag = 0;
                reservation_flag = 0;
                button_1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        if (!bt.isBluetoothAvailable()) {//???????????? ?????? ??????
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        button_6.setOnClickListener(new View.OnClickListener() {//6??? ????????? ????????? ???????????? ????????????
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