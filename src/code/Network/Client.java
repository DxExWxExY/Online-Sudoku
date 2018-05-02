import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    public Client(String host) {
        super("Client");
        serverIP = host;
        configureUI();
        configureClient();
    }

    private void configureUI() {
        userText = new JTextField();
        enableInteraction(false);
        userText.addActionListener(e -> {
            sendMessage(e.getActionCommand());
            userText.setText("");
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

    public void configureClient() {
        try {
            connectToServer();
            configureStreams();
            whileChatting();
        } catch(EOFException e) {
            showMessage("\nClient terminated connection!");
        } catch(IOException a) {
            a.printStackTrace();
        } finally {
            closeClient();
        }
    }

    private void connectToServer() throws IOException {
        showMessage("\nAttempting connection...");
        //pop up window to input host name and port
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to:" + connection.getInetAddress().getHostName());
    }

    private void configureStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
    }

    private void whileChatting() throws IOException {
        enableInteraction(true);
        do {
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
        } while(!message.equals("SERVER - END"));
    }

    private void closeClient() {
        showMessage("\n Closing connections...");
        enableInteraction(false);

        try {
            output.close();
            input.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        try {
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\nCLIENT - " + message);
        } catch(IOException e) {
            e.printStackTrace();
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
