package com.example.fivechess.ActivityPkg;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fivechess.AI.Point;
import com.example.fivechess.R;
import com.example.fivechess.Utils.GameJudger;
import com.example.fivechess.Utils.OperationQueue;
import com.example.fivechess.Utils.PaintChessBoard;
import com.example.fivechess.Utils.ToastUtil;
import com.example.fivechess.adapter.INetView;
import com.example.fivechess.adapter.NetPresenter;

import java.util.List;

import static com.example.fivechess.Utils.Constants.BLUE_TOOTH_MODE;

/*
* 蓝牙对战面板
* */
public class BlueToothGame extends AppCompatActivity implements INetView, PaintChessBoard.PutChessListener
        , View.OnTouchListener, View.OnClickListener{

    private static final int MOVE_BACK_TIMES = 2;
    private boolean mIsHost;
    private boolean mIsMePlay = false;
    private boolean mIsGameEnd = false;
    private boolean mIsOpponentLeaved = false;
    private boolean mCanClickConnect = true;
    private int mLeftMoveBackTimes = MOVE_BACK_TIMES;

    private OperationQueue mOperationQueue;

    private NetPresenter mNetPresenter;


    private PaintChessBoard mBoard;

    private static final String NET_MODE = "netMode";

    //创建时操作
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    //销毁时操作
    @Override
    public void onDestroy() {
        super.onDestroy();
        unInit();
    }






    private void initView() {
        mBoard = (PaintChessBoard) findViewById(R.id.go_bang_board);
        mBoard.setPutChessListener(this);
    }

    private void init() {
        int gameMode = BLUE_TOOTH_MODE;
        mNetPresenter = new NetPresenter(this, this, gameMode);
        mNetPresenter.init();
        //悔棋的队列
        mOperationQueue = new OperationQueue();
    }

    private void unInit() {
        mNetPresenter.unInit();
    }

    private void sendMessage(String message) {
        mNetPresenter.sendToDevice(message, mIsHost);
    }

    @Override
    public void onGetPairedToothPeers(List<BluetoothDevice> deviceList) {

    }

    @Override
    public void onFindBlueToothPeers(List<BluetoothDevice> deviceList) {

    }

    //没有发现对手
    @Override
    public void onPeersNotFound() {

    }


    //消息发送失败
    @Override
    public void onSendMessageFailed() {

    }

    //蓝牙连接成功 TODO
    @Override
    public void onBlueToothDeviceConnected() {
        ToastUtil.showShort(this, "蓝牙连接成功");
        if (mIsHost) {

        }
    }

    //收到数据 TODO
    @Override
    public void onDataReceived(String o) {
//        try {
//
//            switch (o) {
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onBlueToothDeviceConnectFailed() {
        ToastUtil.showShort(this ,"蓝牙连接失败");
        mCanClickConnect = true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mIsGameEnd && mIsMePlay) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    Point point = new Point((int)x,(int)y);
                    String res = x + ":" + y + ":";
                    if (mBoard.putChess(mIsHost, point.x, point.y)) {
                        sendMessage(res);
                        mIsMePlay = false;
                    }
                }
                break;
        }
        return false;
    }


    @Override
    public void onPutChess(int[][] board, int x, int y) {
        if (mIsMePlay && GameJudger.isGameEnd(board, x, y)) {
            ToastUtil.showShort(this, "你赢了");
            String end = "loss";
            sendMessage(end);
            mIsMePlay = false;
            mIsGameEnd = true;
        }
        Point point = new Point(x, y);
        mOperationQueue.addOperation(point);
    }


    @Override
    public void onClick(View v) {

    }
}
