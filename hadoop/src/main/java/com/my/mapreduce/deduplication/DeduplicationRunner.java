package com.my.mapreduce.deduplication;

import com.my.mapreduce.wordcount.IntSumReducer;
import com.my.mapreduce.wordcount.TokenizerMapper;
import com.my.mapreduce.wordcount.WordCount;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

import java.io.IOException;

/**
 * 设计思路
 * 数据去重的最终目标是让原始数据中出现次数超过一次的数据在输出文件中只出现一次。
 * 我们自然而然会想到将同一个数据的所有记录都交给一台reduce机器，无论这个数据出现多少次，
 * 只要在最终结果中输出一次就可以了。具体就是reduce的输入应该以数据作为key，
 * 而对value-list则没有要求。当reduce接收到一个<key，value-list>时就直接将key复制到输出的key中，并将value设置成空值。

 　　在MapReduce流程中，map的输出<key，value>经过shuffle过程聚集成<key，value-list>后会交给reduce。
 所以从设计好的reduce输入可以反推出map的输出key应为数据，value任意。
 继续反推，map输出数据的key为数据，而在这个实例中每个数据代表输入文件中的一行内容，
 所以map阶段要完成的任务就是在采用Hadoop默认的作业输入方式之后，将value设置为key，
 并直接输出（输出中的value任意）。map中的结果经过shuffle过程之后交给reduce。
 reduce阶段不会管每个key有多少个value，它直接将输入的key复制为输出的key，
 并输出就可以了（输出中的value被设置成空了）。
 */
public class DeduplicationRunner {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir", "E:\\web\\hadoop-winutils-2.6.0");

        Configuration conf = new Configuration();
        final String nameNodeUrl = "hdfs://172.20.177.47:9000";
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, nameNodeUrl);

        //1.构建job对象，因为hadoop中可能有多个任务运行，每个任务都有一个名字，以示区分
        final String jobName = "data deduplication";
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(DeduplicationRunner.class);

        //任务内容的输入路径
        FileInputFormat.addInputPath(job, new Path("/mapreduce/deduplication/file1.txt"));
        FileInputFormat.addInputPath(job, new Path("/mapreduce/deduplication/file2.txt"));
        //任务结果的输出路径，如果输出目录已存在，就删除
        final Path outputPath = new Path("/out/deduplication");
        FileSystem fileSystem = FileSystem.get(conf);
        if (fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }
        FileOutputFormat.setOutputPath(job, outputPath);

        //2.设置Mapper
        job.setMapperClass(DeduplicationMapper.class);
        //规约
        job.setCombinerClass(DeduplicationReducer.class);

        //3.设置Reducer
        job.setReducerClass(DeduplicationReducer.class);
        //设置reducer任务数，默认为1
        job.setNumReduceTasks(1);
        //设置分区类
        job.setPartitionerClass(HashPartitioner.class);
        //设置输出的key与value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //提交任务并等待任务完成
        job.waitForCompletion(true);
    }

}
