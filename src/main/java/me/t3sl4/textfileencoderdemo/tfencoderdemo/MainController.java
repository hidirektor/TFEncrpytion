package me.t3sl4.textfileencoderdemo.tfencoderdemo;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javafx.stage.FileChooser;
import me.t3sl4.textfileencoderdemo.tfencoderdemo.utils.AES;
import me.t3sl4.textfileencoderdemo.tfencoderdemo.utils.FileEncryption;
import me.t3sl4.textfileencoderdemo.tfencoderdemo.utils.FileZIP;
import me.t3sl4.textfileencoderdemo.tfencoderdemo.utils.SHA256;

public class MainController {
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

    @FXML
    private TextField selectedDecFilePath;

    @FXML
    private CheckBox sha256CheckBoxDecode;

    @FXML
    private CheckBox spnCheckBoxDecode;

    @FXML
    private TextArea presetKey;

    @FXML
    private TextArea encodedText;

    @FXML
    private TextArea decodedText;

    @FXML
    private TextField fileExtension;

    Alert alert = new Alert(Alert.AlertType.ERROR);
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static String key = null;
    public static String decodingKey = null;
    public static String sha256CipherText = null;
    public static String spnCipherText = null;
    public static String sha256DecodedText = null;
    public static String spnDecodedText = null;
    public static File selectedFile = null;
    public static File selectedDecFile = null;
    public static File finalSelectedFile = null;
    public static String finalPath = null;
    public static String newPath = null;

    public static int keyStat = 0;

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
                        sha256CipherText = SHA256.hashMac(textInput.getText(), key);
                        plainTextArea.setText(textInput.getText());
                        cipherTextArea.setText(sha256CipherText);
                        PrintWriter writer = new PrintWriter("sha256plain.txt", "UTF-8");
                        writer.println(textInput.getText());
                        writer.close();
                        clrChoices();
                    }
                } else if(spnCheckBox.isSelected()) {
                    spnCipherText = AESencrypt(textInput.getText(), key);
                    plainTextArea.setText(AESDecrypt(spnCipherText, key));
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

    public void decodeText() throws Exception {
        if(!sha256CheckBoxDecode.isSelected() && !spnCheckBoxDecode.isSelected()) {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Programın temel mantığı zaten şifrelemek o yüzden lütfen yukarıdan bir tane şifreleme algoritması seç.");
            alert.showAndWait();
        } else {
            if(presetKey.getText() != null) {
                decodingKey = presetKey.getText();
                if(sha256CheckBoxDecode.isSelected()) {
                    if(encodedText.getText() != null) {
                        try {
                            File myObj = new File("sha256plain.txt");
                            Scanner myReader = new Scanner(myObj);
                            while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                sha256DecodedText = data;
                                decodedText.setText(sha256DecodedText);
                            }
                            myReader.close();
                        } catch (FileNotFoundException e) {
                            System.out.println("Dosya Bulunamadı.");
                            e.printStackTrace();
                        }
                        clrChoices();
                    } else {
                        alert.setTitle("HATA!");
                        alert.setHeaderText("Şifreleme Algoritması Hatası.");
                        alert.setContentText("Lütfen şifresi çözülecek metni gir. Aksi takdirde hiçliğin şifresini çözemezsin.");
                        alert.showAndWait();
                        clrChoices();
                    }
                } else if(spnCheckBoxDecode.isSelected()) {
                    if(encodedText.getText() != null) {
                        spnDecodedText = AESDecrypt(encodedText.getText(), presetKey.getText());
                        decodedText.setText(spnDecodedText);
                        clrChoices();
                    } else {
                        alert.setTitle("HATA!");
                        alert.setHeaderText("Şifreleme Algoritması Hatası.");
                        alert.setContentText("Lütfen şifresi çözülecek metni gir. Aksi takdirde hiçliğin şifresini çözemezsin.");
                        alert.showAndWait();
                        clrChoices();
                    }
                }
            } else {
                alert.setTitle("HATA!");
                alert.setHeaderText("Şifreleme Algoritması Hatası.");
                alert.setContentText("Lütfen şifreleme anahtarı belirle. Aksi takdirde düzgün bir şifreleme elde edemezsin.");
                alert.showAndWait();
            }
        }
    }

    public void sha256CheckBoxChangeDecode() {
        if(!spnCheckBoxDecode.isSelected()) {
            if(sha256CheckBoxDecode.isSelected()) {
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

    public void spnCheckBoxChangeDecode() {
        if(!sha256CheckBoxDecode.isSelected()) {
            if(spnCheckBoxDecode.isSelected()) {
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

    public void fileEncodingButton() throws IOException {
        FileChooser ds = new FileChooser();
        ds.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sadece .txt, .dat, .gif", "*.txt", "*.dat", "*.gif"));
        File d = ds.showOpenDialog(null);
        if(d != null) {
            selectedFile = d;
            selectedFilePath.setText(selectedFile.getAbsolutePath());
        }
    }

    public void fileDecodingButton() throws IOException {
        FileChooser ds = new FileChooser();
        ds.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sadece .encrypted", "*.encrypted", "*.zip"));
        File d = ds.showOpenDialog(null);
        if(d != null) {
            selectedDecFile = d;
            selectedDecFilePath.setText(selectedDecFile.getAbsolutePath());
        }
    }

    public void encodeSelectedFile() {
        if(selectedFile != null && key != null) {
            try {
                fileExtension.setText(findExtension(selectedFile.getAbsolutePath()));
                FileEncryption.encryptFile(selectedFile.getAbsolutePath(), key);
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

    public void decodeSelectedFile() {
        if(selectedDecFile != null && key != null && fileExtension.getText() != null) {
            try {
                if(selectedDecFile.getName().contains("encrypted")) {
                    Path source = Paths.get(selectedDecFile.getAbsolutePath());
                    Path target = Paths.get(System.getProperty("user.home") + "/Desktop");
                    FileZIP.unzipFolder(source, target);
                    File delete = new File(selectedDecFile.getAbsolutePath());
                    delete.delete();
                    FileEncryption.decryptFile(newPath, key, fileExtension.getText());
                    selectedDecFilePath.setText(null);
                } else {
                    selectedDecFilePath.setText(null);
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

    public void encodeClearAct() {
        plainTextArea.setText(null);
        cipherTextArea.setText(null);
    }

    public void decodeClearAct() {
        decodedText.setText(null);
    }

    private void clrChoices() {
        sha256CheckBox.setSelected(false);
        spnCheckBox.setSelected(false);
        sha256CheckBoxDecode.setSelected(false);
        spnCheckBoxDecode.setSelected(false);
        textInput.setText(null);
        keyTextArea.setText(null);
        encodedText.setText(null);
        presetKey.setText(null);
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
}