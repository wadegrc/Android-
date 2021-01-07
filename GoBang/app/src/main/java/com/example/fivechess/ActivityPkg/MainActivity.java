package com.example.fivechess.ActivityPkg;
/*
* 菜单实现
* */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fivechess.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private ImageButton fight;
    private ImageButton stand_one;
    private ImageButton competition;
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
        context = this;
        addClickT();
    }
    void addClickT(){
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
                new SweetAlertDialog(context)
                        .setTitleText("Here's a message!")
                        .show();
            }
        });
    }
    //选择蓝牙还是联网
    private void fight_choose(){
        String [] data ={"蓝牙对战","联网对战"};//TODO 优化界面

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_1,data);

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
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .setGravity(Gravity.CENTER)
                .create();
        dialog.show();
    }

}