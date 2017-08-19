package com.freedom.code.common.utils.office;

/**
 * excel 导入导出实体类demo
 * 
 * excel导入导出实体类，每一个属性对应一个数据列，再通过自定义注解对每个数据列做相关操作设置
 * @author luo
 *
 */
public class ExcelEntityDemo_Student {
	 	@ExcelVOAttribute(name = "序号", column = "A")  
	    private int id;  
	  
	    @ExcelVOAttribute(name = "姓名", column = "B", isExport = true)  
	    private String name;  
	  
	    @ExcelVOAttribute(name = "年龄", column = "C", prompt = "年龄保密哦!", isExport = false)  
	    private int age;  
	  
	    @ExcelVOAttribute(name = "班级", column = "D", combo = { "五期提高班", "六期提高班",  
	            "七期提高班" })  
	    private String clazz;  
	  
	    @ExcelVOAttribute(name = "公司", column = "F")  
	    private String company;  
	  
	    //get和set方法(略)...  
	  
	    @Override  
	    public String toString() {  
	        return "StudentVO [id=" + id + ", name=" + name + ", company="  
	                + company + ", age=" + age + ", clazz=" + clazz + "]";  
	    }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public String getCompany() {
			return company;
		}

		public void setCompany(String company) {
			this.company = company;
		}  
	    
	    
}
