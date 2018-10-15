package com.my.mapreduce.friends;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RecommendReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> set = new HashSet<>();

        for (Text val : values) {
            set.add(val.toString());
        }

        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next() + "********************");
        }

        //同一个key表示和key都是好友，所以所有的value都可以相互推荐好友
        if (set.size() > 0) {
            for (Iterator j = set.iterator(); j.hasNext();) {
                String name = (String) j.next();
                for (Iterator i = set.iterator(); i.hasNext();) {
                    String other = (String) i.next();
                    if (!name.equals(other)) {
                        context.write(new Text(name), new Text(other));
                    }
                }
            }
        }

    }
}
