package wolve.dms.libraries;

import com.scottyab.aescrypt.AESCrypt;

import org.apache.commons.codec.binary.Base64;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by tranhuy on 9/26/16.
 */
public class Security {

    public static String encrypt(String input) {
        String password = "dmslub";
        try {
            return AESCrypt.encrypt(password, input);
        }catch (GeneralSecurityException e){
            return "";
        }
    }

    public static String decrypt(String input) {
        String password = "dmslub";
        try {
            return AESCrypt.decrypt(password, input);

        }catch (GeneralSecurityException e){
            return input;
        }catch (Exception e) {
            return input;
        }
    }

//    public static String encrypt(String input) {
//        String key = "123";
//        byte[] crypted = null;
//        try {
//            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, skey);
//            crypted = cipher.doFinal(input.getBytes());
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//        return new String(Base64.encodeBase64(crypted));
//    }
//
//    public static String decrypt(String input) {
//        String key = "123";
//        byte[] outputString = null;
//        try {
//            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, skey);
//            outputString = cipher.doFinal(Base64.decodeBase64(input.getBytes()));
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//        if (outputString != null) {
//            return new String(outputString);
//        }
//        return "";
//    }



}
