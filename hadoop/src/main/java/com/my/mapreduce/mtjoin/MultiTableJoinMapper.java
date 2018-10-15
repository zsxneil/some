package com.my.mapreduce.mtjoin;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 *

 * 在map中先区分输入行属于左表还是右表，然后对两列值进行分割，

 * 保存连接列在key值，剩余列和左右表标志在value中，最后输出

 */

public class MultiTableJoinMapper extends Mapper<Object, Text, Text, Text> {

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String relationType = new String(); //左右表标识

        //输入文件首行，不处理
        if (line.contains("factoryname") == true ||
                line.contains("addressed") == true) {
            return;
        }

        //输入的一行预处理文本
        StringTokenizer str = new StringTokenizer(line);
        String mapKey = new String();
        String mapValue = new String();

        int i = 0;
        while (str.hasMoreTokens()) {
            String token = str.nextToken();
            //判断是地址ID就存储values[0]
            if (token.charAt(0) >= '0' && token.charAt(0) <= '9') {
                mapKey = token;
                if (i > 0) {
                    relationType = "1";
                } else {
                    relationType = "2";
                }
                continue;
            }

            //存工厂名,公司名有空格，所以要一直加
            mapValue += token + " ";
            i++;
        }
        context.write(new Text(mapKey), new Text(relationType + "+" + mapValue));

    }
}
