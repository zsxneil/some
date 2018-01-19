package com.my.simple;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

public class Producer {
    public static void main(String[] args) throws MQClientException, InterruptedException, UnsupportedEncodingException, RemotingException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroup");
        producer.setNamesrvAddr("172.20.69.31:9876");
        producer.setRetryTimesWhenSendFailed(3);//失败重试次数
        producer.start();
        System.out.println("started...");
        for (int i = 0; i < 100; i++) {

            Message message = new Message("topicFirst", "TagFirst", ("Hello RocketMQ " + i).getBytes("utf-8"));
            SendResult sendResult = producer.send(message, 1000);//1秒钟返回，认为失败
            System.out.println(sendResult);

        }
        producer.shutdown();
    }
}
