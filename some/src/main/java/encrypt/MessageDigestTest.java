package encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestTest {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        long current = System.currentTimeMillis();
        String filePath = "F:/14-win7环境-26-vss-20180103135712-0.zip";
        String sha1 = getMD5(new File(filePath));
        System.out.println(sha1);
        System.out.println(System.currentTimeMillis() - current);
    }


    /**
     * 计算大文件 md5获取getMD5(); SHA1获取getSha1() CRC32获取 getCRC32()
     */
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static MessageDigest messagedigest = null;

    /***
     * 计算SHA1码
     *
     * @return String 适用于上G大的文件,大文件会出问题
     * @throws NoSuchAlgorithmException
     * */
    public static String getSha1(File file) throws OutOfMemoryError,
            IOException, NoSuchAlgorithmException {
        messagedigest = MessageDigest.getInstance("SHA-1");

        FileInputStream in = new FileInputStream(file);

        byte[] buffer = new byte[1024 * 1024 * 10];
        int len = 0;

        while ((len = in.read(buffer)) >0) {
            //该对象通过使用 update（）方法处理数据
            messagedigest.update(buffer, 0, len);
        }

        return bufferToHex(messagedigest.digest());
    }

    /**
     * 对一个文件获取md5值
     *
     * @return md5串
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(File file) throws IOException,
            NoSuchAlgorithmException {

        messagedigest = MessageDigest.getInstance("MD5");
        FileInputStream in = new FileInputStream(file);

        byte[] buffer = new byte[1024 * 1024 * 10];
        int len = 0;

        while ((len = in.read(buffer)) >0) {
            //该对象通过使用 update（）方法处理数据
            messagedigest.update(buffer, 0, len);
        }
        return bufferToHex(messagedigest.digest());
    }


    /**
     * @Description 计算二进制数据
     * @return String
     * */
    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
