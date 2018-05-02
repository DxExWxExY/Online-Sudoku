package code.Network;

import code.Sudoku.HistoryNode;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

/** A thread to serve a client. This class receive messages from a
 * client and broadcasts them to all clients including the message
 * sender. */
class ClientHandler extends Thread {

    /** Socket to read client messages. */
    private Socket incoming;
    private JTextArea logT;
    private HistoryNode data;


    /** Create a handler to serve the client on the given socket. */
    ClientHandler(Socket incoming, HistoryNode data, JTextArea logT) {
        this.incoming = incoming;
        this.logT = logT;
        this.data = data;
    }

    /** Start receiving and broadcasting messages. */
    public void run() {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            for (;;) {
                String msg = in.readLine();
                if (msg == null) {
                    break;
                } else {
                    if (msg.equals("END")) {
                        break;

                    } else {
                        logT.append("\nReceived: " + msg);
                        data.setData(msg);
                    }
                }
            }
            incoming.close();
        } catch (Exception e) {
            e.printStackTrace();
            logT.append("\nError: "+e);
        }
    }

    void close() {
        try {
            incoming.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void update(HistoryNode hist) {
        this.data = hist;
    }
}