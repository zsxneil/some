package com.my.mapreduce.invertedIndex;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class InvertedIndexCombine extends Reducer<Text, Text, Text, Text> {

    private Text info = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        //统计词频
        int sum = 0;
        for (Text value : values) {
            sum += Integer.parseInt(value.toString());
        }

        int splitIndex = key.toString().indexOf(":");
        // 重新设置value值由URL和词频组成
        info.set(key.toString().substring(splitIndex + 1) + ":" + sum);
        //重新设置key值为单词
        key.set(key.toString().substring(0, splitIndex));

        context.write(key, info);
    }
}
