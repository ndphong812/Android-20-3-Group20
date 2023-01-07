package com.example.messenger.adapter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messenger.R;
import com.example.messenger.model.Contact;
import com.example.messenger.model.FireMessage;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Contact selfContact;
    Contact currentContact;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");

    Context context;
    private List<FireMessage> listChat;
    final int MSG_TYPE_RIGHT = 1;
    final int MSG_TYPE_LEFT = 0;
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
        if(selfContact.getId().equals(item.getToMail())) {
            sendNotification(item);

        }
    }

    private void sendNotification(FireMessage item) {
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        Intent ii = new Intent(context.getApplicationContext(), context.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_IMMUTABLE);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_baseline_message_24);
        mBuilder.setContentTitle("From: " + item.getFromMail());
        mBuilder.setContentText("Message: " + item.getMessage());
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
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
            Glide.with(context).load(currentContact.getAvatarPath()).into(chatViewHolder.avatar);
        }

        chatViewHolder.message.setText(currentChatItem.getMessage());
        String currentTime = splitTime(listChat.get(position).getSentDate());
        if(position >= 1) {
            if(listChat.get(position - 1).getFromMail().equals(listChat.get(position).getFromMail())) {
                String preTime = splitTime(listChat.get(position - 1).getSentDate());
                if(!preTime.equals(currentTime)) {
                    chatViewHolder.timeText.setText(currentTime);
                }else{
                    chatViewHolder.timeText.setVisibility(View.GONE);
                }
            }else{
                chatViewHolder.timeText.setText(currentTime);
            }
        }else{
            chatViewHolder.timeText.setText(currentTime);
        }
    }

    private String splitTime(String rawDate) {
        String result;
        String[] sub = rawDate.split("T");
//        String[] date = sub[0].split("-");
        result = sub[1].substring(0, 5);
        return result;
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

    public void deleteMessage (int pos) {
        if(listChat.get(pos).getFromMail().equals(currentContact.getId())) {
            // Tin nhắn từ bên kia
            Toast.makeText(context.getApplicationContext(), "Không thể xóa tin nhắn của người khác", Toast.LENGTH_SHORT).show();
        } else {
            //Tin nhắn tự gửi
            databaseReference.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        FireMessage fireMessage = dataSnapshot.getValue(FireMessage.class);
                        if (fireMessage != null) {
                            if (
                                (fireMessage.getFromMail().equals(selfContact.getId())
                                && fireMessage.getToMail().equals(currentContact.getId()) )
                                || (fireMessage.getFromMail().equals(currentContact.getId())
                                && fireMessage.getToMail().equals(selfContact.getId()))
                            ) {
//                                Log.e("abc", "pos" + pos + "Tin nhắn thứ " + count + ", Người gửi " + fireMessage.getFromMail() +", Người nhận " + fireMessage.getToMail() + ", Nội dung" + fireMessage.getMessage());
                                if (count == pos) {
                                    listChat.remove(pos);
                                    notifyItemRemoved(pos);
                                    dataSnapshot.getRef().removeValue();
                                    Toast.makeText(context.getApplicationContext(), "Đã xóa tin nhắn", Toast.LENGTH_SHORT).show();
                                }
                                count += 1;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void copyMessage (int pos) {
        FireMessage message = listChat.get(pos);

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(message.getMessage());
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", message.getMessage());
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context.getApplicationContext(), "Đã lưu vào bộ nhớ tạm.", Toast.LENGTH_SHORT).show();
    }

    public void downloadMessage (int pos) {
        FireMessage message = listChat.get(pos);
        Log.e("Message Menu", "download message" + message.getMessage());
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView message, timeText ;
        private ShapeableImageView avatar;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            avatar = itemView.findViewById(R.id.avatar);
            timeText = itemView.findViewById(R.id.text_time);

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