package com.example.messenger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int itemHeight = 165;

        ListView firstListView = findViewById(R.id.firstListView);
        ListView secondListView = findViewById(R.id.secondListView);
        ListView thirdListView = findViewById(R.id.thirdListView);
        ListView fourthListView = findViewById(R.id.fourthListView);

        String[] firstSectionLabels = {"Chế độ tối", "Tin nhắn đang chờ", "Đoạn chat đã lưu trữ"};
        String[] secondSectionLabels = {"Trạng thái hoạt động", "Tên người dùng"};
        String[] thirdSectionLabels = {"Quyền riêng tư", "Avatar", "Thông báo và âm thanh", "Tin", "SMS", "Danh bạ điện thoại", "Ảnh và file phương tiện", "Bong bóng"};
        String[] fourthSectionLabels = {"Chuyển tài khoản", "Cài đặt tài khoản", "Trợ giúp", "Chính sách và quyền lợi"};

        Integer[] firstSectionIcons = {};
        Integer[] secondSectionIcons = {};
        Integer[] thirdSectionIcons = {};
        Integer[] fourthSectionIcons = {};

        AdapterSettings firstAdapter, secondAdapter, thirdAdapter, fourthAdapter;

        firstListView.getLayoutParams().height = firstSectionLabels.length* itemHeight;
        secondListView.getLayoutParams().height = secondSectionLabels.length* itemHeight;
        thirdListView.getLayoutParams().height = thirdSectionLabels.length * itemHeight;
        fourthListView.getLayoutParams().height = fourthSectionLabels.length * itemHeight;

        firstAdapter = new AdapterSettings(MainActivity.this, R.layout.layout_settings_item, firstSectionLabels);
        secondAdapter = new AdapterSettings(MainActivity.this, R.layout.layout_settings_item, secondSectionLabels);
        thirdAdapter = new AdapterSettings(MainActivity.this, R.layout.layout_settings_item, thirdSectionLabels);
        fourthAdapter = new AdapterSettings(MainActivity.this, R.layout.layout_settings_item, fourthSectionLabels);

        firstListView.setAdapter(firstAdapter);
        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });

        secondListView.setAdapter(secondAdapter);
        secondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });

        thirdListView.setAdapter(thirdAdapter);
        thirdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });

        fourthListView.setAdapter(fourthAdapter);
        fourthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });
    }
}