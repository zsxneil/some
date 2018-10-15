package com.my.mapreduce.stjoin;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/*
     * map将输入分割child和parent，然后正序输出一次作为右表，

     * 反序输出一次作为左表，需要注意的是在输出的value中必须

     * 加上左右表的区别标识。

     */
public class SingleTableJoinMapper extends Mapper<Object, Text, Text, Text> {

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String childName = new String();
        String parentName = new String();
        String relationType = new String(); //左右表标识

        StringTokenizer str = new StringTokenizer(value.toString());
        String[] values = new String[2];
        int i = 0;
        while (str.hasMoreTokens()) {
            values[i] = str.nextToken();
            i++;
        }

        if (values[0].compareTo("child") != 0) {
            childName = values[0];
            parentName = values[1];

            //输出左表
            relationType = "1";
            context.write(new Text(values[1]), new Text(relationType + "+" + childName + "+" + parentName));

            relationType = "2";
            context.write(new Text(values[0]), new Text(relationType + "+" + childName + "+" + parentName));
        }

    }
}
