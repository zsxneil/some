package com.my.canal.my;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class StartMain {

    private static final Logger logger = LoggerFactory.getLogger(StartMain.class);

    public static void main(String[] args) {
        String destination = "example";
        String ip = "172.20.177.47";
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(ip, 11111),
                destination,
                "root",
                "123456");
        MyCanalClient canalClient = new MyCanalClient(destination);
        canalClient.setConnector(connector);
        canalClient.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    logger.info("## stop the canal client");
                    canalClient.stop();
                } catch (Throwable e) {
                    logger.warn("##something goes wrong when stopping canal:", e);
                } finally {
                    logger.info("## canal client is down.");
                }
            }
        });
    }

}
