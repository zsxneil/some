package com.my.mapreduce.avg;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class AvgReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        int sum = 0;
        int count = 0;

        Iterator<IntWritable> itr = values.iterator();
        while (itr.hasNext()) {
            sum += itr.next().get(); //计算总分
            count++;    //计算总科数
        }

        int avg = sum / count; //计算平均成绩
        context.write(key, new IntWritable(avg));
    }
}
