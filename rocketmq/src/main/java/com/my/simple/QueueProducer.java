package com.my.simple;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class QueueProducer {

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("OrderProducer");
        producer.setRetryTimesWhenSendFailed(3);
        producer.setNamesrvAddr("172.20.69.31:9876");
        producer.start();
        String[] tags = new String[]{"createTag", "pagTag", "sendTag"};

        for (int orderId = 0; orderId < 10; orderId++) { //订单消息
            for (int type = 0; type < 3; type++) { //每种订单包括 创建订单消息/支付点单/发货订单
                Message msg = new Message("OrderTopic",
                        tags[type % tags.length],
                        orderId + ":" + type,
                        (orderId + ":" + type).getBytes("utf-8"));
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId);
                System.out.println(sendResult);
            }
            
        }

        producer.shutdown();
    }
}
