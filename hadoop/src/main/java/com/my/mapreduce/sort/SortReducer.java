package com.my.mapreduce.sort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

//reduce将输入中的key复制到输出数据的key上，

//然后根据输入的value-list中元素的个数决定key的输出次数

//用全局linenum来代表key的位次
public class SortReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

    private static IntWritable linenum = new IntWritable(1);

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        for (IntWritable val : values) {
            context.write(linenum, key);
            linenum = new IntWritable(linenum.get() + 1);
        }
    }
}
