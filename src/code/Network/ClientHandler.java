package code.Network;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/** A thread to serve a client. This class receive messages from a
 * client and broadcasts them to all clients including the message
 * sender. */
class ClientHandler extends Thread {

    /** Socket to read client messages. */
    private Socket incoming;
    private JTextArea logT;


    /** Create a handler to serve the client on the given socket. */
    ClientHandler(Socket incoming, JTextArea logT) {
        this.incoming = incoming;
        this.logT = logT;
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
                    }
                }
            }
            incoming.close();
        } catch (Exception e) {
            logT.append("\nError");
        }
    }
}