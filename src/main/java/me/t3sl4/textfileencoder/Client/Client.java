package me.t3sl4.textfileencoder.Client;

import javafx.scene.control.Alert;
import me.t3sl4.textfileencoder.Controllers.TextEncodeController;
import me.t3sl4.textfileencoder.Utils.AES;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
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

    public void sendMessage() {
        try {
            bufferedWriter.flush();
            String messageToSend = null;
            while(socket.isConnected()) {
                if(messageToSend != null) {
                    System.out.println("Mesaj: " + messageToSend);
                    bufferedWriter.write(messageToSend);
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                System.out.println("Mesaj: " + messageToSend);
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
                        if(msgFromGroupChat.contains("hidsha256")) {
                            TextEncodeController.originPlainTextArea.setText(String.valueOf(msgFromGroupChat.split("hidsha256")));
                        } else if(msgFromGroupChat.contains("hidspn")) {
                            String[] newMessage = msgFromGroupChat.split("hidspn");
                            String oldKey = newMessage[0];
                            System.out.println("OldKey: " + oldKey);
                            String spnCipherText = newMessage[1];
                            System.out.println("spnCipherKey: " + spnCipherText);
                            if(oldKey == TextEncodeController.key) {
                                TextEncodeController.originCipherTextArea.setText(spnCipherText);
                                TextEncodeController.originPlainTextArea.setText(AES.decrypt(spnCipherText, TextEncodeController.key));
                                System.out.println("Çözülmüş Hali: " + AES.decrypt(spnCipherText, TextEncodeController.key));
                            } else {
                                alert.setTitle("HATA!");
                                alert.setHeaderText("Anahtar Hatası.");
                                alert.setContentText("Şifreyi gönderenle aynı anahtara sahip değilsin.");
                                alert.showAndWait();
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