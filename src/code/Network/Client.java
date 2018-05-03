package code.Network;

import code.Sudoku.BoardPanel;
import code.Sudoku.HistoryNode;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends NetworkAdapter {
    private String serverIP;
    private int serverPORT;

    Client(int serverPORT, String serverIP, HistoryNode history, JTextArea logT, BoardPanel boardPanel) {
        super(serverPORT, serverIP);
        this.history = history;
        this.logT = logT;
        this.boardPanel = boardPanel;
    }

    @Override
    protected void configureInstance(int serverPORT, String serverIP) {
        this.serverIP = serverIP;
        this.serverPORT = serverPORT;
            try {
                connect();
                configureStreams();
            } catch(IOException a) {
                a.printStackTrace();
            }
    }

    @Override
    protected void connect() throws IOException {
        connection = new Socket(InetAddress.getByName(serverIP), serverPORT);
    }

}
