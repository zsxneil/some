package com.my.canal.sync2redis;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class Sync2RedisMain {

    private static final Logger log = LoggerFactory.getLogger(Sync2RedisMain.class);

    public static void main(String[] args) {


        String destination = "example";
        String ip = "172.20.177.47";
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(ip, 11111),
                destination,
                "root",
                "123456");

        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe();
            while (true) {
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                if (batchId == -1 || message.getEntries().size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                } else {
                    printEntry(message.getEntries());
                }
                connector.ack(batchId);
            }
        } finally {
            connector.disconnect();
        }
    }

    private static void printEntry(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN ||
                    entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange = null;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();
            log.info("============> binlog[{}:{}], name[{},{}], eventType : {}",
                    entry.getHeader().getLogfileName(),
                    entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(),
                    entry.getHeader().getTableName(),
                    eventType);

            List<CanalEntry.RowData> rowDatas = rowChange.getRowDatasList();
            for (CanalEntry.RowData rowData : rowDatas) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    redisDelete(rowData.getBeforeColumnsList(), entry.getHeader().getSchemaName(), entry.getHeader().getTableName());
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    redisInsert(rowData.getAfterColumnsList(), entry.getHeader().getSchemaName(), entry.getHeader().getTableName());
                } else {
                    log.info("-------> before");
                    printColumn(rowData.getBeforeColumnsList());
                    log.info("-------> before <--------");
                    redisInsert(rowData.getAfterColumnsList(), entry.getHeader().getSchemaName(), entry.getHeader().getTableName()); //redis更新逻辑和插入逻辑一致
                    log.info("-------> after");
                    printColumn(rowData.getAfterColumnsList());
                    log.info("-------> after <--------");
                }
            }
        }
    }

    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            //rowData.getBeforeColumnsList()的column.getUpdated()全部为false;rowData.getAfterColumnsList()的column.getUpdated()才是实际是否有更改的标识
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }


    private static void redisInsert(List<CanalEntry.Column> columns, String schemaName, String tableName) {
        if (columns.size() > 0) {
            JSONObject json = new JSONObject();
            for (CanalEntry.Column column : columns) {
                //此处应根据字段类型处理，如int型的转化为数字，日期型的转化为标准格式字符串。方便后续json转对象
                json.put(column.getName(), column.getValue());
            }
            RedisUtil.stringSet(schemaName + ":" + tableName + ":" + columns.get(0).getValue(), json.toJSONString());
        }
    }

    private static void redisDelete(List<CanalEntry.Column> columns, String schemaName, String tableName) {
        if (columns.size() > 0) {
            RedisUtil.delKey(schemaName + ":" + tableName + ":" + columns.get(0).getValue());
        }
    }

}
