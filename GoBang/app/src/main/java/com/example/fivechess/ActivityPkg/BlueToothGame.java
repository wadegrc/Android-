package com.example.fivechess.ActivityPkg;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.fivechess.AI.Point;
import com.example.fivechess.R;
import com.example.fivechess.Utils.BlueToothWrapper;
import com.example.fivechess.Utils.Constants;
import com.example.fivechess.Utils.GameJudger;
import com.example.fivechess.Utils.OperationQueue;
import com.example.fivechess.Utils.PaintChessBoard;
import com.example.fivechess.Utils.ToastUtil;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.robinhood.ticker.TickerView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

/*
* 蓝牙对战面板
* */
public class BlueToothGame extends Activity implements PaintChessBoard.PutChessListener, View.OnClickListener {

    private boolean mIsHost;


    private boolean mIsMePlay = false;
    private boolean mIsGameEnd = false;
    private OperationQueue mOperationQueue;
    private int WhiteOrBlack = Constants.CHESS_BLACK;
    private int time = 0;
    private int my_time = 20;
    private PaintChessBoard mBoard;
    private TickerView total_timer;
    private TickerView my_timer;
    private Timer ttimer;
    private BlueToothWrapper wrapper;
    final String key = "FENG_YE_BING";
    private BroadcastReceiver receiver;
    private Context context;
    private FloatingActionButton moveback;
    //创建时操作
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blue_toothgame_activity);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mIsHost = bundle.getBoolean("isHost");
        if(mIsHost)
            mIsMePlay=true;
        initView();
    }

    //销毁时操作
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mBoard = findViewById(R.id.go_bang_board);
        total_timer = findViewById(R.id.total_timer);
//        my_timer = findViewById(R.id.mytimer);
        total_timer.setPreferredScrollingDirection(TickerView.ScrollingDirection.DOWN);
//        my_timer.setPreferredScrollingDirection(TickerView.ScrollingDirection.DOWN);
        mOperationQueue = new OperationQueue();
        context = this;
        moveback = findViewById(R.id.action_a1);
        //注册广播
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("receive");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (data){
                            case "REGRET":
                                canRegret();
                                break;
                            case "CONFIRM":
                                moveback();
                                break;
                            case "REFUSE":
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("非常遗憾")
                                        .setContentText("ta拒绝了你的请求!")
                                        .show();
                                break;
                            case "RESTART":

                                break;
                            default:
                                String[] temps = data.split(":");
                                Log.v("test",temps[0]+":"+temps[1]);
                                mBoard.putChess(mIsHost,Integer.parseInt(temps[0]),Integer.parseInt(temps[1]));
                                mIsMePlay=true;
                        }
                    }
                });

            }
        };
        IntentFilter filter = new IntentFilter("GONG_REN_CHUN");
        registerReceiver(receiver, filter);
        //我的倒计时开始
        Timer timer = new Timer();
        my_time myTask = new my_time();
        timer.schedule(myTask, 1000, 1000);

        //总计时器开启
        ttimer = new Timer();
        total_time Task = new total_time();
        ttimer.schedule(Task, 1000, 1000);
        mBoard.setPutChessListener(this);

        moveback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res = "REGRET";
                Intent intent = new Intent(key);
                intent.putExtra("send", res);
                sendBroadcast(intent);
            }
        });
    }

    private void canRegret(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("悔棋")
                .setContentText("刚才失误了!")
                .setConfirmText("同意")
                .setCancelText("拒绝")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        moveback();

                        String res = "CONFIRM";
                        Intent intent = new Intent(key);
                        intent.putExtra("send", res);
                        sendBroadcast(intent);
                        sDialog.cancel();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        String res = "REFUSE";
                        Intent intent = new Intent(key);
                        intent.putExtra("send", res);
                        sendBroadcast(intent);
                        sDialog.cancel();
                    }
                })
                .show();
    }
    @Override
    public void onClick(View v) {

    }

    void moveback(){
        if(mOperationQueue.size()<2){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("看清楚")
                    .setContentText("这能悔棋吗!")
                    .show();
        }
        ArrayList<Point> list = new ArrayList<>();
        for(int i=0;i<=1;i++){
            list.add(mOperationQueue.getLastOperation());
            mOperationQueue.removeLastOperation();
        }
        mBoard.RegretOnce(list);

    }

    private class my_time extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    my_time--;
                    total_timer.setText(String.format("%02d:%02d", time / 60 % 60, time % 60));
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.v("touch", "call");
                if (!mIsGameEnd && mIsMePlay) {
                    float x = event.getX();
                    float y = event.getY();
                    Point point = mBoard.convertPoint(x, y);
                    String res = (point.x) + ":" + (point.y) + ":" + (WhiteOrBlack == Constants.CHESS_WHITE ? 1 : 2);
                    if (mBoard.putChess(!mIsHost, point.x, point.y)) {
                        Intent intent = new Intent(key);
                        intent.putExtra("send", res);
                        sendBroadcast(intent);
                        mIsMePlay = false;
                    }
                }
                break;
        }
        Log.v("touch", "call");
        return super.onTouchEvent(event);
    }

    private class total_time extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    time++;
                    total_timer.setText(String.format("%02d:%02d", time / 60 % 60, time % 60));
                }
            });
        }
    }


    @Override
    public void onPutChess(int[][] board, int x, int y) {
        if (mIsMePlay && GameJudger.isGameEnd(board, x, y)) {
            ToastUtil.showShort(this, "你赢了");
            String end = "loss";
            //结束信息
            mIsMePlay = false;
            mIsGameEnd = true;
        }
        Point point = new Point(x, y);
        mOperationQueue.addOperation(point);
    }

}


