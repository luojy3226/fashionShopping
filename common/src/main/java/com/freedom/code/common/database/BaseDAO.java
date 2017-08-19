package com.freedom.code.common.database;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freedom.code.common.exception.DaoException;

/**
 * dao层基础类，所有dao都继承
 * @author luo
 *
 * @param <T> 操作实体对象pojo	
 * @param <K> 主键key的类型
 * @param <M> 操作mapper接口类
 * @param <E> mybatis生成example实体类pojo
 */
public abstract class BaseDAO<T, K, M, E> {
	private static final Logger log = LoggerFactory.getLogger(BaseDAO.class);
	
	/**
	 * 抽象方法，获取mapper接口的引用
	 * @return
	 */
	public abstract M getMapper();
	
	/**
	 * 根据key查询
	 * @param key
	 * @return
	 * @throws DaoException
	 */
	public T getByKey(K key) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("selectByPrimaryKey", key.getClass());
			Object o = method.invoke(mapper, key);
			return o!=null ? (T)o:null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	/**
	 * 根据example来查询
	 * @param example
	 * @return
	 * @throws DaoException
	 */
	public List<T> list(E example) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("selectByExample", example.getClass());
			Object o = method.invoke(mapper, example);
			return o!=null ? (List<T>)o:null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public T getByExample(E example) throws DaoException{
		List<T> list = list(example);
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 有查询工具结合example查询
	 * @param query
	 * @param exampleClass
	 * @return
	 * @throws DaoException
	 */
	public List<T> list(Query query, Class<E> exampleClass) throws DaoException{
		try {
			E example = query.createExample(query, exampleClass);
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method selectMethod = clazz.getDeclaredMethod("selectByExample", exampleClass);

			//检查SqlTools是否包含了totalCounts,可以避免每次都执行一次,在大数据集的时候可以加快速度
	        boolean needCount = (query.getTotalCounts() <= 0);
	        // 如果是pageSize=0 的话，则是所有数据，没有必要做一次查询
	        if (query.getPageSize() == 0)
	            needCount = false;

	        if (needCount) {
	        	Method countMethod = clazz.getDeclaredMethod("countByExample", exampleClass);
	        	int totalCounts = (Integer) countMethod.invoke(mapper, example);
	            log.debug("Total Counts=" + totalCounts);
	            query.setTotalCounts(totalCounts);
//	        	LOG.debug("Total Counts=" + totalCounts);
//	            query.setTotalCounts(totalCounts);//设置总数
	        }

	        if (query.getPageNo() * query.getPageSize() > query.getTotalCounts()) {
	        	log.debug("exceed all page data.pageNo="
	                            + query.getPageNo() + " pageSize="
	                            + query.getPageSize() + " reset pageNo=0");
	            query.setPageNo(0);
	        }
	        //如果需要分页
	        if (query.isNeedPage()) {
	        	log.debug("set page info. PageNo=" + query.getPageNo()
	                    + " pageSize=" + query.getPageSize());

	            Method limitStartMethod = exampleClass.getDeclaredMethod("setLimitStart", int.class);
	            limitStartMethod.invoke(example, query.getPageNo() * query.getPageSize());
	            Method limitEndMethod = exampleClass.getDeclaredMethod("setLimitEnd", int.class);
	            limitEndMethod.invoke(example, query.getPageSize());
	        }

			Object o = selectMethod.invoke(mapper, example);

			List<T> result = o!=null ? (List<T>)o:null;

			//如果是pageSize=0 的话，则是所有数据，没有必要做一次查询
	        if (query.getPageSize() == 0 && result!=null) {
	        	query.setTotalCounts(result.size());
	        }

			return result;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public List<T> listWithBLOBs(E example) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("selectByExampleWithBLOBs", example.getClass());
			Object o = method.invoke(mapper, example);
			return o!=null ? (List<T>)o:null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public int count(E example) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("countByExample", example.getClass());
			Object o = method.invoke(mapper, example);
			return o!=null ? (int)o:0;
		} catch (NoSuchMethodException e) {
			return 0;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public int insert(T entity) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("insertSelective", entity.getClass());
			Object o = method.invoke(mapper, entity);
			return o!=null ? (int)o:0;
		} catch (NoSuchMethodException e) {

		} catch (Exception e) {
			throw new DaoException(e);
		}
        return 0;
	}
	
	public int deleteByKey(K key) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("deleteByPrimaryKey", key.getClass());
			Object o = method.invoke(mapper, key);
			return o!=null ? (int)o:0;
		} catch (NoSuchMethodException e) {

		} catch (Exception e) {
			throw new DaoException(e);
		}
        return 0;
	}
	
	public int delete(E example) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("deleteByExample", example.getClass());
			Object o = method.invoke(mapper, example);
            return o!=null ? (int)o:0;
		} catch (NoSuchMethodException e) {

		} catch (Exception e) {
			throw new DaoException(e);
		}
        return 0;
	}
	
	public int update(T entity) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("updateByPrimaryKeySelective", entity.getClass());
			Object o = method.invoke(mapper, entity);
			return o!=null ? (int)o:0;
		} catch (NoSuchMethodException e) {

		} catch (Exception e) {
			throw new DaoException(e);
		}
		
		return 0;
	}
	
	public int update(T entity, E example) throws DaoException{
		try {
			M mapper = getMapper();
			Class clazz = mapper.getClass();
			Method method = clazz.getDeclaredMethod("updateByExampleSelective", entity.getClass(), example.getClass());
			Object o = method.invoke(mapper, entity, example);
			return o!=null ? (int)o:0;
		} catch (NoSuchMethodException e) {

		} catch (Exception e) {
			throw new DaoException(e);
		}
		
		return 0;
	}
}
