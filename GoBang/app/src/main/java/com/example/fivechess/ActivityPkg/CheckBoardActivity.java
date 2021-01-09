package com.example.fivechess.ActivityPkg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fivechess.AI.AI;
import com.example.fivechess.AI.AICallBack;
import com.example.fivechess.AI.MediumAi;
import com.example.fivechess.AI.Point;
import com.example.fivechess.AI.SimpleAi;
import com.example.fivechess.R;
import com.example.fivechess.Utils.FiveChessView;
import com.example.fivechess.Utils.GameCallBack;
import com.example.fivechess.Utils.OperationQueue;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckBoardActivity extends AppCompatActivity implements  View.OnClickListener,  GameCallBack, AICallBack {
    private FiveChessView fiveChessView;
    private AI ai;
    private SimpleAi simple;
    private MediumAi medium;
    private OperationQueue mOperationQueue;
    private FloatingActionButton moveback;
    //PopUpWindow选择玩家执子
    private PopupWindow chooseChess;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkboard_activity);
        initViews();
        CurrDiffculty();
        fiveChessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //初始化PopupWindow
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                startMan_Machine(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
            }
        });

    }
    //TODO 困难选择
    private void CurrDiffculty(){
        Intent intent = getIntent ();
        int diff = intent.getIntExtra("rank",1);
        switch (diff){
            case 1 :
                ai = simple;
                Log.v("test","low");
                break;
            case 2 :
                ai = medium;
                Log.v("test","medium");
        }
    }
    @Override
    public void GameOver(int winner) {
        //更新游戏胜利局数

        switch (winner) {
            case FiveChessView.BLACK_WIN:
                showToast("黑棋胜利！");
                break;
            case FiveChessView.NO_WIN:
                showToast("平局！");
                break;
            case FiveChessView.WHITE_WIN:
                showToast("白棋胜利！");
                break;
        }
    }

    private void initViews(){
        fiveChessView = (FiveChessView) findViewById(R.id.five_chess_view);
        fiveChessView.setCallBack(this);
        simple = new SimpleAi(fiveChessView.getChessArray(), this);
        medium = new MediumAi(fiveChessView.getChessArray(), this);
        mOperationQueue = new OperationQueue();
        moveback = findViewById(R.id.action_a);
        moveback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doMoveBack();
            }
        });

    }

    private void doMoveBack() {
        if(mOperationQueue.size()<2){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("看清楚")
                    .setContentText("这能悔棋吗!")
                    .show();
        }
        ArrayList<Point>list = new ArrayList<>();
        for(int i=0;i<=1;i++){
            list.add(mOperationQueue.getLastOperation());
            mOperationQueue.removeLastOperation();
        }
        fiveChessView.MoveBack(list);
    }


    @Override
    public void ChangeGamer(boolean isWhite,int x,int y) {
        //ai回合
        Point p = new Point(x,y);
        mOperationQueue.addOperation(p);
        ai.updateArray(fiveChessView.getChessArray());
        ai.aiBout();
        //更改当前落子
    }

    private void startMan_Machine(int width, int height){
        if (chooseChess == null) {
            View view = View.inflate(this, R.layout.pop_choose_chess, null);
            ImageButton white = (ImageButton) view.findViewById(R.id.choose_white);
            ImageButton black = (ImageButton) view.findViewById(R.id.choose_black);
            white.setOnClickListener(this);
            black.setOnClickListener(this);
            chooseChess = new PopupWindow(view, width, height);
            chooseChess.setOutsideTouchable(false);
            chooseChess.showAtLocation(fiveChessView, Gravity.CENTER, 0, 0);
        }
    }
    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void aiAtTheBell(int x,int y) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Point p = new Point(x,y);
                mOperationQueue.addOperation(p);
                //更新UI
                fiveChessView.postInvalidate();
                //检查游戏是否结束
                fiveChessView.checkAiGameOver();
                //设置为玩家回合
                fiveChessView.setUserBout(true);
            }
        });
    }

    //根据玩家选择执子，更新UI
    private void changeUI(boolean isUserWhite) {
        if (isUserWhite) {
            //玩家选择白棋
            fiveChessView.setUserChess(FiveChessView.WHITE_CHESS);
            ai.setAiChess(FiveChessView.BLACK_CHESS);
            //玩家先手
            fiveChessView.setUserBout(true);

        } else {
            //玩家选择黑棋
            fiveChessView.setUserChess(FiveChessView.BLACK_CHESS);
            fiveChessView.setUserBout(false);
            //ai先手
            ai.setAiChess(FiveChessView.WHITE_CHESS);
            ai.aiBout();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_black:
                changeUI(false);
                chooseChess.dismiss();
                break;
            case R.id.choose_white:
                changeUI(true);
                chooseChess.dismiss();
                break;

        }
    }
}
