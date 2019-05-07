package com.my.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * 这个类的声明上有四个泛型，对于KEYOUT、VALUEOUT表示的经过mapper阶段映射后输出的内容。这个很好理解。我们在上一节已经讲过，Map阶段作用就是将原始的内容映射为key-value集合，然后将结果传递给Reducer。因为我们这里统计的是单词的个数，因此KEYOUT表示的应该就是单词内容(字符串类型)，VALUEOUT表示的是单词的出现的次数(整数类型)。

 对于KEYIN和KEYOUT是什么呢？因为我们统计的时候文本的内容是一行一行读取的，所以KEYIN就是行号(整数类型),VALUEIN是这一行的内容(字符串类型)。

 在确定了四个泛型参数之后，我们就可以开始写代码了，不过需要注意的是，在Hadoop中，定义了自己的数据类型，字符串类型用Text表示，而整数类型用IntWriteable表示。在后文，我们将会对Hadoop的数据类型进行详细讲解。
 */
public class TokenizerMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);

    private Text word = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString());
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken());
            context.write(word, one);
        }
    }
}
