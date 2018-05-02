package code.Network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class NetworkAdapter {

    private final int PORT = findFreePort();
    private String message = null;
    private boolean connected;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    protected Socket connection;

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
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
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
        while(connected) {
            try {
                message = (String) input.readObject();
            }
            catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected String sendMessage() {
        try {
            if(message == null) {
                return "Message is null.";
            }
            output.writeObject(message);
            output.flush();
            this.message = null;

            return "Message was sent.";
        } catch(IOException e) {
            return "Message was not sent.";
        }
    }

    /**
     * Returns a free port number on localhost.
     *
     * Heavily inspired from org.eclipse.jdt.launching.SocketUtil (to avoid a dependency to JDT just because of this).
     * Slightly improved with close() missing in JDT. And throws exception instead of returning -1.
     *
     * @return a free port number on localhost
     * @throws IllegalStateException if unable to find a free port
     */
    private int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            return port;
        } catch (IOException ignored) {
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }

    protected void configureInstance() {}

    protected void configureInstance(int serverPORT, String serverIP) {}

    protected abstract void connect() throws IOException;

//    /**
//     * Start accepting messages asynchronously from this network
//     * adapter and notifying them to the registered listener.
//     * This method doesn't block the caller. Instead, a new
//     * background thread is created to read incoming messages.
//     * To receive messages synchronously, use the
//     * {@link #receiveMessages()} method.
//     *
//     * @see #setMessageListener(MessageListener)
//     * @see #receiveMessages()
//     */
//    public void receiveMessagesAsync() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                receiveMessages();
//            }
//        }).start();
//    }
//
//    /** Parse the given message and notify to the registered listener. */
//    private void parseMessage(String msg) {
//        if (msg.startsWith(MessageType.QUIT.header)) {
//                notifyMessage(MessageType.QUIT);
//        } else if (msg.startsWith(MessageType.JOIN_ACK.header)) {
//            parseJoinAckMessage(msgBody(msg));
//        } else if (msg.startsWith(MessageType.JOIN.header)) {
//            notifyMessage(MessageType.JOIN);
//        } else if (msg.startsWith(MessageType.NEW_ACK.header)) {
//        	parseNewAckMessage(msgBody(msg));
//        } else if (msg.startsWith(MessageType.NEW.header)) {
//        	parseNewMessage(msgBody(msg));
//        } else if (msg.startsWith(MessageType.FILL_ACK.header)) {
//            parseFillMessage(MessageType.FILL_ACK, msgBody(msg));
//        } else if (msg.startsWith(MessageType.FILL.header)){
//            parseFillMessage(MessageType.FILL, msgBody(msg));
//        } else {
//            notifyMessage(MessageType.UNKNOWN);
//        }
//    }
//
//    /**
//     * Write messages asynchronously. This class uses a single
//     * background thread to write messages asynchronously in a FIFO
//     * fashion. To stop the background thread, call the stop() method.
//     */
//    private class MessageWriter {
//
//        /** Background thread to write messages asynchronously. */
//        private Thread writerThread;
//
//        /** Store messages to be written asynchronously. */
//        private BlockingQueue<String> messages = new LinkedBlockingQueue<>();
//
//        /** Write the given message asynchronously on a new thread. */
//        public void write(final String msg) {
//            if (writerThread == null) {
//                writerThread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (true) {
//                            try {
//                                String m = messages.take();
//                                out.println(m);
//                                out.flush();
//                            } catch (InterruptedException e) {
//                                return;
//                            }
//                        }
//                    }
//                });
//                writerThread.start();
//            }
//
//            synchronized (messages) {
//                try {
//                    messages.put(msg);
//                    if (logger != null) {
//                        logger.format(" > %s\n", msg);
//                    }
//                } catch (InterruptedException e) {
//                }
//            }
//        }
//
//        /** Stop this message writer. */
//        public void stop() {
//            if (writerThread != null) {
//                writerThread.interrupt();
//            }
//        }
//    }
}
