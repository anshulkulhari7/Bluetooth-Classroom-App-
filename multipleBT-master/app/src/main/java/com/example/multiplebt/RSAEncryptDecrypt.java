package com.example.multiplebt;

import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSAEncryptDecrypt {

    public static final int KEY_LENGTH = 2048;
    public static final String RSA = "RSA";

    public static KeyPair generateRSAKey()
    {
        KeyPairGenerator kpg = null;
        try
        {
            //get an RSA key generator
            kpg = KeyPairGenerator.getInstance(RSA);
        }
        catch (NoSuchAlgorithmException e)
        {
            Log.e(RSAEncryptDecrypt.class.getName(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
        //initialize the key to 2048 bits
        kpg.initialize(KEY_LENGTH);
        //return the generated key pair
        return kpg.genKeyPair();
    }

    public static byte[] encryptRSA(byte[] plain, PublicKey publicKey)
    {
        byte[] enc = null;
        try
        {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            enc = cipher.doFinal(plain);
        }
        //no need to catch 4 different exceptions
        catch (Exception e)
        {
            Log.e(RSAEncryptDecrypt.class.getName(), e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return enc;
    }


    public static byte[] decryptRSA(byte[] enc, PrivateKey privateKey)
    {
        byte[] plain = null;
        try
        {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            plain = cipher.doFinal(enc);
        }
        //no need to catch 4 different exceptions
        catch (Exception e)
        {
            Log.e(RSAEncryptDecrypt.class.getName(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return plain;
    }

}
