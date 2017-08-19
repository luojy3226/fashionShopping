package com.freedom.code.common.utils.office;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * excel 导入导出操作demo
 * @author luo
 *
 */
public class ExcelTestDemo {
	public static void main(String[] args) {
		//导出demo
		 // 初始化数据  
        List<ExcelEntityDemo_Student> list = new ArrayList<ExcelEntityDemo_Student>();  
  
        ExcelEntityDemo_Student vo = new ExcelEntityDemo_Student();  
        vo.setId(1);  
        vo.setName("李坤");  
        vo.setAge(26);  
        vo.setClazz("五期提高班");  
        vo.setCompany("天融信");  
        list.add(vo);  
  
        ExcelEntityDemo_Student vo2 = new ExcelEntityDemo_Student();  
        vo2.setId(2);  
        vo2.setName("曹贵生");  
        vo2.setClazz("五期提高班");  
        vo2.setCompany("中银");  
        list.add(vo2);  
  
        ExcelEntityDemo_Student vo3 = new ExcelEntityDemo_Student();  
        vo3.setId(3);  
        vo3.setName("柳波");  
        vo3.setClazz("五期提高班");  
        list.add(vo3);  
  
        FileOutputStream out = null;  
        try {  
            out = new FileOutputStream("d:\\success3.xls");  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }  
//        ExcelUtil<ExcelEntityDemo_Student> util = new ExcelUtil<ExcelEntityDemo_Student>(ExcelEntityDemo_Student.class);// 创建工具类.  
//        util.exportExcel(list, "学生信息", 65536, out);// 导出  
        System.out.println("----执行完毕----------");
        
        
        //导入demo
        FileInputStream fis = null;  
        try {  
            fis = new FileInputStream("d:\\success3.xls");  
//            ExcelUtil<ExcelEntityDemo_Student> util1 = new ExcelUtil<ExcelEntityDemo_Student>(  
//            		ExcelEntityDemo_Student.class);// 创建excel工具类  
//            List<ExcelEntityDemo_Student> list1 = util.importExcel("学生信息0", fis);// 导入  
            System.out.println(list);  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }  
	}
}
