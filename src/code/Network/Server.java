import code.Network.NetworkAdapter;

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

    public Server() {
        super();
    }

    @Override
    protected void configureInstance() {
        try {
            server = new ServerSocket(getPORT(), 100);
            while(true) {
                try {
                    connect();
                    configureStreams();
                    whileChatting();
                } catch(EOFException e) {
                    e.printStackTrace();
                } finally {
                    closeConnections();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected void connect() throws IOException {
        connection = server.accept();
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