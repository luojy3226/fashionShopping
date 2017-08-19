package com.freedom.code.common.QRcode;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码工具类
 * @author luo
 *
 */
public class QRcodeUtils {
	private static final transient Logger LOGGER = LoggerFactory.getLogger(QRcodeUtils.class);
	
	//静态变量，二维码的默认属性
	private static transient String DEFAULT_FORMAT = "jpg";
	private static transient int DEFAULT_WIDTH = 200;
	private static transient int DEFAULT_HEIGHT = 200;
	
	//静态块，初始化资源
	static{
		try {
			final String[] foo = new String[] { "240", "240" };
			final String format = "jpg";
			
			if(StringUtils.isNotBlank(format)){
				//从开始到结束切割format的空格，然后转为小写
				DEFAULT_FORMAT = StringUtils.strip(format).toLowerCase();
			}
			
			if(ArrayUtils.isNotEmpty(foo) && foo.length == 2){
				Integer tmpWidth = Integer.valueOf(foo[0]);//二维码宽度
				Integer tmpHeight = Integer.valueOf(foo[1]);//二维码高度
				if(tmpWidth > 0 && tmpHeight > 0){
					DEFAULT_WIDTH = tmpWidth;
					DEFAULT_HEIGHT = tmpHeight;
				}else {
					LOGGER.warn("qrcode size must be lager than zero.");
				}
			}
		} catch (Exception e) {
			LOGGER.warn("read default qrcode size config error: ", e);
		}
	}
	
	
	/*====================生成二维码========================================================================*/
	
	/**
	 * 生成二维码
	 * @param content  二维码文本内容
	 * @param output 输出流
	 * @param logoInput 中间logo输入流，为空时中间无logo
	 * @param width 宽度
	 * @param height 高度
	 * @param errorCorrectionLevel 容错级别
	 * @throws Exception
	 */
	public static final void generate(final String content,final OutputStream output, final InputStream logoInput, int width,
		 int height, ErrorCorrectionLevel errorCorrectionLevel) {
		if(StringUtils.isEmpty(content)){
			throw new IllegalArgumentException("qr code content cannot be empty.");
		}
		if(output == null){
			throw new IllegalArgumentException("qr code output stream cannot be null.");
		}
		//生成二维码内容
		final BitMatrix matrix = MatrixToImageWriterEx.createQRCode(content, width, height, errorCorrectionLevel);
		
		if(logoInput == null){//没有中间的logo图片,则直接输出二维码
			try {
				MatrixToImageWriter.writeToStream(matrix, DEFAULT_FORMAT, output);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
		
		//二维码初始化配置
		final MatrixToLogoImageConfig logoConfig = new MatrixToLogoImageConfig(Color.BLUE, 4);
		
		final String destPath = FilenameUtils.normalizeNoEndSeparator(SystemUtils.getJavaIoTmpDir()
				+ File.separator + UUID.randomUUID().toString()+ ".tmp");
		InputStream tmpInput = null;
		final File destFile = new File(destPath);
		try {
			//将二维码写入文件
			MatrixToImageWriterEx.writeToFile(matrix, DEFAULT_FORMAT, destPath, logoInput, logoConfig);	
			tmpInput = new BufferedInputStream(new FileInputStream(destFile));
			//从输入流将文件复制到输出流
			IOUtils.copy(tmpInput, output);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(tmpInput);
			destFile.delete();
		}
	}
	
	/**
	 * 生成二维码
	 * @param content 二维码文本内容
	 * @param destFile  目的文件
	 * @param logoFile 中间logo文件
	 * @param width 宽度
	 * @param height 高度
	 * @throws Exception
	 */
	public static final void generate(final String content, final File destFile,
		   final File logoFile, int width, int height) throws Exception {
//		FolderUtils.mkdirs(destFile.getParent());
		OutputStream output = null;
		InputStream input = null;
		try {
			output = new BufferedOutputStream(new FileOutputStream(destFile));
			if (logoFile != null && logoFile.exists() && logoFile.isFile()) {
				input = new BufferedInputStream(new FileInputStream(logoFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 生成二维码
	 * @param content
	 * @param destPath
	 * @param logoPath
	 * @param width
	 * @param height
	 * @throws Exception
	 */
	public static final void generate(final String content, final String destPath,
		final String logoPath, int width, int height) throws Exception {
		
		File foo = new File(destPath);
		File bar = new File(logoPath);
		generate(content, foo, bar, width, height);
	}
	
	/**
	 * 生成二维码
	 * @param content
	 * @param output
	 * @param logoInput
	 * @param width
	 * @param height
	 */
	public static final void generate(final String content,
		final OutputStream output, final InputStream logoInput, int width, int height){
		generate(content, output, logoInput, width, height, ErrorCorrectionLevel.M);
	}
	
	/**
	 * 生成二维码
	 * @param content
	 * @param output
	 * @param width
	 * @param height
	 */
	public static final void generate(final String content, final OutputStream output, int width, int height){
		generate(content, output, null, width, height);
	}
	
	/**
	 * 生成二维码
	 * @param content 二维码文本内容
	 * @param destFile 存储文件路径
	 * @param width 宽度
	 * @param height 高度
	 */
	public static final void generate(final String content, File destFile, int width, int height){
		OutputStream output = null;
		try {
			output = new BufferedOutputStream(new FileOutputStream(destFile));
			generate(content, output, width, height);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(output);
		}
	}
	
	/**
	 * 生成二维码
	 * @param content
	 * @param destPath
	 * @param width
	 * @param height
	 */
	public static final void generate(final String content, final String destPath, int width, int height){
		generate(content, new File(destPath), width, height);
	}
	
	/**
	 * 生成二维码
	 * @param content
	 * @param destPath
	 * @param logoPath
	 */
	public static final void generate(final String content, final String destPath, final String logoPath){
		try {
			generate(content, destPath, logoPath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成二维码
	 * @param content
	 * @param output
	 * @param logoInput
	 */
	public static final void generate(final String content,
		final OutputStream output, final InputStream logoInput){
		generate(content, output, logoInput, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	/**
	 * 生成二维码
	 * @param content
	 * @param destFile
	 * @param logoFile
	 */
	public static final void generate(final String content, final File destFile, final File logoFile){
		try {
			generate(content, destFile, logoFile, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 生成二维码
	 * @param content
	 * @param destPath
	 */
	 public static final void generate(final String content, final String destPath){
		 generate(content, destPath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	 }
	
	 /**
	  * 生成二维码
	  * @param content
	  * @param output
	  */
	 public static final void generate(final String content, final OutputStream output){
		 generate(content, output, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	 }
	
	 /**
	  * 生成二维码
	  * @param content
	  * @param destFile
	  */
	 public static final void generate(final String content, File destFile){
		 generate(content, destFile, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	 }
	 
	 
	 /*==============================解析二维码=====================================================================*/
	 
	/**
	 * 解析二维码
	 * @param input
	 * @return
	 */
	public static final String parse(InputStream input)throws Exception{
		//读取二维码内容
		Reader reader = null;
		//图片缓存流
		BufferedImage image;
		try {
			image = ImageIO.read(input);
			if(image == null){
				throw new Exception("cannot read image from inputstream.");
			}
			//用于请求灰度亮度值的标准接
			final LuminanceSource source = new BufferedImageLuminanceSource(image);
			//zxing的核心图类， Reader对象接受BinaryBitmap并尝试对其进行解码
			final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			
			final Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
			hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
			// 解码设置编码方式为：utf-8，
			reader = new MultiFormatReader();
			return reader.decode(bitmap, hints).getText();
			
		} catch (IOException  e) {
			e.printStackTrace();
			throw new Exception("parse QR code error: ", e);
		} catch(ReaderException e){
			e.printStackTrace();
			throw new Exception("parse QR code error: ", e);
		}
	}
	
	
	/**
	 * 解析二维码
	 * @param url
	 * @return
	 */
	public static final String parse(URL url){
		 InputStream in = null;
		 try {
			 in = url.openStream();
			 return parse(in);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }finally {
			IOUtils.closeQuietly(in);
		 }
		 return "";
	 }
	
	/**
	 * 解析二维码
	 * @param file
	 * @return
	 */
	public static final String parse(File file){
		 InputStream in = null;
		 try {
			 in = new BufferedInputStream(new FileInputStream(file));
			 return parse(in);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }finally {
			IOUtils.closeQuietly(in);
		 }
		 return "";
	 }
	
	/**
	 * 解析二维码
	 * @param filePath
	 * @return
	 */
	 public static final String parse(String filePath){
		 InputStream in = null;
		 try {
			 in = new BufferedInputStream(new FileInputStream(filePath));
			 return parse(in);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }finally {
			IOUtils.closeQuietly(in);
		 }
		 return "";
	 }
	
	 
	 public static void main(String[] args) {
		 //测试二维码生成
		 final String content = "二维码测试";
		 try {
			 //二维码输出流
			final OutputStream out = new BufferedOutputStream(new FileOutputStream("d:\\二维码.jpg"));
			final OutputStream out2 = new BufferedOutputStream(new FileOutputStream("d:\\二维码2.jpg"));
			final OutputStream out1 = new BufferedOutputStream(new FileOutputStream("d:\\二维码1.jpg"));
			 //二维码中间照片输入流
			final InputStream in = new BufferedInputStream(new FileInputStream("d:\\测试.jpg"));
			 //生成有中间照片二维码
			 generate(content,out,in);
			 System.out.println("====================");
			 generate(content,out,in,250,250,ErrorCorrectionLevel.H);
			 System.out.println("====================");
			 
			 //生成没有中间照片的二维码
			 generate(content,out1);
			 System.out.println("====================");
		 } catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
}
