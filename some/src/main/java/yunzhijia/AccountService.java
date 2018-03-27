package yunzhijia;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AccountService {

    public static final String CLIENT_ID = "200198";
    public static final String CLIENT_SECRET = "3682ad9fa1fb59419a229790cf415631";

    private static OkHttpClient client = null;

    @BeforeClass
    public static void init(){
        client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Test
    public void accountExist() throws IOException {
        String url = "https://api.kingdee.com/passport/account/exist";
        String loginname = "18617096973";
        url = url + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&loginname=" + loginname;
        Request request = new Request
                .Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            System.out.println(response.body().string());
        } else {
            System.out.println(response.code());
        }
    }

    @Test
    public void accountSearch() throws IOException {
        String url = "https://api.kingdee.com/passport/org/member/search?client_secret=%s&sclient_id=%s&keyword=%s&tid=%s";
        String accountName = "17328752127";
        String tid = "533077";
        url = String.format(url, CLIENT_SECRET ,CLIENT_ID ,accountName, tid);
        System.out.println(url);
        Request request = new Request
                .Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            System.out.println(response.body().string());
        } else {
            System.out.println(response.code());
        }
    }

    @Test
    public void search() throws IOException {
        String url = "https://api.kingdee.com/kdproduct/member/search?client_secret=%s&client_id=%s&keyword=%s&tid=%s";
        String accountName = "17328752127";
        String tid = "5";
        url = String.format(url, CLIENT_SECRET ,CLIENT_ID ,accountName, tid);
        System.out.println(url);
        Request request = new Request
                .Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            System.out.println(response.body().string());
        } else {
            System.out.println(response.code());
            System.out.println(response.body().string());
        }
    }

}
