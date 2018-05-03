package code.Network;

import code.Sudoku.HistoryNode;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class NetworkAdapter extends Thread {

    private static final int PORT = 8000;
    private String message = "";
    boolean connected = false;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    Socket connection;
    HistoryNode history;
    JTextArea logT;

    public NetworkAdapter(int serverPORT, String serverIP) {
        configureInstance(serverPORT, serverIP);
    }

    public NetworkAdapter() {
        configureInstance();
    }

    /**
     * Getter for the port.
     * @return returns the port used by this application
     */
    public int getPORT() {
        return PORT;
    }

    /**
     * Returns the current message to be sent or that has been received
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message t
     * @param str
     */
    public void setMessage(String str) {
        message = str;
    }

    /**
     * Configures the streams used to receive and send data
     * @throws IOException To be handled by specific instance
     */
    protected void configureStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        connected = true;
    }

    /**
     * Closes connections with server/client
     */
    protected void closeConnections() {
        try {
            output.close();
            input.close();
            connection.close();
            connected = false;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listens for input from connection while it is connected
     * @throws IOException
     */
    protected void whileChatting() throws IOException {
        while (true) {
            try {
                message = (String) input.readObject();
                logT.append("\nReceived: "+message);
                if (message.equals("end")) {
                    closeConnections();
                } else if (message.matches("new")) {
                    //new game protocol
                } else {
                    history.setData(message);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void sendBoard() {
        for (int i = 0; i < history.getSize(); i++) {
            for (int j = 0; j < history.getSize(); j++) {
                setMessage(history.getData(i,j));
                logT.append("\n"+sendMessage());
            }
        }
    }

    protected String sendMessage() {
        try {
            if(message.equals("")) {
                return "Message is null.";
            }
            output.writeObject(message);
            output.flush();
            String report = "Sent: "+message;
            this.message = "";
            return report;
        } catch(IOException e) {
            return "Not Sent";
        }
    }

    protected void configureInstance() {}

    protected void configureInstance(int serverPORT, String serverIP) {}

    protected abstract void connect() throws IOException;
}
