package com.freedom.code.common.utils.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;

/**
 * json转换工具
 * @author luo
 *
 */
public class JsonUtils {
	//打印日志
	private static Logger log = Logger.getLogger(JsonUtils.class);
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 创建json转换过程中出现exception的提示信息
	 * @param json
	 * @return
	 */
	private static String throwMessageWhileParseJson(String json) {
		return new StringBuilder().append("Error occurs while parsing json string ")
				.append(json).toString();
	}
	
	
	/**
	 * 将json字符串转为map集合
	 * @param json
	 * @return
	 */
	public static Map<String,Object> convertJsonToMap(String json){
		Map<String,Object> result = new HashMap<String,Object>(0);
		if(StringUtils.isBlank(json)){//如果json是空字符串则结束
			return result;
		}
		try {
			result = objectMapper.readValue(json,Map.class);
		} catch (JsonParseException e) {
			log.warn(throwMessageWhileParseJson(json), e);
		} catch(JsonMappingException e){
			log.warn(throwMessageWhileParseJson(json), e);
		} catch(IOException e){
			log.warn(throwMessageWhileParseJson(json), e);
		} 
		return result;
	}
	
	/**
	 * 将json转为map with javabean
	 * @param json
	 * @return
	 */
	 public static <T> Map<String, T> convertJsonToMapBean(String jsonStr, Class<T> clazz)  
	            throws Exception {  
		 try {
			//将json字符串转为map
		     Map<String, Map<String, Object>> map = objectMapper.readValue(jsonStr,  
		         new TypeReference<Map<String, T>>() {});  
		               
		     Map<String, T> result = new HashMap<String, T>();  
		     //遍历转换的map，将单个map对象转为pojo
		     for (Entry<String, Map<String, Object>> entry : map.entrySet()) {  
		         result.put(entry.getKey(), convertMapToPojo(entry.getValue(), clazz));  
		     }  
		    return result;
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		 return null;
	  }  
	 
	 /**
	  * 将map转为pojo
	  * @param map
	  * @param clazz
	  * @return
	  */
	 public static <T> T convertMapToPojo(Map map, Class<T> clazz) {  
		 try {
			 return objectMapper.convertValue(map, clazz);  
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	        return null;
	 }  
	
	 /**
	  * 将json字符串转为list集合
	  * @param json
	  * @return
	  */
	 public static List<Object> convertJsonToList(String json){
			List<Object> result = new ArrayList<Object>(0);
			if (StringUtils.isBlank(json)) {
				return result;
			}
			try{
				result=objectMapper.readValue(json, List.class);
			}catch(Exception ex){
				log.warn(ex.getMessage());
			}
			return result;
	}
	 
	/**
	 * json转为通用list
	 * @param jsonArrayStr
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> convertJsontoTList(String jsonArrayStr, Class<T> clazz)  
	            throws Exception {  
		try {
			 List<Map<String, Object>> list = objectMapper.readValue(jsonArrayStr,  
		                new TypeReference<List<T>>() {});  
		                  
		     List<T> result = new ArrayList<T>();  
		     for (Map<String, Object> map : list) {  
		         result.add(convertMapToPojo(map, clazz));  
		     }  
		     return result; 
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	    return null;  
	} 
	
	/**
	 * 对象转为json 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String convertObjectToJson(Object obj){  
		try {
			return objectMapper.writeValueAsString(obj); 
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	    return null;  
	}  
	
	/**
	 * 将json字符串转为pojo
	 * @param jsonStr
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> T json2pojo(String jsonStr, Class<T> clazz){  
		try {
			return objectMapper.readValue(jsonStr, clazz);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
        return null;  
    }
}
