import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.model.Article;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/14.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("title", "test");
        map.put("content", "this is content");
        map.put("releaseDate",new Date());
        System.out.println(map);

        System.out.println(JSONObject.toJSONString(map));

        ObjectMapper mapper = new ObjectMapper();
        Article article = mapper.readValue(JSONObject.toJSONString(map), Article.class);
        System.out.println(article);
    }
}
