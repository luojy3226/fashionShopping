package com.freedom.code.common.kaptcha;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

/**
 * 验证码实现类，本工具类采用第三方的验证码实现工具 实现
 * 谷歌：kaptcha
 * @author luo
 *
 */
public class KaptchaUtils {
	
	/**
	 * 生成验证码步骤
	 * 1.加载验证码配置文件，***.properties 文件：里面有验证码的各种样式设置
	 * 2.利用config类实现kaptcha初始化配置,将config注入到defaultKaptcha验证码实现类
	 * 3.生成验证码
	 * 4.输出验证码
	 * 
	 * 验证码通常以servlet的方式进行请求返回
	 */
	
	/**
	 * 因这是已经实现的验证码插件，只需要配置好验证码配置，即可立即使用
	 * 可在web.xml中配成servlet，也可以在spring中配置成bean提供注入
	 * 验证验证码通过获取用户的session来判断
	 * 
	 * 下面方式是用java方式读取配置文件调用它的jar生成验证码
	 */
	/*public void generateKaptcha(){
		try {
			System.out.println("读取配置文件开始");
			//读取配置信息
			Properties proe = new Properties();
			proe.load(getClass().getClassLoader().getResourceAsStream("kaptcha.properties"));
			
			//kaptcha初始化配置
			Config config = new Config(proe);
			
			//初始化验证码生成器
			DefaultKaptcha kaptcha = new DefaultKaptcha();
			kaptcha.setConfig(config);
			
			//生成验证码
			String capText = kaptcha.createText();// 生成验证码字符串
			// 生成验证码图片
			BufferedImage bi = kaptcha.createImage(capText);
			
			//输出流
			OutputStream out = new BufferedOutputStream(new FileOutputStream("d:\\验证码.jpg"));
			//将生成的验证码以文件流输出做简单测试
			ImageIO.write(bi, "jpg", out);
			out.flush();//清空输出流
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
//			out.close();
		}
	}*/
	
	public void generateKaptcha(String kaptchaUrl){
		//kaptcha初始化配置
		Config config = new Config(KaptchaConfig.prop);
		
		//初始化验证码生成器
		DefaultKaptcha kaptcha = new DefaultKaptcha();
		kaptcha.setConfig(config);
		//生成验证码
		String capText = kaptcha.createText();// 生成验证码字符串
		// 生成验证码图片
		BufferedImage bi = kaptcha.createImage(capText);
		
		try {
			//输出流
			OutputStream out = new BufferedOutputStream(new FileOutputStream(kaptchaUrl));
			//将生成的验证码以文件流输出做简单测试
			ImageIO.write(bi, "jpg", out);
			out.flush();//清空输出流
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		KaptchaUtils util = new KaptchaUtils();
		util.generateKaptcha("d:\\验证码2");
		System.out.println("=====================");
	}
}
