package com.my;

import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.deserialization.ChecksumType;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;

import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        File binlogFile = new File("F:/test/binlog/mysql-bin.000001");
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setChecksumType(ChecksumType.CRC32);
        BinaryLogFileReader reader = new BinaryLogFileReader(binlogFile, eventDeserializer);

        try {
            for (Event event; (event=reader.readEvent()) != null;) {
                System.out.println("dataLength:" + event.getHeader().getDataLength());
                System.out.println("eventType:" + event.getHeader().getEventType());
                System.out.println("headerLength:" + event.getHeader().getHeaderLength());
                System.out.println("serverId:" + event.getHeader().getServerId());
                System.out.println("timestamp:" + event.getHeader().getTimestamp());
                System.out.println("data:" + event.getData());
                System.out.println("****************************************************");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }

    }
}
