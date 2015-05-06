package com.edr.fingerdodge.net;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Ethan Raymond
 */
public class ServerConnection extends Thread {

    public static final String SERVER_ADDRESS = "";
    public static final int SERVER_PORT = 10444;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_FAILURE_TO_CONNECT = 2;
    public static final int STATE_IDLE = 3;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int state;
    private ArrayList<Runnable> onRecievedResponseRunnables;
    private ArrayList<Integer> onRecievedResponseIDs;
    private boolean isRunning;

    public ServerConnection(){
        super("ServerConnection");
        this.isRunning = true;
        onRecievedResponseRunnables = new ArrayList<>();
        onRecievedResponseIDs = new ArrayList<>();
        this.state = STATE_IDLE;
    }
    @Override
    public synchronized void start() {
        super.start();
        connect();
    }

    public void end(){
        this.isRunning = false;
        disconnect();
    }

    @Override
    public void run() {
        super.run();
        while (isRunning) {
            if (getConnectionState() == STATE_CONNECTED){
                if (socket.isClosed()){
                    state = STATE_IDLE;
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                try {
                    if (in.ready()){
                        String input = in.readLine();
                        JSONObject inputObject = new JSONObject(input);
                        int conversationID = inputObject.getInt("CONVERSATION_ID");
                        String data = inputObject.getString("CONTENT");
                        if (data != null){
                            onRecievedData(data, conversationID);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean connect() {
        if (getConnectionState() == STATE_IDLE || getConnectionState() == STATE_FAILURE_TO_CONNECT){
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.state = STATE_CONNECTED;
                return true;
            } catch (IOException e){
                e.printStackTrace();
                this.state = STATE_FAILURE_TO_CONNECT;
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean disconnect(){
        if (getConnectionState() == STATE_CONNECTED){
            try {
                socket.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean sendData(String data, Runnable onRecievedResponse){
        try {
            if (getConnectionState() == STATE_CONNECTED) {
                JSONObject message = new JSONObject();
                if (onRecievedResponse != null) {
                    int conversationID = getNewConversationID();
                    onRecievedResponseIDs.add(conversationID);
                    onRecievedResponseRunnables.add(onRecievedResponse);
                    message.put("CONVERSATION_ID", conversationID);
                }
                message.put("CONTENT", data);
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void onRecievedData(String data, int conversationID){
        for (int i = 0; i < onRecievedResponseIDs.size(); i++){
            if (conversationID == onRecievedResponseIDs.get(i)){
                onRecievedResponseRunnables.get(i).run();
                return;
            }
        }
    }

    public int getConnectionState() {
        return state;
    }

    private int getNewConversationID(){
        int id;
        Random r = new Random();
        do {
            id = r.nextInt();
            for (int i = 0; i < onRecievedResponseIDs.size(); i++){
                if (id == onRecievedResponseIDs.get(i)){
                    continue;
                }
            }
            break;
        } while (true);
        return new Random().nextInt();
    }

}
