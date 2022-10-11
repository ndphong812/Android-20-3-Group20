package com.example.messenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;
import com.example.messenger.model.Contact;
import com.google.android.material.imageview.ShapeableImageView;

public class CustomContactAdapter extends ArrayAdapter<Contact> {
    final private Context context;
    final private Contact [] contacts;
    public CustomContactAdapter(@NonNull Context context, int resource, @NonNull Contact[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.contacts = objects;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.frame_contact, null);

            viewHolder = new ViewHolder();
            viewHolder.avatar = convertView.findViewById(R.id.avatar);
            viewHolder.chatName = convertView.findViewById(R.id.chatName);
            viewHolder.latestChat = convertView.findViewById(R.id.latestChat);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.avatar.setImageResource(contacts[position].getAvatarPath());
        viewHolder.chatName.setText(contacts[position].getUsername());
        viewHolder.latestChat.setText(contacts[position].getLatestMessageWithUser());

        return convertView;
    }

    public class ViewHolder {
        ShapeableImageView avatar;
        TextView chatName;
        TextView latestChat;
    }
}
