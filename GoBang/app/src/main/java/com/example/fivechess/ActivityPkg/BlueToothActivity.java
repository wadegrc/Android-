package com.example.fivechess.ActivityPkg;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.fivechess.R;
import com.example.fivechess.Utils.Device;
import com.example.fivechess.Utils.ToastUtil;
import com.example.fivechess.adapter.BlueToothConnAdap;
import com.example.fivechess.adapter.INetView;
import com.example.fivechess.adapter.NetPresenter;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.example.fivechess.Utils.Constants.BLUE_TOOTH_MODE;

/*
* 发现好友列表
* 点击发起挑战时，跳转到游戏页面
* 游戏页面需要通过蓝牙交流，所以得有dataListen，所以要将NetPresenter传过去
* NetPresenter继承自callback，里面的函数都可以实现，并且在之后调用
* 进度条在搜索和连接的时候使用 TODO*/
public class BlueToothActivity extends AppCompatActivity implements INetView {

    private SwipeMenuListView listView;//显示列表
    private ArrayList<Device>playerlist;//搜索到的匹配玩家，用来更新列表
    private ArrayList<BluetoothDevice> devices;//存储搜索到的设备信息，用于连接
    private BlueToothConnAdap deviceshowAdapter;
    private ActionProcessButton btn_saomiao;
    private NetPresenter mNetPresenter;
    private int WhoIsFighter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);
        init();
        initView();
    }



    private void init() {
        mNetPresenter = new NetPresenter(this, this, BLUE_TOOTH_MODE);
        mNetPresenter.init();
        //悔棋的队列
    }

    private void initView() {
        mNetPresenter.startService();
        mNetPresenter.findPeers();
        //得到扫描周围蓝牙设备按钮
        btn_saomiao =  findViewById(R.id.btnSignIn);
        //扫描周围设备的ListView
        listView = findViewById(R.id.listView);
        //设备信息ArrayList
        playerlist = new ArrayList<>();
        //设备ArrayList
        devices = new ArrayList<>();
        //显示蓝牙设备信息的adapter
        deviceshowAdapter = new BlueToothConnAdap(this, playerlist);
        listView.setAdapter(deviceshowAdapter);
        //绑定扫描周围蓝牙设备按钮监听器
        btn_saomiao.setOnClickListener(new SaoMiaoButtonListener());
        fight_list();
    }

    private void Connect(BluetoothDevice device){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("输了可不准哭鼻子!")
                .setConfirmText("I Know!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        mNetPresenter.connectToHost(device);
                        mNetPresenter.sendToDevice("FIGHT",true);

                        sDialog
                                .setTitleText("连接成功!")
                                .setContentText("准备好了吗!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();

    }

    //蓝牙对战的列表加载处
    private void fight_list(){
        listView.setMenuCreator(creatorProvider());

        //设置点击事件
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        WhoIsFighter = position;
                        Connect(devices.get(position));
                        break;
                    case 1:
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        //设置向右划
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

    }
    private SwipeMenuCreator creatorProvider(){
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("连接");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
//                deleteItem.setIcon(R.drawable.);
                deleteItem.setTitle("删除");
                //设置字体颜色
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        return creator;
    }










    //蓝牙连接成功 TODO
    @Override
    public void onBlueToothDeviceConnected() {
        ToastUtil.showShort(this, "蓝牙连接成功");
    }
    @Override
    public void onBlueToothDeviceConnectFailed() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("非常遗憾")
                .setContentText("建立连接失败!")
                .show();
    }

    @Override
    public void onGetPairedToothPeers(List<BluetoothDevice> deviceList) {
        Log.v("gong","test");
        for (BluetoothDevice device:
             deviceList) {
            devices.add(device);
            Device dev = new Device(device.getName(),device.getAddress(),1);
            playerlist.add(dev);
        }
        deviceshowAdapter.setDevices(playerlist);
        deviceshowAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFindBlueToothPeers(List<BluetoothDevice> deviceList) {
        Log.v("gong","test1");
        for (BluetoothDevice device:
                deviceList) {
            devices.add(device);
            Device dev = new Device(device.getName(),device.getAddress(),0);
            playerlist.add(dev);
        }
        deviceshowAdapter.setDevices(playerlist);
        deviceshowAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPeersNotFound() {

    }

    /*
    * 接收到数据应做出的处理*/
    @Override
    public void onDataReceived(String o) {
        switch(o){
            case "REJECT":
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("非常遗憾")
                        .setContentText("ta不想和你玩!")
                        .show();
                break;
            case "ACCEPT":
                Intent intent = new Intent(BlueToothActivity.this,BlueToothGame.class);
                intent.putExtra("competitor",WhoIsFighter);
                startActivity(intent);
                break;
            case "FIGHT":
                receiveChall();
                break;
            default:
                break;
        }
    }

    void receiveChall(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("挑战书")
                .setContentText("素问兄台棋力深厚，可否此小弟一败!")
                .setConfirmText("老夫来也")
                .setCancelText("算了")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        mNetPresenter.sendToDevice("ACCEPT",false);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        mNetPresenter.sendToDevice("REJECT",false);
                    }
                })
                .show();
    }
    @Override
    public void onSendMessageFailed() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("难受")
                .setContentText("连接好像不太稳定!")
                .show();
    }


    //扫描周围的蓝牙设备按钮监听器
    private class SaoMiaoButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.v("gong",""+playerlist.size());
            //添加进度条
            playerlist.clear();
            mNetPresenter.findPeers();
        }
    }
    private void unInit() {
        mNetPresenter.unInit();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unInit();
    }
}
