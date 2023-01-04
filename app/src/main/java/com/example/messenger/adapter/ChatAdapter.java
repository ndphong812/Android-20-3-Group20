package com.example.messenger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Entities.Message;
import com.example.messenger.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import javax.mail.MessageAware;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Message> listChat;
    private final int MSG_TYPE_RIGHT = 1;
    private final int MSG_TYPE_LEFT = 0;

    public ChatAdapter(Context context, List<Message> listChat) {
        this.context = context;
        this.listChat = listChat;
        notifyDataSetChanged();
    }

    public void setData(List<Message> list) {
        this.listChat = list;
    }
    public void addChatItem(Message item) {
        this.listChat.add(item);
        notifyDataSetChanged();
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
        Message currentChatItem = listChat.get(position);
        ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
        if(chatViewHolder.avatar != null) {
            chatViewHolder.avatar.setImageResource(R.drawable.ic_launcher_background);
        }

        chatViewHolder.message.setText(currentChatItem.getMessage());
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(listChat.get(position).isMine()) {
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    public void deleteMessage (int pos) {
        Message message = listChat.get(pos);
        Log.e("Message Menu", "delete message" + message.getMessage());
    }

    public void copyMessage (int pos) {
        Message message = listChat.get(pos);
        Log.e("Message Menu", "copy message" + message.getMessage());
    }

    public void downloadMessage (int pos) {
        Message message = listChat.get(pos);
        Log.e("Message Menu", "download message" + message.getMessage());
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView message ;
        private ShapeableImageView avatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            avatar = itemView.findViewById(R.id.avatar);
            message.setOnCreateContextMenuListener(this);
        }

        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Lựa chọn bất kỳ");
            contextMenu.add(getAdapterPosition(), 201, 0, "Xóa tin nhắn");
            contextMenu.add(getAdapterPosition(), 202, 0, "Copy tin nhắn");
            contextMenu.add(getAdapterPosition(), 203, 0, "Tải");
        }
    }
}