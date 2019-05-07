package com.my.mapreduce.mtjoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

import java.io.IOException;

/**
 *  输入是两个文件，一个代表工厂表，包含工厂名列和地址编号列；
 *  另一个代表地址表，包含地址名列和地址编号列。
 *  要求从输入数据中找出工厂名和地址名的对应关系，输出"工厂名——地址名"表。
 */
public class MTJoinRunner {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir", "E:\\web\\hadoop-winutils-2.6.0");

        Configuration conf = new Configuration();
        final String nameNodeUrl = "hdfs://172.20.177.47:9000";
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, nameNodeUrl);

        //1.构建job对象，因为hadoop中可能有多个任务运行，每个任务都有一个名字，以示区分
        final String jobName = "multi table join";
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(MTJoinRunner.class);

        //任务内容的输入路径
        FileInputFormat.addInputPath(job, new Path("/mapreduce/mtjoin/factory.txt"));
        FileInputFormat.addInputPath(job, new Path("/mapreduce/mtjoin/address.txt"));
        //任务结果的输出路径，如果输出目录已存在，就删除
        final Path outputPath = new Path("/out/mtjoin");
        FileSystem fileSystem = FileSystem.get(conf);
        if (fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }
        FileOutputFormat.setOutputPath(job, outputPath);

        //2.设置Mapper
        job.setMapperClass(MultiTableJoinMapper.class);
        //规约
        //job.setCombinerClass(DeduplicationReducer.class);

        //3.设置Reducer
        job.setReducerClass(MultiTableJoinReducer.class);
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
