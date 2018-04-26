package code.Network;

import code.Sudoku.SudokuDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Objects;

public class NetworkUI extends SudokuDialog {

    private JFrame networkSettings = new JFrame("Connection Settings");
    private JPanel config = new JPanel();

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
            startConnection();
        }).run();
    }

    private void startConnection() {

    }

    private void makeNetworkWindow() {
        networkSettings.setSize(new Dimension(300,150));
        networkSettings.setIconImage(Objects.requireNonNull(createImageIcon("online.png")).getImage());
        networkSettings.setResizable(false);
        networkSettings.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        networkSettings.setVisible(true);
        networkSettings.add(config);

    }

    private void makeNetworkOptions() {
        try {
            JLabel ipL = new JLabel("Server Address");
            JLabel portL = new JLabel("Port Number");
            JTextArea ipT = new JTextArea(String.valueOf(InetAddress.getLocalHost()));
            JTextArea portT = new JTextArea(String.valueOf(findFreePort()));
            config.setLayout(new GridLayout(3,2, 0, 10));
            config.add(ipL);
            config.add(ipT);
            config.add(portL);
            config.add(portT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
    private static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }

    public static void main(String[] args) {
        new NetworkUI();
    }
}
