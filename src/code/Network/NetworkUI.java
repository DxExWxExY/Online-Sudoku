package code.Network;

import code.Sudoku.SudokuDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class NetworkUI extends SudokuDialog {
    private JPanel config, log;
    private JButton connect, host, join, disconnect;
    private JTextArea ipT, portT, logT = new JTextArea("Network Log",20,10);
    private static NetworkAdapter server;
    private static NetworkAdapter client;


    private NetworkUI() {
        super();
    }

    /**
     * Create a control panel consisting of new and number buttons.
     */
    @Override
    protected JPanel makeToolBar() {

        /* Create tool bar JPanel and JButtons */
        JPanel toolBar = new JPanel();
        JButton undo = makeOptionButtons("undo.png", KeyEvent.VK_Z);
        JButton redo = makeOptionButtons("redo.png", KeyEvent.VK_Y);
        JButton solve = makeOptionButtons("solve.png", KeyEvent.VK_S);
        JButton can = makeOptionButtons("can.png", KeyEvent.VK_C);
        connect = makeOptionButtons("offline.png", KeyEvent.VK_O);

        /* Add action listeners */
        undo.addActionListener(e -> undo());
        redo.addActionListener(e -> redo());
        solve.addActionListener(e -> solve());
        can.addActionListener(e -> isSolvable());
        connect.addActionListener(e -> networkDialog());

        /* Add buttons to tool bar */
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

        /* Declare menu items */
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem newGame = new JMenuItem("New Game",KeyEvent.VK_N);
        JMenuItem exit = new JMenuItem("Exit",KeyEvent.VK_Q);
        JMenuItem settings = new JMenuItem("Network", KeyEvent.VK_O);

        /* Menu accelerators */
        menu.setMnemonic(KeyEvent.VK_B);
        newGame.setAccelerator(KeyStroke.getKeyStroke("alt A"));
        exit.setAccelerator(KeyStroke.getKeyStroke("alt E"));
        settings.setAccelerator(KeyStroke.getKeyStroke("alt O"));

        /*Menu Items Icons*/
        newGame.setIcon(createImageIcon("new.png"));
        exit.setIcon(createImageIcon("exit.png"));
        settings.setIcon(createImageIcon("net.png"));

        /* Incorporate menu items into menu, and menu into menu bar */
        menu.add(newGame);
        menu.add(exit);
        menu.add(settings);
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
                    history.newGame(4);
                    resetNumberButtons();
                    break;
                case JOptionPane.NO_OPTION:
                    history.newGame(9);
                    resetNumberButtons();
                    break;
                case JOptionPane.CANCEL_OPTION:
                    System.exit(0);
                    break;
            }

            resetPointer();
            boardPanel.reset();
            content.revalidate();
            repaint();
        });
        exit.addActionListener(e -> System.exit(0));
        settings.addActionListener(e -> networkDialog());
    }

    /**
     *
     */
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
        try {
            ipT = new JTextArea(String.valueOf(InetAddress.getLocalHost().getHostAddress()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        portT = new JTextArea("8000");
        host = new JButton("Host");
        join = new JButton("Join");
        join.addActionListener(e -> {
            joinClicked();
            host.setEnabled(false);
            join.setEnabled(false);
        });
        host.setFocusPainted(false);
        host.addActionListener(e -> {
            hostClicked();
            host.setEnabled(false);
            join.setEnabled(false);
        });

        JButton test = new JButton("Test");
        test.addActionListener(e -> {
            try {
                if (server.connected) {
                    onlineStatusUI();
                    JButton disconnect = new JButton("Disconnect");
                    disconnect.addActionListener(a -> {
                    });
                    config.add(disconnect);
                    server.setMessage(hPointer.getData(0,0));
                    logT.append("\n"+server.sendMessage());
                }
            } catch (NullPointerException b) {
                if (client.connected) {
                    onlineStatusUI();
                    JButton disconnect = new JButton("Disconnect");
                    disconnect.addActionListener(a -> {
                    });
                    config.add(disconnect);
                    client.setMessage(hPointer.getData(0,0));
                    logT.append("\n"+client.sendMessage());
                }
            }
        });

        config.add(ipL);
        config.add(ipT);
        config.add(portL);
        config.add(portT);
        config.add(host);
        config.add(join);
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

    private void onlineStatusUI() {
        toolbar.remove(connect);
        connect = makeOptionButtons("online.png", KeyEvent.VK_O);
        connect.addActionListener(e -> {
            Object[] options = {"Yes", "Cancel"};
            int n = JOptionPane.showOptionDialog(null, "Do You Want to join?",
                    "join", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[1]);
            switch (n) {
                case JOptionPane.YES_OPTION:
                    try {
                        server.closeConnections();
                        toolbar.remove(connect);
                        connect = makeOptionButtons("offline.png", KeyEvent.VK_O);
                        connect.addActionListener(a -> networkDialog());
                        toolbar.add(connect);
                        toolbar.revalidate();
                    } catch (NullPointerException b) {
                        client.closeConnections();
                        toolbar.remove(connect);
                        connect = makeOptionButtons("offline.png", KeyEvent.VK_O);
                        connect.addActionListener(a -> networkDialog());
                        toolbar.add(connect);
                        toolbar.revalidate();
                    }
                    break;
                case JOptionPane.NO_OPTION:
                    break;
            }
        });
        toolbar.add(connect);
        toolbar.revalidate();
        config.remove(host);
        config.remove(join);
        disconnect = new JButton("Disconnect");
        disconnect.addActionListener(o -> {
            try {
                server.closeConnections();
                toolbar.remove(connect);
                connect = makeOptionButtons("offline.png", KeyEvent.VK_O);
                connect.addActionListener(a -> networkDialog());
                config.add(host);
                config.add(join);
                config.remove(disconnect);
                config.revalidate();
                config.repaint();
                toolbar.add(connect);
                toolbar.revalidate();
            } catch (NullPointerException i) {
                toolbar.remove(connect);
                connect = makeOptionButtons("offline.png", KeyEvent.VK_O);
                connect.addActionListener(a -> networkDialog());
                config.add(host);
                config.add(join);
                config.remove(disconnect);
                config.revalidate();
                config.repaint();
                toolbar.add(connect);
                toolbar.revalidate();server.closeConnections();
            }
        });
        config.add(disconnect);
        config.revalidate();
        toolbar.revalidate();
    }

    private void joinClicked() {
        new Thread(() -> {
            client = new Client(Integer.parseInt(portT.getText()), ipT.getText(), hPointer, logT);
            logT.append("\nConnected to Server!");
            try {
                onlineStatusUI();
                client.whileChatting();
            } catch(IOException ignored) {
            }
        }).start();
        logT.append("\nWaiting for Server...");
    }

    /** Callback to be called when the connect button is clicked. */
    private void hostClicked(){
        new Thread(() -> {
            server = new Server(hPointer, logT);
            logT.append("\nConnected to Client!");
            try {
                onlineStatusUI();
                server.sendBoard();
                server.whileChatting();
            } catch(IOException ignored) {
            }

        }).start();
        logT.append("\nServer Waiting...");
    }

    public static void main(String[] args) {
        new NetworkUI();
    }
}
