package me.t3sl4.textfileencoder.Client;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import me.t3sl4.textfileencoder.Controllers.TextEncodeController;
import me.t3sl4.textfileencoder.Utils.SPN;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client {

    private static Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private static final int CHUNK_SIZE = 1024;
    private static final File _downloadDir = new File(System.getProperty("user.home") + "/Desktop/");
    Alert alert = new Alert(Alert.AlertType.ERROR);

    public static Thread listenForMessageThread = null;

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

    public void sendFileName(String filePath) {
        try {
            bufferedWriter.write(filePath);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendFile(String path) {
        if (path == null) {
            throw new NullPointerException("Path is null");
        }
        File file = new File(path);
        try {
            System.out.println("Connected to server at " + socket.getInetAddress());

            PrintStream out = new PrintStream(socket.getOutputStream(), true);

            out.println(file.getName());
            out.println(file.length());

            System.out.println("Sending " + file.getName() + " (" + file.length() + " bytes) to server...");
            writeFile(file, socket.getOutputStream());
            System.out.println("Finished sending " + file.getName() + " to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listen4File() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("TEST2");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String name = dis.readUTF();
            System.out.println("TEST3");
            File file = new File(_downloadDir, name);
            long fileSize = dis.readLong();
            System.out.println("Saving " + file + " from user... ("
                    + fileSize + " bytes)");

            System.out.println("Saving " + file + " from user... (" + fileSize + " bytes)");
            saveFile(file, socket.getInputStream());
            System.out.println("Finished downloading " + file + " from user.");
            if (file.length() != fileSize) {
                System.err.println("Error: file incomplete");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveFile(File file, InputStream inStream) {
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);

            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            int pos = 0;
            while ((bytesRead = inStream.read(buffer, 0, CHUNK_SIZE)) >= 0) {
                pos += bytesRead;
                System.out.println(pos + " bytes (" + bytesRead + " bytes read)");
                fileOut.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Finished, filesize = " + file.length());
    }

    private static void writeFile(File file, OutputStream outStream) {
        FileInputStream reader = null;
        try {
            reader = new FileInputStream(file);
            byte[] buffer = new byte[CHUNK_SIZE];
            int pos = 0;
            int bytesRead;
            while ((bytesRead = reader.read(buffer, 0, CHUNK_SIZE)) >= 0) {
                outStream.write(buffer, 0, bytesRead);
                outStream.flush();
                pos += bytesRead;
                System.out.println(pos + " bytes (" + bytesRead + " bytes read)");
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error while reading file");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error while writing " + file.toString() + " to output stream");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendCustomMessage(String message, String key, int type) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                        System.out.println("Mesaj Gönderildi !");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void listenForMessage() {
        Task<Void> listenForMessageTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String msgFromGroupChat;
                String messageListener = "";
                if(!messageListener.equals("stop")) {
                    System.out.println("msglısten: " + messageListener);
                    //if(messageListener.equalsIgnoreCase("start")) {
                        try {
                            msgFromGroupChat = bufferedReader.readLine();
                            while(msgFromGroupChat.contains("hidsha256") || msgFromGroupChat.contains("hidspn")) {
                                if(msgFromGroupChat != null) {
                                    if(msgFromGroupChat.contains("hidsha256")) {
                                        TextEncodeController.originCipherTextArea.setText(Arrays.toString(msgFromGroupChat.split("hidsha256")));
                                    } else if(msgFromGroupChat.contains("hidspn")) {
                                        String[] newMessage = msgFromGroupChat.split("hidspn");
                                        String spnCipherText = newMessage[1];
                                        TextEncodeController.originCipherTextArea.setText(spnCipherText);
                                        TextEncodeController.originPlainTextArea.setText(SPN.decryptWithSpn(spnCipherText, TextEncodeController.key));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                        }
                    //}
                }
                return null;
            }
        };
        listenForMessageThread = new Thread(listenForMessageTask);
        listenForMessageThread.start();
    }

    public Socket getClientSocket() {
        return this.socket;
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