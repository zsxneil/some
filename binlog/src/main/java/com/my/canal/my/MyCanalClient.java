package com.my.canal.my;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyCanalClient {

    private final static Logger logger = LoggerFactory.getLogger(MyCanalClient.class);
    private static final String SEP = SystemUtils.LINE_SEPARATOR;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private volatile boolean running = false;

    private String destination;
    private CanalConnector connector;
    private static String context_format = null;
    private static String row_format = null;
    private static String transaction_format = null;
    private Thread thread = null;

    protected Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread t, Throwable e) {
            logger.error("parse events has an error", e);
        }
    };

    static {
        context_format = SEP + "****************************************************" + SEP;
        context_format += "* Batch Id: [{}] ,count : [{}] , memsize : [{}] , Time : {}" + SEP;
        context_format += "* Start : [{}] " + SEP;
        context_format += "* End : [{}] " + SEP;
        context_format += "****************************************************" + SEP;

        row_format = SEP
                + "----------------> binlog[{}:{}] , name[{},{}] , eventType : {} , executeTime : {}({}) , gtid : ({}) , delay : {} ms"
                + SEP;

        transaction_format = SEP
                + "================> binlog[{}:{}] , executeTime : {}({}) , gtid : ({}) , delay : {}ms"
                + SEP;

    }

    public MyCanalClient(String destination) {
        this.destination = destination;
    }

    public void setConnector(CanalConnector connector) {
        this.connector = connector;
    }

    public void start() {
        Assert.notNull(connector, "connector is null");
        thread = new Thread(() -> {
            process();
        });
        thread.setUncaughtExceptionHandler(handler);
        this.running = true;
        thread.start();
    }

    public void stop() {
        if (!running) {
            return;
        }
        connector.stopRunning();
        running = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
        MDC.remove("destination");
    }

    private void process() {
        int batchSize = 5 * 1024;
        while (running) {
            try {
                MDC.put("destination", destination);
                connector.connect();
                connector.subscribe();
                while (running) {
                    Message message = connector.getWithoutAck(batchSize);
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) { //如果没有数据，等1秒钟后再去请求数据
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } else {
                        printSummary(message, batchId, size);
                        printEntry(message.getEntries());
                    }
                    connector.ack(batchId);
                }

            } catch (Exception e) {
                logger.error("process error!", e);
            } finally {
                connector.disconnect();
                MDC.remove("destination");
            }
        }
    }

    private void printEntry(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            long executeTime = entry.getHeader().getExecuteTime();
            long delayTime = new Date().getTime() - executeTime;
            Date date = new Date(executeTime);
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);

            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                switch (entry.getEntryType()) {
                    case TRANSACTIONBEGIN:
                        CanalEntry.TransactionBegin begin = null;
                        try {
                            begin = CanalEntry.TransactionBegin.parseFrom(entry.getStoreValue());
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException("parse event has an error, data:" + entry.toString(), e);
                        }
                        //打印事务头信息，执行的线程id，事务耗时
                        logger.info(transaction_format,
                                new Object[] {entry.getHeader().getLogfileName(),
                                        String.valueOf(entry.getHeader().getLogfileOffset()),
                                        String.valueOf(executeTime),
                                        format.format(date),
                                        entry.getHeader().getGtid(),
                                        String.valueOf(delayTime)
                        });
                        logger.info("BEGIN ----> Thread id:{}", begin.getThreadId());
                        printXAInfo(begin.getPropsList());
                        break;
                    case TRANSACTIONEND:
                        CanalEntry.TransactionEnd end = null;
                        try {
                            end = CanalEntry.TransactionEnd.parseFrom(entry.getStoreValue());
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException("parse event has an error, data:" + entry.toString(), e);
                        }
                        // 打印事务提交信息，事务id
                        logger.info("----------------\n");
                        logger.info(" END ----> transaction id: {}", end.getTransactionId());
                        printXAInfo(end.getPropsList());
                        logger.info(transaction_format,
                                new Object[] { entry.getHeader().getLogfileName(),
                                        String.valueOf(entry.getHeader().getLogfileOffset()),
                                        String.valueOf(entry.getHeader().getExecuteTime()), format.format(date),
                                        entry.getHeader().getGtid(), String.valueOf(delayTime) });

                        break;
                }
                continue;
            }

            if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                CanalEntry.RowChange rowChange = null;
                try {
                    rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException("parse event has an error, data:" + entry.toString(), e);
                }
                CanalEntry.EventType eventType = rowChange.getEventType();
                logger.info(row_format,
                        new Object[] {
                                entry.getHeader().getLogfileName(),
                                String.valueOf(entry.getHeader().getLogfileOffset()),
                                entry.getHeader().getSchemaName(),
                                entry.getHeader().getTableName(),
                                eventType,
                                String.valueOf(executeTime),
                                format.format(date),
                                entry.getHeader().getGtid(),
                                String.valueOf(delayTime)
                        });

                if (eventType == CanalEntry.EventType.QUERY || rowChange.getIsDdl()) {
                    logger.info("sql -----> " + rowChange.getSql() + SEP);
                    continue;
                }

                printXAInfo(rowChange.getPropsList());
                for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                    if (eventType == CanalEntry.EventType.DELETE) {
                        printColumn(rowData.getBeforeColumnsList());
                    } else if (eventType == CanalEntry.EventType.INSERT) {
                        printColumn(rowData.getAfterColumnsList());
                    } else {
                        printColumn(rowData.getAfterColumnsList());
                    }
                }
            }

        }
    }

    private void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            StringBuilder builder = new StringBuilder();
            builder.append(column.getName()).append(" : ");
            try {
                if (StringUtils.containsIgnoreCase(column.getMysqlType(), "BLOB") ||
                        StringUtils.containsIgnoreCase(column.getMysqlType(), "BINARY")) {
                    //logger.info("if is utf-8:" + column.getValueBytes().isValidUtf8() + ";" + column.getValueBytes().toStringUtf8());
                    builder.append(new String(column.getValue().getBytes("ISO-8859-1"), "UTF-8"));
                    builder.append("utf8true:").append(column.getValueBytes().isValidUtf8());
                } else {
                    builder.append(column.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.append("    type=").append(column.getMysqlType());
            if (column.getUpdated()) {
                builder.append("    update=").append(column.getUpdated());
            }
            builder.append(SEP);
            logger.info(builder.toString());
        }
    }

    private void printXAInfo(List<CanalEntry.Pair> pairs) {
        if (pairs == null)
            return;

        String xaType = null;
        String xaXid = null;
        for (CanalEntry.Pair pair : pairs) {
            String key = pair.getKey();
            if (StringUtils.endsWithIgnoreCase(key, "XA_TYPE")) {
                xaType = pair.getKey();
            } else if (StringUtils.endsWithIgnoreCase(key, "XA_XID")) {
                xaXid = pair.getValue();
            }
            if (xaType != null && xaXid != null) {
                logger.info(" -----> {} {}", xaType, xaXid);
                break;
            }
        }

    }

    private void printSummary(Message message, long batchId, int size) {
        long messageSize = 0;
        List<CanalEntry.Entry> entryList = message.getEntries();
        for (CanalEntry.Entry entry : entryList) {
            messageSize += entry.getHeader().getEventLength();
        }

        String startPos = null;
        String endPos = null;
        if (!CollectionUtils.isEmpty(entryList)) {
            startPos = buildPositionForDump(entryList.get(0));
            endPos = buildPositionForDump(entryList.get(entryList.size() - 1));
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        logger.info(context_format, new Object[] { batchId, size, messageSize, format.format(new Date()), startPos,
                endPos });

    }

    private String buildPositionForDump(CanalEntry.Entry entry) {
        long time = entry.getHeader().getExecuteTime();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String position = entry.getHeader().getLogfileName() + ":" + entry.getHeader().getLogfileOffset()
                + ":" + entry.getHeader().getExecuteTime() + "(" + format.format(date) + ")";
        if (StringUtils.isNotEmpty(entry.getHeader().getGtid())) {
            position += " gtid(" + entry.getHeader().getGtid() + ")";
        }
        return position;
    }


}
