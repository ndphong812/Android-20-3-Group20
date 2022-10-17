package com.example.messenger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        int itemHeight = 165;

        ListView firstListView = findViewById(R.id.firstListView);
        ListView secondListView = findViewById(R.id.secondListView);
        ListView thirdListView = findViewById(R.id.thirdListView);
        ListView fourthListView = findViewById(R.id.fourthListView);

        String[] firstSectionLabels = {"Chế độ tối", "Tin nhắn đang chờ", "Đoạn chat đã lưu trữ"};
        String[] secondSectionLabels = {"Trạng thái hoạt động", "Tên người dùng"};
        String[] thirdSectionLabels = {"Quyền riêng tư", "Avatar", "Thông báo và âm thanh", "SMS", "Danh bạ điện thoại", "Ảnh và file phương tiện", "Bong bóng"};
        String[] fourthSectionLabels = {"Chuyển tài khoản", "Cài đặt tài khoản", "Trợ giúp", "Chính sách và quyền lợi"};

        Integer[] firstSectionIcons = {R.drawable.ic_dark_mode, R.drawable.ic_request_message, R.drawable.ic_archived_chat};
        Integer[] secondSectionIcons = {R.drawable.ic_active_status, R.drawable.ic_username};
        Integer[] thirdSectionIcons = {R.drawable.ic_privacy, R.drawable.ic_avatar, R.drawable.ic_notification, R.drawable.ic_sms, R.drawable.ic_contacts, R.drawable.ic_image_media, R.drawable.ic_bubbles};
        Integer[] fourthSectionIcons = {R.drawable.ic_switch_account, R.drawable.ic_account_settings, R.drawable.ic_help, R.drawable.ic_policies};

        AdapterSettings firstAdapter, secondAdapter, thirdAdapter, fourthAdapter;

        firstListView.getLayoutParams().height = firstSectionLabels.length* itemHeight;
        secondListView.getLayoutParams().height = secondSectionLabels.length* itemHeight;
        thirdListView.getLayoutParams().height = thirdSectionLabels.length * itemHeight;
        fourthListView.getLayoutParams().height = fourthSectionLabels.length * itemHeight;

        firstAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, firstSectionLabels, firstSectionIcons);
        secondAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, secondSectionLabels, secondSectionIcons);
        thirdAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, thirdSectionLabels, thirdSectionIcons);
        fourthAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, fourthSectionLabels, fourthSectionIcons);

        firstListView.setAdapter(firstAdapter);
        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });

        secondListView.setAdapter(secondAdapter);
        secondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });

        thirdListView.setAdapter(thirdAdapter);
        thirdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });

        fourthListView.setAdapter(fourthAdapter);
        fourthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });
    }
}

