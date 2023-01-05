package com.example.messenger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Entities.Message;
import com.example.messenger.R;
import com.example.messenger.Services.LoadImageFromURL;
import com.example.messenger.model.Contact;
import com.example.messenger.model.FireMessage;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Contact selfContact;
    Contact currentContact;

    private Context context;
    private List<FireMessage> listChat;
    private final int MSG_TYPE_RIGHT = 1;
    private final int MSG_TYPE_LEFT = 0;
    @SuppressLint("NotifyDataSetChanged")
    public ChatAdapter(Context context, List<FireMessage> listChat) {
        this.context = context;
        this.listChat = listChat;
        notifyDataSetChanged();
    }

    public void setData(List<FireMessage> list) {
        this.listChat = list;
    }
    public void setSelfContact(Contact self) {
        this.selfContact = self;
    }
    public void setCurrentContact(Contact currentContact) {
        this.currentContact = currentContact;
    }

    public void addChatItem(FireMessage item) {
        this.listChat.add(item);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.frame_item_chat_sent, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.frame_item_chat_received, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FireMessage currentChatItem = listChat.get(position);
        ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
        if(chatViewHolder.avatar != null) {
            LoadImageFromURL loadImageFromURL = new LoadImageFromURL(chatViewHolder.avatar);
            loadImageFromURL.execute(selfContact.getAvatarPath());
        }

        chatViewHolder.message.setText(currentChatItem.getMessage());
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(listChat.get(position).getFromMail().equals(selfContact.getId())) {
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView message ;
        private ShapeableImageView avatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}