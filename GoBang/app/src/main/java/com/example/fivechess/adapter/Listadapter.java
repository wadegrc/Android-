package com.example.fivechess.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fivechess.R;

public class Listadapter extends BaseAdapter {

    private Context context;
    private ImageView operation_img;
    private TextView operation_name;

    public Listadapter(Context context) {
        this.context = context;
    }
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_view, null);
        operation_name = (TextView)v.findViewById(R.id.oper_name);
        operation_img =  (ImageView) v.findViewById(R.id.oper_img);
        if(position == 0 ){
            operation_name.setText("蓝牙对战");
            operation_img.setImageDrawable(context.getResources().getDrawable((R.drawable.black_chess)));
        }else{
            operation_name.setText("联网对战");
            operation_img.setImageDrawable(context.getResources().getDrawable((R.drawable.white_chess)));
        }
        return v;
    }
}
