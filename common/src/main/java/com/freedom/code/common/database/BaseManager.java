package com.freedom.code.common.database;
/**
 * manager基础类，所有manager都要继承
 * @author luo
 *
 * @param <D> 对应相对的dao接口
 */
public abstract class BaseManager<D> {
	public abstract D getDao();
}
