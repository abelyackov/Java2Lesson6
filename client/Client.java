package client;

import server.Server;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;

        final String IP_ADDRESS = "localhost";
        final int PORT = 13001;

        try {
            socket = new Socket(IP_ADDRESS, PORT);
            BufferedReader clientMsg = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader servMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread thread = new Thread(new ClientReadMessage(servMsg));
            thread.start();

            while (true) {
                String client = clientMsg.readLine();
                if (client.equals("/exit")) {
                    out.println("/exit");
                    thread.interrupt();
                    break;
                }
                out.println(client);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static class ClientReadMessage implements Runnable {

        BufferedReader in;

        public ClientReadMessage(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String s = in.readLine();
                    if (s.equals(null) || s.equals("/exit")) {
                        System.out.println("Сервер отключен");
                        break;
                    }
                    System.out.println("Сообщение от сервера: " + s);
                }
            } catch (IOException e) {
                System.err.println("Соединение с сервером разорвано");
            }
        }
    }
}
