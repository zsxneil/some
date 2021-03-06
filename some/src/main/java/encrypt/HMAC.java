package encrypt;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by Administrator on 2017/11/15.
 */
public class HMAC {

    /**
     * 定义加密方式
     * MAC算法可选以下多种算法
     * <pre>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    private final static String KEY_MAC = "HmacSHA256";

    /**
     * 全局数组
     */
    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    /**
     * 构造函数
     */
    public HMAC() {

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
     * BASE64 解密
     * @param key 需要解密的字符串
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] decryptBase64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * 初始化HMAC密钥
     * @return
     */
    public static String init() {
        SecretKey secretKey;
        String str = "";
        try {
            KeyGenerator generator = KeyGenerator.getInstance(KEY_MAC);
            secretKey = generator.generateKey();
            //System.out.println(secretKey.getEncoded());
            str = encryptBase64(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * HMAC加密
     * @param data 需要加密的字节数组
     * @param key 密钥
     * @return 字节数组
     */
    public static byte[] encryptHMAC(byte[] data,String key) {
        SecretKey secretKey;
        byte[] bytes = null;
        try {
            secretKey = new SecretKeySpec(decryptBase64(key),KEY_MAC);
            Mac mac = Mac.getInstance(KEY_MAC);
            mac.init(secretKey);
            bytes = mac.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * HMAC加密
     * @param data 需要加密的字符串
     * @param key 密钥
     * @return 字符串
     */
    public static String encryptHMAC(String data,String key) {
        if (StringUtils.isBlank(data))
            return null;
        byte[] bytes = encryptHMAC(data.getBytes(),key);
        return byteArrayToHexString(bytes);
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

    public static void main(String[] args) throws Exception {

        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            System.out.println(provider.getInfo());
        }

        String key = HMAC.init();
        //key = encryptBase64("".getBytes());
        key = "123456";
        byte[] result = decryptBase64(key);
        System.out.println(new String(result));

        System.out.println("Mac密钥:\n" + key);
        String word = "123";
        word = "{\"api_id\":101,\"api_key\":\"f7aff24c16\",\"api_secret\":\"KJUIHnjHUHlkhUHkjlplijjjKImhUIHlHUhugyGyftrDeseswS678m==\",\"p1\":\"123\",\"p2\":\"abc\",\"timestrap\":1503643692}";
        System.out.println(encryptHMAC(word, key));

        System.out.println("****************************************");
        String secret = "c94cd396702";
        String message = "{\"action\":\"order_create\",\"api_id\":801,\"api_key\":\"c94cd396702\",\"api_secret\":\"Yzk1MGZhN2E1OWQ5ZmNmYTc0MjI5OTQ3NmY5OGNkMWI2Yjk5NjM5Mw==\",\"enddate\":\"2017-12-31\",\"partner_customer_id\":\"kingdee_cus_1\",\"partner_order_id\":\"kingdee_order_1\",\"purchase_cycle\":1,\"sku_id\":\"kingdee000001\",\"timestrap\":1511769027}";
        message = "hello";
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        System.out.println(byteArrayToHexString(sha256_HMAC.doFinal(message.getBytes())));

        String hash = HmacUtils.hmacSha256Hex(secret,message);

        System.out.println(hash);
    }
}
