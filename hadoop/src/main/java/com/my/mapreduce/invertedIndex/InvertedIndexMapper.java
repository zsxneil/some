package com.my.mapreduce.invertedIndex;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.StringTokenizer;

public class InvertedIndexMapper extends Mapper<Object, Text, Text, Text> {

    private Text keyInfo = new Text(); //存储单词和URL组合
    private Text valueInfo = new Text(); //存储词频
    private FileSplit fileSplit;

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // 获得<key,value>对所属的FileSplit对象
        fileSplit = (FileSplit) context.getInputSplit();

        StringTokenizer str = new StringTokenizer(value.toString());

        while (str.hasMoreTokens()) {
            // key值由单词和URL组成，如"MapReduce：file1.txt"
            // 获取文件的完整路径
            // keyInfo.set(itr.nextToken()+":"+split.getPath().toString());
            // 这里为了好看，只获取文件的名称。
            int splitIndex = fileSplit.getPath().toString().indexOf("file");
            keyInfo.set(str.nextToken() + ":" + fileSplit.getPath().toString().substring(splitIndex));
            //词频初始化为1
            valueInfo.set("1");
            context.write(keyInfo, valueInfo);
        }
    }
}
