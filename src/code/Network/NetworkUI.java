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
    private Socket socket;
    private JTextArea ipT, portT, logT = new JTextArea("Network Log",20,10);
    private SudokuServer server;


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
        ipT = new JTextArea("localhost");
        portT = new JTextArea(String.valueOf(8000));
        JButton host = new JButton("Host");
        JButton join = new JButton("join");
        join.addActionListener(e -> testClicked());
        host.setFocusPainted(false);
        host.addActionListener(e -> connectClicked());

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

    /** Callback to be called when the connect button is clicked. */
    private void connectClicked(){
        try {
            socket = new Socket(ipT.getText(), Integer.parseInt(portT.getText()));
            logT.append("\nConnected to "+ipT.getText());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            onlineStatusUI();
        } catch (Exception e) {
            e.printStackTrace();
            logT.append("\nError: "+e);
        }
    }

    private void testClicked() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            for (int i = 0; i < 4; i++) {
                out.println(history.getData(i,i));
                out.flush();
            }
            while (true){
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                else  {
                    out.println("GOT IT!");
                    history.setData(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String text) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        logT.append(text);

                    }
                }
        );
    }

    public static void main(String[] args) {
        new NetworkUI();
    }
}
