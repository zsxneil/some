package com.my.mapreduce.mtjoin;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/*
     * reduce解析map输出，将value中数据按照左右表分别保存，

　　* 然后求出笛卡尔积，并输出。

     */
public class MultiTableJoinReducer extends Reducer<Text, Text, Text, Text> {

    private static int time = 0;

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        //输出表头
        if (time == 0) {
            context.write(new Text("factoryname"), new Text("addressname"));
            time++;
        }

        int factoryNum = 0;
        String[] factory = new String[10];
        int addressNum = 0;
        String[] address = new String[10];

        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            String record = iterator.next().toString();
            int len = record.length();
            int i = 2;
            if (0 == len) {
                continue;
            }

            //取得左右表标识
            char relationType = record.charAt(0);

            //左表
            if ('1' == relationType) {
                factory[factoryNum] = record.substring(i);
                factoryNum++;
            }

            if ('2' == relationType) {
                address[addressNum] = record.substring(i);
                addressNum++;
            }

        }

        //求笛卡尔积
        if (0 != factoryNum && 0 != addressNum) {
            for (int m=0; m<factoryNum; m++) {
                for (int n=0; n<addressNum; n++) {
                    context.write(new Text(factory[m]), new Text(address[n]));
                }
            }
        }
    }
}
