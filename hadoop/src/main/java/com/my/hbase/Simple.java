package com.my.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Simple {

    public static void main(String[] args) throws IOException {
        System.setProperty("hadoop.home.dir", "E:\\web\\hadoop-winutils-2.6.0");
        // Instantiating Configuration class
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "172.20.177.47");
        conf.set("hbase.rootdir", "hdfs://172.20.177.47:9000/hbase");

        Connection connection = null;
        Table table = null;
        try {
            connection = ConnectionFactory.createConnection(conf);
            table = connection.getTable(TableName.valueOf("hbase_study:emp"));
            HTableDescriptor hTableDescriptor = table.getTableDescriptor();
            HColumnDescriptor[] hColumnDescriptors = hTableDescriptor.getColumnFamilies();
            for (int i=0; i<hColumnDescriptors.length; i++) {
                HColumnDescriptor hColumnDescriptor = hColumnDescriptors[i];
                String name = hColumnDescriptor.getNameAsString();
                System.out.println(name + "*********************");
            }

            ResultScanner scanner = table.getScanner(Bytes.toBytes("personal data"));
            Iterator<Result> iterator = scanner.iterator();
            while (iterator.hasNext()) {
                Result result = iterator.next();

                if (result.isEmpty())
                    continue;
                System.out.println(Bytes.toString(result.getRow()) + ".....................");

                List<Cell> cellList = result.listCells();
                for (Cell cell : cellList) {
                    //System.out.println(Bytes.toString(cell.getFamilyArray()) + "-----------------");
                    System.out.println(Bytes.toString(cell.getValueArray()) + "-----------------");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            table.close();
            connection.close();
        }

    }

}
