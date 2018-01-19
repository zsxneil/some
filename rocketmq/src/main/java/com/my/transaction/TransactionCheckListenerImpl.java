package com.my.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Random;

/**
 * 未决事务，服务器回查客户端
 */
public class TransactionCheckListenerImpl implements TransactionCheckListener {
    @Override
    public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
        System.out.println("server checking TrMsg " + msg.toString());
        //由于RMQ迟迟没有收到消息的确认消息，因此主动询问这条prepare消息是否正常
        //可以查询数据库看这条消息是否已经处理
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
