package com.freedom.code.common.activemq.service;

import javax.jms.Destination;
import javax.jms.TextMessage;

/**
 * 消息消费者，读取消费消息
 * @author luo
 *
 */
public interface ConsumerService {
	public TextMessage receive(Destination destination);
}
