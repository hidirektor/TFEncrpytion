package me.t3sl4.textfileencoder.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import me.t3sl4.textfileencoder.Server.Client;
import me.t3sl4.textfileencoder.utils.AES;
import me.t3sl4.textfileencoder.utils.FileEncryption;
import me.t3sl4.textfileencoder.utils.FileZIP;
import me.t3sl4.textfileencoder.utils.SHA256;

public class TextEncodeController implements Initializable {
    @FXML
    private TextField selectedKeyField;

    @FXML
    private TextArea textInput;

    @FXML
    private TextArea keyTextArea;

    @FXML
    public TextArea plainTextArea;

    @FXML
    public TextArea cipherTextArea;

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

    Alert alert = new Alert(Alert.AlertType.ERROR);
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static String key = null;
    public static String sha256CipherText = null;
    public static String spnCipherText = null;
    public static File selectedFile = null;
    public static String newPath = null;
    public static boolean connectionStatus = false;

    private Image FirstTick = new Image("https://i.imgur.com/I1RLGli.png");
    private Image SecTick = new Image("https://i.imgur.com/hOS8sOI.png");

    public static int keyStat = 0;

    public void initialize(URL location, ResourceBundle resources) {
        textEncryptionImageView.setImage(SecTick);
        fileEncryptionImageView.setImage(SecTick);
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

    public void sha256CheckBoxChange() throws UnsupportedEncodingException, NoSuchAlgorithmException {
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
                        plainTextArea.setText(textInput.getText());
                        cipherTextArea.setText(sha256CipherText);
                        textEncryptionImageView.setImage(FirstTick);
                        PrintWriter writer = new PrintWriter("sha256plain.txt", "UTF-8");
                        writer.println(textInput.getText());
                        writer.close();
                        clrChoices();
                    }
                } else if(spnCheckBox.isSelected()) {
                    spnCipherText = AESencrypt(textInput.getText(), key);
                    plainTextArea.setText(AESDecrypt(spnCipherText, key));
                    cipherTextArea.setText(spnCipherText);
                    textEncryptionImageView.setImage(FirstTick);
                    clrChoices();
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
        plainTextArea.setText(null);
        cipherTextArea.setText(null);
        textEncryptionImageView.setImage(SecTick);
    }

    public void clearEncodedFile() {
        fileEncryptionImageView.setImage(SecTick);
    }

    public void fileEncodingButton() throws IOException {
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
                selectedFilePath.setText(null);
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

    public void connectionButton() throws IOException {
        //if(connectionStatus != true) {
            if(!nicknameField.getText().trim().isEmpty()) {
                String username = nicknameField.getText();
                Socket socket = new Socket("localhost", 1234);
                Client client = new Client(socket, username);
                connectionStatus = true;
                //client.listenForMessage();
                //client.sendMessage();
            } else {
                alert.setTitle("HATA!");
                alert.setHeaderText("Kullanıcı Adı Hatası.");
                alert.setContentText("Sunucuya bağlanmak için önce bir kullanıcı adı belirlemelisin.");
                alert.showAndWait();
            }
        //}
    }

    public void sendButtonAction() throws IOException {
        if(connectionStatus) {
            //Mesaj gönderme kısmı
            clearEncodedText();
            clearEncodedFile();
            System.out.println("Mesaj gönderildi !");
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
    }

    private String AESencrypt(String plainText, String key) throws Exception {
        String encryptedTextBase64 = AES.encrypt(plainText.getBytes(UTF_8), key);
        //byte[] encodedAESEncyrpt = encryptedTextBase64.getBytes();
        //byte[] decoded = Base64.decodeBase64(encodedAESEncyrpt);
        //String cipherText = new String(decoded);
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

    private void disconnectSocket(Socket socket) throws IOException {
        socket.close();
    }
}