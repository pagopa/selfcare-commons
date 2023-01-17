package it.pagopa.selfcare.commons.utils.crypto.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CryptoUtils {
    private CryptoUtils(){}

    public static byte[] getDigest(InputStream is) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(is.readAllBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Something gone wrong selecting digest algorithm", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Something gone wrong while reading inputStream", e);
        }
    }
}
