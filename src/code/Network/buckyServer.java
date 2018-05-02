import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    public Server() {
        super("Bucky's Instant Messenger");
        configureGUI();
        configureServer();
    }

    private void configureServer() {
        try {
            server = new ServerSocket(6789, 100);
            while(true) {
                try {
                    waitForConnection();
                    configureStreams();
                    whileChatting();
                } catch(EOFException e) {
                    showMessage("\nServer ended the connection !");
                } finally {
                    closeServer();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void configureGUI() {
        userText = new JTextField();
        enableInteraction(false);
        userText.addActionListener( e -> {
            sendMessage(e.getActionCommand());
            userText.setText("");
        });
        add(userText,BorderLayout.NORTH);

        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300, 150);
        setVisible(true);
    }

    private void waitForConnection() throws IOException {
        showMessage("Waiting for someone to connect... \n");
        connection = server.accept();
        showMessage("Now connected to " + connection.getInetAddress().getHostName());
    }

    private void configureStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
    }

    private void whileChatting() throws IOException {
        String message = "";
        enableInteraction(true);

        do {
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch(ClassNotFoundException e) {
                showMessage("\nNot a string!");
            }
        } while(!message.equals("\nCLIENT - END"));
    }

    private void closeServer() {
        showMessage("\nClosing connections...");
        enableInteraction(false);

        try {
            output.close();
            input.close();
            connection.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        try {
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\nSERVER - " + message);
        } catch(IOException e) {
            chatWindow.append("\nError sending data.");
        }
    }

    private void showMessage(final String text) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        chatWindow.append(text);

                    }
                }
        );
    }

    private void enableInteraction(final boolean value) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        userText.setEditable(value);
                    }
                }
        );
    }
}