package com.example.messenger.P2P;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends Thread{
    private static final int SERVER_PORT = 8888;
    private InetAddress mServerAddr;
    public Client(InetAddress serverAddr){
        mServerAddr = serverAddr;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT),500);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void interrupt() {
        super.interrupt();

    }
}
