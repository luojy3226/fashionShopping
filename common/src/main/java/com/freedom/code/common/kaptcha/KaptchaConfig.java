package com.freedom.code.common.kaptcha;
/**
 * kaptcha配置类，静态读取配置文件
 * @author luo
 *
 */

import java.util.Properties;

public class KaptchaConfig {
	public static Properties prop;
	
	//静态块，初始化prop对象，用于构建kaptcha生成工厂
	static{
		prop = new Properties();
		try {
			//因为静态方法下不能使用this,即不能用getClass()来加载源文件中配置文件
			//静态块，方法属于类，而不属于对象不能用this
			prop.load(Object.class.getResourceAsStream("/kaptcha.properties"));
//			System.out.println(prop.getProperty("kaptcha.border"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(prop.getProperty("kaptcha.border"));
	}
}
