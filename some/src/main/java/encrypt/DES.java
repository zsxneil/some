package encrypt;

import com.sun.crypto.provider.AESKeyGenerator;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by neil on 2017/11/15.
 * * DES 加解密工具类
 *
 * 支持 DES、DESede(TripleDES,就是3DES)、AES、Blowfish、RC2、RC4(ARCFOUR)
 * DES                  key size must be equal to 56
 * DESede(TripleDES)    key size must be equal to 112 or 168
 * AES                  key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
 * Blowfish             key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
 * RC2                  key size must be between 40 and 1024 bits
 * RC4(ARCFOUR)         key size must be between 40 and 1024 bits
 */
public class DES {
    /**
     * 定义加密方式
     */
    private final static String KEY_DES = "DES"; // 定义 加密算法,可用 DES,DESede,Blowfish
    private final static String KEY_AES = "AES";    // 测试

    /**
     * 全局数组
     */
    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    /**
     * 初始化密钥
     * @return
     */
    public static String init(){
        return init(null);
    }

    /**
     * 初始化密钥
     * @param seed 初始化参数
     * @return
     */
    public static String init(String seed) {
        SecureRandom secureRandom = null;
        String str = "";
        try {
            if (null != seed) {
                // 带参数的初始化
                    secureRandom = new SecureRandom(decryptBase64(seed));
            } else {
                //不带参数的初始化
                secureRandom = new SecureRandom();
            }

            KeyGenerator generator = KeyGenerator.getInstance(KEY_DES);
            generator.init(secureRandom);

            SecretKey secretKey = generator.generateKey();
            str = encryptBase64(secretKey.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 转换密钥
     * @param key 密钥的字节数组
     * @return
     */
    public static Key byteToKey(byte[] key) {
        SecretKey secretKey = null;
        try {
            DESKeySpec des = new DESKeySpec(key);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_DES);
            secretKey = secretKeyFactory.generateSecret(des);

            // 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
//            secretKey = new SecretKeySpec(key, KEY_DES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    /**
     * DES 解密
     * @param data 需要解密的字符串
     * @param key 密钥
     * @return
     */
    public static String decryptDES(String data,String key) {
        // 验证传入的字符串
        if (StringUtils.isBlank(data))
            return "";
        // 调用解密方法完成解密
        byte[] bytes = decryptDES(hexString2Bytes(data), key);
        // 将得到的字节数组变成字符串返回
        return new String(bytes);
    }

    /**
     * DES 解密
     * @param data 需要解密的字节数组
     * @param key 密钥
     * @return
     */
    public static byte[] decryptDES(byte[] data,String key) {
        byte[] bytes = null;
        try {
            Key k = byteToKey(decryptBase64(key));
            Cipher cipher = Cipher.getInstance(KEY_DES);
            cipher.init(Cipher.DECRYPT_MODE,k);
            bytes = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bytes;
    }

    /**
     * DES 加密
     * @param data 需要加密的字符串
     * @param key 密钥
     * @return
     */
    public static String encryptDES(String data,String key) {
        // 验证传入的字符串
        if (StringUtils.isBlank(data))
            return "";
        // 调用加密方法完成加密
        byte[] bytes = encryptDES(data.getBytes(), key);
        // 将得到的字节数组变成字符串返回
        return byteArrayToHexString(bytes);
    }

    /**
     * DES 加密
     * @param data 需要加密的字节数组
     * @param key 密钥
     * @return
     */
    public static byte[] encryptDES(byte[] data,String key) {
        byte[] bytes = null;
        try {
            Key k = byteToKey(decryptBase64(key));
            Cipher cipher = Cipher.getInstance(KEY_DES);
            cipher.init(Cipher.ENCRYPT_MODE,k);
            bytes = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bytes;
    }

    /**
     * BASE64 解密
     * @param key 需要解密的字符串
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] decryptBase64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64 加密
     * @param key 需要加密的字节数组
     * @return 字符串
     * @throws Exception
     */
    public static String encryptBase64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    /**
     * 将一个字节转化成十六进制形式的字符串
     * @param b 字节数组
     * @return 字符串
     */
    private static String byteToHexString(byte b) {
        int ret = b;
        //System.out.println("ret = " + ret);
        if (ret < 0) {
            ret += 256;
        }
        int m = ret / 16;
        int n = ret % 16;
        return hexDigits[m] + hexDigits[n];
    }

    /**
     * 转换字节数组为十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(byteToHexString(bytes[i]));
        }
        return sb.toString();
    }


    /**
     * 转换十六进制字符串为字节数组
     * @param hexstr 十六进制字符串
     * @return
     */
    public static byte[] hexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    /**
     * 转换字符类型数据为整型数据
     * @param c 字符
     * @return
     */
    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    public static void main(String[] args) {
        String key = DES.init();
        System.out.println("DES密钥:\n" + key);

        String word = "123ffdfgdfsgfdhhhhhhhhhhhhhhhhhhhhhh";


        String encWord = encryptDES(word, key);
        System.out.println("*************************");
        System.out.println(word + "\n加密后：\n" + encWord);
        System.out.println(word + "\n解密后：\n" + decryptDES(encWord, key));
    }

}
