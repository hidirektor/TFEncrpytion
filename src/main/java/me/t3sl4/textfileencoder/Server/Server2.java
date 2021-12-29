package me.t3sl4.textfileencoder.Server;

import me.t3sl4.textfileencoder.Server.origin.ClientHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server2 {
    static ServerSocket ss;
    static Socket s;
    static DataInputStream din;
    static DataOutputStream dout;

    static List<ServerHandler> connections = new ArrayList<ServerHandler>();
    static boolean serverRun = true;

    public Server2(ServerSocket serverSocket) {
        this.ss = serverSocket;
    }

    public static void main(String[] args){
        try{
            startServer();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void startServer() {
        try {
            while(!ss.isClosed()) {
                System.out.println("Server Run");
                while(serverRun){
                    s = ss.accept();
                    System.out.println("Client connected");
                    ServerHandler sc = new ServerHandler(s);
                    sc.start();
                    connections.add(sc);
                    System.out.println("cli number : " + connections.size());

                }
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        try {
            if(ss != null) {
                ss.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
