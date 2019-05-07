package com.my.mapreduce.deduplication;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

//reduce将输入中的key复制到输出数据的key上，并直接输出
public class DeduplicationReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        context.write(key, new Text(""));
    }
}
