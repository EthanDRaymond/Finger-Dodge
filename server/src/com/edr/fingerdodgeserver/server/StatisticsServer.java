package com.edr.fingerdodgeserver.server;

import com.edr.fingerdodgeserver.net.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Ethan Raymond
 */
public class StatisticsServer extends Thread{

    private ServerSocket serverSocket;
    private boolean isRunning;

    public StatisticsServer(int port) throws IOException {
        super("StatisticsServer");
        this.serverSocket = new ServerSocket(port);
        this.isRunning = false;
    }

    @Override
    public synchronized void start() {
        super.start();
        this.isRunning = true;
    }

    public void end(){
        this.isRunning = false;
    }

    @Override
    public void run() {
        super.run();
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                new ClientConnection(socket).start();
                System.out.println("New Connection.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
