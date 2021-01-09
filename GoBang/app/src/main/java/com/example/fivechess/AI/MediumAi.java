package com.example.fivechess.AI;

import com.example.fivechess.Utils.Constants;
import com.example.fivechess.Utils.GameJudger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * AI算法和主函数入口
 */


public class MediumAi extends AI{
    //
    private int aiChess = Constants.CHESS_BLACK;
    private  int[][] chessBoard ; //棋盘棋子的摆放情况：0无子，1黑子，－1白子
    private static HashSet<Point> toJudge=new HashSet<Point>(); // ai可能会下棋的点
    private static int dr[]=new int[]{-1,1,-1,1,0,0,-1,1}; // 方向向量
    private static int dc[]=new int[]{1,-1,-1,1,-1,1,0,0}; //方向向量
    public static final int MAXN=1<<28;
    public static final int MINN=-MAXN;
    private static int searchDeep=4;    //搜索深度
    private static final int size=14;   //棋盘大小
    public static boolean isFinished=false;
    private int stand_score = 10; //基础分值
    private int isFirstHand = 0; //AI是否先手
    //ai落子结束回调
    private AICallBack callBack;


    private boolean first_down =true;

    public MediumAi(int[][] chessArray, AICallBack callBack) {
        this.chessBoard = chessArray;
        this.callBack = callBack;
    }
    void addTojudge(){
        int cnt = 0;
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                if(chessBoard[i][j]!=0){
                    cnt++;
                    Point p = new Point(i,j);
                    if(toJudge.contains(p)){
                        toJudge.remove(p);
                    }
                    for(int k=0;k<8;k++){
                        if(i+dr[k]>=0&&i+dr[k]<=size&&j+dc[k]>=0&&j+dc[k]<=size&&chessBoard[i+dr[k]][j+dc[k]]==0){
                            toJudge.add(new Point(i+dr[k],j+dc[k]));
                        }
                    }
                }
            }
        }
        if(cnt==0){

        }
    }
    //ai开始落子，开启线程，计算最佳（优先级评分最高）的落子点，并落子
    public void aiBout( ) {
        addTojudge();
        Node node=new Node();
        //TODO
        dfs(0,node,MINN,MAXN);
        Point now= node.bestChild.p;
        chessBoard[now.getX()][now.getY()] = aiChess;
        callBack.aiAtTheBell(now.getX(),now.getY());
    }
    public void updateArray(int[][] chessArray) {
        this.chessBoard = chessArray;
    }
    public void setAiChess(int aiChess) {
        switch (aiChess){
            case 1://AI后手
                isFirstHand = 1;
                break;
            case -1://ai先手
                isFirstHand = 0;
                break;
        }
        this.aiChess = aiChess;
    }

    // alpha beta dfs
    public void dfs(int deep,Node node,int alpha,int beta){
        if(deep == searchDeep){
            node.mark = getMark();
            return ;
        }
        ArrayList<Point>canTake = new ArrayList<Point>();
        Iterator it = toJudge.iterator();
        //当前层可下的位置
        while(it.hasNext()){
            Point now = new Point((Point)it.next());
            canTake.add(now);
        }
        for (Point position:
             canTake) {
            //挂载子节点
            Node currNode = new Node();
            currNode.setPoint(position);
            node.addChild(currNode);
            //准备查询currNode的子节点
            chessBoard[position.x][position.y] = (((deep&1) == 1)?-aiChess:aiChess);
            boolean end = GameJudger.isGameEnd(chessBoard,position.x, position.y);
            if(end){
                node.mark = (((deep&1) == 1)?MINN:MAXN);
                chessBoard[position.x][position.y]=Constants.CHESS_NONE;
            }

            for(int i=0;i<8;i++){
                int x = position.x + dr[i];
                int y = position.y + dc[i];
                if(x>=0&&x<=size&&y>=0&&y<=size&&chessBoard[x][y]==Constants.CHESS_NONE){
                    Point currPos = new Point(x,y);
                    toJudge.add(currPos);
                }
            }

            toJudge.remove(position);

            dfs(deep+1,currNode,alpha,beta);

            toJudge.add(position);
            chessBoard[position.x][position.y]=Constants.CHESS_NONE;
            for(int i=0;i<8;i++){
                int x = position.x + dr[i];
                int y = position.y + dc[i];
                if(x>=0&&x<=size&&y>=0&&y<=size&&chessBoard[x][y]==Constants.CHESS_NONE){
                    Point currPos = new Point(x,y);
                    toJudge.remove(currPos);
                }
            }

            //min层
            if((deep & 1)==1){
                if(node.bestChild==null || node.mark>currNode.mark){
                    node.mark = currNode .mark;
                    node.bestChild  = node;
                    beta = Math.min(beta,node.mark);
                }
                if(node.mark<=alpha)
                    return;
            }else{
                if(node.bestChild==null || node.mark<currNode.mark){
                    node.mark = currNode.mark;
                    node.bestChild = currNode;
                    alpha = Math.max(alpha,node.mark);
                }
                if(node.mark>=beta){
                    return;
                }
            }
        }
    }

    //计算分值
    public  int getMark(){
        int res=0;
        for(int i=0;i<=size;++i){
            for(int j=0;j<=size;++j){
                if(chessBoard[i][j]!=Constants.CHESS_NONE){
                    // 行
                    boolean flag1=false,flag2=false;
                    int x=i,y=j;
                    int cnt=1;
                    int row=x,col=y;
                    //状态为左右两边都没有挡住
                    while(--col>=0 && chessBoard[row][col]==chessBoard[x][y]) ++cnt;//一行的左边与节点相等的棋子数
                    if(col>=0 && chessBoard[row][col]==Constants.CHESS_NONE) flag1=true;
                    row=x;col=y;
                    while(++col<=size && chessBoard[row][col]==chessBoard[x][y]) ++cnt;//一行的右边与节点相等的棋子数
                    if(col<=size && chessBoard[row][col]==Constants.CHESS_NONE) flag2=true;
                    if(flag1 && flag2)
                        res+=stand_score*cnt*cnt;//分数为棋子*左右两边相同棋子的数量
                        // 有一边被挡住的状态
                    else if(flag1 || flag2) res+=chessBoard[i][j]*cnt*cnt/4;
                    if(cnt>=5) res=MAXN*stand_score;//五子连珠，分值最大
                    // 列
                    row=x;col=y;
                    cnt=1;flag1=false;flag2=false;
                    while(--row>=0 && chessBoard[row][col]==chessBoard[x][y]) ++cnt;//一列的左边与节点相等的棋子数
                    if(row>=0 && chessBoard[row][col]==Constants.CHESS_NONE) flag1=true;//左边是否被挡
                    row=x;col=y;
                    while(++row<=size && chessBoard[row][col]==chessBoard[x][y]) ++cnt;//一列的右边与节点相等的棋子数
                    if(row<=size && chessBoard[row][col]==Constants.CHESS_NONE) flag2=true;//右边是否被挡
                    if(flag1 && flag2)//都没有被挡
                        res+=stand_score*cnt*cnt;
                    else if(flag1 || flag2)//挡一边
                        res+=stand_score*cnt*cnt/4;
                    if(cnt>=5) res=MAXN*stand_score;//五子连珠，分值最大
                    // 左对角线 同理
                    row=x;col=y;
                    cnt=1;flag1=false;flag2=false;
                    while(--col>=0 && --row>=0 && chessBoard[row][col]==chessBoard[x][y]) ++cnt;
                    if(col>=0 && row>=0 && chessBoard[row][col]==Constants.CHESS_NONE) flag1=true;
                    col=x;row=y;
                    while(++col<=size && ++row<=size && chessBoard[row][col]==chessBoard[x][y]) ++cnt;
                    if(col<=size && row<=size && chessBoard[row][col]==Constants.CHESS_NONE) flag2=true;
                    if(flag1 && flag2)
                        res+=stand_score*cnt*cnt;
                    else if(flag1 || flag2) res+=stand_score*cnt*cnt/4;
                    if(cnt>=5) res=MAXN*stand_score;
                    // 右对角线 同理
                    row=x;col=y;
                    cnt=1;flag1=false;flag2=false;
                    while(++row<=size && --col>=0 && chessBoard[row][col]==chessBoard[x][y]) ++cnt;
                    if(row<=size && col>=0 && chessBoard[row][col]==Constants.CHESS_NONE) flag1=true;
                    row=x;col=y;
                    while(--row>=0 && ++col<=size && chessBoard[row][col]==chessBoard[x][y]) ++cnt;
                    if(row>=0 && col<=size && chessBoard[i][j]==Constants.CHESS_NONE) flag2=true;
                    if(flag1 && flag2)
                        res+=stand_score*cnt*cnt;
                    else if(flag1 || flag2) res+=stand_score*cnt*cnt/4;
                    if(cnt>=5) res=MAXN*stand_score;

                }
            }
        }
        return res;//返回最后得分
    }

    // for debug
    public  void debug(){
        for(int i=0;i<=size;++i){
            for(int j=0;j<=size;++j){
                System.out.printf("%d\t",chessBoard[i][j]);
            }
            System.out.println("");
        }
    }

    // 判断是否一方取胜
    public  boolean isEnd(int x,int y){
        // 判断一行是否五子连珠
        int cnt=1;
        int col=x,row=y;
        while(--col>=0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        col=x;row=y;
        while(++col<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        if(cnt>=5){
            isFinished=true;
            return true;
        }
        // 判断一列是否五子连珠
        col=x;row=y;
        cnt=1;
        while(--row>=0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        col=x;row=y;
        while(++row<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        if(cnt>=5){
            isFinished=true;
            return true;
        }
        // 判断左对角线是否五子连珠
        col=x;row=y;
        cnt=1;
        while(--col>=0 && --row>=0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        col=x;row=y;
        while(++col<=size && ++row<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        if(cnt>=5){
            isFinished=true;
            return true;
        }
        // 判断右对角线是否五子连珠
        col=x;row=y;
        cnt=1;
        while(++row<=size && --col>=0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        col=x;row=y;
        while(--row>=0 && ++col<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        if(cnt>=5){
            isFinished=true;
            return true;
        }
        return false;
    }



}


// 树节点
class Node{
    public Node bestChild=null;
    public ArrayList<Node> child=new ArrayList<Node>();
    public Point p=new Point();
    public int mark;
    Node(){
        this.child.clear();
        bestChild=null;
        mark=0;
    }
    public void setPoint(Point r){
        p.x=r.x;
        p.y=r.y;
    }
    public void addChild(Node r){
        this.child.add(r);
    }
    public Node getLastChild(){
        return child.get(child.size()-1);
    }
}


