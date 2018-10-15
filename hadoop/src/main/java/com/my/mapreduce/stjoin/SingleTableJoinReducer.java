package com.my.mapreduce.stjoin;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class SingleTableJoinReducer extends Reducer<Text, Text, Text, Text> {

    public static int time = 0;

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        //输出表头
        if (time == 0) {
            context.write(new Text("grandchild"), new Text("grandparent"));
            time ++;
        }

        int grandChildNum = 0;
        String[] grandchild = new String[10];
        int grantParentNum = 0;
        String[] grandparent = new String[10];

        Iterator<Text> itr = values.iterator();
        while (itr.hasNext()) {
            String record = itr.next().toString();
            int len = record.length();
            int i = 2;
            if (0 == len) {
                continue;
            }

            //取得左右表标识
            char relationType = record.charAt(0);
            String childname = new String();
            String parentname = new String();

            // 获取value-list中value的child
            while (record.charAt(i) != '+') {
                childname += record.charAt(i);
                i++;
            }

            i = i + 1;

            // 获取value-list中value的parent
            while (i < len) {
                parentname += record.charAt(i);
                i++;
            }

            //左表，取出child放入grandchildren
            if ('1' == relationType) {
                grandchild[grandChildNum] = childname;
                grandChildNum ++;
            }

            //右边，取出parent放入grandparent
            if ('2' == relationType) {
                grandparent[grantParentNum] = parentname;
                grantParentNum ++;
            }

            // grandchild和grandparent数组求笛卡尔儿积
            if (0 != grandChildNum && 0 != grantParentNum) {
                for (int m = 0; m < grandChildNum; m++) {
                    for (int n = 0; n < grantParentNum; n++) {
                        // 输出结果
                        context.write(new Text(grandchild[m]), new Text(grandparent[n]));
                    }
                }
            }
        }
    }
}
