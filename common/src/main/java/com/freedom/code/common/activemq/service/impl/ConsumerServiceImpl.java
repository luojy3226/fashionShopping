package com.freedom.code.common.activemq.service.impl;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.freedom.code.common.activemq.service.ConsumerService;

/**
 * 消息消费者实现类
 * @author luo
 *
 */

@Service
public class ConsumerServiceImpl implements ConsumerService {

	@Resource(name="jmsTemplate")
    private JmsTemplate jmsTemplate;
	
	@Override
	public TextMessage receive(Destination destination) {
		TextMessage tm = (TextMessage) jmsTemplate.receive(destination);
        try {
            System.out.println("从队列" + destination.toString() + "收到了消息：\t"
                    + tm.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        
        return tm;
	}

}
