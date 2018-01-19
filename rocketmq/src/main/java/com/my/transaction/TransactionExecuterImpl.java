package com.my.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;

import java.util.Random;

public class TransactionExecuterImpl implements LocalTransactionExecuter {
    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
        try {
            /**
             * DB操作，应该带上事务 service->dao
             * 如果数据库操作失败，则需要回滚，同时返回RMQ一个失败消息，意味着消费者将无法消费到这条执行失败的消息
             * 如果成功，需要告诉RMQ一个成功的消息，意味着消费者将读取到消息
             * arg就是attachment
             */
            if (new Random().nextInt(3) == 2) {
                int a = 1 / 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("本地事务执行失败");
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        System.out.println("本地事务执行成功，发送确认消息");
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
