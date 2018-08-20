package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(13001);
            System.out.println("Сервер запущен");

            socket = server.accept();
            System.out.println("Клиент подключен");

            BufferedReader servMsg = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader clientMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread thread = new Thread(new ServReadMessage(clientMsg)); //поток чтения входящих сообщений
            thread.start();

            while (true) {
                String serv = servMsg.readLine();
                if (serv.equals("/exit")) {
                    out.println("/exit");
                    thread.interrupt();
                    break;
                }
                out.println(serv);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ServReadMessage implements Runnable {

        BufferedReader in;

        public ServReadMessage(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String s = in.readLine();
                    if (s.equals(null) || s.equals("/exit")) {
                        System.out.println("Клиент отключился");
                        break;
                    }
                    System.out.println("Сообщение от клиента: " + s);
                }
            } catch (IOException e) {
                System.err.println("Сервер отключен");
            }
        }
    }
}
