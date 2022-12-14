package com.example.messenger.AsyncTasks;

import android.content.Context;
import android.util.Log;

import com.example.messenger.ChatActivity;
import com.example.messenger.Entities.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveMessageServer extends AbstractReceiver {
	private static final int SERVER_PORT = 4445;
	private Context mContext;
	private ServerSocket serverSocket;

	public ReceiveMessageServer(Context context){
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket clientSocket = serverSocket.accept();				
				
				InputStream inputStream = clientSocket.getInputStream();				
				ObjectInputStream objectIS = new ObjectInputStream(inputStream);
				Message message = (Message) objectIS.readObject();
				
				//Add the InetAdress of the sender to the message
				InetAddress senderAddr = clientSocket.getInetAddress();
				message.setSenderAddress(senderAddr);
				
				clientSocket.close();
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
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(Message... values) {
		super.onProgressUpdate(values);
//		playNotification(mContext, values[0]);
		Log.e("isReceiver", values[0].getMessage());

		new SendMessageServer(mContext, false).executeOnExecutor(THREAD_POOL_EXECUTOR, values);
	}

}
