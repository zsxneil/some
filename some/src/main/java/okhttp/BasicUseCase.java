package okhttp;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import okhttp3.internal.http2.Header;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BasicUseCase {
    private static OkHttpClient client = null;

    @BeforeClass
    public static void init(){
        client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Test
    public void asyncGetTest(){
        String url = "https://baidu.com";
        Request request = new Request
                .Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println(response.body().string());
                    Headers headers = response.headers();
                    Set<String> headerNames = headers.names();
                    for (String headerName : headerNames) {
                        System.out.println(headerName + ":" + headers.get(headerName));
                    }
                } else {
                    System.out.println(response.code());
                }
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void syncGetTest() throws IOException {
        String url = "https://baidu.com";
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
    public void postFormTest(){
        String url = "http://localhost:8080/okhttp/form";
        RequestBody requestBody = new FormBody.Builder()
                .add("username", "test")
                .build();

        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //System.out.println(response.body().string());
                String body = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(body);
                System.out.println(jsonObject.getString("username"));
                response.close();
            }
        });
        try {
            Thread.sleep(500000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void postJsonTest() throws IOException {
        String url = "http://localhost:8080/okhttp/json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "test");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonObject.toJSONString());

        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        response.close();
    }

    @Test
    public void fileUploadTest() {
        String url = "http://localhost:8080/okhttp/fileUpload";
        File file = new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\qq.jpg");
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file","head_img.jpg", fileBody)
                .addFormDataPart("name", "fileupload")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println(response.body().string());
                } else {
                    System.out.println(response.code());
                }
                response.close();
            }
        });
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fileDownload() {
        String url = "http://localhost:8080/okhttp/download";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    downlodefile(response, "F:/download.jpg");
                } else {
                    System.out.println(response.code());
                }
            }
        });
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void downlodefile(Response response, String filePath) {
        InputStream is = null;
        byte[] bytes = new byte[4096];
        int len = 0;
        FileOutputStream fos = null;
        try {
           is = response.body().byteStream();
           //long total = response.body().contentLength();
           File file = new File(filePath);
           fos = new FileOutputStream(file);
           long sum = 0;
           while ((len = is.read(bytes)) != -1) {
               fos.write(bytes, 0 , len);
               //进度条
//                sum += len;
//                int progress = (int) (sum * 1.0f / total * 100);
           }
           fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
