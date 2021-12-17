package me.t3sl4.textfileencoder;

import me.t3sl4.textfileencoder.Server.Server;

import java.io.IOException;

public class Runner {

    public static void main(String[] args) throws IOException {
        TFEncoder.main(args);
        /*if(!Server.serverStatus) {
            Server.main(args);
            Server.serverStatus = true;
        } else {
            System.out.println("Server is already opened !");
        }*/
    }
}
