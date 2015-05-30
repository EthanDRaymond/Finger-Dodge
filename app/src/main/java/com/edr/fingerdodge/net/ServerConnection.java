package com.edr.fingerdodge.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Ethan Raymond
 */
public class ServerConnection extends Thread {

    public static final String SERVER_ADDRESS = /*"10.0.2.2";*/ "104.131.251.167";
    public static final int SERVER_PORT = 4500;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_FAILURE_TO_CONNECT = 2;
    public static final int STATE_IDLE = 3;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int state;
    //private ArrayList<Runnable> onRecievedResponseRunnables;
    //private ArrayList<Integer> onRecievedResponseIDs;
    private boolean isRunning;

    public ServerConnection() {
        super("ServerConnection");
        this.isRunning = true;
        //onRecievedResponseRunnables = new ArrayList<Runnable>();
        //onRecievedResponseIDs = new ArrayList<Integer>();
        this.state = STATE_IDLE;
    }

    /**
     * Connects to the server and initializes the threat behind it.
     */
    @Override
    public synchronized void start() {
        super.start();
        // connect();
    }

    /**
     * Disconnects the server and causes the thread to cease.
     */
    public void end() {
        this.isRunning = false;
        disconnect();
    }

    @Override
    public void run() {
        super.run();
        connect();
        while (isRunning) {
            /*
            if (getConnectionState() == STATE_CONNECTED){
                try {
                    if (!isSocketConnected()){
                        disconnect();
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (getConnectionState() == STATE_FAILURE_TO_CONNECT) {
                try {
                    connect();
                } catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    connect();
                } catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            try {
                if (isConnected()) {
                    if (!isSocketConnected()) {
                        disconnect();
                    }

                } else if (isIdle()) {
                    connect();
                } else if (isFailedToConnect()) {
                    connect();
                    Thread.sleep(4000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a connection between the client and the server. If the connection already exits then
     * this does nothing.
     *
     * @return true if the connection is successful, false if the connection fails.
     */
    public boolean connect() {
        Log.i("SERVER-CONNECTED", "Attempting to connect to a server...");
        if (getConnectionState() == STATE_IDLE || getConnectionState() == STATE_FAILURE_TO_CONNECT) {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.state = STATE_CONNECTED;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                this.state = STATE_FAILURE_TO_CONNECT;
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * Cuts off the connection to the server. If there is no connection than this doesn't do
     * anything.
     *
     * @return true if the connection successfully disconnects, false if there is an error.
     */
    public boolean disconnect() {
        Log.i("SERVER-CONNECTED", "Attempting to disconnect to a server...");
        if (getConnectionState() == STATE_CONNECTED) {
            try {
                socket.close();
                state = STATE_IDLE;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                state = STATE_IDLE;
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * Sends the given string of data to the server if there is a connection available.
     *
     * @param data               the date to be sent to the server
     * @param onRecievedResponse this runnable is run if there is a response from the server
     * @return true if the data is send successfully, false if the data is not
     */
    public boolean sendData(String data, Runnable onRecievedResponse) {
        Log.i("SERVER-CONNECTED", "Attempting to send Data: \"" + data + "\"");
        try {
            if (isConnected() && isSocketConnected()) {
                /*
                JSONObject message = new JSONObject();
                if (onRecievedResponse != null) {
                    //int conversationID = getNewConversationID();
                    // onRecievedResponseIDs.add(conversationID);
                    // onRecievedResponseRunnables.add(onRecievedResponse);
                    // message.put("CONVERSATION_ID", conversationID);
                }
                message.put("CONTENT", data);
                */
                out.println(data);
                Log.i("SERVER-CONNECTED", "Succesfully sent data: \"" + data + "\"");
                return true;
            } else {
                disconnect();
                Log.i("SERVER-CONNECTED", "Failed to send data, no connection: \"" + data + "\"");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
            Log.i("SERVER-CONNECTED", "Failed to send data, exception thrown: \"" + data + "\"");
            return false;


            /**
             * Called when data is received from the server.
             * @param data              the data from the server
             * @param conversationID    the conversation id used to find the appropriate runnable.
             */
    /*
    private void onRecievedData(String data, int conversationID){
        for (int i = 0; i < onRecievedResponseIDs.size(); i++){
            if (conversationID == onRecievedResponseIDs.get(i)){
                onRecievedResponseRunnables.get(i).run();
                return;
            }
        }
    }
    */
        }
    }

    public boolean isConnected() {
        return getConnectionState() == STATE_CONNECTED;
    }

    public boolean isFailedToConnect() {
        return getConnectionState() == STATE_FAILURE_TO_CONNECT;
    }

    public boolean isIdle() {
        return getConnectionState() == STATE_IDLE;
    }

    public boolean isSocketConnected() {
        return socket.isConnected();
    }

    /**
     * Gets the state of the connection. The list of states are static constants within the
     * ServerConnection.
     */
    public int getConnectionState() {
        return state;
    }


    /**
     * Finds a new conversation ID that is not in use.
     */
    /*
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
    */

}
