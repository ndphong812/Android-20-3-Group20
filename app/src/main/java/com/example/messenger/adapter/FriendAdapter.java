package com.example.messenger.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.ChatActivity;
import com.example.messenger.ChatsFragment;
import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.model.Contact;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    private List<Contact> contacts;
    private boolean isLoadingAdd;
    private Context context;
    private ContactAdapter adapter;

    public FriendAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Contact> list) {
        this.contacts = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(contacts != null && position == contacts.size() - 1 && isLoadingAdd == true) {
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_contact, parent, false);
            return new ContactViewHolder(view);
        }else if(viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_loading_item, parent, false);
            return new ContactViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(holder.getItemViewType() == TYPE_ITEM) {
            Contact contact = contacts.get(position);
            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            contactViewHolder.avatar.setImageResource(contact.getAvatarPath());
            contactViewHolder.chatName.setText(contact.getUsername());
            contactViewHolder.latestChat.setText(contact.getLatestMessage());
            contactViewHolder.layoutItem.setOnClickListener(view -> {
                chatWithOther(contact);
            });
        }
    }

    public void chatWithOther(Contact contact) {
        Intent intent = new Intent(context, ChatActivity.class);

        //Pass data here
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        intent.putExtras(bundle);

        //Change screen
        context.startActivity(intent);

    }
    @Override
    public int getItemCount() {
        if(contacts != null) {
            return contacts.size();
        }
        return 0;
    }
    public void addFooterLoading() {
        this.isLoadingAdd = true;
        contacts.add(new Contact());
    }
    public void removeFooterLoading() {
        this.isLoadingAdd = false;

        int pos = contacts.size() - 1;
        Contact contact = contacts.get(pos);
        if(contact != null) {
            contacts.remove(pos);
            notifyItemRemoved(pos);
        }
    }
    public void removeItem(int pos) {
        Contact contact = contacts.get(pos);
        if(contact != null) {
            contacts.remove(pos);
            notifyItemRemoved(pos);
        }}
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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}

