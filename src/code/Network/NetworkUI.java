package code.Network;

import code.Sudoku.SudokuDialog;

import javax.swing.*;
import javax.swing.text.Document;
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

    private JFrame networkSettings;
    private JPanel config, log;
    private JButton connect;
    private JTextArea ipT, portT, logT;
    private Thread connection;
    private ServerSocket server;
    /** Default port number on which this server to be run. */
    private static final int PORT_NUMBER = findFreePort();
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
        JButton undo, redo, solve, can;
        undo = makeOptionButtons("undo.png", KeyEvent.VK_Z);
        redo = makeOptionButtons("redo.png", KeyEvent.VK_Y);
        solve = makeOptionButtons("solve.png", KeyEvent.VK_S);
        can = makeOptionButtons("can.png", KeyEvent.VK_C);
        connect = makeOptionButtons("offline.png", KeyEvent.VK_O);
        undo.addActionListener(e -> undo());
        redo.addActionListener(e -> redo());
        solve.addActionListener(e -> solve());
        can.addActionListener(e -> isSolvable());
        connect.addActionListener(e -> networkDialog());
        toolBar.add(undo);
        toolBar.add(redo);
        toolBar.add(solve);
        toolBar.add(can);
        toolBar.add(connect);
        toolBar.setBackground(BACKGROUND);
        return toolBar;
    }

    private void networkDialog() {
       // new Thread(() -> {
            makeNetworkOptions();
            makeNetworkLog();
            makeNetworkWindow();
       // }).run();
    }


    private void makeNetworkOptions() {
        config = new JPanel(new GridLayout(3,2, 0, 10));
        config.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Network Settings"),
                BorderFactory.createEmptyBorder(30,5,30,5)));
        config.setSize(new Dimension(300,150));
        JLabel ipL = new JLabel("Server Address");
        JLabel portL = new JLabel("Port Number");
        try {
            ipT = new JTextArea(String.valueOf(InetAddress.getLocalHost()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        portT = new JTextArea(String.valueOf(PORT_NUMBER));
        JButton connectButton = new JButton("Connect");

        connectButton.setFocusPainted(false);
        connectButton.addActionListener(e -> connectionStart());

        config.add(ipL);
        config.add(ipT);
        config.add(portL);
        config.add(portT);
        config.add(connectButton);

    }

    private void makeNetworkLog() {
        log = new JPanel(new GridLayout(1,1));
        log.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Network Log"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        logT = new JTextArea("Network Log",20,30);
        logT.setLineWrap(true);
        logT.setEditable(false);
        JScrollPane scroll = new JScrollPane(logT);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        log.add(scroll);
    }

    private void makeNetworkWindow() {
        Dimension pos = Toolkit.getDefaultToolkit().getScreenSize();
        networkSettings = new JFrame("Connection Settings");
        networkSettings.setLayout(new GridLayout(2,1));
        networkSettings.setSize(new Dimension(300,400));
        networkSettings.setLocation(pos.width/2-500, pos.height/2-300);
        networkSettings.setIconImage(Objects.requireNonNull(createImageIcon("online.png")).getImage());
        networkSettings.setResizable(false);
        networkSettings.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        networkSettings.setVisible(true);
        networkSettings.add(config);
        networkSettings.add(log);
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
    private static int findFreePort() {
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

    private void onlineStatusUI() {
        toolbar.remove(connect);
        connect = makeOptionButtons("online.png", KeyEvent.VK_O);
        connect.addActionListener(e -> {
            Object[] options = {"Yes", "Cancel"};
            int n = JOptionPane.showOptionDialog(null, "Do You Want to Disconnect?",
                    "Disconnect", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[1]);
            switch (n) {
                case JOptionPane.YES_OPTION:
                    //connection.interrupt();
                    try {
                        server.close();
                        toolbar.remove(connect);
                        connect = makeOptionButtons("offline.png", KeyEvent.VK_O);
                        connect.addActionListener(a -> networkDialog());
                        toolbar.add(connect);
                        toolbar.revalidate();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case JOptionPane.NO_OPTION:
                    break;
            }
        });
        toolbar.add(connect);
        toolbar.revalidate();
    }

    /** Callback to be called when the connect button is clicked. */
    private void connectionStart(){
        try {
            server = new ServerSocket(PORT_NUMBER);
            onlineStatusUI();
            logT.append("\nServer Started on Port " + PORT_NUMBER + ".");
//            while (true){
//                Socket socket = server.accept();
//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new NetworkUI();
    }
}
