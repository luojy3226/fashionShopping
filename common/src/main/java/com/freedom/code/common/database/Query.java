package com.freedom.code.common.database;
/**
 * 自定义查询工具
 * @author luo
 *
 */

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freedom.code.common.ConvertUtils;
import com.freedom.code.common.ValidateUtils;
import com.freedom.code.common.utils.json.Object2Map;

public class Query {
	
	private static final Logger log = LoggerFactory.getLogger(Query.class);
	
	//============================第一部分=======================================================================
	/**
     * 本工具中用到的几个内部类和枚举 Filter,Order,FilterType,OrderType,ValueType
     */
	
	/**
     * 过滤类型
     * @author qgl
     *
     */
    public enum FilterType {
        LIKE, // 模糊查找
        EQUALS, // 等于
        GREATETHAN, // 大于
        GREATEEQUAL, // 大于等于
        LESSTHAN, // 小于
        LESSEQUAL, // 小于等于
        NOTEQUAL, // 不等于
        IN;

        public static FilterType toType(String type) {
            if ("1".equals(type)) {
                return LIKE;
            }
            if ("2".equals(type)) {
                return EQUALS;
            }
            if ("3".equals(type)) {
                return GREATETHAN;
            }
            if ("4".equals(type)) {
                return GREATEEQUAL;
            }
            if ("5".equals(type)) {
                return LESSTHAN;
            }
            if ("6".equals(type)) {
                return LESSEQUAL;
            }
            if ("7".equals(type)) {
                return NOTEQUAL;
            }
            if ("8".equals(type)) {
                return IN;
            }
            return null;
        }

        public String toString() {
            if (this == LIKE) {
                return "1";
            }
            if (this == EQUALS) {
                return "2";
            }
            if (this == GREATETHAN) {
                return "3";
            }
            if (this == GREATEEQUAL) {
                return "4";
            }
            if (this == LESSTHAN) {
                return "5";
            }
            if (this == LESSEQUAL) {
                return "6";
            }
            if (this == NOTEQUAL) {
                return "7";
            }
            if (this == IN) {
                return "8";
            }
            return "";
        }
    }
	
    /**
     * 排序类型
     * @author qgl
     *
     */
    public enum OrderType {

        ASC, DESC;

        public static OrderType toType(String type) {
            if ("0".equals(type)) {
                return ASC;
            }
            if ("1".equals(type)) {
                return DESC;
            }
            return null;
        }

        public String toString() {
            if (this == ASC) {
                return "0";
            }
            if (this == DESC) {
                return "1";
            }
            return "";
        }
    }
    
    /**
     * 值类型
     * @author qgl
     *
     */
    public enum ValueType {
        String, DateTime, Integer, Float, Boolean, Date, Long;
        public static ValueType toValueType(Object type) {
            if (type instanceof String) {
                return String;
            }
            if (type instanceof Timestamp) {
                return DateTime;
            }
            if (type instanceof Integer) {
                return Integer;
            }
            if (type instanceof Float) {
                return Float;
            }
            if (type instanceof Boolean) {
                return Boolean;
            }
            if (type instanceof Date) {
                return Date;
            }
            if (type instanceof Long) {
                return Long;
            }
            return null;
        }

        public static ValueType toType(String type) {
            if ("1".equals(type)) {
                return String;
            }
            if ("2".equals(type)) {
                return DateTime;
            }
            if ("3".equals(type)) {
                return Integer;
            }
            if ("4".equals(type)) {
                return Float;
            }
            if ("5".equals(type)) {
                return Boolean;
            }
            if ("6".equals(type)) {
                return Date;
            }
            if ("7".equals(type)) {
                return Long;
            }
            return null;
        }

        public String toString() {
            if (this == String) {
                return "1";
            }
            if (this == DateTime) {
                return "2";
            }
            if (this == Integer) {
                return "3";
            }
            if (this == Float) {
                return "4";
            }
            if (this == Boolean) {
                return "5";
            }
            if (this == Date) {
                return "6";
            }
            if (this == Long) {
                return "7";
            }
            return "1"; // 默认为String
        }

        public static Object convertValue(String value, ValueType type) {
            if (ValidateUtils.isNull(value)) {
                return null;
            }
            switch (type) {
            case String:
                return value;
            case Date:
                return toDate(value);
            case Boolean:
                return new Boolean(value);
            case Integer:
            	if(ValidateUtils.isInt(value)){
            		return new Integer(value);	
            	}else if(ValidateUtils.isLong(value)){
            		return new Long(value);
            	}
                return new Integer(value);
            case DateTime:
                return toDateTime(value);
            case Float:
                return new Float(value);
            case Long:
                return new Long(value);
            }
            return null;
        }
    }
    
    /**
     * 转换成系统date
     * @param arg
     * @return
     */
    public static Date toDate(String arg) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(arg);
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.parse(arg);
        } catch (ParseException e1) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(arg);
        } catch (ParseException e2) {

        }
        return new Date();
    }
    
    /**
     * 转换为数据库sqldate
     * @param arg
     * @return
     */
    public static Timestamp toDateTime(String arg) {
        Locale locale;
        Timestamp ret;
        locale = Locale.ENGLISH;
        if (arg == null || "".equals(arg))
            return null;
        ret = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                    locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e1) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e2) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e3) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss z yyyy", locale);
            return new Timestamp(sdf.parse(arg).getTime());
        } catch (Exception e4) {

        }
        throw new IllegalArgumentException("参数非法:" + arg);
    }
    
	/**
     * 过滤条件
     * 
     * @author qgl
     * 
     */
    public class Filter {
        public String     name;

        public FilterType type;

        public Object     value;

        public ValueType  valueType;

        public Filter(String name, FilterType type, Object value) {
            super();
            this.name = name;
            this.type = type;
            this.value = value;
            this.valueType = ValueType.toValueType(value);
        }

        public String toString() {
            String valueStr = value.toString();
            if (valueType == ValueType.Date) {
                valueStr = String.format("%1$tY-%1$tm-%1$td", value);
            }
            if (valueType == ValueType.DateTime) {
                valueStr = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS",
                        value);
            }
            if (valueType == ValueType.Float) {
                valueStr = String.format("%.2f", value);
            }
            return name + "," + type + "," + valueStr + "," + valueType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public FilterType getType() {
            return type;
        }

        public void setType(FilterType type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public ValueType getValueType() {
            return valueType;
        }

        public void setValueType(ValueType valueType) {
            this.valueType = valueType;
        }
    }
    
    /**
     * 排序条件
     * @author qgl
     *
     */
    public class Order {
        public String    name;

        public OrderType type;

        public Order(String name, OrderType type) {
            super();
            this.name = name;
            this.type = type;
        }

        public String toString() {
            return name + "," + type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public OrderType getType() {
            return type;
        }

        public void setType(OrderType type) {
            this.type = type;
        }
    }
    
    //================================第二部分================================================================
    /**
     * 本工具的几个属性 条件，排序，分页，子表
     */
    //列表页的分类，同一个模板使用在不同展示时候使用
    private int 			listType = 0;
    //针对模糊多条件       关键字可能表示名称或则ID时候使用
    private String			keyword;
    // --------查询信息
    private List<Filter> filters  = new ArrayList<Filter>();

    // --------排序信息
    private List<Order>  orders   = new ArrayList<Order>();

    //---------去掉重复的查询条件
    private Set<String> filterKeys = new HashSet<String>();
    private Set<String> filterNames = new HashSet<String>();

    // --------分组信息
    private String          groupBy;

    //------------分页信息
    private int             pageNo   = 0;                         // 页的序号

    private int             pageSize = 10;                        // 页的大小

    private int             totalCounts;                          // 总共记录数，用来回调

    //------------多表联合查询
    // private Set<String>     from;                                 // 定义 from 的条件

    //private String          select;                               // 定义结果的输出
    
    //private String          selectCount;                          // 如果自定义 select 的情况必须指定总数项

    //private String          where;                                //定义查询的条件

    public Query() {
        super();
    }
    
    //=======================================第三部分===========================================
    /**
     * 操作方法和属性方法
     * @return
     */
    
    //属性方法
    /**
     * 添加listType
     */
    public Query addListType(int listType) {
        this.listType = listType;
        return this;
    }
    
    /**
     * 添加keyword
     */
    public Query addKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }
    
    /**
     * 添加过滤条件
     * @param filterName 过滤条件字段名称
     * @param filterType 过滤条件类型
     * @param filterValue 过滤条件的值
     * @return
     * @throws RuntimeException
     */
    public Query addFilter(String filterName, FilterType filterType, Object filterValue) throws RuntimeException {
        if (ValidateUtils.isNull(filterName)
                || ValidateUtils.isNull(filterValue)) {
            if (log.isWarnEnabled()) {
                log.warn("invaild filterName ,filterValue. Name=" + filterName
                        + " value=" + filterValue);
            }
            throw new RuntimeException("invalid param");
        }
        // 1.只有字符串才能like
        if (!(filterValue instanceof String) && filterType == FilterType.LIKE) {
            throw new RuntimeException("only string type can do 'like' query");
        }
        // 2.只有list才能in
        if (!(filterValue instanceof List) && filterType == FilterType.IN) {
            throw new RuntimeException("only list type can do 'in' query");
        }
        log.debug("add a valid filter=" + filterName + ",filterType="
                + filterType + ",filterValue" + filterValue);

        Filter filter = new Filter(filterName, filterType, filterValue);

        if(filterKeys.contains(filter.toString())){
        	log.debug("filter is has="+filter.toString());
        }else{
        	filters.add(new Filter(filterName, filterType, filterValue));
        	filterKeys.add(filter.toString());
        	filterNames.add(filterName);
        }
        return this;
    }
    
    /**
     * 添加过滤条件
     * @param filterName  过滤条件字段名称
     * @param filterType   过滤条件类型
     * @param filterValue   过滤条件的值
     * @param valueType    过滤条件的值的类型
     * @return
     * @throws RuntimeException
     */
    public Query addFilter(String filterName, FilterType filterType, String filterValue, ValueType valueType) throws RuntimeException {
        if (ValidateUtils.isNull(valueType)) {
            valueType = ValueType.String; // 为了兼容以前程序
        }
        return addFilter(filterName, filterType, ValueType.convertValue(filterValue, valueType));
    }
    
    public int getListType() {
		return listType;
	}
    
    public String getKeyword() {
		return keyword;
	}

    public List<Filter> getFilters() {
		return filters;
	}

    public Filter getFilter(String key) {
    	for(Filter filter : filters){
    		if(key.equals(filter.name)){
    			return filter;
    		}
    	}
		return null;
	}
    
    /**
     * 添加分组
     */
    /**
     * 定义分组的条件
     */
    public Query addGroupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }
    
    /**
     * 获得分组的条件
     */
    public String getGroupBy() {
        return groupBy;
    }
    
    /**
     * 添加排序
     */
    public Query addOrder(String name, OrderType orderType) throws RuntimeException {
        if (ValidateUtils.isNull(name)) {
            if (log.isWarnEnabled()) {
                log.warn("invaild name and orderType. Name=" + name
                        + " OrderType=" + orderType);
            }

            throw new RuntimeException("invalid param");
        }

        for (Order order : orders) {
            if (name.equals(order.name)) {//如果排序类型和指定类型相同，则直接返回该类型，否则添加新类型
                if (log.isWarnEnabled()) {
                    log.warn("order name=" + name
                            + " already existed!.can't add order.return");
                }

                return this;
            }
        }
        log.debug("add a valid order=" + name + ",type=" + orderType);
        orders.add(new Order(name, orderType));
        return this;
    }
    
	public List<Order> getOrders() {
		return orders;
	}
	
	/**
     * 设置当前页数
     * @param pageNo
     */
	public Query setPageNo(int pageNo) {
		this.pageNo = pageNo;
		return this;
	}
	
	/**
     * 获得当前页数
     * @return
     */
	public int getPageNo() {
		return pageNo;
	}

	/**
     * 设置每页大小
     * 
     * @return
     */
	public Query setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	/**
     * 获得每页大小
     * 
     * @return
     */
	public int getPageSize() {
		return pageSize;
	}

	/**
     * 设置总的大小数
     * 
     * @return
     */
    public Query setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;

        // 当设置了总数的时候,需要判断当前的分页是否有效
        if ((pageNo * pageSize) > totalCounts) {
            pageNo = 0;
        }
        return this;
    }

	public int getTotalCounts() {
		return totalCounts;
	}

    
    /**
     * 设置分页信息
     * 
     * @param indexPage
     * @param pageSize
     * @throws SqlException
     */
    public Query setPage(int indexPage, int pageSize) throws RuntimeException {
        if ((indexPage < 0) || (pageSize < 0)) {
            if (log.isWarnEnabled()) {
                log.warn("invaild indexpage and pageSize. IndexPage="
                        + indexPage + " pageSize=" + pageSize
                        + ". Set default value");
            }

            throw new RuntimeException("invalid param");
        }

        this.pageNo = indexPage;
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 获得页的大小
     * 
     * @return
     */
    public int getTotalPage() {
        // 如果是不分页,则总页数为1
        if (getPageSize() <= 0) {
            return 1;
        }

        int temp = getTotalCounts() / getPageSize();

        if ((getTotalCounts() % getPageSize()) > 0) {
            // 如果是有余数，则页数加一
            temp++;
        }

        return temp;
    }
    
    //==========================第四部分==============================================================
    /**
     * 几个属性的状态判断方法
     */
    
    /**
     * 是否需要分页(pageSize>0)
     * 
     * @return
     */
    public boolean isNeedPage() {
        return (pageSize > 0);
    }
    
    /**
     * 是否需要过滤
     * 
     * @return
     */
    public boolean isNeedFilter() {
        return (filters.size() > 0);
    }
    
    /**
     * 是否存在指定条件
     * @param filterName
     * @return
     */
    public boolean isNeedFilter(String filterName){
    	return filterNames.contains(filterName);
    }
    
    /**
     * 是否需要分组
     * 
     * @return
     */
    public boolean isNeedGroupBy() {
        return groupBy!=null&&!"".equals(groupBy);
    }
    
    /**
     * 是否需要排序
     * 
     * @return
     */
    public boolean isNeedOrder() {
        return (orders.size() > 0);
    }
    
    //======================第五部分==========================================================================
    /**
     * 生成example
     */
    public <E> E createExample(Query query, Class<E> exampleClass) throws RuntimeException{
		try {
			E example = exampleClass.newInstance();
			Method createMethod = exampleClass.getDeclaredMethod("createCriteria");
			Object criteria = createMethod.invoke(example);

			//条件
			if (isNeedFilter()) {
				List<Filter> filters = getFilters();
		        for (int i = 0; filters != null && i < filters.size(); i++) {
		            Filter filter = filters.get(i);
		            String filterName = filter.name.substring(0,1).toUpperCase()+filter.name.substring(1);
		            FilterType filterType = filter.type;
		            String filterTypStr = "";
		            Object filterValue = filter.value;
		            switch (filterType) {
		            case LIKE:
		                filterTypStr = "Like";
		                break;
		            case EQUALS:
		                filterTypStr = "EqualTo";
		                break;
		            case GREATETHAN:
		                filterTypStr = "GreaterThan";
		                break;
		            case GREATEEQUAL:
		                filterTypStr = "GreaterThanOrEqualTo";
		                break;
		            case LESSTHAN:
		                filterTypStr = "LessThan";
		                break;
		            case LESSEQUAL:
		                filterTypStr = "LessThanOrEqualTo";
		                break;
		            case NOTEQUAL:
		                filterTypStr = "NotEqualTo";
		                break;
		            case IN:
		                filterTypStr = "In";
		                break;
		            }

		            try{
			            String methodName = "and" + filterName + filterTypStr ;
		                Method filterMethod = criteria.getClass().getDeclaredMethod(methodName, filterValue.getClass());
		                filterMethod.invoke(criteria, filterValue);
		            }catch(java.lang.NoSuchMethodException e){
	            	
		            }
		        }
			}

			//分组
			if(isNeedGroupBy()){
				try{
					Method groupByMethod = exampleClass.getDeclaredMethod("setGroupByClause", groupBy.getClass());
					groupByMethod.invoke(example, Object2Map.keyName(groupBy));
				}catch(java.lang.NoSuchMethodException e){
	            	
	            }
			}

			//排序
	        if (isNeedOrder()) {
	            String orderStr = "";
	            for (int i = 0; orders != null && i < orders.size(); i++) {
	                Order order = orders.get(i);
	                String name = order.name;
	                OrderType type = order.type;
	                if (type == OrderType.ASC) {
	                    orderStr += name + " asc";
	                }
	                if (type == OrderType.DESC) {
	                    orderStr += name + " desc";
	                }
	                if (i != orders.size() - 1) {
	                    orderStr += " , ";
	                }
	            }

	            if(orderStr!=null && !"".equals(orderStr)){
	            	try{
	            		Method orderByMethod = exampleClass.getDeclaredMethod("setOrderByClause", orderStr.getClass());
	            		orderByMethod.invoke(example, Object2Map.keyName(orderStr));
	            	}catch(java.lang.NoSuchMethodException e){
		            	
		            }
	            }
	        }
	        
	        return example;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
    /**
     * 生成SolrQuery
     */
    public <E> E createSolrQuery(Query query, Class<E> solrQueryClass) throws RuntimeException{
		try {
			E solrQuery = solrQueryClass.newInstance();
			Class clazz = solrQuery.getClass();
			
			String keyword = ConvertUtils.toSpecial(query.getKeyword());
			if (keyword.length() <= 0) {
				keyword = "*:*";
			}
			StringBuilder q = new StringBuilder(getSolrProcessQ(keyword));

			//条件
			if (isNeedFilter()) {
				List<Filter> filters = getFilters();
		        for (int i = 0; filters != null && i < filters.size(); i++) {
		            Filter filter = filters.get(i);
		            String filterName = filter.name.substring(0,1).toUpperCase()+filter.name.substring(1);
		            FilterType filterType = filter.type;
		            String filterTypStr = "";
		            Object filterValue = ConvertUtils.toSpecial(filter.value.toString());
		            switch (filterType) {
		            case LIKE:
		                filterTypStr = "*"+filterValue+"*";
		                break;
		            case EQUALS:
		                filterTypStr = ""+filterValue;
		                break;
		            case GREATETHAN:
		                filterTypStr = "{"+filterValue+" TO *]";
		                break;
		            case GREATEEQUAL:
		                filterTypStr = "["+filterValue+" TO *]";
		                break;
		            case LESSTHAN:
		                filterTypStr = "[* TO "+filterValue+"}";
		                break;
		            case LESSEQUAL:
		                filterTypStr = "[* TO "+filterValue+"]";
		                break;
		            case NOTEQUAL:
		            	filterName = "-"+filterName;
		                filterTypStr = ""+filterValue;
		                break;
		            case IN:
		                filterTypStr = getSolrQStringLikeSqlIns(filterValue, filterName);
		                break;
		            }
		            q.append(" AND ").append(filterName).append(":\"" + filterTypStr + "\"");
		        }
			}

			Method setMethod = clazz.getDeclaredMethod("set", String.class, String.class);
			setMethod.invoke(solrQuery, "q.op", "AND");
			Method queryMethod = clazz.getDeclaredMethod("setQuery", String.class);
			queryMethod.invoke(solrQuery, q.toString());
			Method fieldMethod = clazz.getDeclaredMethod("addField", String.class);
			fieldMethod.invoke(solrQuery, "*");

			//分组
			if(isNeedGroupBy()){
				Method facetMethod = clazz.getDeclaredMethod("setFacet", Boolean.class);
				facetMethod.invoke(solrQuery, true);
				Method addFacetMethod = clazz.getDeclaredMethod("addFacetField", String.class);
				addFacetMethod.invoke(solrQuery, groupBy);
			}

			//排序
	        if (isNeedOrder()) {
	        	Class orderClazz = null;
	        	Object orderDesc = null;
	        	Object orderAsc = null;
	        	for(Class c : clazz.getDeclaredClasses()){
	    			if(c.isEnum() && "ORDER".equals(c.getSimpleName())){
	    				orderClazz = c;
	    				Method orderMethod = c.getDeclaredMethod("valueOf", String.class);
	    				orderDesc = orderMethod.invoke(solrQuery, "desc");
	    				orderAsc = orderMethod.invoke(solrQuery, "asc");
	    			}
	    		}
	        	Method sortMethod = clazz.getDeclaredMethod("addSort", String.class, orderClazz);
	            for (int i = 0; orders != null && i < orders.size(); i++) {
	                Order order = orders.get(i);
	                String name = order.name;
	                OrderType type = order.type;
	                if (type == OrderType.ASC) {
	                    sortMethod.invoke(solrQuery, name, orderAsc);
	                }
	                if (type == OrderType.DESC) {
	                    sortMethod.invoke(solrQuery, name, orderDesc);
	                }
	            }
	        }
	        
	        return solrQuery;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
    public String getSolrQStringLikeSqlIns(Object objList, String fieldName) {
    	if(objList instanceof List){
    		return getSolrQStringLikeSqlIn((List)objList, fieldName);
    	}else if(objList instanceof String[]){
    		return getSolrQStringLikeSqlIn((String[])objList, fieldName);
    	}else{
    		return getSolrQStringLikeSqlIn(objList.toString(), fieldName);
    	}
    }

    /**
     * 多个字段或查询
     * @param ins  [1,2,3]
     * @param fieldName "id"
     * @return "(id:1 OR id:2 OR id:3 )"
     */
    public String getSolrQStringLikeSqlIn(List<?> ins, String fieldName) {
        String q = "";
        if (ins.size() > 0) {
            for (Object obj : ins) {
                String d = obj.toString();
                if (d.equals("-1")) {
                    d = "*";
                }
                q += fieldName + ":\"" + d + "\" OR ";
            }

            if (q.endsWith("OR ")) {
                q = q.substring(0, q.lastIndexOf("OR "));
            }

            if (q.length() > 0) {
                q = " (" + q + ") ";
            }
        }
        return q;
    }

    /**
     * 多个字段或查询
     * @param ins  1,2,3
     * @param fieldName "id"
     * @return "(id:1 OR id:2 OR id:3 )"
     */
    public String getSolrQStringLikeSqlIn(String ins, String fieldName) {
        String q = "";
        if (ins.length() > 0) {
            String[] arr = ins.split(",");
            return getSolrQStringLikeSqlIn(arr, fieldName);
        }
        return q;
    }

    /**
     * 多个字段或查询
     * @param ins  [1,2,3]
     * @param fieldName "id"
     * @return "(id:1 OR id:2 OR id:3 )"
     */
    public String getSolrQStringLikeSqlIn(String[] ins, String fieldName) {
        String q = "";
        if (ins.length > 0) {

            for (Object obj : ins) {
                String d = obj.toString();
                if (d.equals("-1")) {
                    d = "*";
                }
                q += fieldName + ":" + d + " OR ";
            }

            if (q.endsWith("OR ")) {
                q = q.substring(0, q.lastIndexOf("OR "));
            }

            if (q.length() > 0) {
                q = " (" + q + ") ";
            }

        }
        return q;
    }

    /**
     * 模糊查询
     * @param q 南京
     * @return "南京"~30   text字段包含"南京"或则相似度30%
     */
    public String getSolrProcessQ(String q) {
        if (q != null && !q.equals("")) {
            q = q.trim();
            if (q.indexOf(" ") == -1 && q.indexOf(":") == -1) {
                q = "\"" + q + "\"~30 ";
            }
            return q;
        }
        return q;
    }
}
