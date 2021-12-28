import me.t3sl4.textfileencoder.Utils.FileEncryption;
import me.t3sl4.textfileencoder.Utils.FileZIP;
import me.t3sl4.textfileencoder.Utils.SHA256;
import me.t3sl4.textfileencoder.Utils.SPN;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SignatureException;

public class UnitTest {

    @Test
    public void testSHA256Encryption() throws SignatureException {
        String definedPlainText = "ahmetmelihomerabdullah";
        String encryptedText = SHA256.hashMac(definedPlainText, "tfencoder");
        String expected = "1cc7b6e4b757f541ce1912293d1e92f7cfe0db25bf4fc40cbbb11208951fc3e5";
        assertEquals(encryptedText, expected);
    }

    @Test
    public void testSPNEncryption() {
        String definedPlainText = "ahmetmelihomerabdullah";
        String encryptedText = SPN.encryptWithSpn(definedPlainText, "tfencoder");
        String expected = "00000111000011100000101100000011000100100000101100000011000010100000111100001110000010010000101100000011000101000000011100000100000000100001001100001010000010100000011100001110";
        assertEquals(encryptedText, expected);
    }

    @Test
    public void testSPNDecryption() {
        String definedCipherText = "00000111000011100000101100000011000100100000101100000011000010100000111100001110000010010000101100000011000101000000011100000100000000100001001100001010000010100000011100001110";
        String plainText = SPN.decryptWithSpn(definedCipherText, "tfencoder");
        String expected = "ahmetmelihomerabdullah";
        assertEquals(plainText, expected);
    }

    @Test
    public void testFileCompress() throws IOException {
        boolean zipStatus = FileZIP.testCompressFile(System.getProperty("user.home") + "/Desktop/" + "TextFileEncoder\\src\\testCompressFile.txt");
        assertEquals(zipStatus, true);
    }

    @Test
    public void testFileUnzip() throws IOException {
        boolean unzipStatus = FileZIP.testUnzipFile(Path.of(System.getProperty("user.home") + "/Desktop/" + "TextFileEncoder\\src\\testUnzipFile.zip"), Path.of(System.getProperty("user.home") + "/Desktop/" + "TextFileEncoder\\src"));
        assertEquals(unzipStatus, true);
    }

    @Test
    public void testFileEncryption() throws GeneralSecurityException, IOException {
        boolean fileEncryptStatus = FileEncryption.testEncryptFile(System.getProperty("user.home") + "/Desktop/" + "TextFileEncoder\\src\\testEncrypFile.txt", "tfencoder");
        assertEquals(fileEncryptStatus, true);
    }
}
