package com.my.mapreduce.avg;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class AvgMapper extends Mapper<LongWritable, Text, Text, IntWritable> {



    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        /*String line = value.toString();
        // 将输入的数据首先按行进行分割
        StringTokenizer tokenizerArticle = new StringTokenizer(line, "\n");
        // 分别对每一行进行处理
        while (tokenizerArticle.hasMoreElements()) {
            //System.out.println(tokenizerArticle.nextElement());
            // 每行按空格划分
            String str = tokenizerArticle.nextToken();
            //System.out.println(str + "**********************************");
            StringTokenizer tokenizerLine = new StringTokenizer(str);
            String strName = tokenizerLine.nextToken();
            String strScore = tokenizerLine.nextToken();

            Text name = new Text(strName);
            int scoreInt = Integer.parseInt(strScore);
            //输出姓名和分数
            context.write(name, new IntWritable(scoreInt));
        }*/

        StringTokenizer str = new StringTokenizer(value.toString());
        String strName = str.nextToken();
        String strScore = str.nextToken();

        Text name = new Text(strName);
        int scoreInt = Integer.parseInt(strScore);
        //输出姓名和分数
        context.write(name, new IntWritable(scoreInt));
    }
}
