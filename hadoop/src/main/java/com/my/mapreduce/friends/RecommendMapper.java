package com.my.mapreduce.friends;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 将数据导入mapper，两列分别作key和value
 */
public class RecommendMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] ss = StringUtils.split(line,"\t");
        context.write(new Text(ss[0]), new Text(ss[1]));
        context.write(new Text(ss[1]), new Text(ss[0]));
    }
}
