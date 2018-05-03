package code.Network;

import code.Network.NetworkAdapter;
import code.Sudoku.HistoryNode;

import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends NetworkAdapter {
    private ServerSocket server;
    private HistoryNode local;

    public Server(HistoryNode reference) {
        super();
        this.local = reference;
    }

    @Override
    protected void configureInstance() {
        new Thread(() -> {
            while (true) {
                try {
                    server = new ServerSocket(getPORT(), 100);
                    connection = server.accept();
                    configureStreams();
                    whileChatting();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnections();
                }
            }
        }).start();

    }

    @Override
    protected void connect() throws IOException {

    }

//    private void configureGUI() {
//        userText = new JTextField();
//        enableInteraction(false);
//        userText.addActionListener( e -> {
//            sendMessage(e.getActionCommand());
//            userText.setText("");
//        });
//        add(userText,BorderLayout.NORTH);
//
//        chatWindow = new JTextArea();
//        add(new JScrollPane(chatWindow));
//        setSize(300, 150);
//        setVisible(true);
//    }
}