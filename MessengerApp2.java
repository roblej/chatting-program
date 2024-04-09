package final_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessengerApp2 extends JFrame {
    private String username;
    private JTextArea chatTextArea;
    private JTextField messageField;

    private static final String SERVER_IP = "192.168.0.14";  // 서버의 IP 주소
    private static final int SERVER_PORT = 5000;

    public MessengerApp2(String username) {
        this.username = username;
        initializeUI();
        connectToServer();
    }

    private void initializeUI() {
        setTitle("홍톡2.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        messageField = new JTextField();
        panel.add(messageField, BorderLayout.SOUTH);

        JButton sendButton = new JButton("전송");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panel.add(sendButton, BorderLayout.EAST);

        setVisible(true);
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            // 클라이언트에서 서버로 사용자 이름 전송
            outputStream.writeObject(username);

            // 클라이언트에서 서버로 메시지 전송을 위한 스레드 시작
            new Thread(new ClientMessageHandler(socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            // 메시지가 비어있지 않으면 서버로 메시지 전송
            sendMessageToServer(username + ": " + message);
            messageField.setText("");  // 메시지 전송 후 입력 창 비우기
        }
    }

    private void sendMessageToServer(String message) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            // 클라이언트에서 서버로 메시지 전송
            outputStream.writeObject(message);
            outputStream.reset();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientMessageHandler implements Runnable {
        private Socket socket;

        public ClientMessageHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    // 서버에서 클라이언트로 메시지 수신
                    String message = (String) inputStream.readObject();

                    // 채팅 메시지 출력
                    chatTextArea.append(message + "\n");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("사용자 이름을 입력하세요:");
        if (username != null && !username.isEmpty()) {
            new MessengerApp(username);
        } else {
            JOptionPane.showMessageDialog(null, "사용자 이름을 입력하세요.");
        }
    }
}
