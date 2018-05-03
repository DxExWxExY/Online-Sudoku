package code.Network;

import code.Sudoku.SudokuDialog;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
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

    private JPanel config, log;
    private JButton connect;
    /**
     * Default port number on which this server to be run.
     */
    private static final int PORT = findFreePort();
    private Socket socket;
    private JTextArea ipT, portT, logT = new JTextArea("Network Log",20,10);
    private SudokuServer server;


    private NetworkUI() {
        super();
        server = new SudokuServer(logT, history, PORT);
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

    /**
     * Configure the UI.
     */
    @Override
    protected void configureMenu() {
        JMenu menu = new JMenu("Menu");
        JMenuBar mb = new JMenuBar();
        JMenuItem newGame, exit, settings;
        setJMenuBar(mb);
        /*Menu Items Declaration*/
        newGame = new JMenuItem("New Game",KeyEvent.VK_N);
        exit = new JMenuItem("Exit",KeyEvent.VK_Q);
        settings = new JMenuItem("Network", KeyEvent.VK_O);
        /*Menu Accelerators*/
        newGame.setAccelerator(KeyStroke.getKeyStroke("alt A"));
        exit.setAccelerator(KeyStroke.getKeyStroke("alt E"));
        settings.setAccelerator(KeyStroke.getKeyStroke("alt O"));
        /*Menu Items Icons*/
        newGame.setIcon(createImageIcon("new.png"));
        exit.setIcon(createImageIcon("exit.png"));
        settings.setIcon(createImageIcon("net.png"));

        menu.add(newGame);
        menu.add(exit);
        menu.add(settings);
        menu.setMnemonic(KeyEvent.VK_B);
        mb.add(menu);
        setJMenuBar(mb);
        setLayout(null);
        setVisible(true);
        /*Menu Items Listeners*/
        newGame.addActionListener(e -> {
            Object[] options = {"4x4", "9x9", "Exit"};
            int n = JOptionPane.showOptionDialog(null, "Select a Sudoku Size",
                    "New Game", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[2]);
            switch (n) {
                case JOptionPane.YES_OPTION:
                    super.newHistory(4);
                    content.remove(numberButtons);
                    numberButtons = super.makeNumberButtons();
                    content.add(numberButtons);
                    break;
                case JOptionPane.NO_OPTION:
                    super.newHistory(9);
                    content.remove(numberButtons);
                    numberButtons = super.makeNumberButtons();
                    content.add(numberButtons);
                    break;
                case JOptionPane.CANCEL_OPTION:
                    System.exit(0);
                    break;
            }
            boardPanel.reset = true;
            boardPanel.setBoard(history.getBoard());
            history.generateBoard();
            content.revalidate();
            repaint();
        });
        exit.addActionListener(e -> System.exit(0));
        settings.addActionListener(e -> networkDialog());
    }


    private void networkDialog() {
        makeNetworkOptions();
        makeNetworkLog();
        makeNetworkWindow();
    }

    private void makeNetworkOptions() {
        config = new JPanel(new GridLayout(3,2, 0, 10));
        config.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Network Settings"),
                BorderFactory.createEmptyBorder(30,5,30,5)));
        config.setSize(new Dimension(300,150));
        JLabel ipL = new JLabel("Server Address");
        JLabel portL = new JLabel("Port Number");
        ipT = new JTextArea("localhost");
        portT = new JTextArea(String.valueOf(PORT));
        JButton connectButton = new JButton("Connect");
        JButton test = new JButton("test");
        test.addActionListener(e -> {});
        connectButton.setFocusPainted(false);
        connectButton.addActionListener(e -> {});

        config.add(ipL);
        config.add(ipT);
        config.add(portL);
        config.add(portT);
        config.add(connectButton);
        config.add(test);

    }

    private void makeNetworkLog() {
        log = new JPanel(new GridLayout(1,1));
        log.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Network Log"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        logT.setLineWrap(true);
        logT.setEditable(false);
        logT.setAutoscrolls(true);
        JScrollPane scroll = new JScrollPane(logT);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setAutoscrolls(true);
        log.add(scroll);
    }

    private void makeNetworkWindow() {
        Dimension pos = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame networkSettings = new JFrame("Connection Settings");
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
                    try {
                        socket.close();
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

    public static void main(String[] args) {
        new NetworkUI();
    }
}
