package com.example.fivechess.ActivityPkg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fivechess.R;

public class MainMachineActivity extends AppCompatActivity {
    private ImageButton low;
    private ImageButton medium;
    private ImageButton high;
    private Intent intent;
    private ImageButton return_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.man_machine_activity);
        initview();
        addListen();

    }

    private void initview(){
        low =findViewById(R.id.low);
        medium = findViewById(R.id.medium);
        high = findViewById(R.id.high);
        return_main=findViewById(R.id.return_main);
        intent = new Intent(MainMachineActivity.this, CheckBoardActivity.class);

    }
    //其他难度
    private void addListen(){

        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra ( "rank", 1);
                startActivity(intent);
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra ( "rank", 2);
                startActivity(intent);
            }
        });

        return_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMachineActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
