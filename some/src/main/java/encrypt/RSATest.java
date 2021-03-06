package encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2017/11/20.
 */
public class RSATest {
    public static void main(String[] args) throws Exception {
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDFVrNjYkF1M9OeRvySiVKFS3453kzYcUXvN5qTyZmD4fppxGihIDnftfBCCL/6cFOoI7aFtzOP+lpuBqrd4AfpFTdSsCyI6ffOPWbriDhPRYbq4XeVw6YKjijDfh/W/2r+Cr84N+cb161Lur2N0rldqUn8sJSN6/KQdRqMN3AO0qX1KghhTxeYTE17CAm7JMRY83KRFHd2uc8M2urkYxhvFVNT6T2o0ALhthL3FotuyJWGwUsLZLNdzCoAzgT7kZSX5mLM2vlg2VFIhoNin7B58ABc7rmfkXlonToAyCA62Wru80l3eKSgzF45vqYpcDqlLRtV/9EyFDiPe0ljdLOLAgMBAAECggEBAJRWAsxO/GNMTOMp+wtve/9EayK2rk74fpg91M0IeoP/JsvAXuHXJom56lrI1v461zeJeNb0iibNCpKwIlpb37ZeQ1K9qQxVyvdVgnrDz4gO/EasqsaB5dsDVzL0KJzlLBjAHVL1+pwcOGC3g2sLHlmAOmRem5w+4vuhew4xUeNhNHlU3+neotAiYmK3zwgq0sn4wnmS/fOILjDc9B8uISJ/s1zCc1bTw1r1tiYffctHESbPLAFnyZ/fxhFNe80iZBw+KmzKt0ajPLJPd2uOZGQWRG7gdUZpt+wkfne8VK4afcYBbP/HAuX1qR7Y7w5FWPzy3iPlcU2Ye6btb7L5hfkCgYEA5GZTIKwazn8XUF3thRuSI90JJj46QldUAGeupzpJFKkOKjEP5kr/4wf9jVdlX9Py4X1OnEqquM1f1fXL9RV4Fjo8OeKx74DOZS2zUjdTbNSiCks2zrTpf4aJrjOXC9YjJp9pdExIAauI+mlxQtF3iYkTKLuFLtQBDtrL2ZFJCvcCgYEA3S98O8zt1j/pBFd0hkFT+MPRVNQoKgUeKG/e4xXCG4IioXCvQjr+yKHYvDBeAnWYwAlXpUwXBLxTv6oFZdTt1J2fe4fJuOO5cW5ZWqH2k+uXHb/rWk/C3ePHVwtiuc3WmHOQvSt1zL0lwNctV1VPBtoi8UjjsV1aiB2OsJWDww0CgYBhskdChXud8o0FangL5MQ5JPYh7qI6+GHNHox9qZfTB5EwiPX1GwWjliplyAlr2RxeSZxTjDjEfczNl6sbdYh9cYGLfSWBfgZLDyZSN6outjFt18hk2Mu8Kc9AvX6RqJI2lh+kiHCQjlA0VOAGpdTM9t5EIRoR8stVEE/8C4JD9wKBgDWz2c4LK5PhYIoVUc1m/yRfJuwZtsa9B116C/vGxKI5Ln9UBLln6YR9B0pp0+l08+lsW0Ne/hfAQfU/KqBW2ofM3XP4vBmP8O6BhGBlAqFXihFHD7WVuBJ3w2Z29Tl3qKrLJFFJxAg5xHjVxcR2igF35RQARohBykFSK9rj8mHhAoGADrp0PdObINpezbRiB1Y/cMdAB5Ox4uZpmOA/tTzMbIRR7g8NzQ9yJjpuTOq/kEGXkkMcPfhZhtIeWP38q2hhV5vJAE9WPLWGInq3564XXW7rDke4/K6c75biwC+PHYGZiyx5K4kDmIxRLULrRalsSlXbiDJyOGmOuxx3nn1s4Lg=";
        String filePath = "F:/test/rsa/";
        privateKey = RSA.loadPrivateKeyByFile(filePath);
        System.out.println("私钥： \n\r" + privateKey);

        System.out.println("私钥加密--------公钥解密");
        String english = "{\"externalUserId\": \"KH20100819-00435\"}";
        byte[] encEnglish = RSA.encryptByPrivateKey(english.getBytes("utf-8"), privateKey);

        System.out.println("加密前: " + english + "\n\r" + "加密后: " + Hex.encodeHexString(RSA.encryptBase64(encEnglish).getBytes("utf-8")));
        System.out.println("********************************");
        String str = "RFRlcxj0m7TrtxQ+LtvC5m+zNxPCa+wmAgM05hGDiuRlwV4DC3Vei3KZvU21TMJq0Fa8efQL4qSpyNKSwvXetFtXQlNJSpvKz88N3jfaTZ6rHFO//WD0I0mpf8qjk6Oci3UvhqGIlbANM+p6/yOjTUSEz2pZsRqgMzRDy3p74pM=";
        System.out.println(Hex.encodeHexString(str.getBytes("utf-8")));

    }


}
