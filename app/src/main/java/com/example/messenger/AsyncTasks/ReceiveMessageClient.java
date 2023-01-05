package com.example.messenger.AsyncTasks;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.example.messenger.ChatActivity;
import com.example.messenger.Entities.Message;
import com.example.messenger.Register;
import com.example.messenger.model.FireMessage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ReceiveMessageClient extends AbstractReceiver {
	private static final int SERVER_PORT = 4446;
	private Context mContext;
	private ServerSocket socket;

	public ReceiveMessageClient(Context context){
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new ServerSocket(SERVER_PORT);
			while (true){
				Socket destinationSocket = socket.accept();
				
				InputStream inputStream = destinationSocket.getInputStream();
				BufferedInputStream buffer = new BufferedInputStream(inputStream);
				ObjectInputStream objectIS = new ObjectInputStream(buffer);
				Message message = (Message) objectIS.readObject();
				
				destinationSocket.close();
				publishProgress(message);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        
		return null;
	}

	@Override
	protected void onCancelled() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(Message... values) {
		super.onProgressUpdate(values);
//		playNotification(mContext, values[0]);
		Log.e("Message1", values[0].getMessage());
		Message msg = values[0];
		ChatActivity.refreshList(new FireMessage(msg.getmType(),msg.getFromMail(), msg.getToMail(), msg.getMessage(), msg.getSentDate(), false), false);
	}

}
