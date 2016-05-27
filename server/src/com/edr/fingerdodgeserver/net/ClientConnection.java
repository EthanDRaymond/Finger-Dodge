package com.edr.fingerdodgeserver.net;

import com.edr.fingerdodgeserver.Main;
import com.edr.fingerdodgeserver.stat.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Ethan Raymond
 */

public class ClientConnection extends Thread {

    private Socket socket;
    private BufferedReader in;
    private boolean isRunning;

    public ClientConnection(Socket socket) throws IOException {
        super("ClientConnection");
        this.isRunning = false;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public synchronized void start() {
        super.start();
        this.isRunning = true;
    }

    public void end() {
        this.isRunning = false;
    }

    @Override
    public void run() {
        super.run();
        while (isRunning) {
            try {
                if (!socket.isConnected()) {
                    break;
                }
                receiveAndHandleInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Disconnected");
    }


    private void receiveAndHandleInput() {
        while (true) {
            try {
                String input = in.readLine();
                if (input != null) {
                    Packet packet = new Packet(input);
                    handleReceivedPacket(packet, input);
                } else {
                    end();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleReceivedPacket(Packet packet, String json) {
        if (packet.title.equals(StatisticPacket.TITLE)) {
            StatisticPacket statisticPacket = new StatisticPacket(json);
            handleReceivedStatisticPacket(statisticPacket);
        }
    }

    private void handleReceivedStatisticPacket(StatisticPacket packet) {
        ArrayList<Statistic> statistics = packet.getStatistics();
        for (int i = 0; i < statistics.size(); i++) {
            writeStatisticToDatabase(statistics.get(i));
        }
    }

    private void writeStatisticToDatabase(Statistic statistic) {
        StatisticsDatabaseConnection statisticsDatabaseConnection = Main.getStatisticsDatabaseConnection();
        if (statistic instanceof ActivityOpenStatistic) {
            statisticsDatabaseConnection.addActivityOpenStatistic((ActivityOpenStatistic) statistic);
        } else if (statistic instanceof ActivityCloseStatistic) {
            statisticsDatabaseConnection.addActivityCloseStatistic((ActivityCloseStatistic) statistic);
        } else if (statistic instanceof GameStatistic) {
            statisticsDatabaseConnection.addGameStatistic((GameStatistic) statistic);
        }
    }

}
