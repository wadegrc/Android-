package com.example.fivechess.ActivityPkg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.fivechess.NetServer.Constants;
import com.example.fivechess.NetServer.Message;
import com.example.fivechess.NetServer.User;
import com.example.fivechess.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {
    private ActionProcessButton btnSignIn;
    private TextView account;
    private TextView passw;
    private TextView name;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        initView();
        addListener();
    }

    void initView(){
        context = this;
        btnSignIn = findViewById(R.id.btnSignIn);
        account = findViewById(R.id.accou);
        passw = findViewById(R.id.passw);
        name =findViewById(R.id.name);
        msg = new Message();
        user = new User();
    }

    void addListener(){
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateArg();
                register();
               while(isEnd);
                if(result){
                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("恭喜你")
                            .setContentText("注册成功!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                else{
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("非常遗憾")
                            .setContentText("注册失败!")
                            .show();
                }
            }
        });
    }

    void updateArg(){
        Accou = account.getText().toString();
        Passw = passw.getText().toString();
        Name =name.getText().toString();
        user.setAccount(Accou);
        user.setName(Name);
        user.setPassword(Passw);
        msg.setUser(user);
        msg.setState(Constants.REGISTER);
    }
    void register(){
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
                        case Constants.FAIL_TO_REGISTER:
                            result = false;
                            break;
                        case Constants.SUCCESS_TO_REGISTER:
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
