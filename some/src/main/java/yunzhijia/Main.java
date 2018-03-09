package yunzhijia;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import oauth1a.AuthorizationUtil;
import okhttp3.OkHttpClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final String UTF8 = "UTF-8";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static final String OAUTH_CONSUMER_KEY = "500042526";
    public static final String OAUTH_CONSUMER_SECRET = "CsWbL8NUpmIKwFP1mwVq";
    public static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
    public static final float OAUTH_VERSION = 1.0f;


    @Test
    public void ticketTest() throws Exception{
        String ticket = "APPURLWITHTICKET245f1ffdb6ad4bea085983a11b10b8ac";
        String url = "https://www.yunzhijia.com/openapi/third/v1/ticket/public/tickettocontext";
        Map<String, Object> paras = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        paras.put("ticket", ticket);
        jsonObject.put("ticket", ticket);
        jsonObject.put("access_token","920953850e95f6a360cb699584f72314");
        jsonObject.put("callback","false");
        long oauth_timestamp = System.currentTimeMillis()/1000;
        String oauth_nonce = String.valueOf(oauth_timestamp + new Random().nextInt());


        /*HttpHelperAsync.Response response = post(OAUTH_CONSUMER_KEY,
                OAUTH_CONSUMER_SECRET,
                OAUTH_SIGNATURE_METHOD,
                oauth_timestamp,
                oauth_nonce,
                OAUTH_VERSION,
                null,
                null,
                null,
                 url,
                null,
                 paras,
                0);*/
        HttpHelperAsync.Response response = commitPostJsonRequest(url, jsonObject);
        System.out.println(response);


    }


    public static void main(String[] args) throws Exception {

        String generatetodoURL = "https://www.yunzhijia.com/openapi/third/v1/newtodo/open/generatetodo.json";
        JSONObject json = new JSONObject();
        json.put("sourceId", "000001");
        json.put("appId", "500042526");
        //json.put("senderId", "");
        json.put("content", "测试代办的content");
        json.put("url", "http://easksm.kingdee.com/lightorg/messages");
        json.put("title", "测试代办的title");
        json.put("itemtitle", "测试代办的itemtitle");
        json.put("headImg", "https://goss3.vcg.com/creative/vcg/800/version23/VCG41569078245.jpg");
        JSONArray params = new JSONArray();
        JSONObject param = new JSONObject();
        //param.put("openId", "20961602");
        param.put("openId", "56f109c9e4b0f6ee6198f072");
        //param.put("openId", "766a81f0-b8c3-11e3-8d13-e41f137ad9f4");
        JSONObject status = new JSONObject();
        status.put("READ", "1");
        status.put("DO", "1");
        param.put("status", status);
        params.add(param);
        json.put("params", params);

        HttpHelperAsync.Response response = commitPostJsonRequest(generatetodoURL, json);

    }

    public static HttpHelperAsync.Response commitPostJsonRequest(String url, JSONObject json) throws Exception {
        long oauth_timestamp = System.currentTimeMillis()/1000;
        String oauth_nonce = String.valueOf(oauth_timestamp + new Random().nextInt());
        String oauth_token = "3f3f94d26637acc3f265c016ca471183";
        String oauth_token_secret = "9a392124d84d6bddd44f86c88bbeca4";
        String oauth_verifier = null;

        HttpHelperAsync.Response response = postJSON(OAUTH_CONSUMER_KEY,
                OAUTH_CONSUMER_SECRET,
                OAUTH_SIGNATURE_METHOD,
                oauth_timestamp,
                oauth_nonce,
                OAUTH_VERSION,
                null,
                null,
                null,
                url,
                null,
                json,
                0);
        System.out.println(response);
        return response;
    }

    public static HttpHelperAsync.Response postJSON(String consumerKey, String consumerSecret,
                                                    String signatureMethod, long timestamp,
                                                    String nonce, float version,
                                                    String oauthToken, String oauthTokenSecret,
                                                    String verifier, String url, HttpHelperAsync.Headers headers, JSONObject parameters, long timeoutMillis) throws Exception {

        if (null == headers) {
            headers = new HttpHelperAsync.Headers();
            headers.put("Content-Type", HttpHelperAsync.APPLICATION_JSON);
        }

        String authorizationHeader = AuthorizationUtil.generateAuthorizationHeader(consumerKey, consumerSecret,
                signatureMethod, timestamp, nonce, version,
                oauthToken, oauthTokenSecret, verifier, url, null, "POST");
        System.out.println(authorizationHeader);
        headers.put("Authorization", authorizationHeader);
        return HttpHelperAsync.postJSON(url, headers, parameters, timeoutMillis);
    }

    public static HttpHelperAsync.Response get(String consumerKey, String consumerSecret,
                                               String signatureMethod, long timestamp,
                                               String nonce, float version,
                                               String oauthToken, String oauthTokenSecret,
                                               String verifier, String url, HttpHelperAsync.Headers headers, Map<String, Object> parameters, long timeoutMillis) throws Exception {
        headers = headers(consumerKey, consumerSecret, signatureMethod,
                timestamp, nonce, version, oauthToken, oauthTokenSecret,
                verifier, url, headers, parameters, timeoutMillis, "GET");
        return HttpHelperAsync.get(url, headers, parameters, timeoutMillis);
    }

    public static HttpHelperAsync.Response post(String consumerKey, String consumerSecret,
                                                String signatureMethod, long timestamp,
                                                String nonce, float version,
                                                String oauthToken, String oauthTokenSecret,
                                                String verifier, String url, HttpHelperAsync.Headers headers, Map<String, Object> parameters, long timeoutMillis) throws Exception {
        headers = headers(consumerKey, consumerSecret, signatureMethod,
                timestamp, nonce, version, oauthToken, oauthTokenSecret,
                verifier, url, headers, parameters, timeoutMillis, "POST");

        return HttpHelperAsync.post(url, headers, parameters, timeoutMillis);
    }



    private static HttpHelperAsync.Headers headers(String consumerKey, String consumerSecret,
                                                   String signatureMethod, long timestamp,
                                                   String nonce, float version,
                                                   String oauthToken, String oauthTokenSecret,
                                                   String verifier, String url, HttpHelperAsync.Headers headers, Map<String, Object> parameters, long timeoutMillis, String reqType) throws Exception {
        if (null != headers) {
            Object contentType = headers.get("Content-Type");
            if (null != contentType && APPLICATION_JSON.equals(contentType.toString())) {
                headers.put("Authorization", AuthorizationUtil.generateAuthorizationHeader(consumerKey, consumerSecret,
                        signatureMethod, timestamp, nonce, version,
                        oauthToken, oauthTokenSecret, verifier, url, null, reqType));
                return headers;
            }
        } else {
            headers = new HttpHelperAsync.Headers();
        }
        headers.put("Authorization", AuthorizationUtil.generateAuthorizationHeader(consumerKey, consumerSecret,
                signatureMethod, timestamp, nonce, version,
                oauthToken, oauthTokenSecret, verifier, url, parameters, reqType));
        return headers;
    }

}



