package code.Network;

import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * A simple chat server implemented using TCP/IP sockets. A client can
 * connect to this server and send messages to other clients. The chat
 * server receives messages from clients and broadcast them to all the
 * connected clients. A message is an arbitrary text and is also
 * printed on stdout. The default port number is 8008.
 *
 * <pre>
 *  Usage: java SudokuServer
 * </pre>
 *
 * @author Yoonsik Cheon
 */
class SudokuServer {

    /** Default port number on which this server to be run. */
    private int PORT_NUMBER;
    private ClientHandler service;

    /** Create a new server. */
    SudokuServer(JTextArea logT, int port) {
        this.PORT_NUMBER = port;
        start(logT);
    }

    /** Start the server. */
    private void start(JTextArea logT) {
        logT.append("\nSudoku Server on " + PORT_NUMBER + ".");
        try {
            ServerSocket s = new ServerSocket(PORT_NUMBER);
            for (;;) {
                Socket incoming = s.accept();
                service = new ClientHandler(incoming,logT);
                service.start();
            }
        } catch (Exception e) {
            logT.append("\nError: "+e);
        }
    }
    void kill() {
        service.kill();
    }
}
