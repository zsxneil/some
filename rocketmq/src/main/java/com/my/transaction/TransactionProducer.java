package com.my.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.Date;


public class TransactionProducer {
    public static void main(String[] args) throws MQClientException {
        TransactionCheckListener transactionCheckListener = new TransactionCheckListenerImpl();
        TransactionMQProducer producer = new TransactionMQProducer("transactionProduceGroup");
        producer.setNamesrvAddr("172.20.69.31:9876");
        producer.setTransactionCheckListener(transactionCheckListener);//似乎无效
        producer.start();

        TransactionExecuterImpl transactionExecuter = new TransactionExecuterImpl();
        try {
            Message msg = new Message("TransactionTopic", "Tag", "KEY1", "Hello RocketMQ 1".getBytes("UTF-8"));
            Message msg2 = new Message("TransactionTopic", "Tag", "KEY1", "Hello RocketMQ 2".getBytes("UTF-8"));
            SendResult sendResult = producer.sendMessageInTransaction(msg, transactionExecuter, null);
            System.out.println(new Date() + "msg1" + sendResult);

            SendResult sendResult2 = producer.sendMessageInTransaction(msg2, transactionExecuter, null);
            System.out.println(new Date() + "msg2" + sendResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        producer.shutdown();


    }
}
