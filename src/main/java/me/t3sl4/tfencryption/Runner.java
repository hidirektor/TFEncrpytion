package me.t3sl4.tfencryption;

import me.t3sl4.tfencryption.Main.TFEncoder;

import java.net.ServerSocket;

public class Runner {

    public static void main(String[] args) {
        TFEncoder.main(args);
    }

    public static boolean checkServerSocket(ServerSocket ss) {
        if(ss == null) {
            return false;
        }
        return true;
    }
}
