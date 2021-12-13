package me.t3sl4.textfileencoder.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javafx.stage.FileChooser;
import me.t3sl4.textfileencoder.utils.AES;
import me.t3sl4.textfileencoder.utils.FileEncryption;
import me.t3sl4.textfileencoder.utils.SHA256;

public class TextEncodeController {
    @FXML
    private CheckBox sha256CheckBox;

    @FXML
    private CheckBox spnCheckBox;

    @FXML
    private TextArea textInput;

    @FXML
    private TextArea keyTextArea;

    @FXML
    private TextArea plainTextArea;

    @FXML
    private TextArea cipherTextArea;

    @FXML
    private TextField selectedKeyField;

    @FXML
    private TextField selectedFilePath;

    Alert alert = new Alert(Alert.AlertType.ERROR);
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static String key = null;
    public static String sha256CipherText = null;
    public static String spnCipherText = null;
    public static File selectedFile = null;
    public static String oldExtension = null;

    public static int keyStat = 0;

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

    public void textInputPressed() {
        String textInputPre = textInput.getText();
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
                        sha256CipherText = SHA256.hashMac(textInput.getText(), keyTextArea.getText());
                        plainTextArea.setText(textInput.getText());
                        cipherTextArea.setText(sha256CipherText);
                        clrChoices();
                    }
                } else if(spnCheckBox.isSelected()) {
                    spnCipherText = AESencrypt(textInput.getText(), keyTextArea.getText());
                    plainTextArea.setText(AESDecrypt(spnCipherText, keyTextArea.getText()));
                    cipherTextArea.setText(spnCipherText);
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

    public void sendButtonAction() {
        System.out.println("Mesaj Gönderildi!");
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
                FileEncryption.encryptFile(selectedFile.getAbsolutePath(), key);
                //FileEncryption.decryptFile(selectedFile.getAbsolutePath(), key, findExtension(selectedFile.getName()));
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
}