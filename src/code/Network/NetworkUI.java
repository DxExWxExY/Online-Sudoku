package code.Network;

import code.Sudoku.SudokuDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class NetworkUI extends SudokuDialog {

    private JFrame networkSettings = new JFrame("Connection Settings");
    private JPanel config;
    private JTextArea ipT, portT;
//    int [][] share = history.getBoard().board;


    private NetworkUI() {
        super();
        content.remove(toolbar);
        content.remove(numberButtons);
        toolbar = makeToolBar();
        content.add(toolbar);
        content.add(numberButtons);
        content.revalidate();
    }

    /**
     * Create a control panel consisting of new and number buttons.
     */
    @Override
    protected JPanel makeToolBar() {
        JPanel toolBar = new JPanel();
        JButton undo, redo, solve, can, connect;
        undo = makeOptionButtons("undo.png", KeyEvent.VK_Z);
        redo = makeOptionButtons("redo.png", KeyEvent.VK_Y);
        solve = makeOptionButtons("solve.png", KeyEvent.VK_S);
        can = makeOptionButtons("can.png", KeyEvent.VK_C);
        connect = makeOptionButtons("offline.png", KeyEvent.VK_O);
        undo.addActionListener(e -> undo());
        redo.addActionListener(e -> redo());
        solve.addActionListener(e -> solve());
        can.addActionListener(e -> isSolvable());
        connect.addActionListener(e -> onlineGame());
        toolBar.add(undo);
        toolBar.add(redo);
        toolBar.add(solve);
        toolBar.add(can);
        toolBar.add(connect);
        toolBar.setBackground(BACKGROUND);
        return toolBar;
    }

    private void onlineGame() {
        new Thread(() -> {
            makeNetworkOptions();
            makeNetworkWindow();
//            startConnection();
        }).run();
    }

    /**
     * Default port number on which this server to be run.
     */
    private static final int PORT_NUMBER = findFreePort();

    private void startConnection() {
        System.out.println("Sudoku server started on port "
                + PORT_NUMBER + "!");
        try {
            ServerSocket s = new ServerSocket(PORT_NUMBER);
            for (; ; ) {
                Socket incoming = s.accept();
                new ClientHandler(incoming).start();             //share board
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("sudoku online server stopped.");

    }

    private void makeNetworkWindow() {
        networkSettings.setSize(new Dimension(300, 150));
        networkSettings.setIconImage(Objects.requireNonNull(createImageIcon("online.png")).getImage());
        networkSettings.setResizable(false);
        networkSettings.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        networkSettings.setVisible(true);
        networkSettings.add(config);


    }

    private void makeNetworkOptions() {
        try {
            config = new JPanel(new GridLayout(3, 2, 0, 10));
            JLabel ipL = new JLabel("Server Address");
            JLabel portL = new JLabel("Port Number");
            ipT = new JTextArea(String.valueOf(InetAddress.getLocalHost()));
            portT = new JTextArea(String.valueOf(findFreePort()));
            JButton connectButton = new JButton("Connect");

            connectButton.setFocusPainted(false);
            connectButton.addActionListener(this::connectClicked);

            config.add(ipL);
            config.add(ipT);
            config.add(portL);
            config.add(portT);
            config.add(connectButton);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns a free port number on localhost.
     * <p>
     * Heavily inspired from org.eclipse.jdt.launching.SocketUtil (to avoid a dependency to JDT just because of this).
     * Slightly improved with close() missing in JDT. And throws exception instead of returning -1.
     *
     * @return a free port number on localhost
     * @throws IllegalStateException if unable to find a free port
     */
    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException ignored) {
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }


//---------------------------------------------------------------------------------------------------------------------------------------


    /**
     * A thread to serve a client. This class receive messages from a
     * client and broadcasts them to all clients including the message
     * sender.
     */
    private class ClientHandler extends Thread {

        /**
         * Socket to read client messages.
         */
        private Socket incoming;

        /**
         * Create a handler to serve the client on the given socket.
         */
        public ClientHandler(Socket incoming) {
            this.incoming = incoming;
        }

        /**
         * Start receiving and broadcasting messages.
         */
        public void run() {
            PrintWriter out = null;
            try {
                out = new PrintWriter(
                        new OutputStreamWriter(incoming.getOutputStream()));

                // inform the server of this new client
//                ChatServer.this.addClient(out);
//                out.print("Welcome to JavaChat! ");
//                out.println("Enter BYE to exit.");
//                out.flush();

                System.out.println("connected");


                BufferedReader in
                        = new BufferedReader(
                        new InputStreamReader(incoming.getInputStream()));
                for (; ; ) {
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
//---------------------------------------------------------------------------------------------------------------------------------------


    /**
     * Callback to be called when the connect button is clicked.
     */
    private void connectClicked(ActionEvent event) {
        try {
            Socket socket = new Socket(ipT.getText(), Integer.parseInt(portT.getText()));
//            System.out.println(serverEdit.getText());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NetworkUI();
        new NetworkUI().startConnection();

    }
}
