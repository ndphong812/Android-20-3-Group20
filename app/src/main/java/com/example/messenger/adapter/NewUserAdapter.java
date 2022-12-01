package com.example.messenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.messenger.R;
import com.example.messenger.model.Contact;
import com.google.android.material.imageview.ShapeableImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NewUserAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private ArrayList<Contact> listUsers;

    public NewUserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
        this.listUsers = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.frame_new_user, null);

            viewHolder.avatar = (ShapeableImageView) convertView.findViewById(R.id.avatar);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.avatar.setImageBitmap(BitmapFactory.decodeFile(listUsers.get(position).getAvatarPath()));
        viewHolder.username.setText(listUsers.get(position).getUsername());

        return convertView;
    }

    public class ViewHolder {
        ShapeableImageView avatar;
        TextView username;
    }
}
