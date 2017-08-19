package com.freedom.code.common.activemq.service.impl;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.freedom.code.common.activemq.service.ProducerService;
/**
 * 消息生产者实现类
 * @author luo
 *
 */

@Service
public class ProducerServiceImpl implements ProducerService{

	@Resource
    private JmsTemplate jmsTemplate;
	
	@Override
	public void sendMessage(Destination destination, String msg) {
		System.out.println("向队列" + destination.toString() + "发送了消息------------" + msg);
        jmsTemplate.send(destination, new MessageCreator() {
          public Message createMessage(Session session) throws JMSException {
            return session.createTextMessage(msg);
          }
        });
	}

	@Override
	public void sendMessage(String msg) {
		String destination =  jmsTemplate.getDefaultDestination().toString();
        System.out.println("向队列" +destination+ "发送了消息------------" + msg);
        jmsTemplate.send(new MessageCreator() {
          public Message createMessage(Session session) throws JMSException {
            return session.createTextMessage(msg);
          }
        });
	}

}
