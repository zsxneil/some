package encrypt;

import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

/**
 * Created by Administrator on 2017/11/16.
 * PBE 加解密工具类
 */
public class PBE {
    /**
     * 定义加密方式
     * 支持以下任意一种算法
     * <p/>
     * <pre>
     * PBEWithMD5AndDES
     * PBEWithMD5AndTripleDES
     * PBEWithSHA1AndDESede
     * PBEWithSHA1AndRC2_40
     * </pre>
     */
    private final static String KEY_PBE = "PBEWITHMD5andDES";
    private final static int SALT_COUNT = 100;


    /**
     * 初始化盐（salt）
     *
     * @return
     */
    public static byte[] init() {
        Random random = new Random();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 转换密钥
     *
     * @param key 字符串
     * @return
     */
    public static Key stringToKey(String key) {
        SecretKey secretKey = null;
        PBEKeySpec pbe = new PBEKeySpec(key.toCharArray());
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_PBE);
            secretKey = keyFactory.generateSecret(pbe);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    /**
     * PBE 加密
     *
     * @param data 需要加密的字节数组
     * @param key  密钥
     * @param salt 盐
     * @return
     */
    public static byte[] encryptPBE(byte[] data,String key,byte[] salt) {
        byte[] bytes = null;
        // 获取密钥
        try {
            Key k = stringToKey(key);
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt,SALT_COUNT);
            Cipher cipher = Cipher.getInstance(KEY_PBE);
            cipher.init(Cipher.ENCRYPT_MODE,k,pbeParameterSpec);
            bytes = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * PBE 解密
     *
     * @param data 需要解密的字节数组
     * @param key  密钥
     * @param salt 盐
     * @return
     */
    public static byte[] decryptPBE(byte[] data,String key,byte[] salt) {
        byte[] bytes = null;
        try {
            Key k = stringToKey(key);
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt,SALT_COUNT);
            Cipher cipher = Cipher.getInstance(KEY_PBE);
            cipher.init(Cipher.DECRYPT_MODE,k,pbeParameterSpec);
            bytes = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * BASE64 加密
     *
     * @param key 需要加密的字节数组
     * @return 字符串
     * @throws Exception
     */
    public static String encryptBase64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    /**
     * 测试方法
     *
     * @param args
     */
    public static void main(String[] args) {
        // 加密前的原文
        String str = "hello world !!!";
        // 口令
        String key = "qwert";
        // 初始化盐
        byte[] salt = init();
        // 采用PBE算法加密
        byte[] encData = encryptPBE(str.getBytes(), key, salt);
        // 采用PBE算法解密
        byte[] decData = decryptPBE(encData, key, salt);
        String encStr = null;
        String decStr = null;
        try {
            encStr = encryptBase64(encData);
            decStr = new String(decData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("加密前：" + str);
        System.out.println("加密后：" + encStr);
        System.out.println("解密后：" + decStr);
    }

}
