package com.example.fivechess.ActivityPkg;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.fivechess.R;
import com.example.fivechess.Utils.GamePlayer;
import com.example.fivechess.adapter.MyListAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;

public class BlueToothActivity extends AppCompatActivity {

    private SwipeMenuListView listView;
    private AVLoadingIndicatorView avi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);


        listView = findViewById(R.id.listView);

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
                        avi = findViewById(R.id.avi);//开一个窗口加载动画
                        break;
                    case 1:
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        //设置向右划
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        //TODO
        List<GamePlayer> mPlayerDataList = new ArrayList<>();
        GamePlayer first = new GamePlayer();
        first.setName("工人春");
        first.setAccount("1234");
        first.setPhoto(R.drawable.icon_test);

        mPlayerDataList.add(first);

        MyListAdapter mMyListAdapter = new MyListAdapter(mPlayerDataList,this);
        listView.setAdapter(mMyListAdapter);
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
                openItem.setTitle("Open");
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
                openItem.setTitle("Close");
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        return creator;
    }

}
