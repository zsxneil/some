package com.my.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * 这个类依然有四个泛型。我们已经知道，Reducer是接受Map阶段的输出，按照相同key进行归一。那么map阶段的输出类型肯定就是reduce阶段的输入类型。

 因此，在我们的案例中，KEYIN应该单词(字符串),VALUEIN应该是单词出现的次数(整数)。那么KEYOUT、VALUEOUT是什么？因为我们统计的就是各个单词出现的次数(字符串)，所以还应该是单词和出现的次数(整数)
 */
public class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private IntWritable result = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        result.set(sum);
        context.write(key, result);
    }
}
