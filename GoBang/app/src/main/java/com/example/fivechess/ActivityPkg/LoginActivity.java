package com.example.fivechess.ActivityPkg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fivechess.MusicServer;
import com.example.fivechess.NetServer.Constants;
import com.example.fivechess.NetServer.Message;
import com.example.fivechess.NetServer.User;
import com.example.fivechess.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText account;
    private EditText password;
    private ImageButton login;
    private ImageButton register;
    private String Accou;
    private String Name;
    private String Passw;
    private Message msg;
    private User user;
    private boolean result = true;
    private Context context;
    private Socket socket;
    private boolean isEnd=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(LoginActivity.this, MusicServer.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
        addListener();

        startService(intent);
    }
    private void initView(){
        account = findViewById(R.id.input_user_text);
        password = findViewById(R.id.input_key_text);
        login  = findViewById(R.id.denglu_button);
        register = findViewById(R.id.zhuce_button);
        context = this;
        msg = new Message();
        user = new User();
    }
    private void addListener(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContent();
                sendMsgToServer();

                if(result){
                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("恭喜你")
                            .setContentText("登录成功!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                else{
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("非常遗憾")
                            .setContentText("登录失败!")
                            .show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateContent(){
        Accou = account.getText().toString();
        Passw = password.getText().toString();
        user.setAccount(Accou);
        user.setPassword(Passw);
        msg.setUser(user);
        msg.setState(Constants.LOGIN);
    }
    private void sendMsgToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket=new Socket("192.168.43.237", 8081);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    out.writeObject(msg);
                    out.flush();
                    socket.shutdownOutput();

                    Message rmsg = (Message) in.readObject();
                    switch(rmsg.getState()){
                        case Constants.FAIL_TO_LOGIN:
                            result = false;
                            break;
                        case Constants.SUCCESS_TO_LOGIN:
                            result=true;
                            break;
                    }
                    socket.close();
                } catch (IOException | ClassNotFoundException e) {
                    result=false;
                    isEnd=false;
                    e.printStackTrace();
                }
                isEnd=false;
            }
        }).start();
    }
}
