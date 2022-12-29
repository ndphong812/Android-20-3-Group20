package com.example.messenger.adapter;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.Services.LoadImageFromURL;
import com.example.messenger.model.Contact;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Contact> contacts;
    private final Context context;
    ContactAdapter adapter;

    public ContactAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Contact> list) {
        this.contacts = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Contact contact = contacts.get(position);
        ContactViewHolder viewHolder = (ContactViewHolder) holder;

        if(contact.getAvatarPath().length() == 0) {
            viewHolder.avatar.setImageResource(R.drawable.user);
        } else {
            LoadImageFromURL loadImageFromURL = new LoadImageFromURL(viewHolder.avatar);
            loadImageFromURL.execute(contact.getAvatarPath());
        }

        viewHolder.chatName.setText(contact.getUsername());
        viewHolder.latestChat.setText(contact.getLatestMessage());

        //Handle click on each item
        viewHolder.layoutItem.setOnClickListener(view -> {
            chatWithOther(contact);
        });

        //Handle long click on each item
        viewHolder.layoutItem.setOnLongClickListener(view -> {
            AlertDialog.Builder builder1=new AlertDialog.Builder(context);
            builder1.setMessage("Xóa cuộc trò chuyện này?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Có",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            contacts.remove(position);
                            notifyItemRemoved(position);
                            setData(contacts);
                            Toast.makeText(context,"Đã xóa cuộc trò chuyện",Toast.LENGTH_SHORT).show();
                        }
                    });
            builder1.setNegativeButton(
                    "Không",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return this.contacts.size();
    }

    public void chatWithOther(Contact contact) {
        Intent intent = new Intent(context.getApplicationContext(), ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Pass data here
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        intent.putExtras(bundle);

        //Change screen
        context.getApplicationContext().startActivity(intent);
    }
    
    public void removeItem(int pos) {
        Contact contact = contacts.get(pos);
        if(contact != null) {
            contacts.remove(pos);
            notifyItemRemoved(pos);
        }
    }
    
    public class ContactViewHolder extends RecyclerView.ViewHolder   {
        private ShapeableImageView avatar;
        private TextView chatName;
        private TextView latestChat;
        private LinearLayout layoutItem;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar);
            this.chatName = itemView.findViewById(R.id.chat_name);
            this.latestChat = itemView.findViewById(R.id.latest_chat);
            this.layoutItem = itemView.findViewById(R.id.layout_item);
        }

        public ShapeableImageView getShapeableImageView() {
            return avatar;
        }

        public TextView getChatName() {
            return chatName;
        }

        public TextView getLatestChat() {
            return latestChat;
        }
    }
}