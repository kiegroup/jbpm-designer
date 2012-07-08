package org.jbpm.designer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

public class Base64EncodingUtil {

    public static String encode(String input) throws IOException {
        if (input == null || input.length() == 0) {
            throw new IOException("Can not base 64 encode null or empty string!");
        }

        return encode(input.getBytes());
    }
    
    public static String encode(byte [] input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream base64OutputStream = MimeUtility.encode(baos, "base64");
            base64OutputStream.write(input);
            base64OutputStream.close();
        } catch (MessagingException me) {
            throw new IOException("Unable to encode string in base 64: " + me.getMessage(), me);
        }

        return new String(baos.toByteArray());
    }

    public static String decode(String input) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        byte[] decodedBytes;
        try {
            InputStream base64InputStream = MimeUtility.decode(bais, "base64");
            byte[] tmp = new byte[input.length()];
            int n = base64InputStream.read(tmp);
            decodedBytes = new byte[n];
            System.arraycopy(tmp, 0, decodedBytes, 0, n);
        } catch (MessagingException me) {
            throw new IOException("Unable to decode string from base 64: " + me.getMessage(), me);
        }
        
        return new String(decodedBytes);
    }

}
