package code.Network;

import code.Sudoku.HistoryNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OnlineService {
    private int port;
    private String ip;
    private HistoryNode history;
    private Socket socket;
    private ServerSocket server;
    private Thread serverThread, clientThread;
    private NetworkAdapter adapter;

    OnlineService(int port, String ip, HistoryNode history) {
        this.port = port;
        this.ip = ip;
        this.history = history;
    }

    void startServer() {
        serverThread = new Thread(() -> {
            try {
                server = new ServerSocket(port);
                while (true) {
                    socket = server.accept();
                    adapter = new NetworkAdapter(socket);
                    adapter.setMessageListener(new NetworkAdapter.MessageListener() {
                        /**
                         * To be called when a message is received.
                         * The type of the received message along with optional content
                         * (x, y, z and others) are provided as arguments.
                         *
                         * @param type   Type of the message received
                         * @param x      First argument
                         * @param y      Second argument
                         * @param z      Third argument
                         * @param others Additional arguments
                         */
                        @Override
                        public void messageReceived(NetworkAdapter.MessageType type, int x, int y, int z, int[] others) {
                            switch (type) {
                                case JOIN:
                                    adapter.writeJoinAck(history.size(), );
                                case JOIN_ACK:  // x (response), y (size), others (board)
                                case NEW:      // x (size), others (board)
                                case NEW_ACK:   // x (response)
                                case FILL:     // x (x), y (y), z (number)
                                case FILL_ACK:  // x (x), y (y), z (number)
                                case QUIT:
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        serverThread.start();
    }

}
