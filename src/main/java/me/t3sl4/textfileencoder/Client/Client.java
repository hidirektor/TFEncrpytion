package me.t3sl4.textfileencoder.Client;

import javafx.scene.control.Alert;
import me.t3sl4.textfileencoder.Controllers.TextEncodeController;
import me.t3sl4.textfileencoder.Utils.AES;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private static int CHUNKSIZE = 16384;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendUserName(String userName) {
        try {
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void recieveFile() throws IOException {
        InputStream in = socket.getInputStream();
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        out.write("all");
        out.flush();

        File file = new File(this.outputFilePath);
        FileOutputStream fileOut = new FileOutputStream(file);

        int count;
        byte[] buffer = new byte[8192];
        while ((count = in.read(buffer)) > 0) {
            fileOut.write(buffer, 0, count);
            fileOut.flush();
        }
        fileOut.close();

    }

    private void sendFile(Socket client) throws IOException {
        Scanner in       = new Scanner(client.getInputStream());
        PrintWriter  printOut = new PrintWriter(client.getOutputStream(), true);
        OutputStream out      = client.getOutputStream();

        File file = new File(this.filePath);
        if (!file.exists()) {
            System.out.println("The file doesn't exist!");
            System.exit(-1);
        }

        long chunks = file.length() / CHUNKSIZE;
        long lastChunkSize = file.length() % CHUNKSIZE;
        if ( lastChunkSize != 0) {
            chunks++;
        }
        System.out.println("Serving " + this.filePath + "(" + file.length() + " Bytes / " + chunks +  " Chunks)");

        InputStream fileInput = null;
        try {
            fileInput = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("The file doesn't exist!");
            System.exit(-1);
        }

        byte[] buffer = new byte[CHUNKSIZE];

        int readData;

        System.out.println("Sending file...");
        int sendcounter = 0;
        while ( (readData = fileInput.read(buffer)) != -1 ) {
            out.write(buffer, 0, readData);
            System.out.print(".");
            sendcounter++;
        }

        System.out.println("\nfinished (" + sendcounter +")!");
    }*/

    public void sendCustomMessage(String message, String key, int type) {
        try {
            bufferedWriter.flush();
            String messageToSend = null;
            while(socket.isConnected()) {
                if(type == 1) {
                    messageToSend = "hidsha256" + message;
                } else if(type == 2) {
                    messageToSend = key + "hidspn" + message;
                }
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while(socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        if(msgFromGroupChat != null) {
                            if(msgFromGroupChat.contains("hidsha256")) {
                                TextEncodeController.originCipherTextArea.setText(Arrays.toString(msgFromGroupChat.split("hidsha256")));
                                break;
                            } else if(msgFromGroupChat.contains("hidspn")) {
                                String[] newMessage = msgFromGroupChat.split("hidspn");
                                String spnCipherText = newMessage[1];
                                TextEncodeController.originCipherTextArea.setText(spnCipherText);
                                TextEncodeController.originPlainTextArea.setText(AES.decrypt(spnCipherText, TextEncodeController.key));
                                break;
                            }
                        }
                        System.out.println(msgFromGroupChat);
                    } catch (Exception e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}