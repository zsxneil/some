package com.my.rsa;

import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by Administrator on 2017/11/14.
 */
public class Test {

    public static String ALGORITHM = "PBEWITHMD5andDES";
    public static void main(String[] args) throws Exception {
       Test test = new Test();
//       PrivateKey key = test.getPrivateKey();
//        System.out.println(key.getAlgorithm());
//        System.out.println(key.getFormat());
        Key key = Test.toKey("ksm4eas");
        System.out.println(key.getAlgorithm());
        System.out.println(key.getFormat());
    }

    private PrivateKey getPrivateKey() throws Exception {
        PrivateKey privateKey = null;
        String priKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJVGIOQQpfL8oFN75SaQD596rXMZYiAHGSBvPtoEkZvn54woqiINGgQfOUad3AUqbiZBY1+24w581yiDNikYpIR8Luyp8E8MewY9mLExbMvcnXTOQxNajowMhcoqowbU+B6rXAQ18mNxhJT96dhJmSDesVM5oFIUh36us+M+jtjjAgMBAAECgYABtnxKIabF0wBD9Pf8KUsEmXPEDlaB55LyPFSMS+Ef2NlfUlgha+UQhwsxND6CEKqS5c0uG/se/2+4l0jXz+CTYBEh+USYB3gxcMKEo5XDFOGaM2Ncbc7FAKJIkYYN2DHmr4voSM5YkVibw5Lerw0kKdYyr0Xd0kmqTok3JLiLgQJBAOGZ1ao9oqWUzCKnpuTmXre8pZLmpWPhm6S1FU0vHjI0pZh/jusc8UXSRPnx1gLsgXq0ux30j968x/DmkESwxX8CQQCpY1+2p1aX2EzYO3UoTbBUTg7lCsopVNVf41xriek7XF1YyXOwEOSokp2SDQcRoKJ2PyPc2FJ/f54pigdsW0adAkAM8JTnydc9ZhZ7WmBhOrFuGnzoux/7ZaJWxSguoCg8OvbQk2hwJd3U4mWgbHWY/1XB4wHkivWBkhRpxd+6gOUjAkBH9qscS52zZzbGiwQsOk1Wk88qKdpXku4QDeUe3vmSuZwC85tNyu+KWrfM6/H74DYFbK/MzK7H8iz80uJye5jVAkAEqEB/LwlpXljFAxTID/SLZBb+bCIoV/kvg+2145F+CSSUjEWRhG/+OH0cQfqomfg36WrvHl0g/Xw06fg31HgK";
        priKey = "MIICxjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQI4jVrubSZmuoCAggA" +
                "MBQGCCqGSIb3DQMHBAje8WA1NOKonQSCAoBOBae56N1wc5SJfNdFdEypUEY1VnL9" +
                "QbbPzPsxMli4ab5WI63DuAGHLN8gi5zhCo+HngA4jAEuFy2RDzK2H9z1TKrdw9Gp" +
                "T81T9ZofPaivqg1AdEBvRNQR1Vs7rCzKLIVKCs8GyNfROFA6LU7orPHRa7tw51wK" +
                "IxTcwxYCwzTf/glRKgUrKS50IhH8mpLhCK60KtFPR/SLEgiWVFAA4Uy1uklxtA2T" +
                "WJsVRtujGl72wE04rwaK0XaNpIVVqxCBgAK+c+Dd/SUzEYon1j31RS97eNHgOuE1" +
                "qOa3pMdKPW7pxKI4sQC0nqX0aTsS12IZbTWYtH/REKIWMe8jVz1PIznTOUAVRlMc" +
                "MQKHITxKiqXpaZpPea97xWrU7ZasSTGTGSMGcUOEOpQH+qdjlbYtUA3CBEzcxsnM" +
                "iUwXumhvLhaPkHpmBZa1Z5OIjM8tHoferQVJlWR12LJkYxTxLc6i7zzcoGApnu8k" +
                "FgeF3v8HVQLX2kDoZD2IXgpcu5l8NnD+GvjH4tRZG5zvGgPLQCP0hrpHCRBnMBwi" +
                "zZDDY5B1BxbtqWAH6gboMLESmB8Usobqzd7s6biACu/vM4v1Gb7LVWA4/j/0k/rB" +
                "gdFKGlbv6JFAniTRLQHGAqODKK6P7uKLIwGR1A9POgRrwPc1EJRTE09hHBMs+kkR" +
                "QZc+mOqfIdq43Z9VcIvjwZ6PSUxEeDp/FaQx9q/JnnJXVZThD3Pw3L0fftum88KJ" +
                "VGGExmBzjettWDzhOEgDgrnuWfMeNsA55jRJbl5vAKC69hdTQtCsarSRyrmn/z/F" +
                "FyLvAuGfJqe64xb0CWnNENwtLYVGWuI44KxyOmHU5fkHqHAziMi+aXvG";
//        priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJF916ci+oquTKyy" +
//                "/6NhAzNIbqoMWEJajuITmuotqoLXZmil6sdAafzPIdxBTz5cBjqsiGhPyEk0l0Zu" +
//                "m1tuEfR7O+t0TGU1QrnGBWoMZU0pRuRbSX6/7sh/egsscRTvS6pyESeqDS0JhSpA" +
//                "7a+rDqnQn71rmVPr4kIqS0Yh881JAgMBAAECgYAM0yv8VrABI4Ysvwabz0Rp89uV" +
//                "b8sj5jmsIhBIzke2iuhDF0UP1VTCbTvN34/trkz6RRUtJMNB+LnRApT9RUGdymzk" +
//                "9G4bZL5lwRsTnIsODjf/WHyJM8Q3uB/lhf7KHm2m72GteMmP3BnpxjoCwA1jtBtv" +
//                "nyuGZHRNpq45QkWxxQJBAMEX/UPvkpdFheKWWfksCJNhlHNRNakXyxTquoOwuSLl" +
//                "ZIfhlPX0XIueRFPBuoAVSAw790QyhP1ZX9oae9dtauMCQQDA49YGClGJvzrpd84b" +
//                "4TCtM8+YvgbfB8H5+dD/D39qvgrX222MTUDMK4BPHUp4B4deEDXi14RxAfG8MELY" +
//                "usLjAkAyzDXTnn4JD8AmGPm4hG4JH72nDLE6N8k0SoAgzugVlxV4bc77WXrTPCKr" +
//                "iiafNXoLxebJ6SR1ZgLCZP85cE6TAkBjfiGLBA+CyYyNmCpL3x/Idw0BcCMDL3Ey" +
//                "i1Tt+wRx/oyBQsGCuOZPabDJHganWtrL/fiNhTTfRW128D3KpKqZAkEAv4rwinoT" +
//                "TnpTARMrnpnCS5hnlKqzFYUjJutWUsVrUi2sNJu9ILJd74sLjeBd0J/eVBtRDX4Q" +
//                "WTDaoGAwHecj6A==";
        PKCS8EncodedKeySpec priPKCS8;
        try {
            System.out.println(new BASE64Decoder().decodeBuffer(priKey));
            priPKCS8 = new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(priKey));


            PBEKeySpec pbeKeySpec = new PBEKeySpec("ksm4eas".toCharArray());

            KeyFactory keyf = KeyFactory.getInstance("RSA");
            privateKey = keyf.generatePrivate(priPKCS8);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 转换密钥<br>
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key toKey(String password) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        return secretKey;
    }

    /**
     * 加密
     *
     * @param data 数据
     * @param password 密码
     * @param salt  盐
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String password, byte[] salt)
            throws Exception {

        Key key = toKey(password);

        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param data  数据
     * @param password 密码
     * @param salt  盐
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String password, byte[] salt)
            throws Exception {

        Key key = toKey(password);

        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        return cipher.doFinal(data);
    }


}
