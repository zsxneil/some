package com.my;

import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.ChecksumType;
import com.github.shyiko.mysql.binlog.event.deserialization.ColumnType;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 本来计划写一个通过mysql binlog文件解析出某个表的sql语句，但是因为无法通过binlog文件获取到column name,放弃
 * 后面研究通过增量方式的实时解析
 */
public class BinLogParser {

    private Map<Long, Map<String, Object>> tableMap = new HashMap<>();
    private static final String TABLE = "table";
    private static final String DATABASE = "database";
    private static final String COLUMN_TYPES = "columnTypes";

    private String filterTableName;

    public String getFilterTableName() {
        return filterTableName;
    }

    public BinLogParser(String filterTableName) {
        this.filterTableName = filterTableName;
    }

    public BinLogParser() {
    }

    /**
     * 解析表结构，主要是通过tableId拿到表信息
     * @param event
     */
    public void parseTableMap(Event event) {
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
    }

    /**
     * 构建插入语句
     * @param event
     * @param filterTableName 只过滤某个表的，目前无数据库过滤
     * @return
     */
    public String writeRowsSql(Event event, String filterTableName) {
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
        if (StringUtils.hasText(filterTableName) && !filterTableName.equalsIgnoreCase(tableName)) {
            return null;
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
                        String value = null;
                        try {
                            value = new String(bytes, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (value == null) {
                            stringBuilder.append("null");
                        } else {
                            stringBuilder.append("'").append(value).append("'");
                        }
                    } else {
                        stringBuilder.append(column == null ? null : column.toString());
                    }
                    stringBuilder.append(",");
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                builder.append(stringBuilder).append("),");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(";");
            return builder.toString();
        }
        return null;
    }

    /**
     * 构建删除语句
     * @param event
     * @param filterTableName
     * @return
     */
    public String deleteSql(Event event, String filterTableName) {
        EventData eventData = event.getData();
        DeleteRowsEventData deleteEventData;
        if (eventData instanceof EventDeserializer.EventDataWrapper) {
            deleteEventData = (DeleteRowsEventData) ((EventDeserializer.EventDataWrapper) eventData).getInternal();
        } else {
            deleteEventData = (DeleteRowsEventData) eventData;
        }
        Map<String, Object> tableInfoMap = tableMap.get(deleteEventData.getTableId());
        String tableName = (String) tableInfoMap.get("table");
        if (StringUtils.hasText(filterTableName) && !filterTableName.equalsIgnoreCase(tableName)) {
            return null;
        }
        System.out.println(deleteEventData.toString());


        return null;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public String format(Date date) {
        return sdf.format(date);
    }

    public static void main(String[] args) throws IOException {
        //String filterTableName = "t_diag_reportInfo";
        String filterTableName = "t_openaccess_sign";
        BinLogParser parser = new BinLogParser(filterTableName);
        File binlogFile = new File("F:/test/binlog/mysql-bin.000056");
        //File binlogFile = new File("F:/test/binlog/mysql-bin.000001");
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setChecksumType(ChecksumType.CRC32);
        BinaryLogFileReader reader = new BinaryLogFileReader(binlogFile, eventDeserializer);

        try {
            for (Event event; (event = reader.readEvent()) != null; ) {
                EventHeader eventHeader = event.getHeader();
                EventType eventType = eventHeader.getEventType();
                if (EventType.isWrite(eventType)) { //插入
                    String sql = parser.writeRowsSql(event, parser.getFilterTableName());
                    if (StringUtils.hasText(sql)) {
                        //System.out.println(sql);
                    }
                }else if (eventType == EventType.TABLE_MAP) {
                    parser.parseTableMap(event);
                } else if (EventType.isDelete(eventType)) { //删除
                    String sql = parser.deleteSql(event, parser.getFilterTableName());
                }
            }
        } catch (Exception e) {

        }
    }



}
