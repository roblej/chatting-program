package final_project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ObjectOutputStream> clients = new ArrayList<>();

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("서버가 실행 중입니다. 클라이언트 연결 대기 중...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 연결되었습니다.");

                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                clients.add(outputStream);

                // 클라이언트와 통신을 위한 스레드 시작
                new Thread(new ClientHandler(clientSocket, outputStream)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;

        public ClientHandler(Socket clientSocket, ObjectOutputStream outputStream) {
            this.clientSocket = clientSocket;
            this.outputStream = outputStream;
            try {
                this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 클라이언트에서 서버로 메시지 수신
                    String message = (String) inputStream.readObject();

                    // 서버에서 모든 클라이언트에게 메시지 전송
                    broadcastMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String message) {
        for (ObjectOutputStream clientOutput : clients) {
            try {
                clientOutput.writeObject(message);
                clientOutput.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


