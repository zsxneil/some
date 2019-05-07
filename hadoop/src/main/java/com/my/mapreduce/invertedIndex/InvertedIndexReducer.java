package com.my.mapreduce.invertedIndex;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {

    private Text result = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        //生成文档列表
        String fileList = new String();
        for (Text val : values) {
            fileList += val.toString() + ";";
        }

        result.set(fileList);
        context.write(key, result);
    }
}
