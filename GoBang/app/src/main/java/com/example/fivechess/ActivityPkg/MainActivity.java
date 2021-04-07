package com.example.fivechess.ActivityPkg;
/*
* 菜单实现
* */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fivechess.R;
import com.example.fivechess.adapter.Listadapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private ImageButton fight;
    private ImageButton stand_one;
    private ImageButton competition;
    private ImageButton room;
    private FloatingActionButton exit;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    void initView(){
        fight = findViewById(R.id.fight_button);
        stand_one = findViewById(R.id.stand_alone);
        competition = findViewById(R.id.rank_button);
        room = findViewById(R.id.creat_room);
        exit = findViewById(R.id.action_a);
        context = this;
        addClickT();
    }
    void addClickT(){
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        fight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fight_choose();
            }
        });

        stand_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainMachineActivity.class);
                startActivity(intent);
            }
        });

        competition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new SweetAlertDialog(context)
//                        .setTitleText("现在没有比赛打!")
//                        .show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket("172.22.190.248", 8081);
                            OutputStream os = socket.getOutputStream();
                            os.write(123);
                            os.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    //选择蓝牙还是联网
    private void fight_choose(){
//        String [] data ={"蓝牙对战","联网对战"};//TODO 优化界面
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this,android.R.layout.simple_list_item_1,data);
        Listadapter adapter = new Listadapter(this);
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ListHolder())
                .setAdapter(adapter)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        switch (position) {
                            case 0:
                                //蓝牙
                                Intent intent = new Intent(MainActivity.this, BlueToothActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                //联网
                                break;
                        }
                    }
                })
                .setMargin(0, 0, 0, 0)
                .setContentBackgroundResource(R.drawable.chessboard)
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .setGravity(Gravity.CENTER)
                .create();
        dialog.show();
    }

}