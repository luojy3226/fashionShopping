package com.freedom.code.common.activemq.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 队列消息监听器，继承消息监听器,也是消息消费者
 * @author luo
 *
 */
public class QueueMessageListener implements MessageListener{

	//这里只是简单读取打印，没做详细操作
	@Override
	public void onMessage(Message message) {
		TextMessage tm = (TextMessage) message;
        try {
            System.out.println("QueueMessageListener监听到了文本消息：\t"
                    + tm.getText());
            //do something ...
        } catch (JMSException e) {
            e.printStackTrace();
        }
	}

}
