package com.my;

import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.ChecksumType;
import com.github.shyiko.mysql.binlog.event.deserialization.ColumnType;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {

        Map<Long, Map<String, Object>> tableMap = new HashMap<>();


        File binlogFile = new File("F:/test/binlog/mysql-bin.000056");
        //File binlogFile = new File("F:/test/binlog/mysql-bin.000001");
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setChecksumType(ChecksumType.CRC32);
        BinaryLogFileReader reader = new BinaryLogFileReader(binlogFile, eventDeserializer);

        try {
            for (Event event; (event=reader.readEvent()) != null;) {
                EventHeader eventHeader = event.getHeader();
                EventType eventType = eventHeader.getEventType();
                if (EventType.isWrite(eventType)) {
                    EventData eventData = event.getData();
                    WriteRowsEventData writeEventData;
                    if (eventData instanceof EventDeserializer.EventDataWrapper) {
                        writeEventData = (WriteRowsEventData) ((EventDeserializer.EventDataWrapper) eventData).getInternal();
                    } else {
                        writeEventData = (WriteRowsEventData) eventData;
                    }
                    //打印插入的字段的index
                    BitSet bitSet = writeEventData.getIncludedColumns();
                    //System.out.println(bitSet.toString());
                    Map<String, Object> tableInfoMap = tableMap.get(writeEventData.getTableId());
                    String tableName = (String) tableInfoMap.get("table");
                    if (!"t_diag_reportInfo".equalsIgnoreCase(tableName)) {
                        continue;
                    }
                    //System.out.println("tableInfo:" + Arrays.asList(tableMap.get(writeEventData.getTableId())));
                    StringBuilder builder = new StringBuilder("insert into ");
                    builder.append((String) tableInfoMap.get("database")).append(".").append((String) tableInfoMap.get("table"));
                    builder.append("(");
                    bitSet.stream().forEach(x -> builder.append("?,"));
                    builder.deleteCharAt(builder.length() - 1);
                    builder.append(") values(");

                    //打印每个事件中插入的数据
                    List<Serializable[]> rows = writeEventData.getRows();
                    if (rows != null) {
                        byte[] columnTypes = (byte[]) tableInfoMap.get("columnTypes");
                        for (Serializable[] row : rows) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < row.length; i++) {
                                Serializable column = row[i];
                                if (column == null) {
                                    stringBuilder.append("null,");
                                    continue;
                                }
                                if (columnTypes[i] == ColumnType.VARCHAR.getCode()) {
                                    stringBuilder.append("'").append(column.toString()).append("'");
                                } else if (columnTypes[i] == ColumnType.DATETIME_V2.getCode()) { //日期类型
                                    Date date = (Date) column;
                                    stringBuilder.append("'").append(format(date)).append("'");
                                } else if (-4 == columnTypes[i]) { //测试了一下 Text字段的类型返回的是-4
                                    byte[] bytes = (byte[]) column;
                                    String value = new String(bytes, "UTF-8");
                                    stringBuilder.append("'").append(value).append("'");
                                } else {
                                    stringBuilder.append(column == null ? null : column.toString());
                                }
                                stringBuilder.append(",");
                            }
                            if (stringBuilder.length() > 0) {
                                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                            }
                            //System.out.println(stringBuilder.toString());
                            builder.append(stringBuilder).append("),");
                        }
                        builder.deleteCharAt(builder.length() - 1);
                        builder.append(";");
                        System.out.println(builder.toString());
                    }

                    System.out.println("*********************************");
                } else if (eventType == EventType.TABLE_MAP) {
                    EventData eventData = event.getData();
                    TableMapEventData tableMapEventData;
                    if (eventData instanceof EventDeserializer.EventDataWrapper) {
                        tableMapEventData = (TableMapEventData) ((EventDeserializer.EventDataWrapper) eventData).getInternal();
                    } else {
                        tableMapEventData = (TableMapEventData) eventData;
                    }
                    String database = tableMapEventData.getDatabase();
                    String table = tableMapEventData.getTable();
                    long tableId = tableMapEventData.getTableId();
                    byte[] columnTypes = tableMapEventData.getColumnTypes();
                    StringBuilder columnTypeBuilder = new StringBuilder();
                    for (int i=0; i<columnTypes.length; i++) {
                        columnTypeBuilder.append(columnTypes[i]).append(",");
                    }
                    columnTypeBuilder.deleteCharAt(columnTypeBuilder.length() - 1);
                    /*if ("t_diag_reportInfo".equalsIgnoreCase(table)) {
                        System.out.println(columnTypeBuilder.toString());
                    }*/

                    Map<String, Object> tableInfoMap = new HashMap<>();
                    tableInfoMap.put("table", table);
                    tableInfoMap.put("database", database);
                    tableInfoMap.put("columnTypes", tableMapEventData.getColumnTypes());
                    tableMap.put(tableId, tableInfoMap);
                    //System.out.println(tableMapEventData.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }

    }

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public static String format(Date date) {
        return sdf.format(date);
    }
}
