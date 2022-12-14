package com.example.messenger.AsyncTasks;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.messenger.ChatActivity;
import com.example.messenger.Entities.Message;
import com.example.messenger.P2P.Server;
import com.example.messenger.model.FireMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SendMessageServer extends AsyncTask<Message, Message, Message>{
	private static final String TAG = "SendMessageServer";
	private Context mContext;
	private static final int SERVER_PORT = 4446;
	private boolean isMine;


	public SendMessageServer(Context context, boolean mine){
		mContext = context;
		isMine = mine;
	}
	
	@Override
	protected Message doInBackground(Message... msg) {
		Log.v(TAG, "doInBackground");
		publishProgress(msg);

		//Send the message to clients
		try {
			ArrayList<InetAddress> listClients = Server.clients;

			Log.d("COUNT_CLIENT", "doInBackground: number of clients: "+ listClients.size() +" ");
			for(InetAddress addr : listClients){
				if(msg[0].getSenderAddress() != null && addr.getHostAddress().equals(msg[0].getSenderAddress().getHostAddress())){
					return msg[0];
				}			
				
				Socket socket = new Socket();
				socket.setReuseAddress(true);
				socket.bind(null);
				Log.e(TAG,"Connect to client: " + addr.getHostAddress());
				socket.connect(new InetSocketAddress(addr, SERVER_PORT));

				Log.e(TAG, "doInBackground: connect to "+ addr.getHostAddress() +" succeeded");

				OutputStream outputStream = socket.getOutputStream();
				
				new ObjectOutputStream(outputStream).writeObject(msg[0]);
				
			    Log.e(TAG, "doInBackground: write to "+ addr.getHostAddress() +" succeeded");
			    socket.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return msg[0];
	}

	@Override
	protected void onProgressUpdate(Message... values) {
		super.onProgressUpdate(values);
		Log.e("Message3", values[0].getMessage());
		Message msg = values[0];

		ChatActivity.refreshList(new FireMessage(msg.getmType(),msg.getFromMail(), msg.getToMail(), msg.getMessage(), msg.getSentDate(), isMine), isMine);
	}

	@Override
	protected void onPostExecute(Message result) {
//		Log.v(TAG, "onPostExecute");
		super.onPostExecute(result);
	}

}