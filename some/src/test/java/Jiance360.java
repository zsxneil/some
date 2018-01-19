import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Jiance360 {
    public static void main(String[] args) throws UnsupportedEncodingException {


        String url = "https://jiance.360.net/event/index?act=visual&callback=jindie";
        String apitoken = "188bcfea08c0bd4778f9c2d75e6d8641";
        String username = "jindie@360.cn";

        long timestamp = System.currentTimeMillis() / 1000 - 3600 * 48 * 5;
        String sign = String.format("secret=%s&timestamp=%s&username=%s",apitoken,timestamp,username);
        System.out.println(sign);

        url = String.format("%s&apitoken=%s&username=%s&timestamp=%s&sign=%s", url, apitoken, username, timestamp + "", DigestUtils.md5Hex(sign));
        System.out.println(url);

    }
}
