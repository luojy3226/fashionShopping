package com.freedom.code.common.utils.office;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.apache.poi.ss.usermodel.Workbook;

import com.freedom.code.test.Employee;

import net.sf.jxls.transformer.XLSTransformer;

/**
 * JXLS 报表excel工具类
 * 
 * @author luo
 *
 */
public class ExcelUtilsJXLS {
	/**
	 * 用JXLS导出成excel
	 * @param templateName 模板名称
	 * @param request
	 * @param response
	 */
	public static void exportJXLS(String templateName,HttpServletRequest request, HttpServletResponse response){
		//1.生成模板路径
		//1.2 由模板路径生成输入流
		//2.装载数据，或取数方法
		//3.将数据填充到 .xls 模板文件
		XLSTransformer transformer = new XLSTransformer();  
		//4.将有数据的 .xls文件输出
	}
	
	
	/**
	 * 测试JXLS导出
	 * @param args
	 */
	public static void main(String[] args) {
		//1.获取测试模板
		String templateURI = "F:/my_workspace1/common/src/main/resources/template_test.xls";
		System.out.println(templateURI);
		
		
		//3.创建测试数据，进行数据填充
		List<Employee> staff = new ArrayList<Employee>();  
        staff.add(new Employee("Derek", 35, 3000, 0.30));  
        staff.add(new Employee("Elsa", 28, 1500, 0.15));  
        staff.add(new Employee("Oleg", 32, 2300, 0.25));
        System.out.println(staff);
		//4.将文件输出
        Map<String,Object> beans = new HashMap<String,Object>();  
        beans.put("employees", staff);  
        XLSTransformer transformer = new XLSTransformer();
        
        try {
        	//2.生成输入流
        	InputStream in = new BufferedInputStream(new FileInputStream(templateURI));
        	//JXLS转换报表模板
        	Workbook workbook=transformer.transformXLS(in, beans);  
        	//输出流
        	
        	FileOutputStream  out = new FileOutputStream("D:/TEST.xls");
        	workbook.write(out);
        	
        	in.close();
        	out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
       
	}
}
