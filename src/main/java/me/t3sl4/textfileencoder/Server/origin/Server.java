package me.t3sl4.textfileencoder.Server.origin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public static boolean serverStatus = false;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                System.out.println("SERVER:: " + clientHandler.clientUsername +" sunucuya katıldı!");

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
                serverStatus = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1334);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}