package code.Network;

import code.Sudoku.HistoryNode;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;

public class Server extends NetworkAdapter {
    private ServerSocket server;


    Server(HistoryNode history, JTextArea logT) {
        super();
        this.history = history;
        this.logT = logT;
    }

    @Override
    protected void configureInstance() {
        try {
            server = new ServerSocket(getPORT(), 100);
            connect();
            configureStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void connect() throws IOException {
        connection = server.accept();
    }
}