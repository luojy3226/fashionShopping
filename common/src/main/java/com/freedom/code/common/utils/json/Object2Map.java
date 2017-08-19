package com.freedom.code.common.utils.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

/**
 * 对象和map转换类
 * @author luo
 *
 */
public class Object2Map {
	
	/**
	 * 存入map集合
	 * @param obj
	 * @param map
	 * @return
	 */
	public static Map<String, Object> put(final Object obj, final Map<String, Object> map) {
		//field 类，java反射机制，通过其获取类的属性类型
		for(final Field field : obj.getClass().getDeclaredFields()) {
			//判断obj的属性类型
			if(Modifier.isStatic(field.getModifiers()) || Modifier.isNative(field.getModifiers()) 
					|| Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			field.setAccessible(true);//设置访问权
			try {
				map.put(keyName(field.getName()), field.get(obj));//将对象及其属性名放入map
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		//遍历对象的父类，将其父对象的属性名称存入map
		for(final Field field : obj.getClass().getSuperclass().getDeclaredFields()) {
			if(Modifier.isStatic(field.getModifiers()) || Modifier.isNative(field.getModifiers()) 
					|| Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			field.setAccessible(true);
			try {
				map.put(keyName(field.getName()), field.get(obj));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	/**
	 * 重构put方法
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> put(final Object obj) {
		if (obj == null) {
			return new HashMap<String, Object>();
		}
		return put(obj, new HashMap<String, Object>());
	}
	
	/**
	 * 将非空属性存入map集合
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> putSelect(final Object obj) {
		 Map map  = new HashMap<String, Object>();
		 //根据反射机制获取对象的属性
		for(final Field field : obj.getClass().getDeclaredFields()) {
			if(Modifier.isStatic(field.getModifiers()) || Modifier.isNative(field.getModifiers()) 
					|| Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			field.setAccessible(true);//设置属性访问权
			try {
				if(field.get(obj)!=null){
				map.put(keyName(field.getName()), field.get(obj));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 * 将list转为list<map<,>>集合
	 * @param list
	 * @return
	 */
	public static List<Map<String, Object>> list2MapList(final List<?> list) {
		if(!CollectionUtils.isEmpty(list)){
			final ArrayList<Map<String, Object>> ret = new ArrayList<>(list.size());
			//遍历list集合，将集合对象存入map中，再将map存入list
			for(final Object obj : list) {
				ret.add(put(obj));
			}
			return ret;
		}
		return new ArrayList<>(0);
	}
	
	/**
	 * List中对象的某一属性为键值，构造Map，对象存入值中 返回map
	 * @param list
	 * @param fieldName
	 * @param clazz
	 * @return
	 */
	public static <T, C> Map<C, T> list2Map(final List<T> list, final String fieldName, Class<C> clazz ) {
		if(!CollectionUtils.isEmpty(list)){
			final Map<C, T> ret = new HashMap<>(list.size());
			//遍历list集合
			for(final T obj : list) {
				//遍历list的单个对象的属性
				for(final Field field : obj.getClass().getDeclaredFields()) {
					if(Modifier.isStatic(field.getModifiers()) || Modifier.isNative(field.getModifiers()) 
							|| Modifier.isTransient(field.getModifiers())) {
						continue;
					}
					field.setAccessible(true);//设置属性访问权
					if(fieldName.equals(field.getName())){//如果对象的属性名和指定名称相同，则存入结果集返回
						try {
							ret.put((C)field.get(obj), obj);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						break;
					}
				}
				//遍历list集合单个对象的父对象属性
				for(final Field field : obj.getClass().getSuperclass().getDeclaredFields()) {
					if(Modifier.isStatic(field.getModifiers()) || Modifier.isNative(field.getModifiers()) 
							|| Modifier.isTransient(field.getModifiers())) {
						continue;
					}
					field.setAccessible(true);//设置属性访问权
					if(fieldName.equals(field.getName())){//如果对象的属性名和指定名称相同，则存入结果集返回
						try {
							ret.put((C)field.get(obj), obj);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
			return ret;
		}
		return new HashMap<>(0);
	}
	
	/**
	 * List中取某一属性，构造新List，包括了所选择的属性
	 * @param list
	 * @param fieldName
	 * @param clazz
	 * @return
	 */
	public static <T,C> List<C> list2list(final List<T> list, final String fieldName, Class<C> clazz ) {
		if(!CollectionUtils.isEmpty(list)){
			final List<C> ret = new ArrayList<>(list.size());
			//遍历list
			for(final T obj : list) {
				//遍历list的单个对象的属性
				for(final Field field : obj.getClass().getDeclaredFields()) {
					if(Modifier.isStatic(field.getModifiers()) || Modifier.isNative(field.getModifiers()) 
							|| Modifier.isTransient(field.getModifiers())) {
						continue;
					}
					field.setAccessible(true);//设置属性访问权
					if(fieldName.equals(field.getName())){//如果属性名称和指定名称相同，则加入新list集合
						try {
							if(!ret.contains((C)field.get(obj))){
								ret.add((C)field.get(obj));
							}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						break;
					}
				}
				//遍历list的单个对象的父对象的属性
				for(final Field field : obj.getClass().getSuperclass().getDeclaredFields()) {
					if(Modifier.isStatic(field.getModifiers()) || Modifier.isNative(field.getModifiers()) 
							|| Modifier.isTransient(field.getModifiers())) {
						continue;
					}
					field.setAccessible(true);//设置属性访问权限
					if(fieldName.equals(field.getName())){//如果属性名和指定名称相等，则加入新list集合
						try {
							if(!ret.contains((C)field.get(obj))){
								ret.add((C)field.get(obj));
							}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
			return ret;
		}
		return new ArrayList<>(0);
	}
	
	/**
	 * int转为string
	 * @param map
	 * @return
	 */
	public static Map<String, Object> int2str(final Map<String, Object> map) {
		//遍历集合，如果map集合的值可以转为integer,则将其转为string类型
		for(final Map.Entry<String, Object> entry : map.entrySet()) {
			if(entry.getValue() instanceof Integer) {
				entry.setValue(entry.getValue().toString());
			}
		}
		return map;
	}
	
	/**
	 * 替换map中的key名字
	 * @param target
	 * @param regex 替换的目标
	 * @param replacement 替换的值
	 * @return
	 */
	public static List<Map<String, Object>> replaceMapKey(List<Map<String, Object>> target, String regex, String replacement){
		if(!CollectionUtils.isEmpty(target)){
			//遍历list集合，替换要替换的值的key
			for(Map<String, Object> m : target){
				if(m.containsKey(regex) && !m.containsKey(replacement)){
					m.put(replacement, m.get(regex));
					m.remove(regex);
				}
			}
		}
		return target;
	}
	
	/**
	 * 把list<T>  => Map<C,List<T>>
	 * @param list
	 * @param fieldName
	 * @param clazz
	 * @return
	 */
	public static <T, C> Map<C,List<T>> list2MapList(final List<T> list, final String fieldName, Class<C> clazz ) {
		Map<C,List<T>> returnMap =  new HashMap<C,List<T>>();
		//遍历list集合
		for(T t:list){
			//遍历list单个对象的属性
			for(final Field field : t.getClass().getDeclaredFields()) {
				if(Modifier.isStatic(field.getModifiers()) || Modifier.isNative(field.getModifiers()) 
						|| Modifier.isTransient(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);//设置访问权限
				if(fieldName.equals(field.getName())){
					try {
						C key  = (C)field.get(t);//将list的单个对象强制转为指定类型
						if(returnMap.get(key)==null){//如果结果集中没有该对象的值，则增加
							List<T> listn = new ArrayList<T>();
							listn.add(t);
							returnMap.put((C)field.get(t), listn);
						}else{//结果集中有对象，则替换该对象
							List<T> listp = returnMap.get(key);
							listp.add(t);
							returnMap.put((C)field.get(t), listp);
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
		return returnMap;
	}
	
	/**
	 * 通过key生成keyName,对大写字母改为  "_小写字母"的形式
	 * @param key
	 * @return
	 */
	public static String keyName(final String key) {
		final StringBuilder sb = new StringBuilder();
		//遍历字符串
		for(int i = 0; i < key.length(); i++) {
			final char c = key.charAt(i);//返回指定下标的字符
			if(Character.isUpperCase(c)) {
				sb.append('_').append((char)(c+'a'-'A'));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	
	public static void main(String[] args) {
		System.out.println(keyName("AAA"));
	}
	
}
