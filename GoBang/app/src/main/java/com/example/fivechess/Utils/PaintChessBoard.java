package com.example.fivechess.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.fivechess.AI.Point;
import com.example.fivechess.R;

import java.util.ArrayList;

public class PaintChessBoard extends View {

    //画笔
    private Paint paint;
    //bitmap
    private Bitmap whiteChess;
    private Bitmap blackChess;
    //Rect
    private Rect rect;

    //棋盘宽高
    private float len;
    //棋盘格数
    private int GRID_NUMBER = 15;
    //每格之间的距离
    private float preWidth;
    //边距
    private float offset;
    //棋子数组
    private static int[][] chessArray = new int[15][15];

    private PutChessListener mPutChessListener;


    public PaintChessBoard(Context context) {
        this(context, null);
    }

    public PaintChessBoard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaintChessBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        //初始化Paint
        paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);

        //初始化棋子图片bitmap
        whiteChess = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_chess);
        blackChess = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_chess);

        //初始化Rect
        rect = new Rect();
    }
    public void setPutChessListener(PutChessListener listener) {
        mPutChessListener = listener;
    }

    public boolean putChess(boolean isWhite, int x, int y) {


        if (chessArray[x][y] != Constants.CHESS_NONE) {
            return false;
        }

        if (isWhite) {
            chessArray[x][y] = Constants.CHESS_WHITE;
        } else {
            chessArray[x][y] = Constants.CHESS_BLACK;
        }
        mPutChessListener.onPutChess(chessArray, x, y);
        postInvalidate();
        return true;
    }
    public Point convertPoint(float x, float y) {
        int i = (int) (Math.rint((x - (offset)) / preWidth));
        int j = (int) (Math.rint((y - (offset+4*preWidth)) / preWidth));
        Log.v("test",i+":"+j);
        Point point = new Point(i,j);
        return point;
    }
    /**
     * 重新测量宽高，确保宽高一样
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取高宽值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //获取宽高中较小的值
        int len = width > height ? height : width;
        //重新设置宽高
        setMeasuredDimension(len, len);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //棋盘为一个GRID_NUMBER*GRID_NUMBER的正方形，所有棋盘宽高必须一样
        len = getWidth() > getHeight() ? getHeight() : getWidth();
        preWidth = len / GRID_NUMBER;//每个的宽度
        //边距
        offset = preWidth / 2;

        Paint paintBackground = new Paint();
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.chessboard);
        canvas.drawBitmap(bitmap, null,
                new Rect((int) (offset-preWidth),(int)(offset -preWidth),(int)(offset+len) , (int)(offset+len)), paintBackground);
        //棋盘线条
        Paint point = new Paint();
        point.setAntiAlias(true);
        point.setColor(Color.BLACK);
        point.setStrokeWidth(10);

        for (int i = 0; i < GRID_NUMBER; i++) {
            float start = i * preWidth + offset;
            //横线
            canvas.drawLine(offset, start, len - offset, start, paint);

            //竖线
            canvas.drawLine(start, offset, start, len - offset, paint);
            if(i==3){
                canvas.drawPoint(start,start,point);
                canvas.drawPoint(start,len -start,point);
            }
            else if(i==7){
                canvas.drawPoint(start,start,point);
            }else if(i==11){
                canvas.drawPoint(start,start,point);
                canvas.drawPoint(start,len -start,point);
            }
        }
        //绘制棋子
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                //rect中点坐标
                float rectX = offset + i * preWidth;
                float rectY = offset + j * preWidth;
                //设置rect位置
                rect.set((int) (rectX - offset), (int) (rectY - offset),
                        (int) (rectX + offset), (int) (rectY + offset));
                //遍历chessArray
                switch (chessArray[i][j]) {
                    case Constants.CHESS_WHITE:
                        //绘制白棋
                        canvas.drawBitmap(whiteChess, null, rect, paint);
                        break;
                    case Constants.CHESS_BLACK:
                        //绘制黑棋
                        canvas.drawBitmap(blackChess, null, rect, paint);
                        break;
                }
            }
        }


    }
    public void RegretOnce(ArrayList<Point>list){
        for(Point p:list){
            chessArray[p.x][p.y]=Constants.CHESS_NONE;
        }
        postInvalidate();
    }
    public void resetGame() {
        //重置棋盘状态
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                chessArray[i][j] = 0;
            }
        }
        //更新UI
        postInvalidate();
    }

    public interface PutChessListener {
        void onPutChess(int[][] board, int x, int y);
    }

}
