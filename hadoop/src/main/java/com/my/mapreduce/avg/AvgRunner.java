package com.my.mapreduce.avg;

import com.my.mapreduce.sort.SortMapper;
import com.my.mapreduce.sort.SortReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

import java.io.IOException;


/**
 * 　计算学生平均成绩是一个仿"WordCount"例子，用来重温一下开发MapReduce程序的流程。程序包括两部分的内容：Map部分和Reduce部分，分别实现了map和reduce的功能。

 Map处理的是一个纯文本文件，文件中存放的数据时每一行表示一个学生的姓名和他相应一科成绩。Mapper处理的数据是由InputFormat分解过的数据集，其中InputFormat的作用是将数据集切割成小数据集InputSplit，每一个InputSlit将由一个Mapper负责处理。此外，InputFormat中还提供了一个RecordReader的实现，并将一个InputSplit解析成<key,value>对提供给了map函数。InputFormat的默认值是TextInputFormat，它针对文本文件，按行将文本切割成InputSlit，并用LineRecordReader将InputSplit解析成<key,value>对，key是行在文本中的位置，value是文件中的一行。

 Map的结果会通过partion分发到Reducer，Reducer做完Reduce操作后，将通过以格式OutputFormat输出。

 Mapper最终处理的结果对<key,value>，会送到Reducer中进行合并，合并的时候，有相同key的键/值对则送到同一个Reducer上。Reducer是所有用户定制Reducer类地基础，它的输入是key和这个key对应的所有value的一个迭代器，同时还有Reducer的上下文。Reduce的结果由Reducer.Context的write方法输出到文件中。
 */
public class AvgRunner {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir", "E:\\web\\hadoop-winutils-2.6.0");

        Configuration conf = new Configuration();
        final String nameNodeUrl = "hdfs://172.20.177.47:9000";
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, nameNodeUrl);

        //1.构建job对象，因为hadoop中可能有多个任务运行，每个任务都有一个名字，以示区分
        final String jobName = "score avg";
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(AvgRunner.class);

        //任务内容的输入路径
        FileInputFormat.addInputPath(job, new Path("/mapreduce/avg/math.txt"));
        FileInputFormat.addInputPath(job, new Path("/mapreduce/avg/china.txt"));
        FileInputFormat.addInputPath(job, new Path("/mapreduce/avg/english.txt"));
        //任务结果的输出路径，如果输出目录已存在，就删除
        final Path outputPath = new Path("/out/avg");
        FileSystem fileSystem = FileSystem.get(conf);
        if (fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }
        FileOutputFormat.setOutputPath(job, outputPath);

        //2.设置Mapper
        job.setMapperClass(AvgMapper.class);
        //规约
        job.setCombinerClass(AvgReducer.class);

        //3.设置Reducer
        job.setReducerClass(AvgReducer.class);
        //设置reducer任务数，默认为1
        job.setNumReduceTasks(1);
        //设置分区类
        job.setPartitionerClass(HashPartitioner.class);
        //设置输出的key与value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        /*// 将输入的数据集分割成小数据块splites，提供一个RecordReder的实现
        job.setInputFormatClass(TextInputFormat.class);

        // 提供一个RecordWriter的实现，负责数据输出
        job.setOutputFormatClass(TextOutputFormat.class);*/

        //提交任务并等待任务完成
        job.waitForCompletion(true);
    }

}
