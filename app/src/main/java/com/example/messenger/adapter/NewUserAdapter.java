package com.example.messenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.messenger.R;
import com.example.messenger.Services.LoadImageFromURL;
import com.example.messenger.model.Contact;
import com.example.messenger.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NewUserAdapter extends ArrayAdapter<User> {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");

    private Context context;
    private ArrayList<User> listUsers;
    private User currentUser;

    public NewUserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects, User currentUser) {
        super(context, resource, objects);
        this.context = context;
        this.listUsers = objects;
        this.currentUser = currentUser;
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
            viewHolder.addBtn = (Button) convertView.findViewById(R.id.addFriend);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        LoadImageFromURL loadImageFromURL = new LoadImageFromURL(viewHolder.avatar);
        loadImageFromURL.execute(listUsers.get(position).getImage());
        viewHolder.username.setText(listUsers.get(position).getName());

        viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update 2 party
                currentUser.pushFriends(listUsers.get(position).getID());
                listUsers.get(position).pushFriends(currentUser.getID());

                //Update in firebase
                databaseReference
                        .child("User")
                        .child(currentUser.getID())
                        .child("friends")
                        .setValue(currentUser.getFriends());
                databaseReference
                        .child("User")
                        .child(listUsers.get(position).getID())
                        .child("friends")
                        .setValue(listUsers.get(position).getFriends());

                notifyDataSetChanged();
                Toast.makeText(context, "Kết bạn thành công", Toast.LENGTH_SHORT).show();
            }
        });

        if(currentUser.getFriends().contains(listUsers.get(position).getName())) {
            viewHolder.addBtn.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
    public class ViewHolder {
        ShapeableImageView avatar;
        TextView username;
        Button addBtn;
    }
}
