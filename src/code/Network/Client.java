import code.Network.NetworkAdapter;

import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends NetworkAdapter {
    private String serverIP;
    private int serverPORT;

    public Client(int serverPORT, String serverIP) {
        super(serverPORT, serverIP);
    }

    @Override
    protected void configureInstance(int serverPORT, String serverIP) {
        this.serverIP = serverIP;
        this.serverPORT = serverPORT;

        try {
            connect();
            configureStreams();
            whileChatting();
        } catch(EOFException e) {
            e.printStackTrace();
        } catch(IOException a) {
            a.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    protected void connect() throws IOException {
        connection = new Socket(InetAddress.getByName(serverIP), serverPORT);
    }

//    private void configureUI() {
//        userText = new JTextField();
//        enableInteraction(false);
//        userText.addActionListener(e -> {
//            sendMessage(e.getActionCommand());
//            userText.setText("");
//        });
//        add(userText, BorderLayout.NORTH);
//        chatWindow = new JTextArea();
//        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
//        setSize(300, 150);
//        setVisible(true);
//    }

}
