package com.my.mapreduce.sort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class SortMapper extends Mapper<Object, Text, IntWritable, IntWritable> {

    private static IntWritable data = new IntWritable();

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        data.set(Integer.parseInt(value.toString()));
        context.write(data, new IntWritable(1));
    }
}
