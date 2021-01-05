package com.example.fivechess.ActivityPkg;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fivechess.AI.AICallBack;
import com.example.fivechess.AI.SimpleAi;
import com.example.fivechess.R;
import com.example.fivechess.Utils.FiveChessView;
import com.example.fivechess.Utils.GameCallBack;

public class CheckBoardActivity extends AppCompatActivity implements  GameCallBack, AICallBack {
    private FiveChessView fiveChessView;
    private SimpleAi ai;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkboard_activity);

        fiveChessView = (FiveChessView) findViewById(R.id.five_chess_view);
        ai = new SimpleAi(fiveChessView.getChessArray(), this);
        fiveChessView.setCallBack(this);
        fiveChessView.setUserChess(FiveChessView.WHITE_CHESS);
        ai.setAiChess(FiveChessView.BLACK_CHESS);
        fiveChessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //初始化PopupWindow
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//                initPop(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
            }
        });

        fiveChessView.setUserBout(true);
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

    @Override
    public void ChangeGamer(boolean isWhite) {
        //ai回合
        ai.aiBout();
        //更改当前落子

    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void aiAtTheBell() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更新UI
                fiveChessView.postInvalidate();
                //检查游戏是否结束
                fiveChessView.checkAiGameOver();
                //设置为玩家回合
                fiveChessView.setUserBout(true);
            }
        });
    }
}
