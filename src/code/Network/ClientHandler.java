package code.Network;

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

    /** Create a handler to serve the client on the given socket. */
    ClientHandler(Socket incoming) {
        this.incoming = incoming;
    }

    /** Start receiving and broadcasting messages. */
    public void run() {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));

            // inform the server of this new client
//                ChatServer.this.addClient(out);
//                out.print("Welcome to JavaChat! ");
//                out.println("Enter BYE to exit.");
//                out.flush();

            System.out.println("connected");


            BufferedReader in
                    = new BufferedReader(
                    new InputStreamReader(incoming.getInputStream()));
            for (;;) {
                String msg = in.readLine();
                if (msg == null) {
                    break;
                } else {
                    if (msg.trim().equals("BYE"))
                        break;

                    System.out.println("Received: " + msg);
                    // broadcast the receive message
//                        ChatServer.this.broadcast(msg);
                }
            }
            incoming.close();
//                ChatServer.this.removeClient(out);
        } catch (Exception e) {
            if (out != null) {
//                    ChatServer.this.removeClient(out);
            }
            e.printStackTrace();
        }
    }
}