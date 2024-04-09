package final_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatFrame extends JFrame {
    private String senderUsername;
    private String receiverUsername;
    private JTextArea chatTextArea;
    private JTextField messageField;
    private ObjectOutputStream outputStream;

    public ChatFrame(String senderUsername, String receiverUsername) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        initializeUI();
        setupServer();  // 채팅을 시작하면 서버를 시작
    }

    private void initializeUI() {
        setTitle("1:1 채팅 - " + receiverUsername);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messagePanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("전송");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        messagePanel.add(sendButton, BorderLayout.EAST);

        panel.add(messagePanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            appendMessage(senderUsername + ": " + message);
            messageField.setText("");

            try {
                outputStream.writeObject(message);
                outputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendMessage(String message) {
        chatTextArea.append(message + "\n");
    }

    private void setupServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);  // 랜덤 포트 사용
            int port = serverSocket.getLocalPort();
            setTitle("1:1 채팅 - " + receiverUsername + " (포트: " + port + ")");

            // 클라이언트에게 포트 전송
            new Thread(new ServerWaiter(serverSocket)).start();

            // 클라이언트와 연결
            Socket socket = serverSocket.accept();
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            new Thread(new ClientListener(socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerWaiter implements Runnable {
        private ServerSocket serverSocket;

        public ServerWaiter(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                Socket clientSocket = serverSocket.accept();
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                new Thread(new ClientListener(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientListener implements Runnable {
        private Socket socket;
        private ObjectInputStream inputStream;

        public ClientListener(Socket socket) {
            this.socket = socket;
            try {
                this.inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = (String) inputStream.readObject();
                    appendMessage(receiverUsername + ": " + message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

