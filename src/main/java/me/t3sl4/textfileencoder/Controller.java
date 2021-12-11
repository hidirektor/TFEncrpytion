package me.t3sl4.textfileencoder;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import me.t3sl4.textfileencoder.utils.AES;
import me.t3sl4.textfileencoder.utils.SHA256;

import org.apache.commons.codec.binary.Base64;

public class Controller {
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

    Alert alert = new Alert(Alert.AlertType.ERROR);

    public String sha256CipherText;
    public String spnCipherText;

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

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
            if(keyTextArea.getText() != null) {
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
                alert.setContentText("Lütfen şifreleme anahtarı gir. Aksi takdirde düzgün bir şifreleme elde edemezsin.");
                alert.showAndWait();
            }
        }
    }
    public void sendButtonAction() {
        System.out.println("Mesaj Gönderildi!");
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
}