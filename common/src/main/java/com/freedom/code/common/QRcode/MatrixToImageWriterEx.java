package com.freedom.code.common.QRcode;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 *二维码数据生成类
 * @author luo
 *
 */
public class MatrixToImageWriterEx {
	//默认带logo的二维码基础配置对象
	private static final MatrixToLogoImageConfig DEFAULT_CONFIG = new MatrixToLogoImageConfig();
	
	/**
	 * 根据内容生成二维码数据
	 * @param content 二维码文字内容[为了信息安全性，一般都要先进行数据加密]
	 * @param width 二维码照片宽度
	 * @param height 二维码照片高度
	 * @param errorCorrectionLevel 纠错等级
	 * @return
	 */
	public static BitMatrix createQRCode(String content,int width,int height,ErrorCorrectionLevel errorCorrectionLevel){
		//对象和编码类型集合
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 设置字符编码
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		// 指定纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
		BitMatrix matrix = null;
		try {
			//将指定内容按照一定编码和规定转换输出
			matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matrix;
	}
	
	/**
	 * 根据内容生成二维码数据
	 * @param content 二维码文字内容[为了信息安全性，一般都要先进行数据加密]
	 * @param width 二维码照片宽度
	 * @param height 二维码照片高度
	 * @return
	 */
	public static BitMatrix createQRCode(String content, int width, int height) {
		return createQRCode(content, width, height, ErrorCorrectionLevel.H);
	}
	
	/**
	 * 将二维码写到文件
	 * @param matrix 要写入的二维码
	 * @param format 二维码照片格式
	 * @param imagePath 二维码照片保存路径
	 * @param logoPath logo路径
	 */
	public static void writeToFile(BitMatrix matrix, String format,String imagePath, String logoPath) {
		 //输入流
		 InputStream in = null;
		 try {
			 //设置logo输入流，读取logo
			 in = new BufferedInputStream(new FileInputStream(logoPath));
			 writeToFile(matrix, format, imagePath, in);
		} catch (Exception e) {

		}
	 }
	 
	 /**
	  * 写入二维码、以及将照片logo写入二维码中
	  * @param matrix 要写入的二维码
	  * @param format 二维码照片格式
	  * @param imagePath 二维码照片保存路径
	  * @param logoInputStream logo输入流
	  */
	 public static void writeToFile(BitMatrix matrix, String format,
			String imagePath, InputStream logoInputStream) {
		 try {
			 MatrixToImageWriter.writeToPath(matrix, format, new File(imagePath).toPath(), new MatrixToImageConfig());
			// 添加logo图片, 此处一定需要重新进行读取，而不能直接使用二维码的BufferedImage 对象
			 BufferedImage img = ImageIO.read(new File(imagePath));
			 MatrixToImageWriterEx.overlapImage(img, format, imagePath, logoInputStream, DEFAULT_CONFIG);
		 } catch (Exception e) {
			e.printStackTrace();
		}
		
	 }
	 
	 /**
	  * 写入二维码、以及将照片logo写入二维码中
	  * @param matrix 要写入的二维码
	  * @param format  二维码照片格式
	  * @param imagePath 二维码照片保存路径
	  * @param logoPath logo路径
	  * @param logoConfig logo配置对象
	  */
	 public static void writeToFile(BitMatrix matrix, String format, String imagePath, InputStream logoPath,
         MatrixToLogoImageConfig logoConfig){
		 try {
			 //照片二维码写入路径
			 MatrixToImageWriter.writeToPath(matrix, format, new File(imagePath).toPath(), new MatrixToImageConfig());
			// 添加logo图片, 此处一定需要重新进行读取，而不能直接使用二维码的BufferedImage 对象
			 BufferedImage img = ImageIO.read(new File(imagePath));
			 MatrixToImageWriterEx.overlapImage(img, format, imagePath, logoPath, logoConfig);
		 } catch (Exception e) {
			e.printStackTrace();
		}
		 
	 }
	 
	 /**
	  * 将logo放入二维码中间
	  * @param image 生成的二维码照片对象
	  * @param formate 照片格式
	  * @param imagePath 照片保存路径
	  * @param logoInputStream logo输入流
	  * @param logoConfig 带logo的二维码格式配置
	  */
	 public static void overlapImage(BufferedImage image, String formate,
		String imagePath, InputStream logoInputStream,MatrixToLogoImageConfig logoConfig) {
		 try {
			 BufferedImage logo = ImageIO.read(logoInputStream);
			 Graphics2D g = image.createGraphics();//二维图
			// 考虑到logo照片贴到二维码中，建议大小不要超过二维码的1/5;
			 int width = image.getWidth() / logoConfig.getLogoPart();
			 int height = image.getHeight() / logoConfig.getLogoPart();
			// logo起始位置，此目的是为logo居中显示
			 int x = (image.getWidth() - width) / 2;
			 int y = (image.getHeight() - height) / 2;
			// 绘制图
			 g.drawImage(logo, x, y, width, height, null);
			// 给logo画边框
			// 构造一个具有指定线条宽度以及 cap 和 join 风格的默认值的实心 BasicStroke
			 g.setStroke(new BasicStroke(logoConfig.getBorder()));
			 g.setColor(logoConfig.getBorderColor());
			 g.drawRect(x, y, width, height);
			 //释放使用的资源
			 g.dispose();
			// 写入logo照片到二维码
			 ImageIO.write(image, formate, new File(imagePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
}
