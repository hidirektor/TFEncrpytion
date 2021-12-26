package me.t3sl4.textfileencoder.Controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import me.t3sl4.textfileencoder.Client.Client;
import me.t3sl4.textfileencoder.Server.Server;
import me.t3sl4.textfileencoder.Utils.AES;
import me.t3sl4.textfileencoder.Utils.FileEncryption;
import me.t3sl4.textfileencoder.Utils.FileZIP;
import me.t3sl4.textfileencoder.Utils.SHA256;

public class TextEncodeController implements Initializable {
    @FXML
    private TextField selectedKeyField;

    @FXML
    private TextArea textInput;

    @FXML
    private TextArea keyTextArea;

    @FXML
    private TextArea plainTextArea;

    @FXML
    private TextArea cipherTextArea;

    @FXML
    private TextField selectedFilePath;

    @FXML
    private TextField nicknameField;

    @FXML
    private CheckBox sha256CheckBox;

    @FXML
    private CheckBox spnCheckBox;

    @FXML
    private TextField fileExtension;

    @FXML
    private ImageView textEncryptionImageView;

    @FXML
    private ImageView fileEncryptionImageView;

    @FXML
    private ImageView serverStatusImageView;

    public static TextArea originPlainTextArea;
    public static TextArea originCipherTextArea;

    Alert alert = new Alert(Alert.AlertType.ERROR);
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static String key = null;
    public static String sha256CipherText = null;
    public static String sha256PlainText = null;
    public static String spnCipherText = null;
    public static File selectedFile = null;
    public static String newPath = null;
    public static boolean connectionStatus = false;

    private boolean fileEncodeStat = false;
    private boolean textEncodeStat = false;
    public static boolean sha256EncodeStat = false;
    public static boolean spnEncodeStat = false;

    private Thread connectionThread, startServerThread, stopServerThread;
    private Client client;
    private Server server;

    private Image FirstTick = new Image(getClass().getResourceAsStream("/images/FirstTick.png"));
    private Image SecTick = new Image(getClass().getResourceAsStream("/images/SecTick.png"));

    public static int keyStat = 0;

    public void initialize(URL location, ResourceBundle resources) {
        this.originPlainTextArea = plainTextArea;
        this.originCipherTextArea = cipherTextArea;

        textEncryptionImageView.setImage(SecTick);
        fileEncryptionImageView.setImage(SecTick);

        checkServerStatus();
    }

    public void showKey() {
        if(key != null) {
            if(keyStat == 0) {
                selectedKeyField.setText(key);
                keyStat = 1;
            } else if(keyStat == 1) {
                selectedKeyField.setText("••••••••");
                keyStat = 0;
            }
        }
    }

    public void sha256CheckBoxChange() {
        if(!spnCheckBox.isSelected()) {
            if(sha256CheckBox.isSelected()) {
                System.out.println("SHA256 SEÇİLDİ!");
            }
        } else {
            clrChoices();
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Aynı anda sadece tek bir şifreleme algoritması seçebilirsin.");
            alert.showAndWait();
        }
    }

    public void spnCheckBoxChange() {
        if(!sha256CheckBox.isSelected()) {
            if(spnCheckBox.isSelected()) {
                System.out.println("SPN SEÇİLDİ!");
            }
        } else {
            clrChoices();
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Aynı anda sadece tek bir şifreleme algoritması seçebilirsin.");
            alert.showAndWait();
        }
    }

    public void encryptPlainText() throws Exception {
        if(!sha256CheckBox.isSelected() && !spnCheckBox.isSelected()) {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Programın temel mantığı zaten şifrelemek o yüzden lütfen yukarıdan bir tane şifreleme algoritması seç.");
            alert.showAndWait();
        } else {
            if(key != null) {
                if(sha256CheckBox.isSelected()) {
                    if(textInput.getText() != null) {
                        sha256CipherText = SHA256.hashMac(textInput.getText(), key);
                        sha256PlainText = plainTextArea.getText();
                        plainTextArea.setText(textInput.getText());
                        cipherTextArea.setText(sha256CipherText);
                        textEncryptionImageView.setImage(FirstTick);
                        textEncodeStat = true;
                        sha256EncodeStat = true;
                    }
                } else if(spnCheckBox.isSelected()) {
                    spnCipherText = AESencrypt(textInput.getText(), key);
                    plainTextArea.setText(AESDecrypt(spnCipherText, key));
                    cipherTextArea.setText(spnCipherText);
                    textEncryptionImageView.setImage(FirstTick);
                    textEncodeStat = true;
                    spnEncodeStat = true;
                }
            } else {
                alert.setTitle("HATA!");
                alert.setHeaderText("Şifreleme Algoritması Hatası.");
                alert.setContentText("Lütfen şifreleme anahtarı belirle. Aksi takdirde düzgün bir şifreleme elde edemezsin.");
                alert.showAndWait();
            }
        }
    }

    public void settingKey() {
        if(keyTextArea.getText() != null) {
            key = keyTextArea.getText();
            selectedKeyField.setText("••••••••");
            clrChoices();
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Lütfen şifreleme anahtarı gir. Aksi takdirde düzgün bir şifreleme elde edemezsin.");
            alert.showAndWait();
        }
    }

    public void clearEncodedText() {
        if(textEncodeStat) {
            plainTextArea.setText(null);
            cipherTextArea.setText(null);
            textEncryptionImageView.setImage(SecTick);
            textEncodeStat = false;
            spnEncodeStat = false;
            sha256EncodeStat = false;
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Temizleme Hatası.");
            alert.setContentText("Hiçbir metni şifrelemediğin için dosya bölümünü temizleyemezsin.");
            alert.showAndWait();
        }
    }

    public void clearEncodedFile() {
        if(fileEncodeStat) {
            fileEncryptionImageView.setImage(SecTick);
            fileEncodeStat = false;
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Temizleme Hatası.");
            alert.setContentText("Hiçbir dosyayı şifrelemediğin için dosya bölümünü temizleyemezsin.");
            alert.showAndWait();
        }
    }

    public void clearAllAction() {
        if(textEncodeStat || fileEncodeStat) {
            plainTextArea.setText(null);
            cipherTextArea.setText(null);
            clrChoices();
            textEncryptionImageView.setImage(SecTick);
            fileEncryptionImageView.setImage(SecTick);
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Temizleme Hatası.");
            alert.setContentText("Hiçliği temizleyemezsin.");
            alert.showAndWait();
        }
    }

    public void startServer() throws IOException {
        serverStartStop(1, isPortAvailable(1334));
    }

    public void stopServer() throws IOException {
        serverStartStop(0, isPortAvailable(1334));
    }

    public void fileEncodingButton() {
        FileChooser ds = new FileChooser();
        ds.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sadece .txt, .dat, .gif", "*.txt", "*.dat", "*.gif", "*.encrypted"));
        File d = ds.showOpenDialog(null);
        if(d != null) {
            selectedFile = d;
            selectedFilePath.setText(selectedFile.getAbsolutePath());
        }
    }

    public void encodeSelectedFile() {
        if(selectedFile != null && key != null) {
            try {
                fileExtension.setText(findExtension(selectedFile.getAbsolutePath()));
                FileEncryption.encryptFile(selectedFile.getAbsolutePath(), key);
                fileEncryptionImageView.setImage(FirstTick);
                fileEncodeStat = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Dosya şifrelemek için önce bir anahtar belirlemeli ve şifrelenecek dosyayı seçmelisin.");
            alert.showAndWait();
        }
    }

    public void connectionButton() {
        if(!isPortAvailable(1334)) {
            if(!nicknameField.getText().trim().isEmpty()) {
                String userName = nicknameField.getText();
                connectServer(userName, 1);
                connectionStatus = true;
            } else {
                alert.setTitle("HATA!");
                alert.setHeaderText("Kullanıcı Adı Hatası.");
                alert.setContentText("Sunucuya bağlanmak için önce bir kullanıcı adı belirlemelisin.");
                alert.showAndWait();
            }
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Sunucu Hatası.");
            alert.setContentText("Açık olmayan sunucuya bağlanamazsın.");
            alert.showAndWait();
        }
    }

    public void sendButtonAction() {
        if(connectionStatus) {
            Task<Void> sendTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if(textEncodeStat) {
                        if(sha256CipherText != null) {
                            //client.sendCustomMessage(sha256CipherText, "randomkeyrandomkey", 1);
                        } else if(spnCipherText != null) {
                            //client.sendCustomMessage(spnCipherText, key, 2);
                        }
                    } else if(fileEncodeStat) {
                        //client.sendFile(selectedFilePath.getText());
                        clearEncodedFile();
                    }
                    System.out.println("Mesaj gönderildi !");
                    return null;
                }
            };
            Thread sendThread = new Thread(sendTask);
            sendThread.start();
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Bağlantı Hatası.");
            alert.setContentText("Mesaj || dosya göndermek için önce sunucuya bağlanmalısın.");
            alert.showAndWait();
        }
    }

    //Gerekli fonksiyonlar:
    public void decodeSelectedFile(File selectedDecFile) {
        if(selectedDecFile != null && key != null && fileExtension.getText() != null) {
            try {
                if(selectedDecFile.getName().contains("encrypted")) {
                    Path source = Paths.get(selectedDecFile.getAbsolutePath());
                    Path target = Paths.get(System.getProperty("user.home") + "/Desktop");
                    FileZIP.unzipFolder(source, target);
                    File delete = new File(selectedDecFile.getAbsolutePath());
                    delete.delete();
                    FileEncryption.decryptFile(newPath, key, fileExtension.getText());
                } else {
                    alert.setTitle("HATA!");
                    alert.setHeaderText("Şifreleme Algoritması Hatası.");
                    alert.setContentText("Yalnızca şifrelenmiş dosyaları içeren arşivleri seçebilirsin.");
                    alert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Dosya şifrelemek için önce bir anahtar belirlemeli ve şifrelenecek dosyayı seçmelisin.");
            alert.showAndWait();
        }
    }

    private void clrChoices() {
        sha256CheckBox.setSelected(false);
        spnCheckBox.setSelected(false);
        textInput.setText(null);
        keyTextArea.setText(null);
        textEncodeStat = false;
        fileEncodeStat = false;
        sha256EncodeStat = false;
        spnEncodeStat = false;
    }

    private String AESencrypt(String plainText, String key) throws Exception {
        String encryptedTextBase64 = AES.encrypt(plainText.getBytes(UTF_8), key);
        return encryptedTextBase64;
    }

    private String AESDecrypt(String cipherText, String key) throws Exception {
        String decryptedTextBase64 = AES.decrypt(cipherText, key);
        return decryptedTextBase64;
    }

    private String findExtension(String fileName) {
        String extension = "";

        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }
        return "." + extension;
    }

    private static boolean isPortAvailable(int port) {
        try {
            ServerSocket srv = new ServerSocket(port);
            srv.close();
            srv = null;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void serverStartStop(int serverStatus, boolean portStatus) throws IOException {
        if(serverStatus == 1) {
            Task<Void> startServerTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ServerSocket serverSocket = new ServerSocket(1334);
                    server = new Server(serverSocket);
                    Task<Void> startFunctionCallTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            server.startServer();
                            return null;
                        }
                    };
                    Thread startFunctionCallThread = new Thread(startFunctionCallTask);
                    startFunctionCallThread.start();
                    System.out.println("SUNUCU BASLATILDI");
                    return null;
                }
            };

            startServerThread = new Thread(startServerTask);
            if(portStatus == false) {
                alert.setTitle("HATA!");
                alert.setHeaderText("Server Hatası.");
                alert.setContentText("Açık olan sunucuyu tekrar açamazsın.");
                alert.showAndWait();
            } else if(portStatus == true) {
                startServerThread.start();
                serverStatusImageView.setImage(FirstTick);
                connectionStatus = true;
            }
        } else if(serverStatus == 0) {
            Task<Void> stopServerTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    server.closeServerSocket();
                    return null;
                }
            };

            stopServerThread = new Thread(stopServerTask);
            if(portStatus == false) {
                stopServerThread.start();
                serverStatusImageView.setImage(SecTick);
                connectionStatus = false;
            } else if(portStatus == true) {
                alert.setTitle("HATA!");
                alert.setHeaderText("Server Hatası.");
                alert.setContentText("Açık olmayan sunucuyu kapatamazsın.");
                alert.showAndWait();
            }
        }
    }

    private void checkServerStatus() {
        Runnable serverStatusImageRunnable = new Runnable() {
            public void run() {
                if(isPortAvailable(1334)) {
                    serverStatusImageView.setImage(SecTick);
                } else {
                    serverStatusImageView.setImage(FirstTick);
                }
            }
        };

        ScheduledExecutorService serverStatusExec = Executors.newScheduledThreadPool(1);
        serverStatusExec.scheduleAtFixedRate(serverStatusImageRunnable , 0, 1, TimeUnit.SECONDS);
    }

    private void connectServer(String userName, int type) {
        Task<Void> connectionServerTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Socket socket = new Socket("localhost", 1334);
                Task<Void> connectionConverterTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        client = new Client(socket, userName);
                        client.listenForMessage();
                        //client.sendMessage();
                        return null;
                    }
                };
                Thread connectionConverterThread = new Thread(connectionConverterTask);
                connectionConverterThread.start();
                System.out.println(userName + " olarak sunucuya bağlandın.");
                return null;
            }
        };
        connectionThread = new Thread(connectionServerTask);
        if(type == 1) {
            connectionThread.start();
        }
    }
}