package com.my.mapreduce.sort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

import java.io.IOException;


/**
 * 　这个实例仅仅要求对输入数据进行排序，熟悉MapReduce过程的读者会很快想到在MapReduce过程中就有排序，
 * 是否可以利用这个默认的排序，而不需要自己再实现具体的排序呢？答案是肯定的。

 　　但是在使用之前首先需要了解它的默认排序规则。它是按照key值进行排序的，
 如果key为封装int的IntWritable类型，那么MapReduce按照数字大小对key排序，
 如果key为封装为String的Text类型，那么MapReduce按照字典顺序对字符串排序。

 　　了解了这个细节，我们就知道应该使用封装int的IntWritable型数据结构了。
 也就是在map中将读入的数据转化成IntWritable型，然后作为key值输出（value任意）。
 reduce拿到<key，value-list>之后，将输入的key作为value输出，
 并根据value-list中元素的个数决定输出的次数。输出的key（即代码中的linenum）是一个全局变量，它
 统计当前key的位次。需要注意的是这个程序中没有配置Combiner，也就是在MapReduce过程中不使用Combiner。
 这主要是因为使用map和reduce就已经能够完成任务了。
 */
public class SortRunner {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir", "E:\\web\\hadoop-winutils-2.6.0");

        Configuration conf = new Configuration();
        final String nameNodeUrl = "hdfs://172.20.177.47:9000";
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, nameNodeUrl);

        //1.构建job对象，因为hadoop中可能有多个任务运行，每个任务都有一个名字，以示区分
        final String jobName = "data sort";
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(SortRunner.class);

        //任务内容的输入路径
        FileInputFormat.addInputPath(job, new Path("/mapreduce/sort/file1.txt"));
        FileInputFormat.addInputPath(job, new Path("/mapreduce/sort/file2.txt"));
        FileInputFormat.addInputPath(job, new Path("/mapreduce/sort/file3.txt"));
        //任务结果的输出路径，如果输出目录已存在，就删除
        final Path outputPath = new Path("/out/sort");
        FileSystem fileSystem = FileSystem.get(conf);
        if (fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }
        FileOutputFormat.setOutputPath(job, outputPath);

        //2.设置Mapper
        job.setMapperClass(SortMapper.class);
        //规约
        //job.setCombinerClass(DeduplicationReducer.class);

        //3.设置Reducer
        job.setReducerClass(SortReducer.class);
        //设置reducer任务数，默认为1
        job.setNumReduceTasks(1);
        //设置分区类
        job.setPartitionerClass(HashPartitioner.class);
        //设置输出的key与value
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);

        //提交任务并等待任务完成
        job.waitForCompletion(true);
    }

}
