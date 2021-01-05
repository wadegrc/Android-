package com.example.fivechess.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fivechess.R;
import com.example.fivechess.Utils.GamePlayer;

import java.util.List;

/**
 * Created by lum on 2018/5/27.
 */

public class MyListAdapter extends BaseAdapter {

    private List<GamePlayer> mPlayerList;   //创建一个StudentData 类的对象 集合
    private LayoutInflater inflater;

    public  MyListAdapter (List<GamePlayer> mPlayerList, Context context) {
        this.mPlayerList = mPlayerList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mPlayerList == null?0:mPlayerList.size();  //判断有说个Item
    }

    @Override
    public Object getItem(int position) {
        return mPlayerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.bluetooth_activity,null);
        GamePlayer mPlayer = (GamePlayer) getItem(position);

        //在view 视图中查找 组件
        TextView tv_name = (TextView) view.findViewById(R.id.gamer_name);
        TextView tv_account = (TextView) view.findViewById(R.id.gamer_accou);
        ImageView im_photo = (ImageView) view.findViewById(R.id.gamer_img);

        //为Item 里面的组件设置相应的数据
        tv_name.setText(mPlayer.getName());
        tv_account.setText(mPlayer.getAccount());
        im_photo.setImageResource(mPlayer.getPhoto());

        //返回含有数据的view
        return view;
    }
}

