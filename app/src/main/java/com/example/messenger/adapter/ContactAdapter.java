package com.example.messenger.adapter;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.messenger.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");

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
    
    public void deleteMessage(int pos) {
        Contact contact = contacts.get(pos);
        if(contact != null) {
            contacts.remove(pos);
            //Call API for remove
            notifyItemRemoved(pos);
        }
    }

    public void unfriendAndDeleteMessage(int pos, User currentUser) {
        //handle UI
        String friendId = contacts.get(pos).getId();
        currentUser.getFriends().remove(friendId);
        //Call API to firebase
        databaseReference
                .child("User")
                .child(currentUser.getID())
                .child("friends")
                .setValue(currentUser.getFriends());

        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null ) {
                        if(user.getID().equals(friendId)) {
                            user.getFriends().remove(currentUser.getID());
                            databaseReference
                                    .child("User")
                                    .child(user.getID())
                                    .child("friends")
                                    .setValue(user.getFriends())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            deleteMessage(pos);
                                        }
                                    });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void blockFriend(int pos, User currentUser) {
        //handle UI
        String friendId = contacts.get(pos).getId();
        currentUser.getBlocks().add(friendId);
        //Call API to firebase
        databaseReference
                .child("User")
                .child(currentUser.getID())
                .child("blocks")
                .setValue(currentUser.getBlocks());
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
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
            layoutItem.setOnCreateContextMenuListener(this);
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

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Lựa chọn bất kỳ");
            contextMenu.add(getAdapterPosition(), 101, 0, "Xóa cuộc trò chuyện");
            contextMenu.add(getAdapterPosition(), 102, 0, "Hủy kết bạn");
            contextMenu.add(getAdapterPosition(), 103, 0, "Chặn người dùng này");
        }
    }
}