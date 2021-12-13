package me.t3sl4.textfileencoderdemo.tfencoderdemo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FileEncryption {
    private static final byte[] salt = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };

    private static Cipher makeCipher(String pass, Boolean decryptMode) throws GeneralSecurityException{
        PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");

        if(decryptMode){
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
        }

        return cipher;
    }

    public static void encryptFile(String fileName, String pass)
            throws IOException, GeneralSecurityException{
        byte[] decData;
        byte[] encData;
        File inFile = new File(fileName);

        Cipher cipher = FileEncryption.makeCipher(pass, true);

        FileInputStream inStream = new FileInputStream(inFile);

        int blockSize = 8;

        int paddedCount = blockSize - ((int)inFile.length()  % blockSize );

        int padded = (int)inFile.length() + paddedCount;

        decData = new byte[padded];


        inStream.read(decData);

        inStream.close();

        for( int i = (int)inFile.length(); i < padded; ++i ) {
            decData[i] = (byte)paddedCount;
        }

        encData = cipher.doFinal(decData);


        FileOutputStream outStream = new FileOutputStream(new File(fileName + ".encrypted"));
        outStream.write(encData);
        outStream.close();
    }

    public static void decryptFile(String fileName, String pass, String fileFormat)
            throws GeneralSecurityException, IOException{
        byte[] encData;
        byte[] decData;
        File inFile = new File(fileName);

        Cipher cipher = FileEncryption.makeCipher(pass, false);

        FileInputStream inStream = new FileInputStream(inFile );
        encData = new byte[(int)inFile.length()];
        inStream.read(encData);
        inStream.close();
        decData = cipher.doFinal(encData);

        int padCount = (int)decData[decData.length - 1];

        if( padCount >= 1 && padCount <= 8 ) {
            decData = Arrays.copyOfRange( decData , 0, decData.length - padCount);
        }

        fileName = fileName.replace(".encrypted", fileFormat);
        FileOutputStream target = new FileOutputStream(new File(fileName));
        target.write(decData);
        target.close();
    }
}
