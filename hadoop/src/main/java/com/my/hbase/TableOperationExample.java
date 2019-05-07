package com.my.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class TableOperationExample {

    private static final String TABLE_NAME = "hbase_study:test";
    private static final String CF_DEFAULT = "personal data";

    public static void createOrOverWrite(Admin admin, HTableDescriptor table) throws IOException {
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        admin.createTable(table);

    }

    public static void createSchemaTables(Configuration config) throws IOException {
        Connection connection = null;
        Admin admin = null;
        Table table = null;
        try {
            connection = ConnectionFactory.createConnection(config);
            admin = connection.getAdmin();
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
            tableDescriptor.addFamily(new HColumnDescriptor(CF_DEFAULT).setCompressionType(Compression.Algorithm.NONE));
            System.out.println("Creating table.");
            createOrOverWrite(admin, tableDescriptor);
            table = connection.getTable(TableName.valueOf(TABLE_NAME));
            addData(table);
            System.out.println("Done.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            admin.close();
            table.close();
            connection.close();
        }
    }

    private static void addData(Table table) throws IOException {
        Put put = new Put(Bytes.toBytes("row1"));
        put.addColumn(Bytes.toBytes(CF_DEFAULT), Bytes.toBytes("name"), Bytes.toBytes("neil1"));
        table.put(put);
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("hadoop.home.dir", "E:\\web\\hadoop-winutils-2.6.0");
        // Instantiating Configuration class
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "172.20.177.47");
        conf.set("hbase.rootdir", "hdfs://172.20.177.47:9000/hbase");

       // createSchemaTables(conf);
//        addData(conf);
        getData(conf);

    }

    public static void addData(Configuration conf) throws IOException {
        Connection connection = null;
        Table table = null;
        try {
            connection = ConnectionFactory.createConnection(conf);

            table = connection.getTable(TableName.valueOf(TABLE_NAME));
            addData(table);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            table.close();
            connection.close();
        }
    }

    public static void getData(Configuration config) throws IOException {
        Connection connection = null;
        Table table = null;
        try {
            connection = ConnectionFactory.createConnection(config);

            table = connection.getTable(TableName.valueOf(TABLE_NAME));
            getData(table);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            table.close();
            connection.close();
        }
    }

    private static void getData(Table table) throws IOException {

        Get get = new Get(Bytes.toBytes("row2"));
        Result result = table.get(get);
        if (!result.isEmpty()) {
            List<Cell> cellList = result.listCells();
            if (cellList != null) {
                for (Cell cell : cellList) {
                    System.out.println(Bytes.toString(cell.getFamilyArray()) + " : " + Bytes.toString(cell.getValueArray())  + "***********************");
                }
            }
        }

        ResultScanner resultScanner = table.getScanner(Bytes.toBytes(CF_DEFAULT), Bytes.toBytes("name"));
        Iterator<Result> iterator = resultScanner.iterator();
        while (iterator.hasNext()) {
            Result ret = iterator.next();
            if (!ret.isEmpty()) {
                byte[] value = ret.getValue(Bytes.toBytes(CF_DEFAULT), Bytes.toBytes("name"));
                System.out.println(Bytes.toString(value) + "/////////////////////");

            }
        }
    }

}
