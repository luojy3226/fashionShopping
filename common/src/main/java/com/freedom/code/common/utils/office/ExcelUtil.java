package com.freedom.code.common.utils.office;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.freedom.code.common.exception.ServiceException;

//import jxl.read.biff.BiffException;
/* 
 * excel 导入导出通用工具
 * ExcelUtil工具类实现功能: 
 * 导出时传入list<T>,即可实现导出为一个excel,其中每个对象Ｔ为Excel中的一条记录. 
 * 导入时读取excel,得到的结果是一个list<T>.T是自己定义的对象. 
 * 需要导出的实体对象只需简单配置注解就能实现灵活导出,通过注解您可以方便实现下面功能: 
 * 1.实体属性配置了注解就能导出到excel中,每个属性都对应一列. 
 * 2.列名称可以通过注解配置. 
 * 3.导出到哪一列可以通过注解配置. 
 * 4.鼠标移动到该列时提示信息可以通过注解配置. 
 * 5.用注解设置只能下拉选择不能随意填写功能. 
 * 6.用注解设置是否只导出标题而不导出内容,这在导出内容作为模板以供用户填写时比较实用. 
 * 本工具类以后可能还会加功能,请关注我的博客: http://blog.csdn.net/lk_blog 
 */ 
public class ExcelUtil<T> {
	Class<T> clazz;  
	  
    public ExcelUtil(Class<T> clazz) {  
        this.clazz = clazz;  
    }  
    
    /**
     * 导入excel 仅支持2003版office
     * @param sheetName
     * @param input
     * @return
     */
    public List<T> importExcelHSSF(String sheetName, InputStream input) {  
        int maxCol = 0;  //最大列数
        List<T> list = new ArrayList<T>();  
        try {  
        	//新建一个工作簿
            HSSFWorkbook workbook = new HSSFWorkbook(input);  
            //新建一个sheet工作栏
            HSSFSheet sheet = workbook.getSheet(sheetName);  
            if (!sheetName.trim().equals("")) {  
                sheet = workbook.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.  
            }  
            if (sheet == null) {  
                sheet = workbook.getSheetAt(0); // 如果传入的sheet名不存在则默认指向第1个sheet.  
            }  
            //获取sheet栏的记录行数
            int rows = sheet.getPhysicalNumberOfRows();  
  
            if (rows > 0) {// 有数据时才处理  
                // Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.  
            	//获取导入类的所有带注解的属性列表
                List<Field> allFields = getMappedFiled(clazz, null);  
                // 定义一个map用于存放列的序号和field. 
                Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>(); 
                //变量实体类的field属性集合
                for (Field field : allFields) {  
                    // 将有注解的field存放到map中.  
                    if (field.isAnnotationPresent(ExcelVOAttribute.class)) {  
                    	//获取属性的注解
                        ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                        int col = getExcelCol(attr.column());// 获得列号  
                        maxCol = Math.max(col, maxCol);  
                        // System.out.println(col + "====" + field.getName());  
                        field.setAccessible(true);// 设置类的私有字段属性可访问.  
                        fieldsMap.put(col, field); //加入到集合
                    }  
                }  
                //变量sheet栏的数据行
                for (int i = 1; i < rows; i++) {// 从第2行开始取数据,默认第一行是表头.  
                    HSSFRow row = sheet.getRow(i);  
                    // int cellNum = row.getPhysicalNumberOfCells();  
                    // int cellNum = row.getLastCellNum();  
                    int cellNum = maxCol;  
                    T entity = null;  
                    //获取每一行的第n列的cell单元格
                    for (int j = 0; j < cellNum; j++) {  
                        HSSFCell cell = row.getCell(j);  
                        if (cell == null) {  
                            continue;  
                        }  
                        //获取单元格的数据类型
                        int cellType = cell.getCellType();  
                        String c = "";  
                        if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {//如果是数值类型
                            c = String.valueOf(cell.getNumericCellValue());  
                        } else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {//如果是boolean类型
                            c = String.valueOf(cell.getBooleanCellValue());  
                        } else { //其他类型
                            c = cell.getStringCellValue();  
                        }  
                        if (c == null || c.equals("")) {  
                            continue;  
                        }  
                        entity = (entity == null ? clazz.newInstance() : entity);// 如果不存在实例则新建.  
                        // System.out.println(cells[j].getContents());  
                        Field field = fieldsMap.get(j);// 从map中得到对应列的field.  
                        if (field==null) {  
                            continue;  
                        }  
                        // 取得类型,并根据对象类型设置值.  
                        Class<?> fieldType = field.getType();  
                        if (String.class == fieldType) {  
                            field.set(entity, String.valueOf(c));  
                        } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {  
                            field.set(entity, Integer.parseInt(c));  
                        } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) { 
                            field.set(entity, Long.valueOf(c));  
                        } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {            
                            field.set(entity, Float.valueOf(c));  
                        } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {         
                            field.set(entity, Short.valueOf(c));  
                        } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {                
                            field.set(entity, Double.valueOf(c));  
                        } else if (Character.TYPE == fieldType) {  
                            if ((c != null) && (c.length() > 0)) {  
                                field.set(entity, Character  
                                        .valueOf(c.charAt(0)));  
                            }  
                        }  
  
                    }  
                    if (entity != null) {  
                        list.add(entity);  
                    }  
                }  
            }  
  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (InstantiationException e) {  
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            e.printStackTrace();  
        } catch (IllegalArgumentException e) {  
            e.printStackTrace();  
        }  
        return list;  
    }  
    
    
    /**
     * 对list数据源将其里面的数据导入到excel表单 
     * @param lists: List<T>集合型数组
     * @param sheetNames：sheet工作栏名称数组，与lists的大小对应
     * @param output：输出流
     * @return
     */
    public boolean exportExcelHSSF(List<T> lists[], String sheetNames[], OutputStream output) {  
             
        if (lists.length != sheetNames.length) {  
            System.out.println("数组长度不一致");  
            return false;  
        }  
  
        HSSFWorkbook workbook = new HSSFWorkbook();// 产生工作薄对象  
        //遍历List<T>集合数组
        for (int ii = 0; ii < lists.length; ii++) {  
            List<T> list = lists[ii];  
            String sheetName = sheetNames[ii];//sheet栏名称  
            //获取类的属性
            List<Field> fields = getMappedFiled(clazz, null);  
            //创建一个sheet工作栏
            HSSFSheet sheet = workbook.createSheet();// 产生工作表对象  
            //设置工作栏名称
            workbook.setSheetName(ii, sheetName);  
  
            HSSFRow row; //数据行
            HSSFCell cell;// 产生单元格  
            HSSFCellStyle style = workbook.createCellStyle();//单元格样式  
            style.setFillForegroundColor(HSSFColor.SKY_BLUE.index); //设置前景填充颜色 
            style.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index); //设置背景颜色 
            row = sheet.createRow(0);// 产生一行  
            
            // 写入各个字段的列头名称 ，并对每列数据进行相应的格式设置：提示信息或下拉框
            for (int i = 0; i < fields.size(); i++) {  
                Field field = fields.get(i);  
                ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);//获取属性的注解  
                int col = getExcelCol(attr.column());// 获得列号  
                cell = row.createCell(col);// 创建列  
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置列中写入内容为String类型  
                cell.setCellValue(attr.name());// 写入列名  
  
                // 如果设置了提示信息则鼠标放上去提示.  
                if (!attr.prompt().trim().equals("")) {  
                    setHSSFPrompt(sheet, "", attr.prompt(), 1, 100, col, col);// 这里默认设了2-101列提示.  
                }  
                // 如果设置了combo属性则本列只能选择不能输入  
                if (attr.combo().length > 0) {  
                    setHSSFValidation(sheet, attr.combo(), 1, 100, col, col);// 这里默认设了2-101列只能选择不能输入.  
                }  
                cell.setCellStyle(style);  
            }  
  
            int startNo = 0;  
            int endNo = list.size();  
            // 写入各条记录,每条记录对应excel表中的一行  
            for (int i = startNo; i < endNo; i++) {  
                row = sheet.createRow(i + 1 - startNo);//从第二行开始，第一行默认是标题行  
                T vo = (T) list.get(i); // 得到导出对象.  
                //变量对象的属性列
                for (int j = 0; j < fields.size(); j++) {  
                    Field field = fields.get(j);// 获得field.  
                    field.setAccessible(true);// 设置实体类私有属性可访问  
                    //获取属性注解
                    ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);  
                              
                    try {  
                        // 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.  
                        if (attr.isExport()) {  
                            cell = row.createCell(getExcelCol(attr.column()));// 创建每行的cell  
                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
                            cell.setCellValue(field.get(vo) == null ? "": String.valueOf(field.get(vo)));// 如果数据存在就填入,不存在填入空格.            
                        }  
                    } catch (IllegalArgumentException e) {  
                        e.printStackTrace();  
                    } catch (IllegalAccessException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
  
        try {  
            output.flush();  //清除剩余在输出流中的字节
            workbook.write(output); //输出成excel文件
            output.close();  
            return true;  
        } catch (IOException e) {  
            e.printStackTrace();  
            System.out.println("Output is closed ");  
            return false;  
        }  
  
    }  
    
    
    /**
     * 导入excel 支持xlsx类型
     * @param sheetName
     * @param input
     * @return
     */
    public List<T> importExcelXSSF(String sheetName, InputStream input){
    	List<T> list = new ArrayList<T>();
		try {
			//创建xlsx类型的工作簿
			XSSFWorkbook book = new XSSFWorkbook(input);
			XSSFSheet sheet = null;//sheet工作栏
			
			if (!sheetName.trim().equals("")) {
				sheet = book.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
			}
			if (sheet == null) {
				sheet = book.getSheetAt(0);// 如果传入的sheet名不存在则默认指向第1个sheet.
			}
			//获取sheet栏的数据行数
			int rows = sheet.getPhysicalNumberOfRows();// 得到数据的行数
			if (rows > 0) {// 有数据时才处理
				//获取类的所有属性
				Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
				Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();// 定义一个map用于存放列的序号和field.
				for (Field field : allFields) {
					// 将有注解的field存放到map中.
					if (field.isAnnotationPresent(ExcelVOAttribute.class)) {//判断是否含有注解
						//获取属性前的注解
						ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
								
						int col = getExcelCol(attr.column());// 获得列号
						// System.out.println(col + "====" + field.getName());
						field.setAccessible(true);// 设置类的私有字段属性可访问.
						fieldsMap.put(col, field);//存入集合
					}
				}
				//遍历sheet的行记录
				for (int i = 1; i < rows; i++) {// 从第2行开始取数据,默认第一行是表头.
					XSSFRow row = sheet.getRow(i);
					String c;
					T entity = null;
					//遍历行记录
					for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {
							
						// 通过 row.getCell(j).toString() 获取单元格内容，
						c = row.getCell(j) != null ? row.getCell(j).toString(): "";
								
						if (c.equals("")) {
							continue;
						}
						entity = (entity == null ? clazz.newInstance() : entity);// 如果不存在实例则新建.
						// System.out.println(cells[j].getContents());
						Field field = fieldsMap.get(j);// 从map中得到对应列的field.
						// 取得类型,并根据对象类型设置值.
						Class<?> fieldType = field.getType();
						if ((Integer.TYPE == fieldType)|| (Integer.class == fieldType)) {//整型
								
							field.set(entity, Integer.parseInt(c));
						} else if (String.class == fieldType) {//string
							field.set(entity, String.valueOf(c));
						} else if ((Long.TYPE == fieldType)|| (Long.class == fieldType)) {//long
								
							field.set(entity, Long.valueOf(c));
						} else if ((Float.TYPE == fieldType)|| (Float.class == fieldType)) {//float
								
							field.set(entity, Float.valueOf(c));
						} else if ((Short.TYPE == fieldType)|| (Short.class == fieldType)) {//short
								
							field.set(entity, Short.valueOf(c));
						} else if ((Double.TYPE == fieldType)|| (Double.class == fieldType)) {//double
								
							field.set(entity, Double.valueOf(c));
						} else if (Character.TYPE == fieldType) {//chart
							if ((c != null) && (c.length() > 0)) {
								field.set(entity, Character
										.valueOf(c.charAt(0)));
							}
						}
					}
					if (entity != null) {
						list.add(entity);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return list;
    }
    
    /**
     * 导出excel 支持xlsx后缀
     * @param lists：list集合数组
     * @param sheetNames：sheetname数组
     * @param output
     * @return
     */
    public boolean exportExcelXSSF(List<T> lists[], String sheetNames[], OutputStream output) {  
        
        if (lists.length != sheetNames.length) {  
            System.out.println("数组长度不一致");  
            return false;  
        }  
  
        XSSFWorkbook workbook = new XSSFWorkbook();// 产生工作薄对象  
        //遍历List<T>集合数组
        for (int ii = 0; ii < lists.length; ii++) {  
            List<T> list = lists[ii];  
            String sheetName = sheetNames[ii];//sheet栏名称  
            //获取类的属性
            List<Field> fields = getMappedFiled(clazz, null);  
            //创建一个sheet工作栏
            XSSFSheet sheet = workbook.createSheet();// 产生工作表对象  
            //设置工作栏名称
            workbook.setSheetName(ii, sheetName);  
  
            XSSFRow row; //数据行
            XSSFCell cell;// 产生单元格  
            XSSFCellStyle style = workbook.createCellStyle();//单元格样式  
            style.setFillForegroundColor(new XSSFColor(Color.black)); //设置前景填充颜色 
            style.setFillBackgroundColor(new XSSFColor(Color.white)); //设置背景颜色 
            row = sheet.createRow(0);// 产生一行  
            
            // 写入各个字段的列头名称 ，并对每列数据进行相应的格式设置：提示信息或下拉框
            for (int i = 0; i < fields.size(); i++) {  
                Field field = fields.get(i);  
                ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);//获取属性的注解  
                int col = getExcelCol(attr.column());// 获得列号  
                cell = row.createCell(col);// 创建列  
                cell.setCellType(XSSFCell.CELL_TYPE_STRING);// 设置列中写入内容为String类型  
                cell.setCellValue(attr.name());// 写入列名  
  
                // 如果设置了提示信息则鼠标放上去提示.  
                if (!attr.prompt().trim().equals("")) {  
                	setXSSFPrompt(sheet, "", attr.prompt(), 1, 100, col, col);// 这里默认设了2-101列提示.  
                }  
                // 如果设置了combo属性则本列只能选择不能输入  
                if (attr.combo().length > 0) {  
                	setXSSFValidation(sheet, attr.combo(), 1, 100, col, col);// 这里默认设了2-101列只能选择不能输入.  
                }  
                cell.setCellStyle(style);  
            }  
  
            int startNo = 0;  
            int endNo = list.size();  
            // 写入各条记录,每条记录对应excel表中的一行  
            for (int i = startNo; i < endNo; i++) {  
                row = sheet.createRow(i + 1 - startNo);//从第二行开始，第一行默认是标题行  
                T vo = (T) list.get(i); // 得到导出对象.  
                //变量对象的属性列
                for (int j = 0; j < fields.size(); j++) {  
                    Field field = fields.get(j);// 获得field.  
                    field.setAccessible(true);// 设置实体类私有属性可访问  
                    //获取属性注解
                    ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);  
                              
                    try {  
                        // 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.  
                        if (attr.isExport()) {  
                            cell = row.createCell(getExcelCol(attr.column()));// 创建每行的cell  
                            cell.setCellType(XSSFCell.CELL_TYPE_STRING);  
                            cell.setCellValue(field.get(vo) == null ? "": String.valueOf(field.get(vo)));// 如果数据存在就填入,不存在填入空格.            
                        }  
                    } catch (IllegalArgumentException e) {  
                        e.printStackTrace();  
                    } catch (IllegalAccessException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
  
        try {  
            output.flush();  //清除剩余在输出流中的字节
            workbook.write(output); //输出成excel文件
            output.close();  
            return true;  
        } catch (IOException e) {  
            e.printStackTrace();  
            System.out.println("Output is closed ");  
            return false;  
        }  
  
    }  
    
    /** 
     * 对list数据源将其里面的数据导入到excel表单 
     *  
     * @param sheetName 
     *            工作表的名称 
     * @param sheetSize 
     *            每个sheet中数据的行数,此数值必须小于65536 
     * @param output 
     *            java输出流 
     */  
    public boolean exportExcelXls(List<T> list, String sheetName,  
            OutputStream output) {  
        List<T>[] lists = new ArrayList[1];  
        lists[0] = list;  
  
        String[] sheetNames = new String[1];  
        sheetNames[0] = sheetName;  
  
        return exportExcelHSSF(lists, sheetNames, output);  
    }  
    
    /**
     * 导出xls类型 excel
     * @param map
     * @param output
     * @return
     */
    public boolean exportExcelXls(Map<String,List<T>> map,OutputStream output) {
    	if(map.size() == 0 || map == null){
//    		throw new ServiceException("数据不能为空，请检查数据");
    		return false;
    	}
    	//创建list<T>数组存储List<T>集合
    	 List<T>[] lists = new ArrayList[map.size()];
    	 //存储sheet栏名称
    	String [] sheetNames = new String[map.size()];
    	//遍历map集合
    	//设置map迭代器
    	Iterator<Map.Entry<String,List<T>>> it = map.entrySet().iterator();
    	int i = 0;//数组下标
    	
    	while(it.hasNext()){
    		lists[i] = it.next().getValue();//获取map的value
    		sheetNames[i] = it.next().getKey();//获取map的key
    		i++;//下标自增
    	}
    	
    	/*
    	for(Map.Entry<String, List<T>> entry:map.entrySet()){
    		lists[i] = entry.getValue(); //获取map的value
    		sheetNames[i] = entry.getKey();//获取map的key
    		i++;//下标自增
    	}*/
        return exportExcelHSSF(lists, sheetNames, output);  
    }  
    
    /**
     * 导出xlsx类型excel
     * @param list
     * @param sheetName
     * @param output
     * @return
     */
    public boolean exportExcelXlsx(List<T> list, String sheetName,  
            OutputStream output) {  
        List<T>[] lists = new ArrayList[1];  
        lists[0] = list;  
  
        String[] sheetNames = new String[1];  
        sheetNames[0] = sheetName;  
  
        return exportExcelXSSF(lists, sheetNames, output);  
    } 
    
    /**
     * 导出xlsx的excel
     * @param map
     * @param output
     * @return
     */
    public boolean exportExcelXlsx(Map<String,List<T>> map,OutputStream output){
    	if(map.size() == 0 || map == null){
//    		throw new ServiceException("数据不能为空，请检查数据");
    		return false;
    	}
    	//创建list<T>数组存储List<T>集合
    	 List<T>[] lists = new ArrayList[map.size()];
    	 //存储sheet栏名称
    	String [] sheetNames = new String[map.size()];
    	//遍历map集合
    	//设置map迭代器
    	Iterator<Map.Entry<String,List<T>>> it = map.entrySet().iterator();
    	int i = 0;//数组下标
    	
    	while(it.hasNext()){
    		lists[i] = it.next().getValue();//获取map的value
    		sheetNames[i] = it.next().getKey();//获取map的key
    		i++;//下标自增
    	}
    	
    	/*
    	for(Map.Entry<String, List<T>> entry:map.entrySet()){
    		lists[i] = entry.getValue(); //获取map的value
    		sheetNames[i] = entry.getKey();//获取map的key
    		i++;//下标自增
    	}*/
    	
    	return exportExcelXSSF(lists,sheetNames,output);
    }
    
  
    /** 
     * 将EXCEL中A,B,C,D,E列映射成0,1,2,3 
     *  
     * @param col 
     */  
    public static int getExcelCol(String col) {  
        col = col.toUpperCase();  
        // 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。  
        int count = -1;  
        char[] cs = col.toCharArray();  
        for (int i = 0; i < cs.length; i++) {  
            count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);  
        }  
        return count;  
    }  
  
    /** 
     * 设置单元格上提示 
     *  
     * @param sheet 
     *            要设置的sheet. 
     * @param promptTitle 
     *            标题 
     * @param promptContent 
     *            内容 
     * @param firstRow 
     *            开始行 
     * @param endRow 
     *            结束行 
     * @param firstCol 
     *            开始列 
     * @param endCol 
     *            结束列 
     * @return 设置好的sheet. 
     */  
    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle,  
            String promptContent, int firstRow, int endRow, int firstCol,  
            int endCol) {  
        // 构造constraint对象  ，用于对数据进行美化约束
        DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("DD1");  
                  
        // 四个参数分别是：起始行、终止行、起始列、终止列  ，设置单元格的范围
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow, firstCol, endCol);  
                  
        // 数据有效性对象  
        HSSFDataValidation data_validation_view = new HSSFDataValidation(regions, constraint);  
                  
        data_validation_view.createPromptBox(promptTitle, promptContent);//创建提示信息  
        sheet.addValidationData(data_validation_view);//给sheet工作栏添加提示信息
        return sheet;  
    }  
    
  
    /**
     * 设置单元格提示,xlsx类型
     * @param sheet
     * @param promptTitle
     * @param promptContent
     * @param firstRow
     * @param endRow
     * @param firstCol
     * @param endCol
     * @return
     */
    public static XSSFSheet setXSSFPrompt(XSSFSheet sheet, String promptTitle,  
            String promptContent, int firstRow, int endRow, int firstCol,  
            int endCol) {  
        // 构造constraint对象  ，用于对数据进行美化约束
    	XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                .createCustomConstraint("DD1");//创建自定义约束
        // 四个参数分别是：起始行、终止行、起始列、终止列  ，设置单元格的范围
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow, firstCol, endCol);  
                  
        //创建约束
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(
                dvConstraint, regions);
        validation.createPromptBox(promptTitle, promptContent);//设置提示信息
        sheet.addValidationData(validation);//给sheet工作栏添加提示信息
        return sheet;  
    }  
  
    /** 
     * 设置某些列的值只能输入预制的数据,显示下拉框. 
     *  
     * @param sheet 
     *            要设置的sheet. 
     * @param textlist 
     *            下拉框显示的内容 
     * @param firstRow 
     *            开始行 
     * @param endRow 
     *            结束行 
     * @param firstCol 
     *            开始列 
     * @param endCol 
     *            结束列 
     * @return 设置好的sheet. 
     */  
    public static HSSFSheet setHSSFValidation(HSSFSheet sheet,  
            String[] textlist, int firstRow, int endRow, int firstCol,  
            int endCol) {  
        // 加载下拉列表内容 ，
        DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);  
                  
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列  
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow, firstCol, endCol);  
                  
        // 数据有效性对象  
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);  
                  
        sheet.addValidationData(data_validation_list);//sheet栏添加下拉选数据  
        return sheet;  
    }  
    
    /**
     * 设置下拉框 xlsx类型
     * @param sheet
     * @param textlist
     * @param firstRow
     * @param endRow
     * @param firstCol
     * @param endCol
     * @return
     */
    public static XSSFSheet setXSSFValidation(XSSFSheet sheet,  
            String[] textlist, int firstRow, int endRow, int firstCol,  
            int endCol) {  
        // 加载下拉列表内容 ，
    	XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                .createExplicitListConstraint(textlist);//设置下拉框约束
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列  
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow, firstCol, endCol);  
                  
        // 数据有效性对象  
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(
                dvConstraint, regions);
        sheet.addValidationData(validation);//sheet栏添加下拉选数据  
        return sheet;  
    }  
  
    /** 
     * 得到实体类所有通过注解映射了数据表的字段 
     *  
     * @param map 
     * @return 
     */  
    private List<Field> getMappedFiled(Class clazz, List<Field> fields) {  
        if (fields == null) {  
            fields = new ArrayList<Field>();  
        }  
  
        Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段  
        // 得到所有field并存放到一个list中.  
        for (Field field : allFields) {  
        	//判断属性是否使用了注解，如果是则添加到集合返回
            if (field.isAnnotationPresent(ExcelVOAttribute.class)) {  
                fields.add(field);  
            }  
        }  
        if (clazz.getSuperclass() != null  
                && !clazz.getSuperclass().equals(Object.class)) {  
            getMappedFiled(clazz.getSuperclass(), fields);  
        }  
  
        return fields;  
    }  
}
