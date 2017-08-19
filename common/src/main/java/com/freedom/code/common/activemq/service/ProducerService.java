package com.freedom.code.common.activemq.service;

import javax.jms.Destination;

/**
 * 消息生产者接口，发送消息
 * @author luo
 *
 */
public interface ProducerService {
	public void sendMessage(Destination destination, final String msg);
	public void sendMessage(final String msg);
}
